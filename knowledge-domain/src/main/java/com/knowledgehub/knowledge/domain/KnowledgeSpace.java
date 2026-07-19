package com.knowledgehub.knowledge.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * 知识空间领域对象，用于划分和组织一组相关文档。
 *
 * <p>该对象刻意不使用 Spring、JPA 等框架注解，使业务规则不依赖具体技术实现。</p>
 *
 * @param id 知识空间唯一标识
 * @param name 知识空间名称
 * @param description 知识空间描述
 * @param createdAt 创建时间
 */
public record KnowledgeSpace(
        UUID id,
        String name,
        String description,
        Instant createdAt
) {

    /**
     * 校验并规范化领域对象字段。
     */
    public KnowledgeSpace {
        Objects.requireNonNull(id, "Knowledge space id must not be null");
        Objects.requireNonNull(createdAt, "Creation time must not be null");
        name = requireText(name, "Knowledge space name must not be blank");
        description = description == null ? "" : description.trim();
    }

    /**
     * 创建一个新的知识空间。
     *
     * @param name 知识空间名称
     * @param description 知识空间描述
     * @return 具有新 ID 和当前创建时间的知识空间
     */
    public static KnowledgeSpace create(String name, String description) {
        return new KnowledgeSpace(UUID.randomUUID(), name, description, Instant.now());
    }

    /**
     * 修改名称和描述，同时保持 ID 与创建时间不变。
     *
     * @param name 修改后的名称
     * @param description 修改后的描述
     * @return 修改后的新领域对象
     */
    public KnowledgeSpace update(String name, String description) {
        return new KnowledgeSpace(id, name, description, createdAt);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
