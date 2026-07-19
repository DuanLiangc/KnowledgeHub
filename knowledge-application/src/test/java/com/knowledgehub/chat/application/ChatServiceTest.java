package com.knowledgehub.chat.application;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

class ChatServiceTest {

    @Test
    void shouldUseModelPortForNormalChat() {
        FakeChatModel model = new FakeChatModel();
        ChatService service = new ChatService(model);

        ChatResult result = service.chat("什么是 RAG？");

        assertThat(result.answer()).isEqualTo("这是 Fake Model 的答案");
        assertThat(model.lastCommand.systemInstruction()).isNotBlank();
        assertThat(model.lastCommand.question()).isEqualTo("什么是 RAG？");
    }

    @Test
    void shouldForwardStreamingEvents() {
        FakeChatModel model = new FakeChatModel();
        ChatService service = new ChatService(model);
        List<ChatStreamEvent> events = new ArrayList<>();

        service.stream("请流式回答", events::add);

        assertThat(events).extracting(ChatStreamEvent::type)
                .containsExactly(ChatStreamEvent.Type.DELTA, ChatStreamEvent.Type.COMPLETED);
        assertThat(events.getFirst().text()).isEqualTo("分段答案");
    }

    private static final class FakeChatModel implements ChatModelPort {

        private ChatCommand lastCommand;

        @Override
        public ChatResult chat(ChatCommand command) {
            lastCommand = command;
            return new ChatResult("这是 Fake Model 的答案", "fake-model", new TokenUsage(10, 5, 15), 1);
        }

        @Override
        public void stream(ChatCommand command, Consumer<ChatStreamEvent> eventConsumer) {
            lastCommand = command;
            eventConsumer.accept(ChatStreamEvent.delta("分段答案"));
            eventConsumer.accept(ChatStreamEvent.completed("fake-model", new TokenUsage(10, 5, 15), 1));
        }
    }
}
