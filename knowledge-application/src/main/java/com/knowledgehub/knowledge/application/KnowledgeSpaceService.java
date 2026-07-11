package com.knowledgehub.knowledge.application;

import com.knowledgehub.knowledge.domain.KnowledgeSpace;

import java.util.List;

/** Coordinates knowledge-space use cases. */
public final class KnowledgeSpaceService {

    private final KnowledgeSpaceRepository repository;

    public KnowledgeSpaceService(KnowledgeSpaceRepository repository) {
        this.repository = repository;
    }

    public KnowledgeSpace create(CreateKnowledgeSpaceCommand command) {
        KnowledgeSpace knowledgeSpace = KnowledgeSpace.create(command.name(), command.description());
        return repository.save(knowledgeSpace);
    }

    public List<KnowledgeSpace> findAll() {
        return repository.findAll();
    }
}

