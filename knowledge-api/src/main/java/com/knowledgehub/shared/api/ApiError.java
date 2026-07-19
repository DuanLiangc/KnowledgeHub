package com.knowledgehub.shared.api;

import java.time.Instant;
import java.util.Map;

/**
 * API 统一错误响应。
 *
 * @param code 供程序识别的错误码
 * @param message 供用户阅读的错误说明
 * @param details 字段级错误详情
 * @param timestamp 错误发生时间
 */
public record ApiError(
        String code,
        String message,
        Map<String, String> details,
        Instant timestamp
) {
}
