# Knowledge Hub

一个用于学习 AI 应用开发的企业知识库项目。项目从 Java 工程能力出发，逐步实现文档处理、Embedding、检索、RAG、权限控制、评估和生产化能力。

当前已完成 PostgreSQL 数据基础和模型调用阶段：支持知识空间管理、普通问答、SSE 流式问答，以及用于功能验收的 Vue 企业知识库界面。阶段 2 的详细原理见 [LLM 与模型调用基础](docs/STAGE_2_LLM_MODEL_CALLING.md)。

## 环境要求

- JDK 21
- Maven 3.8 或更高版本
- Docker Desktop
- Node.js 20 或更高版本

启动前可执行 `java -version` 和 `mvn -version`，两者都应显示 Java 21。如果终端仍使用旧 JDK，可在 macOS 当前会话执行：

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```

## 启动 PostgreSQL

项目使用 `pgvector/pgvector:pg17-trixie` 镜像，其中包含 PostgreSQL 17 和 pgvector 扩展，为后续向量检索做准备。

本地开发配置直接写在 `compose.yml` 中：数据库名和用户名为 `knowledge_hub`，密码为 `knowledge_hub_local`，端口为 `5432`。这些值只用于本机开发。启动数据库：

```bash
docker compose up -d
docker compose ps
```

当状态显示 `healthy` 后，测试数据库连接：

```bash
docker compose exec postgres psql -U knowledge_hub -d knowledge_hub
```

进入 `psql` 后执行 `SELECT version();`，输入 `\q` 退出。

## 启动项目

在项目根目录执行：

```bash
mvn clean verify
mvn -pl knowledge-api -am spring-boot:run
```

`application.yml` 提供了与 Compose 一致的本地默认值，因此不需要额外设置环境变量。部署到其他环境时仍可通过 `DB_URL`、`DB_USERNAME` 和 `DB_PASSWORD` 覆盖默认配置。

在 macOS 上，Maven 会自动启用 `macos-docker-desktop` Profile，让 Testcontainers 连接 Docker Desktop。第一次运行测试时还会下载 `testcontainers/ryuk` 辅助镜像，后续不再重复下载。

服务默认运行在 `http://localhost:8080`。健康检查：

```bash
curl http://localhost:8080/actuator/health
```

另开一个终端启动 Vue 前端：

```bash
cd knowledge-web
npm install
npm run dev
```

浏览器访问 `http://localhost:5173`。开发服务器会把 `/api` 和 `/actuator` 请求代理到后端 `8080` 端口，因此不需要额外配置 CORS。生产构建使用：

```bash
cd knowledge-web
npm run build
```

## 配置 AI 模型

API Key 属于敏感凭据，不写入公共 `application.yml`、`compose.yml` 或 Git。推荐复制本地配置示例：

```bash
cp application-local.example.yml application-local.yml
```

然后只在 `application-local.yml` 中填写本机 API Key。该文件已被 Git 忽略，应用启动时会自动读取，不需要启用额外 Profile。也可以改用环境变量，在启动应用前执行：

```bash
export OPENAI_API_KEY='你的_API_Key'
export OPENAI_MODEL='gpt-5.6-luna'
mvn -pl knowledge-api -am spring-boot:run
```

`OPENAI_MODEL` 可以省略，默认值为 `gpt-5.6-luna`。如果使用兼容 OpenAI Responses API 的网关，还可以设置 `OPENAI_BASE_URL`。应用会读取以下配置：

| 环境变量 | 默认值 | 作用 |
| --- | --- | --- |
| `OPENAI_API_KEY` | 无 | 调用模型的认证密钥 |
| `OPENAI_BASE_URL` | `https://api.openai.com/v1` | Responses API 根地址 |
| `OPENAI_MODEL` | `gpt-5.6-luna` | 模型名称 |
| `DB_URL` | 本地 PostgreSQL 地址 | 数据库 JDBC 地址 |
| `DB_USERNAME` | `knowledge_hub` | 数据库用户名 |
| `DB_PASSWORD` | `knowledge_hub_local` | 本地数据库密码 |

