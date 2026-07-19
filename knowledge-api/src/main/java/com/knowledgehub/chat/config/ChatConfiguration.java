package com.knowledgehub.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knowledgehub.chat.application.ChatModelPort;
import com.knowledgehub.chat.application.ChatService;
import com.knowledgehub.chat.infrastructure.openai.OpenAiProperties;
import com.knowledgehub.chat.infrastructure.openai.OpenAiResponsesClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 模型问答功能的 Spring 装配配置。
 *
 * <p>应用层保持为普通 Java 代码，API 层在这里选择具体模型适配器并创建服务。</p>
 */
@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class ChatConfiguration {

    /**
     * 创建 OpenAI Responses API 适配器。
     *
     * @param properties OpenAI 配置
     * @param objectMapper JSON 序列化工具
     * @return 大模型调用端口实现
     */
    @Bean
    ChatModelPort chatModelPort(OpenAiProperties properties, ObjectMapper objectMapper) {
        return new OpenAiResponsesClient(properties, objectMapper);
    }

    /**
     * 创建模型问答应用服务。
     *
     * @param chatModelPort 大模型调用端口
     * @return 模型问答服务
     */
    @Bean
    ChatService chatService(ChatModelPort chatModelPort) {
        return new ChatService(chatModelPort);
    }

    /**
     * 创建每任务一个虚拟线程的执行器，用于阻塞式模型流读取。
     *
     * @return 虚拟线程执行器
     */
    @Bean(destroyMethod = "close")
    ExecutorService chatExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
