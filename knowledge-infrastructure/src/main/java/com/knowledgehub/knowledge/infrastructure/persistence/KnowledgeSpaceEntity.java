package com.knowledgehub.knowledge.infrastructure.persistence;

import com.knowledgehub.knowledge.domain.KnowledgeSpace;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * {@code knowledge_space} 表的 JPA 映射对象。
 *
 * <p>Entity 只负责数据库映射，不承载领域规则；持久化边界处会与
 * {@link KnowledgeSpace} 相互转换。</p>
 */
@Entity
@Table(name = "knowledge_space")
class KnowledgeSpaceEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * 供 JPA 反射创建对象使用。
     */
    protected KnowledgeSpaceEntity() {
    }

    private KnowledgeSpaceEntity(UUID id, String name, String description, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    /**
     * 将领域对象转换为 JPA Entity。
     *
     * @param knowledgeSpace 知识空间领域对象
     * @return 可持久化的 Entity
     */
    static KnowledgeSpaceEntity from(KnowledgeSpace knowledgeSpace) {
        return new KnowledgeSpaceEntity(
                knowledgeSpace.id(),
                knowledgeSpace.name(),
                knowledgeSpace.description(),
                knowledgeSpace.createdAt()
        );
    }

    /**
     * 将数据库映射对象还原为领域对象。
     *
     * @return 知识空间领域对象
     */
    KnowledgeSpace toDomain() {
        return new KnowledgeSpace(id, name, description, createdAt);
    }
}
