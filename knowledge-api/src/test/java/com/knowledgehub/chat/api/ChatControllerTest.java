package com.knowledgehub.chat.api;

import com.knowledgehub.chat.application.ChatResult;
import com.knowledgehub.chat.application.ChatService;
import com.knowledgehub.chat.application.ChatStreamEvent;
import com.knowledgehub.chat.application.ModelNotConfiguredException;
import com.knowledgehub.chat.application.TokenUsage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ExecutorService;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private ExecutorService chatExecutor;

    @Test
    void shouldReturnNormalChatResult() throws Exception {
        when(chatService.chat("什么是 RAG？")).thenReturn(new ChatResult(
                "RAG 是检索增强生成。",
                "fake-model",
                new TokenUsage(10, 6, 16),
                25
        ));

        mockMvc.perform(post("/api/chat")
                        .contentType("application/json")
                        .content("""
                                {"question":"什么是 RAG？"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answer").value("RAG 是检索增强生成。"))
                .andExpect(jsonPath("$.model").value("fake-model"))
                .andExpect(jsonPath("$.usage.totalTokens").value(16));
    }

    @Test
    void shouldRejectBlankQuestion() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType("application/json")
                        .content("""
                                {"question":" "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.details.question").value("问题不能为空"));
    }

    @Test
    void shouldReturnServiceUnavailableWhenModelIsNotConfigured() throws Exception {
        when(chatService.chat("你好")).thenThrow(new ModelNotConfiguredException());

        mockMvc.perform(post("/api/chat")
                        .contentType("application/json")
                        .content("""
                                {"question":"你好"}
                                """))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("MODEL_NOT_CONFIGURED"));
    }

    @Test
    void shouldReturnStreamingEvents() throws Exception {
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(chatExecutor).submit(any(Runnable.class));
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            var consumer = (java.util.function.Consumer<ChatStreamEvent>) invocation.getArgument(1);
            consumer.accept(ChatStreamEvent.delta("你"));
            consumer.accept(ChatStreamEvent.delta("好"));
            consumer.accept(ChatStreamEvent.completed("fake-model", new TokenUsage(5, 2, 7), 20));
            return null;
        }).when(chatService).stream(any(), any());

        var result = mockMvc.perform(post("/api/chat/stream")
                        .contentType("application/json")
                        .content("""
                                {"question":"你好"}
                                """))
                .andExpect(status().isOk())
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/event-stream"))
                .andExpect(content().encoding(StandardCharsets.UTF_8))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("event:delta")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data:你")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("event:completed")));
    }
}
