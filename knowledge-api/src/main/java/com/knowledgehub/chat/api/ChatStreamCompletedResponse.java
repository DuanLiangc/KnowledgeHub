package com.knowledgehub.chat.api;

import com.knowledgehub.chat.application.ChatStreamEvent;

/**
 * SSE 流结束时返回的统计信息。
 *
 * @param model 实际模型名称
 * @param usage Token 用量
 * @param elapsedMillis 调用耗时，单位毫秒
 */
public record ChatStreamCompletedResponse(
        String model,
        TokenUsageResponse usage,
        long elapsedMillis
) {

    /**
     * 从应用层完成事件创建响应。
     *
     * @param event 应用层流式完成事件
     * @return SSE 完成事件数据
     */
    static ChatStreamCompletedResponse from(ChatStreamEvent event) {
        return new ChatStreamCompletedResponse(
                event.model(),
                TokenUsageResponse.from(event.usage()),
                event.elapsedMillis()
        );
    }
}
