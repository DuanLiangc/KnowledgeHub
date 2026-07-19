CREATE TABLE knowledge_space
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(100)             NOT NULL,
    description VARCHAR(500)             NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_knowledge_space_created_at ON knowledge_space (created_at);
