package com.knowledgehub.chat.api;

import com.knowledgehub.chat.application.ChatResult;

/**
 * 普通模型问答响应。
 *
 * @param answer 模型答案
 * @param model 实际模型名称
 * @param usage Token 用量
 * @param elapsedMillis 调用耗时，单位毫秒
 */
public record ChatResponse(
        String answer,
        String model,
        TokenUsageResponse usage,
        long elapsedMillis
) {

    /**
     * 将应用层结果转换为 HTTP 响应。
     *
     * @param result 应用层问答结果
     * @return HTTP 响应对象
     */
    static ChatResponse from(ChatResult result) {
        return new ChatResponse(
                result.answer(),
                result.model(),
                TokenUsageResponse.from(result.usage()),
                result.elapsedMillis()
        );
    }
}
