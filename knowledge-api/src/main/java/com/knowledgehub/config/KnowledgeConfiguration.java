package com.knowledgehub.config;

import com.knowledgehub.knowledge.application.KnowledgeSpaceRepository;
import com.knowledgehub.knowledge.application.KnowledgeSpaceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 知识库业务对象的 Spring 装配配置。
 *
 * <p>应用层本身不依赖 Spring，因此在 API 层集中创建业务服务，
 * 并注入由基础设施层实现的 Repository。</p>
 */
@Configuration
public class KnowledgeConfiguration {

    /**
     * 创建知识空间业务服务。
     *
     * @param repository 知识空间持久化端口
     * @return 知识空间业务服务
     */
    @Bean
    KnowledgeSpaceService knowledgeSpaceService(KnowledgeSpaceRepository repository) {
        return new KnowledgeSpaceService(repository);
    }
}
