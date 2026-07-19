package com.knowledgehub.knowledge.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改知识空间的 HTTP 请求参数。
 *
 * @param name 修改后的知识空间名称
 * @param description 修改后的知识空间描述
 */
public record UpdateKnowledgeSpaceRequest(
        @NotBlank(message = "知识空间名称不能为空")
        @Size(max = 100, message = "知识空间名称不能超过 100 个字符")
        String name,

        @Size(max = 500, message = "知识空间描述不能超过 500 个字符")
        String description
) {
}
