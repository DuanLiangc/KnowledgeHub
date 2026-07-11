# Knowledge Hub 项目结构说明

## 架构选择

当前项目是一个 Maven 多模块的“模块化单体”。部署时只有一个 Spring Boot 应用，代码中则用模块明确职责。它比微服务容易运行和调试，又能避免所有代码堆在一起。

依赖方向如下：

```text
knowledge-api ───────────────┐
     │                       ▼
     ├──────────────► knowledge-application ───► knowledge-domain
     │                       ▲
     └──► knowledge-infrastructure ┘
```

核心规则：内层代码不知道外层技术。`knowledge-domain` 不知道 Spring、数据库或大模型，`knowledge-application` 只通过接口表达自己需要的外部能力。

## 目录职责

```text
KnowledgeHub/
├── pom.xml                       # 父工程：模块与版本管理
├── knowledge-domain/             # 业务概念和业务规则
├── knowledge-application/        # 用例编排、输入命令、输出端口
├── knowledge-infrastructure/     # 数据库、对象存储、模型等外部适配器
├── knowledge-api/                # Spring Boot 启动、HTTP API、配置装配
└── docs/                         # 学习和架构文档
```

## 一次请求如何流动

创建知识空间时，请求按下面的顺序执行：

```text
HTTP POST
  -> KnowledgeSpaceController（接收和校验 HTTP 参数）
  -> KnowledgeSpaceService（编排创建用例）
  -> KnowledgeSpace（执行领域规则）
  -> KnowledgeSpaceRepository（应用层定义的接口）
  -> InMemoryKnowledgeSpaceRepository（基础设施实现）
```

将来接入 PostgreSQL 时，只需新增一个 Repository 实现并修改装配配置，Controller、Service 和领域对象都不需要知道数据库已经发生变化。这就是端口与适配器思想的直接价值。

## 新功能放在哪里

| 要添加的内容 | 所属模块 |
| --- | --- |
| 文档、Chunk、知识空间等业务对象 | `knowledge-domain` |
| 上传文档、执行检索、生成答案等用例 | `knowledge-application` |
| PostgreSQL、pgvector、文件解析、模型客户端 | `knowledge-infrastructure` |
| Controller、请求/响应 DTO、Spring 配置 | `knowledge-api` |

包按业务功能组织。例如知识空间相关代码放在 `knowledge` 下；将来会增加 `document`、`retrieval`、`chat` 和 `evaluation`，而不是建立全局 `controller`、`service`、`repository` 大目录。

## 演进路线

1. 用 PostgreSQL 替换内存存储，学习持久化和数据库迁移。
2. 增加文档上传与对象存储，建立异步处理状态。
3. 实现文档解析、Chunk 切分和 Embedding。
4. 接入 pgvector，完成第一次语义检索。
5. 接入大模型，完成带引用的流式 RAG 问答。
6. 增加混合检索、Rerank、权限过滤和评估集。

