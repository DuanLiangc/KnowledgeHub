package com.knowledgehub.config;

import com.knowledgehub.knowledge.application.KnowledgeSpaceRepository;
import com.knowledgehub.knowledge.application.KnowledgeSpaceService;
import com.knowledgehub.knowledge.infrastructure.InMemoryKnowledgeSpaceRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Wires application ports to their current infrastructure adapters. */
@Configuration
public class KnowledgeConfiguration {

    @Bean
    KnowledgeSpaceRepository knowledgeSpaceRepository() {
        return new InMemoryKnowledgeSpaceRepository();
    }

    @Bean
    KnowledgeSpaceService knowledgeSpaceService(KnowledgeSpaceRepository repository) {
        return new KnowledgeSpaceService(repository);
    }
}

