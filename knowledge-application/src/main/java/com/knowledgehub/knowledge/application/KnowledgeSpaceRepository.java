package com.knowledgehub.knowledge.application;

import com.knowledgehub.knowledge.domain.KnowledgeSpace;

import java.util.List;

/**
 * Output port used by the application layer to persist knowledge spaces.
 * Infrastructure decides whether the implementation uses memory or PostgreSQL.
 */
public interface KnowledgeSpaceRepository {

    KnowledgeSpace save(KnowledgeSpace knowledgeSpace);

    List<KnowledgeSpace> findAll();
}

