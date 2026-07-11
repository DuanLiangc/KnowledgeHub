package com.knowledgehub.knowledge.infrastructure;

import com.knowledgehub.knowledge.application.KnowledgeSpaceRepository;
import com.knowledgehub.knowledge.domain.KnowledgeSpace;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Temporary repository for the first runnable version. Data is lost on restart. */
public final class InMemoryKnowledgeSpaceRepository implements KnowledgeSpaceRepository {

    private final Map<UUID, KnowledgeSpace> storage = new ConcurrentHashMap<>();

    @Override
    public KnowledgeSpace save(KnowledgeSpace knowledgeSpace) {
        storage.put(knowledgeSpace.id(), knowledgeSpace);
        return knowledgeSpace;
    }

    @Override
    public List<KnowledgeSpace> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(KnowledgeSpace::createdAt))
                .toList();
    }
}

