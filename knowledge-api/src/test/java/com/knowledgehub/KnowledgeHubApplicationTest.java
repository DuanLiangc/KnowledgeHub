package com.knowledgehub;

import com.knowledgehub.knowledge.application.CreateKnowledgeSpaceCommand;
import com.knowledgehub.knowledge.application.KnowledgeSpaceService;
import com.knowledgehub.knowledge.application.KnowledgeSpaceNotFoundException;
import com.knowledgehub.knowledge.application.UpdateKnowledgeSpaceCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Testcontainers
class KnowledgeHubApplicationTest {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName
            .parse("pgvector/pgvector:pg17-trixie")
            .asCompatibleSubstituteFor("postgres");

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(POSTGRES_IMAGE);

    @Autowired
    private KnowledgeSpaceService service;

    @Test
    void shouldCompleteKnowledgeSpaceCrudInPostgres() {
        var created = service.create(new CreateKnowledgeSpaceCommand("集成测试空间", "验证 PostgreSQL 持久化"));

        var found = service.findById(created.id());
        var updated = service.update(
                created.id(),
                new UpdateKnowledgeSpaceCommand("已修改的空间", "验证修改操作")
        );
        service.delete(created.id());

        assertThat(found.name()).isEqualTo("集成测试空间");
        assertThat(updated.name()).isEqualTo("已修改的空间");
        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.createdAt()).isEqualTo(created.createdAt());
        assertThatThrownBy(() -> service.findById(created.id()))
                .isInstanceOf(KnowledgeSpaceNotFoundException.class);
    }
}
