package com.knowledgehub.chat.api;

import com.knowledgehub.chat.application.TokenUsage;

/**
 * 返回给客户端的 Token 用量。
 *
 * @param inputTokens 输入 Token 数量
 * @param outputTokens 输出 Token 数量
 * @param totalTokens 总 Token 数量
 */
public record TokenUsageResponse(long inputTokens, long outputTokens, long totalTokens) {

    /**
     * 将应用层用量对象转换为 HTTP 响应。
     *
     * @param usage 应用层 Token 用量
     * @return HTTP 响应对象
     */
    static TokenUsageResponse from(TokenUsage usage) {
        return new TokenUsageResponse(usage.inputTokens(), usage.outputTokens(), usage.totalTokens());
    }
}
