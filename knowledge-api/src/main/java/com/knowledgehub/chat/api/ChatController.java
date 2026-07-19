package com.knowledgehub.chat.api;

import com.knowledgehub.chat.application.ChatService;
import com.knowledgehub.chat.application.ChatStreamEvent;
import com.knowledgehub.chat.application.ModelCallException;
import com.knowledgehub.chat.application.ModelNotConfiguredException;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

/**
 * 提供普通问答和 SSE 流式问答接口。
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private static final long SSE_TIMEOUT_MILLIS = 120_000L;

    private final ChatService chatService;
    private final ExecutorService chatExecutor;

    /**
     * 创建模型问答控制器。
     *
     * @param chatService 模型问答应用服务
     * @param chatExecutor 执行阻塞模型流读取的虚拟线程执行器
     */
    public ChatController(ChatService chatService, ExecutorService chatExecutor) {
        this.chatService = chatService;
        this.chatExecutor = chatExecutor;
    }

    /**
     * 发起普通问答，等待完整答案后返回 JSON。
     *
     * @param request 问答请求
     * @return 完整问答响应
     */
    @PostMapping
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return ChatResponse.from(chatService.chat(request.question()));
    }

    /**
     * 发起 SSE 流式问答。
     *
     * <p>请求线程创建连接后立即返回，虚拟线程负责阻塞读取上游模型流。
     * 客户端断开连接时，后续 SSE 写入会失败并结束本次任务。</p>
     *
     * @param request 问答请求
     * @param response HTTP 响应，用于明确设置 SSE 的 UTF-8 编码
     * @return SSE 事件流
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            @Valid @RequestBody ChatRequest request,
            HttpServletResponse response
    ) {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MILLIS);
        chatExecutor.submit(() -> executeStream(request.question(), emitter));
        return emitter;
    }

    private void executeStream(String question, SseEmitter emitter) {
        try {
            chatService.stream(question, event -> sendEvent(emitter, event));
            emitter.complete();
        } catch (ModelNotConfiguredException exception) {
            sendError(emitter, "MODEL_NOT_CONFIGURED", exception.getMessage());
        } catch (ModelCallException exception) {
            sendError(emitter, "MODEL_CALL_FAILED", exception.getMessage());
        } catch (RuntimeException exception) {
            logger.error("处理模型流式响应时发生未预期异常", exception);
            emitter.completeWithError(exception);
        }
    }

    private void sendEvent(SseEmitter emitter, ChatStreamEvent event) {
        try {
            if (event.type() == ChatStreamEvent.Type.DELTA) {
                emitter.send(SseEmitter.event().name("delta").data(event.text()));
            } else {
                emitter.send(SseEmitter.event()
                        .name("completed")
                        .data(ChatStreamCompletedResponse.from(event)));
            }
        } catch (IOException exception) {
            throw new ModelCallException("客户端已断开流式连接", exception);
        }
    }

    private void sendError(SseEmitter emitter, String code, String message) {
        try {
            emitter.send(SseEmitter.event()
                    .name("error")
                    .data(new ChatStreamErrorResponse(code, message)));
            emitter.complete();
        } catch (IOException exception) {
            emitter.completeWithError(exception);
        }
    }
}
