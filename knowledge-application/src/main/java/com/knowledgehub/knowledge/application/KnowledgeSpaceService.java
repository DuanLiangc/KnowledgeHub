package com.knowledgehub.knowledge.application;

import com.knowledgehub.knowledge.domain.KnowledgeSpace;

import java.util.List;
import java.util.UUID;

/**
 * 编排知识空间相关用例。
 *
 * <p>该服务负责调用领域对象执行规则，再通过 Repository 端口持久化结果。</p>
 */
public final class KnowledgeSpaceService {

    private final KnowledgeSpaceRepository repository;

    /**
     * 创建知识空间应用服务。
     *
     * @param repository 知识空间持久化端口
     */
    public KnowledgeSpaceService(KnowledgeSpaceRepository repository) {
        this.repository = repository;
    }

    /**
     * 执行创建知识空间用例。
     *
     * @param command 创建命令
     * @return 创建并保存后的知识空间
     */
    public KnowledgeSpace create(CreateKnowledgeSpaceCommand command) {
        KnowledgeSpace knowledgeSpace = KnowledgeSpace.create(command.name(), command.description());
        return repository.save(knowledgeSpace);
    }

    /**
     * 查询全部知识空间。
     *
     * @return 知识空间列表
     */
    public List<KnowledgeSpace> findAll() {
        return repository.findAll();
    }

    /**
     * 按 ID 查询知识空间。
     *
     * @param id 知识空间 ID
     * @return 匹配的知识空间
     * @throws KnowledgeSpaceNotFoundException ID 不存在时抛出
     */
    public KnowledgeSpace findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new KnowledgeSpaceNotFoundException(id));
    }

    /**
     * 执行修改知识空间用例。
     *
     * @param id 知识空间 ID
     * @param command 修改命令
     * @return 修改并保存后的知识空间
     * @throws KnowledgeSpaceNotFoundException ID 不存在时抛出
     */
    public KnowledgeSpace update(UUID id, UpdateKnowledgeSpaceCommand command) {
        KnowledgeSpace knowledgeSpace = findById(id);
        return repository.save(knowledgeSpace.update(command.name(), command.description()));
    }

    /**
     * 执行删除知识空间用例。
     *
     * @param id 知识空间 ID
     * @throws KnowledgeSpaceNotFoundException ID 不存在时抛出
     */
    public void delete(UUID id) {
        findById(id);
        repository.deleteById(id);
    }
}
