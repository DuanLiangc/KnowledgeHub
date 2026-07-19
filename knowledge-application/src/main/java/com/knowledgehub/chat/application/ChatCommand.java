package com.knowledgehub.chat.application;

/**
 * 单轮模型问答用例的输入命令。
 *
 * @param systemInstruction 系统指令，用于约束模型行为
 * @param question 用户问题
 */
public record ChatCommand(String systemInstruction, String question) {
}
