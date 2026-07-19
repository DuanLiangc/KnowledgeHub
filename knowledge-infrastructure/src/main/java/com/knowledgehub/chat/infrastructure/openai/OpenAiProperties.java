package com.knowledgehub.chat.infrastructure.openai;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

/**
 * OpenAI Responses API 配置。
 *
 * <p>API Key 没有默认值，必须通过环境变量提供；其他字段提供适合本地学习的默认值。</p>
 *
 * @param apiKey OpenAI API Key
 * @param baseUrl API 根地址，可用于兼容 OpenAI 协议的网关
 * @param model 模型名称
 * @param connectTimeout 建立连接的最长等待时间
 * @param requestTimeout 单次请求的最长等待时间
 * @param maxOutputTokens 最大输出 Token 数
 */
@ConfigurationProperties(prefix = "knowledge-hub.ai.openai")
public record OpenAiProperties(
        String apiKey,
        URI baseUrl,
        String model,
        Duration connectTimeout,
        Duration requestTimeout,
        int maxOutputTokens
) {
}
