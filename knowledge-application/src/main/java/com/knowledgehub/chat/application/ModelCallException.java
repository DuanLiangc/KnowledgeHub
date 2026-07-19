package com.knowledgehub.chat.application;

/** 调用外部模型失败时抛出的统一异常。 */
public class ModelCallException extends RuntimeException {

    /**
     * 创建模型调用异常。
     *
     * @param message 可安全返回给客户端的错误说明
     */
    public ModelCallException(String message) {
        super(message);
    }

    /**
     * 创建带原始原因的模型调用异常。
     *
     * @param message 可安全返回给客户端的错误说明
     * @param cause 原始异常
     */
    public ModelCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
