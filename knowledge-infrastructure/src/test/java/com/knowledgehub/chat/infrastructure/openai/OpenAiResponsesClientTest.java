package com.knowledgehub.chat.infrastructure.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgehub.chat.application.ChatCommand;
import com.knowledgehub.chat.application.ChatStreamEvent;
import com.knowledgehub.chat.application.ModelCallException;
import com.knowledgehub.chat.application.ModelNotConfiguredException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenAiResponsesClientTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private HttpServer server;
    private URI baseUrl;

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.start();
        baseUrl = URI.create("http://localhost:" + server.getAddress().getPort());
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    @Test
    void shouldParseNormalResponseAndUsage() {
        server.createContext("/responses", exchange -> {
            JsonNode request = objectMapper.readTree(exchange.getRequestBody());
            assertThat(exchange.getRequestHeaders().getFirst("Authorization")).isEqualTo("Bearer test-key");
            assertThat(request.path("model").asText()).isEqualTo("test-model");
            assertThat(request.path("input").asText()).isEqualTo("你好");
            assertThat(request.path("stream").asBoolean()).isTrue();
            respond(exchange, 200, """
                    event: response.output_text.delta
                    data: {"type":"response.output_text.delta","delta":"你好，"}

                    event: response.output_text.delta
                    data: {"type":"response.output_text.delta","delta":"我是模型。"}

                    event: response.completed
                    data: {"type":"response.completed","response":{"model":"test-model-2026","usage":{"input_tokens":12,"output_tokens":8,"total_tokens":20}}}

                    """, "text/event-stream");
        });

        var result = client("test-key").chat(new ChatCommand("系统指令", "你好"));

        assertThat(result.answer()).isEqualTo("你好，我是模型。");
        assertThat(result.model()).isEqualTo("test-model-2026");
        assertThat(result.usage().totalTokens()).isEqualTo(20);
    }

    @Test
    void shouldParseStreamingDeltaAndCompletedEvents() {
        server.createContext("/responses", exchange -> respond(exchange, 200, """
                event: response.output_text.delta
                data: {"type":"response.output_text.delta","delta":"你"}

                event: response.output_text.delta
                data: {"type":"response.output_text.delta","delta":"好"}

                event: response.completed
                data: {"type":"response.completed","response":{"model":"test-model-2026","usage":{"input_tokens":5,"output_tokens":2,"total_tokens":7}}}

                """, "text/event-stream"));
        List<ChatStreamEvent> events = new ArrayList<>();

        client("test-key").stream(new ChatCommand("系统指令", "你好"), events::add);

        assertThat(events).hasSize(3);
        assertThat(events.get(0).text()).isEqualTo("你");
        assertThat(events.get(1).text()).isEqualTo("好");
        assertThat(events.get(2).type()).isEqualTo(ChatStreamEvent.Type.COMPLETED);
        assertThat(events.get(2).usage().totalTokens()).isEqualTo(7);
    }

    @Test
    void shouldFailClearlyWhenApiKeyIsMissing() {
        OpenAiResponsesClient client = client("");

        assertThatThrownBy(() -> client.chat(new ChatCommand("系统指令", "你好")))
                .isInstanceOf(ModelNotConfiguredException.class)
                .hasMessageContaining("OPENAI_API_KEY");
    }

    @Test
    void shouldTranslateProviderError() {
        server.createContext("/responses", exchange -> respond(exchange, 401, """
                {"error":{"message":"Incorrect API key"}}
                """, "application/json"));

        assertThatThrownBy(() -> client("bad-key").chat(new ChatCommand("系统指令", "你好")))
                .isInstanceOf(ModelCallException.class)
                .hasMessage("Incorrect API key");
    }

    private OpenAiResponsesClient client(String apiKey) {
        OpenAiProperties properties = new OpenAiProperties(
                apiKey,
                baseUrl,
                "test-model",
                Duration.ofSeconds(2),
                Duration.ofSeconds(5),
                100
        );
        return new OpenAiResponsesClient(properties, objectMapper);
    }

    private void respond(HttpExchange exchange, int status, String body, String contentType) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
