package com.knowledgehub.knowledge.infrastructure.persistence;

import com.knowledgehub.knowledge.application.KnowledgeSpaceRepository;
import com.knowledgehub.knowledge.domain.KnowledgeSpace;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 基于 PostgreSQL 和 Spring Data JPA 的知识空间持久化适配器。
 *
 * <p>该适配器实现应用层端口，并负责领域对象与 JPA Entity 的转换。</p>
 */
@Repository
public class PostgresKnowledgeSpaceRepository implements KnowledgeSpaceRepository {

    private final SpringDataKnowledgeSpaceRepository repository;

    /**
     * 创建 PostgreSQL 持久化适配器。
     *
     * @param repository Spring Data JPA 底层接口
     */
    public PostgresKnowledgeSpaceRepository(SpringDataKnowledgeSpaceRepository repository) {
        this.repository = repository;
    }

    /** {@inheritDoc} */
    @Override
    public KnowledgeSpace save(KnowledgeSpace knowledgeSpace) {
        return repository.save(KnowledgeSpaceEntity.from(knowledgeSpace)).toDomain();
    }

    /** {@inheritDoc} */
    @Override
    public List<KnowledgeSpace> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "createdAt")).stream()
                .map(KnowledgeSpaceEntity::toDomain)
                .toList();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<KnowledgeSpace> findById(UUID id) {
        return repository.findById(id).map(KnowledgeSpaceEntity::toDomain);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