普通问答会在答案完整生成后返回 JSON：

```bash
curl -X POST http://localhost:8080/api/chat \
  -H 'Content-Type: application/json' \
  -d '{"question":"请用简单的话解释什么是 RAG"}'
```

SSE 流式问答会逐段返回文本，`-N` 表示不要缓冲输出：

```bash
curl -N -X POST http://localhost:8080/api/chat/stream \
  -H 'Content-Type: application/json' \
  -d '{"question":"请用简单的话解释什么是 RAG"}'
```

未设置 `OPENAI_API_KEY` 时，应用和知识空间接口仍可正常使用；普通问答会返回 `503 MODEL_NOT_CONFIGURED`，流式问答会发送 `error` 事件。

## 知识空间 API

创建一个知识空间：

```bash
curl -X POST http://localhost:8080/api/knowledge-spaces \
  -H 'Content-Type: application/json' \
  -d '{"name":"技术文档","description":"研发团队的技术知识库"}'
```

查询所有知识空间：

```bash
curl http://localhost:8080/api/knowledge-spaces
```

按 ID 查询：

```bash
curl http://localhost:8080/api/knowledge-spaces/{id}
```

修改知识空间：

```bash
curl -X PUT http://localhost:8080/api/knowledge-spaces/{id} \
  -H 'Content-Type: application/json' \
  -d '{"name":"新名称","description":"新描述"}'
```

删除知识空间：

```bash
curl -X DELETE http://localhost:8080/api/knowledge-spaces/{id}
```

知识空间保存在 PostgreSQL 中，应用重启后数据不会丢失。Flyway 会在第一次启动时自动执行 `V1__create_knowledge_space.sql` 建表。

## 模块说明

| 模块 | 作用 | 当前内容 |
| --- | --- | --- |
| `knowledge-domain` | 纯业务规则，不依赖框架 | `KnowledgeSpace` 领域对象 |
| `knowledge-application` | 组织业务用例，定义外部能力接口 | 知识空间用例、`ChatService`、Repository 与模型端口 |
| `knowledge-infrastructure` | 对接数据库、模型和文件系统 | PostgreSQL Adapter、Flyway、Responses API 客户端 |
| `knowledge-api` | 应用入口和 HTTP 接口 | 知识空间 API、普通/流式问答、配置装配、异常处理 |
| `knowledge-web` | Vue 3 企业知识库界面 | 知识空间 CRUD、SSE 聊天、服务状态和模型用量 |

后续开发以 [项目主线](docs/PROJECT_ROADMAP.md) 为范围基准；更详细的依赖关系和代码放置规则见 [项目结构说明](docs/PROJECT_STRUCTURE.md)。

## 推荐阅读顺序

1. `KnowledgeSpaceController`：HTTP 请求从这里进入。
2. `KnowledgeSpaceService`：观察用例如何被编排。
3. `KnowledgeSpace`：观察业务对象如何保护自身规则。
4. `KnowledgeSpaceRepository`：理解应用层为何只定义接口。
5. `KnowledgeSpaceEntity`：理解数据库表映射为何不放进领域对象。
6. `PostgresKnowledgeSpaceRepository`：理解基础设施如何实现应用层接口。
7. `KnowledgeConfiguration`：观察 Spring 如何装配业务服务。
8. `ChatController`：观察普通问答和 SSE 流式问答。
9. `OpenAiResponsesClient`：观察底层模型 HTTP 请求与响应解析。
10. `knowledge-web/src/App.vue`：观察前端如何组合状态栏、知识空间和聊天工作区。

## 当前边界

- 没有用户、租户和权限系统。
- 没有文档上传、解析和向量化。
- 已接入单轮聊天模型，但没有多轮会话、Prompt 模板和结构化输出。
- 没有接入 Embedding 或 RAG 检索。
- 已有基础操作界面，但还没有文档上传、引用来源和登录页面。

这些内容会按可验证的小步骤逐个加入，避免同时引入太多概念。
