package com.knowledgehub.chat.api;

/**
 * SSE 流执行失败时返回的错误事件。
 *
 * @param code 错误码
 * @param message 错误说明
 */
public record ChatStreamErrorResponse(String code, String message) {
}
