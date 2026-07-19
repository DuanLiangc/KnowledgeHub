package com.knowledgehub.knowledge.application;

/**
 * 修改知识空间用例的输入命令。
 *
 * @param name 修改后的名称
 * @param description 修改后的描述
 */
public record UpdateKnowledgeSpaceCommand(String name, String description) {
}
