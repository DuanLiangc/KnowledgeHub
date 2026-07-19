package com.knowledgehub.knowledge.api;

import com.knowledgehub.knowledge.application.KnowledgeSpaceService;
import com.knowledgehub.knowledge.application.KnowledgeSpaceNotFoundException;
import com.knowledgehub.knowledge.domain.KnowledgeSpace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KnowledgeSpaceController.class)
class KnowledgeSpaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KnowledgeSpaceService service;

    @Test
    void shouldCreateKnowledgeSpace() throws Exception {
        when(service.create(any())).thenReturn(KnowledgeSpace.create("技术文档", "研发团队知识库"));

        mockMvc.perform(post("/api/knowledge-spaces")
                        .contentType("application/json")
                        .content("""
                                {"name":"技术文档","description":"研发团队知识库"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("技术文档"));
    }

    @Test
    void shouldRejectBlankName() throws Exception {
        mockMvc.perform(post("/api/knowledge-spaces")
                        .contentType("application/json")
                        .content("""
                                {"name":" ","description":"研发团队知识库"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.details.name").value("知识空间名称不能为空"));
    }

    @Test
    void shouldListKnowledgeSpaces() throws Exception {
        when(service.findAll()).thenReturn(List.of(KnowledgeSpace.create("公司制度", "人事与财务制度")));

        mockMvc.perform(get("/api/knowledge-spaces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("公司制度"));
    }

    @Test
    void shouldFindKnowledgeSpaceById() throws Exception {
        KnowledgeSpace knowledgeSpace = KnowledgeSpace.create("技术文档", "研发团队知识库");
        when(service.findById(knowledgeSpace.id())).thenReturn(knowledgeSpace);

        mockMvc.perform(get("/api/knowledge-spaces/{id}", knowledgeSpace.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(knowledgeSpace.id().toString()))
                .andExpect(jsonPath("$.name").value("技术文档"));
    }

    @Test
    void shouldReturnNotFoundWhenKnowledgeSpaceDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenThrow(new KnowledgeSpaceNotFoundException(id));

        mockMvc.perform(get("/api/knowledge-spaces/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("KNOWLEDGE_SPACE_NOT_FOUND"));
    }

    @Test
    void shouldUpdateKnowledgeSpace() throws Exception {
        KnowledgeSpace knowledgeSpace = KnowledgeSpace.create("新名称", "新描述");
        when(service.update(eq(knowledgeSpace.id()), any())).thenReturn(knowledgeSpace);

        mockMvc.perform(put("/api/knowledge-spaces/{id}", knowledgeSpace.id())
                        .contentType("application/json")
                        .content("""
                                {"name":"新名称","description":"新描述"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("新名称"))
                .andExpect(jsonPath("$.description").value("新描述"));
    }

    @Test
    void shouldRejectBlankNameWhenUpdating() throws Exception {
        mockMvc.perform(put("/api/knowledge-spaces/{id}", UUID.randomUUID())
                        .contentType("application/json")
                        .content("""
                                {"name":" ","description":"新描述"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    void shouldDeleteKnowledgeSpace() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(service).delete(id);

        mockMvc.perform(delete("/api/knowledge-spaces/{id}", id))
                .andExpect(status().isNoContent());
    }
}
