package com.knowledgehub.chat.application;

import java.util.function.Consumer;

/**
 * 应用层调用大模型的输出端口。
 *
 * <p>应用层只依赖该接口，不感知 OpenAI、Claude 等供应商的 HTTP 协议。</p>
 */
public interface ChatModelPort {

    /**
     * 发起普通模型调用，等待完整答案后返回。
     *
     * @param command 问答命令
     * @return 完整问答结果
     */
    ChatResult chat(ChatCommand command);

    /**
     * 发起流式模型调用，每收到一个事件就通知调用方。
     *
     * <p>该方法在当前线程中持续读取上游响应，直到完成或失败。</p>
     *
     * @param command 问答命令
     * @param eventConsumer 流式事件消费者
     */
    void stream(ChatCommand command, Consumer<ChatStreamEvent> eventConsumer);
}
