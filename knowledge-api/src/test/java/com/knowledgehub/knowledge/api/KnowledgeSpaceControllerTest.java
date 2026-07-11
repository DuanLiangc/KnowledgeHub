package com.knowledgehub.knowledge.api;

import com.knowledgehub.knowledge.application.KnowledgeSpaceService;
import com.knowledgehub.knowledge.domain.KnowledgeSpace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}

