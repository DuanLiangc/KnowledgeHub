package com.knowledgehub.chat.application;

import java.util.function.Consumer;

/**
 * 编排单轮模型问答用例。
 */
public final class ChatService {

    private static final String SYSTEM_INSTRUCTION = "你是企业知识库助手。请准确、清晰地回答用户问题。";

    private final ChatModelPort chatModel;

    /**
     * 创建问答应用服务。
     *
     * @param chatModel 大模型调用端口
     */
    public ChatService(ChatModelPort chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 执行普通问答。
     *
     * @param question 用户问题
     * @return 完整问答结果
     */
    public ChatResult chat(String question) {
        return chatModel.chat(new ChatCommand(SYSTEM_INSTRUCTION, question));
    }

    /**
     * 执行流式问答。
     *
     * @param question 用户问题
     * @param eventConsumer 流式事件消费者
     */
    public void stream(String question, Consumer<ChatStreamEvent> eventConsumer) {
        chatModel.stream(new ChatCommand(SYSTEM_INSTRUCTION, question), eventConsumer);
    }
}
