package com.knowledgehub.chat.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 模型问答的 HTTP 请求参数。
 *
 * @param question 用户问题
 */
public record ChatRequest(
        @NotBlank(message = "问题不能为空")
        @Size(max = 4000, message = "问题不能超过 4000 个字符")
        String question
) {
}
