package com.knowledgehub.knowledge.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA 提供的底层数据库访问接口。
 *
 * <p>该接口仅在基础设施模块内部使用，不向应用层暴露 JPA 类型。</p>
 */
interface SpringDataKnowledgeSpaceRepository extends JpaRepository<KnowledgeSpaceEntity, UUID> {
}
