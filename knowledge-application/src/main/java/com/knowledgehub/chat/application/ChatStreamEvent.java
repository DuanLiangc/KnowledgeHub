package com.knowledgehub.chat.application;

/**
 * 模型流式输出事件。
 *
 * <p>{@link Type#DELTA} 携带新增文本，{@link Type#COMPLETED} 携带最终模型、
 * Token 用量和总耗时。</p>
 *
 * @param type 事件类型
 * @param text 本次新增文本，完成事件中为空
 * @param model 实际模型名称，仅完成事件提供
 * @param usage Token 用量，仅完成事件提供
 * @param elapsedMillis 调用耗时，仅完成事件提供
 */
public record ChatStreamEvent(
        Type type,
        String text,
        String model,
        TokenUsage usage,
        long elapsedMillis
) {

    /** 流式事件类型。 */
    public enum Type {
        /** 新增文本片段。 */
        DELTA,
        /** 模型响应完成。 */
        COMPLETED
    }

    /**
     * 创建文本增量事件。
     *
     * @param text 新增文本
     * @return 文本增量事件
     */
    public static ChatStreamEvent delta(String text) {
        return new ChatStreamEvent(Type.DELTA, text, null, null, 0);
    }

    /**
     * 创建流式完成事件。
     *
     * @param model 实际模型名称
     * @param usage Token 用量
     * @param elapsedMillis 调用耗时
     * @return 完成事件
     */
    public static ChatStreamEvent completed(String model, TokenUsage usage, long elapsedMillis) {
        return new ChatStreamEvent(Type.COMPLETED, "", model, usage, elapsedMillis);
    }
}
