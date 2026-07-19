package com.knowledgehub.knowledge.application;

/**
 * 创建知识空间用例的输入命令。
 *
 * <p>Command 与 HTTP 请求 DTO 分离，使应用层可以被 HTTP 以外的入口复用。</p>
 *
 * @param name 知识空间名称
 * @param description 知识空间描述
 */
public record CreateKnowledgeSpaceCommand(String name, String description) {
}
