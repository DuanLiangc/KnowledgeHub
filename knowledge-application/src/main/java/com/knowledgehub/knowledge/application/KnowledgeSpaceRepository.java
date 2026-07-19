package com.knowledgehub.knowledge.application;

import com.knowledgehub.knowledge.domain.KnowledgeSpace;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 应用层访问知识空间持久化能力的输出端口。
 *
 * <p>应用层只依赖该接口，不关心数据最终保存在 PostgreSQL 还是其他存储中。</p>
 */
public interface KnowledgeSpaceRepository {

    /**
     * 新增或更新知识空间。
     *
     * @param knowledgeSpace 待保存的知识空间
     * @return 保存后的知识空间
     */
    KnowledgeSpace save(KnowledgeSpace knowledgeSpace);

    /**
     * 查询全部知识空间。
     *
     * @return 知识空间列表
     */
    List<KnowledgeSpace> findAll();

    /**
     * 按 ID 查询知识空间。
     *
     * @param id 知识空间 ID
     * @return 找到时返回知识空间，否则返回空值容器
     */
    Optional<KnowledgeSpace> findById(UUID id);

    /**
     * 按 ID 删除知识空间。
     *
     * @param id 知识空间 ID
     */
    void deleteById(UUID id);
}
