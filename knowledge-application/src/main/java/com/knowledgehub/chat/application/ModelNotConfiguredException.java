package com.knowledgehub.chat.application;

/** API Key 等必要模型配置缺失时抛出的异常。 */
public class ModelNotConfiguredException extends RuntimeException {

    /** 创建模型未配置异常。 */
    public ModelNotConfiguredException() {
        super("AI 模型尚未配置，请设置 OPENAI_API_KEY");
    }
}
