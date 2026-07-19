package com.knowledgehub.knowledge.application;

import java.util.UUID;

/**
 * 查询、修改或删除不存在的知识空间时抛出的应用异常。
 */
public class KnowledgeSpaceNotFoundException extends RuntimeException {

    /**
     * 创建知识空间不存在异常。
     *
     * @param id 未找到的知识空间 ID
     */
    public KnowledgeSpaceNotFoundException(UUID id) {
        super("知识空间不存在：" + id);
    }
}
