package com.knowledgehub.chat.application;

/**
 * 普通模型问答的完整结果。
 *
 * @param answer 模型生成的文本答案
 * @param model 实际响应的模型名称
 * @param usage Token 用量
 * @param elapsedMillis 调用耗时，单位毫秒
 */
public record ChatResult(
        String answer,
        String model,
        TokenUsage usage,
        long elapsedMillis
) {
}
