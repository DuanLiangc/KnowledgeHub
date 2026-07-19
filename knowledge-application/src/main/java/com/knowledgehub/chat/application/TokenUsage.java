package com.knowledgehub.chat.application;

/**
 * 一次模型调用的 Token 用量。
 *
 * @param inputTokens 输入 Token 数量
 * @param outputTokens 输出 Token 数量
 * @param totalTokens 总 Token 数量
 */
public record TokenUsage(long inputTokens, long outputTokens, long totalTokens) {

    /** 未获得用量数据时使用的零值。 */
    public static final TokenUsage ZERO = new TokenUsage(0, 0, 0);
}
