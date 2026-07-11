# Knowledge Hub

一个用于学习 AI 应用开发的企业知识库项目。项目从 Java 工程能力出发，逐步实现文档处理、Embedding、检索、RAG、权限控制、评估和生产化能力。

当前版本是第一阶段骨架：一个可运行的 Spring Boot 模块化单体，以及“创建/查询知识空间”的最小业务闭环。此时还没有接入数据库和大模型，这是刻意的安排：先看懂一次请求如何穿过各层，再逐步替换基础设施。

## 环境要求

- JDK 21
- Maven 3.8 或更高版本

## 启动项目

在项目根目录执行：

```bash
mvn clean verify
mvn -pl knowledge-api -am spring-boot:run
```

服务默认运行在 `http://localhost:8080`。健康检查：

```bash
curl http://localhost:8080/actuator/health
```

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

当前数据保存在内存中，应用重启后会清空。下一阶段会用 PostgreSQL 替换它。

## 模块说明

| 模块 | 作用 | 当前内容 |
| --- | --- | --- |
| `knowledge-domain` | 纯业务规则，不依赖框架 | `KnowledgeSpace` 领域对象 |
| `knowledge-application` | 组织业务用例，定义外部能力接口 | 创建、查询知识空间及 Repository 端口 |
| `knowledge-infrastructure` | 对接数据库、模型和文件系统 | 内存 Repository 适配器 |
| `knowledge-api` | 应用入口和 HTTP 接口 | REST API、参数校验、异常处理、健康检查 |

后续开发以 [项目主线](docs/PROJECT_ROADMAP.md) 为范围基准；更详细的依赖关系和代码放置规则见 [项目结构说明](docs/PROJECT_STRUCTURE.md)。

## 推荐阅读顺序

1. `KnowledgeSpaceController`：HTTP 请求从这里进入。
2. `KnowledgeSpaceService`：观察用例如何被编排。
3. `KnowledgeSpace`：观察业务对象如何保护自身规则。
4. `KnowledgeSpaceRepository`：理解应用层为何只定义接口。
5. `InMemoryKnowledgeSpaceRepository`：理解外部技术如何实现接口。
6. `KnowledgeConfiguration`：观察 Spring 如何把接口和实现装配起来。

## 当前边界

- 没有数据库，数据重启即丢失。
- 没有用户、租户和权限系统。
- 没有文档上传、解析和向量化。
- 没有接入 Embedding 或聊天模型。
- 没有前端页面。

这些内容会按可验证的小步骤逐个加入，避免同时引入太多概念。
