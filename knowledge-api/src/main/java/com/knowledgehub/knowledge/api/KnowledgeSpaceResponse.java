package com.knowledgehub.knowledge.api;

import com.knowledgehub.knowledge.domain.KnowledgeSpace;

import java.time.Instant;
import java.util.UUID;

/**
 * 返回给客户端的知识空间数据。
 *
 * <p>响应 DTO 与领域对象分离，避免领域模型直接成为 HTTP 契约。</p>
 *
 * @param id 知识空间唯一标识
 * @param name 知识空间名称
 * @param description 知识空间描述
 * @param createdAt 创建时间
 */
public record KnowledgeSpaceResponse(
        UUID id,
        String name,
        String description,
        Instant createdAt
) {

    /**
     * 将领域对象转换为 HTTP 响应。
     *
     * @param knowledgeSpace 知识空间领域对象
     * @return HTTP 响应对象
     */
    static KnowledgeSpaceResponse from(KnowledgeSpace knowledgeSpace) {
        return new KnowledgeSpaceResponse(
                knowledgeSpace.id(),
                knowledgeSpace.name(),
                knowledgeSpace.description(),
                knowledgeSpace.createdAt()
        );
    }
}
