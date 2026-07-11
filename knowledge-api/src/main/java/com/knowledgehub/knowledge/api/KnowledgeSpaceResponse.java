package com.knowledgehub.knowledge.api;

import com.knowledgehub.knowledge.domain.KnowledgeSpace;

import java.time.Instant;
import java.util.UUID;

public record KnowledgeSpaceResponse(
        UUID id,
        String name,
        String description,
        Instant createdAt
) {

    static KnowledgeSpaceResponse from(KnowledgeSpace knowledgeSpace) {
        return new KnowledgeSpaceResponse(
                knowledgeSpace.id(),
                knowledgeSpace.name(),
                knowledgeSpace.description(),
                knowledgeSpace.createdAt()
        );
    }
}

