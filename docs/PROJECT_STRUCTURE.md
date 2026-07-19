# Knowledge Hub 项目结构说明

## 架构选择

后端是 Maven 多模块的“模块化单体”，前端是独立的 Vue 3 工程。后端部署时只有一个 Spring Boot 应用，代码中用模块明确职责；前端开发时通过 Vite 代理调用后端。

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
├── knowledge-web/                # Vue 3 知识空间管理与聊天界面
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
  -> PostgresKnowledgeSpaceRepository（基础设施实现）
```

项目已用 PostgreSQL Adapter 替换最初的内存实现，Controller、Service 和领域对象都不需要知道存储方式已经变化。这就是端口与适配器思想的直接价值。

模型问答也遵循相同的分层方式：

```text
POST /api/chat 或 /api/chat/stream
  -> ChatController（接收问题，选择 JSON 或 SSE 返回方式）
  -> ChatService（添加系统指令，编排问答用例）
  -> ChatModelPort（应用层定义的模型调用端口）
  -> OpenAiResponsesClient（基础设施层的 Responses API 适配器）
```

`ChatService` 不依赖 OpenAI。阶段 3 改用 Spring AI，或以后增加其他模型供应商时，只需替换或新增基础设施适配器。流式接口使用 Java 21 虚拟线程读取上游 SSE，避免长时间占用昂贵的平台线程。

## 新功能放在哪里

| 要添加的内容 | 所属模块 |
| --- | --- |
| 文档、Chunk、知识空间等业务对象 | `knowledge-domain` |
| 上传文档、执行检索、生成答案等用例 | `knowledge-application` |
| PostgreSQL、pgvector、文件解析、模型客户端 | `knowledge-infrastructure` |
| Controller、请求/响应 DTO、Spring 配置 | `knowledge-api` |

包按业务功能组织。例如知识空间相关代码放在 `knowledge` 下；将来会增加 `document`、`retrieval`、`chat` 和 `evaluation`，而不是建立全局 `controller`、`service`、`repository` 大目录。

## 演进路线

1. 完善知识空间 CRUD 与 PostgreSQL 集成测试。
2. 接入大模型，学习普通响应与 SSE 流式响应。
3. 增加文档上传与对象存储，建立异步处理状态。
4. 实现文档解析、Chunk 切分、Embedding 和 pgvector 检索。
5. 完成带引用的流式 RAG 问答。
6. 增加评估集、混合检索、Rerank 和权限过滤。
