package com.knowledgehub.shared.api;

import com.knowledgehub.chat.application.ModelCallException;
import com.knowledgehub.chat.application.ModelNotConfiguredException;
import com.knowledgehub.knowledge.application.KnowledgeSpaceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 将应用异常转换为格式统一的 HTTP 错误响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 将模型未配置异常转换为 503 响应。
     *
     * @param exception 模型未配置异常
     * @return API 错误信息
     */
    @ExceptionHandler(ModelNotConfiguredException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    ApiError handleModelNotConfigured(ModelNotConfiguredException exception) {
        return new ApiError("MODEL_NOT_CONFIGURED", exception.getMessage(), Map.of(), Instant.now());
    }

    /**
     * 将外部模型调用失败转换为 502 响应。
     *
     * @param exception 模型调用异常
     * @return API 错误信息
     */
    @ExceptionHandler(ModelCallException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    ApiError handleModelCall(ModelCallException exception) {
        return new ApiError("MODEL_CALL_FAILED", exception.getMessage(), Map.of(), Instant.now());
    }

    /**
     * 将知识空间不存在异常转换为 404 响应。
     *
     * @param exception 知识空间不存在异常
     * @return API 错误信息
     */
    @ExceptionHandler(KnowledgeSpaceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiError handleKnowledgeSpaceNotFound(KnowledgeSpaceNotFoundException exception) {
        return new ApiError(
                "KNOWLEDGE_SPACE_NOT_FOUND",
                exception.getMessage(),
                Map.of(),
                Instant.now()
        );
    }

    /**
     * 汇总请求 DTO 的字段校验错误。
     *
     * @param exception 参数校验异常
     * @return 包含字段错误详情的 API 错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> details = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> details.putIfAbsent(error.getField(), error.getDefaultMessage()));

        return new ApiError("VALIDATION_FAILED", "请求参数不合法", details, Instant.now());
    }

    /**
     * 将领域参数错误转换为 400 响应。
     *
     * @param exception 非法参数异常
     * @return API 错误信息
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ApiError handleIllegalArgument(IllegalArgumentException exception) {
        return new ApiError("INVALID_ARGUMENT", exception.getMessage(), Map.of(), Instant.now());
    }
}
