package com.knowledgehub.knowledge.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A knowledge space is a business boundary that groups related documents.
 *
 * <p>This domain object deliberately has no Spring or database annotations.
 * Business rules therefore remain independent from frameworks.</p>
 */
public record KnowledgeSpace(
        UUID id,
        String name,
        String description,
        Instant createdAt
) {

    public KnowledgeSpace {
        Objects.requireNonNull(id, "Knowledge space id must not be null");
        Objects.requireNonNull(createdAt, "Creation time must not be null");
        name = requireText(name, "Knowledge space name must not be blank");
        description = description == null ? "" : description.trim();
    }

    public static KnowledgeSpace create(String name, String description) {
        return new KnowledgeSpace(UUID.randomUUID(), name, description, Instant.now());
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}

