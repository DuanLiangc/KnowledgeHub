package com.knowledgehub.chat.infrastructure.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgehub.chat.application.ChatCommand;
import com.knowledgehub.chat.application.ChatModelPort;
import com.knowledgehub.chat.application.ChatResult;
import com.knowledgehub.chat.application.ChatStreamEvent;
import com.knowledgehub.chat.application.ModelCallException;
import com.knowledgehub.chat.application.ModelNotConfiguredException;
import com.knowledgehub.chat.application.TokenUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 使用 JDK {@link HttpClient} 调用 OpenAI Responses API 的适配器。
 *
 * <p>阶段 2 刻意不使用 Spring AI SDK，以便直观看到请求体、认证头、普通 JSON
 * 响应和 SSE 事件。阶段 3 接入 Spring AI 时，应用层接口无需改变。</p>
 */
public final class OpenAiResponsesClient implements ChatModelPort {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiResponsesClient.class);
    private static final String DELTA_EVENT = "response.output_text.delta";
    private static final String COMPLETED_EVENT = "response.completed";

    private final OpenAiProperties properties;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    /**
     * 创建 OpenAI Responses API 客户端。
     *
     * @param properties OpenAI 配置
     * @param objectMapper JSON 序列化工具
     */
    public OpenAiResponsesClient(OpenAiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();
    }

    /** {@inheritDoc} */
    @Override
    public ChatResult chat(ChatCommand command) {
        ensureConfigured();
        long startedAt = System.nanoTime();
        HttpRequest request = createRequest(command, true);
        StringBuilder answer = new StringBuilder();
        ChatStreamEvent[] completedEvent = new ChatStreamEvent[1];

        try {
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String errorBody = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
                ensureSuccess(response.statusCode(), errorBody);
            }
            readServerSentEvents(response.body(), event -> {
                if (event.type() == ChatStreamEvent.Type.DELTA) {
                    answer.append(event.text());
                } else {
                    completedEvent[0] = event;
                }
            }, startedAt, false);

            if (answer.isEmpty()) {
                throw new ModelCallException("模型响应中没有文本答案");
            }
            ChatStreamEvent completed = completedEvent[0];
            ChatResult result = new ChatResult(
                    answer.toString(),
                    completed == null ? properties.model() : completed.model(),
                    completed == null ? TokenUsage.ZERO : completed.usage(),
                    completed == null ? elapsedMillis(startedAt) : completed.elapsedMillis()
            );
            logUsage(result.model(), result.usage(), result.elapsedMillis(), false);
            return result;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ModelCallException("模型调用已中断", exception);
        } catch (IOException exception) {
            throw new ModelCallException("无法连接 AI 模型服务", exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void stream(ChatCommand command, Consumer<ChatStreamEvent> eventConsumer) {
        ensureConfigured();
        long startedAt = System.nanoTime();
        HttpRequest request = createRequest(command, true);

        try {
            HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String errorBody = new String(response.body().readAllBytes(), StandardCharsets.UTF_8);
                ensureSuccess(response.statusCode(), errorBody);
            }
            readServerSentEvents(response.body(), eventConsumer, startedAt, true);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ModelCallException("流式模型调用已中断", exception);
        } catch (IOException exception) {
            throw new ModelCallException("读取模型流式响应失败", exception);
        }
    }

    private HttpRequest createRequest(ChatCommand command, boolean stream) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", properties.model());
        body.put("instructions", command.systemInstruction());
        body.put("input", command.question());
        body.put("max_output_tokens", properties.maxOutputTokens());
        body.put("store", false);
        body.put("stream", stream);

        try {
            return HttpRequest.newBuilder(responsesUri())
                    .timeout(properties.requestTimeout())
                    .header("Authorization", "Bearer " + properties.apiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();
        } catch (JsonProcessingException exception) {
            throw new ModelCallException("构造模型请求失败", exception);
        }
    }

    private URI responsesUri() {
        String baseUrl = properties.baseUrl().toString().replaceAll("/+$", "");
        return URI.create(baseUrl + "/responses");
    }

    private void readServerSentEvents(
            InputStream inputStream,
            Consumer<ChatStreamEvent> eventConsumer,
            long startedAt,
            boolean logCompletedUsage
    ) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String eventName = null;
            StringBuilder data = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    handleEvent(eventName, data.toString(), eventConsumer, startedAt, logCompletedUsage);
                    eventName = null;
                    data.setLength(0);
                } else if (line.startsWith("event:")) {
                    eventName = line.substring("event:".length()).trim();
                } else if (line.startsWith("data:")) {
                    if (!data.isEmpty()) {
                        data.append('\n');
                    }
                    data.append(line.substring("data:".length()).trim());
                }
            }
            handleEvent(eventName, data.toString(), eventConsumer, startedAt, logCompletedUsage);
        }
    }

    private void handleEvent(
            String eventName,
            String data,
            Consumer<ChatStreamEvent> eventConsumer,
            long startedAt,
            boolean logCompletedUsage
    ) throws JsonProcessingException {
        if (data.isBlank() || "[DONE]".equals(data)) {
            return;
        }

        JsonNode root = objectMapper.readTree(data);
        String type = eventName == null ? root.path("type").asText() : eventName;
        if (DELTA_EVENT.equals(type)) {
            eventConsumer.accept(ChatStreamEvent.delta(root.path("delta").asText()));
        } else if (COMPLETED_EVENT.equals(type)) {
            JsonNode response = root.path("response");
            String model = response.path("model").asText(properties.model());
            TokenUsage usage = extractUsage(response.path("usage"));
            long elapsedMillis = elapsedMillis(startedAt);
            if (logCompletedUsage) {
                logUsage(model, usage, elapsedMillis, true);
            }
            eventConsumer.accept(ChatStreamEvent.completed(model, usage, elapsedMillis));
        } else if ("error".equals(type) || "response.failed".equals(type)) {
            throw new ModelCallException(extractErrorMessage(root));
        }
    }

    private TokenUsage extractUsage(JsonNode usage) {
        if (usage.isMissingNode() || usage.isNull()) {
            return TokenUsage.ZERO;
        }
        return new TokenUsage(
                usage.path("input_tokens").asLong(),
                usage.path("output_tokens").asLong(),
                usage.path("total_tokens").asLong()
        );
    }

    private void ensureConfigured() {
        if (properties.apiKey() == null || properties.apiKey().isBlank()) {
            throw new ModelNotConfiguredException();
        }
    }

    private void ensureSuccess(int statusCode, String responseBody) {
        if (statusCode >= 200 && statusCode < 300) {
            return;
        }
        String message;
        try {
            message = extractErrorMessage(objectMapper.readTree(responseBody));
        } catch (JsonProcessingException ignored) {
            message = "模型服务返回 HTTP " + statusCode;
        }
        throw new ModelCallException(message);
    }

    private String extractErrorMessage(JsonNode root) {
        String message = root.path("error").path("message").asText();
        return message.isBlank() ? "AI 模型服务调用失败" : message;
    }

    private long elapsedMillis(long startedAt) {
        return Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
    }

    private void logUsage(String model, TokenUsage usage, long elapsedMillis, boolean streaming) {
        logger.info(
                "模型调用完成 model={}, streaming={}, inputTokens={}, outputTokens={}, totalTokens={}, elapsedMs={}",
                model,
                streaming,
                usage.inputTokens(),
                usage.outputTokens(),
                usage.totalTokens(),
                elapsedMillis
        );
    }
}
