# Repository Guidelines

## 项目结构与模块划分

Knowledge Hub 是一个基于 Java 21、Spring Boot 3.5 和 Maven 的多模块项目：

- `knowledge-domain`：不依赖框架的领域对象与业务规则。
- `knowledge-application`：业务用例，以及应用所需的外部能力接口。
- `knowledge-infrastructure`：数据库、文件系统和模型供应商等适配器。
- `knowledge-api`：Spring Boot 启动入口、REST DTO、参数校验和配置装配。
- `docs`：架构说明与项目主线文档。

生产代码放在 `<模块>/src/main/java`，测试代码在 `<模块>/src/test/java` 中使用相同包结构。包按业务功能组织，例如 `knowledge`、`document`、`retrieval`、`chat`。依赖必须指向内层：API 和基础设施层可以依赖应用层，应用层可以依赖领域层；领域层不得依赖 Spring 或持久化框架。

## 构建、测试与本地开发

- `mvn clean verify`：编译所有模块并运行完整测试。
- `mvn -pl knowledge-api -am spring-boot:run`：构建依赖模块并在 `8080` 端口启动 API。
- `curl http://localhost:8080/actuator/health`：检查服务健康状态。

所有命令都从仓库根目录执行。扩大功能范围前，应先完成 `docs/PROJECT_ROADMAP.md` 中的当前阶段。

## 编码风格与命名规范

Java 使用四空格缩进。类型使用 `PascalCase`，方法和字段使用 `camelCase`，包名使用 `com.knowledgehub` 下的全小写名称。不可变 DTO 优先使用 `record`；必要依赖使用构造器注入；适用时将字段声明为 `final`。领域代码不得出现框架注解。端口按能力命名，如 `KnowledgeSpaceRepository`；适配器按实现方式命名，如 `PostgresKnowledgeSpaceRepository`。项目暂未强制格式化工具，请使用 IntelliJ 默认 Java 格式。

## 测试规范

项目使用 JUnit 5、Mockito、Spring Boot Test 和 MockMvc。测试类以 `*Test` 结尾，方法按行为命名，例如 `shouldRejectBlankName`。领域层和应用层规则使用单元测试；HTTP 契约使用 `@WebMvcTest`；仅在验证完整装配时使用 `@SpringBootTest`。新功能应覆盖成功流程、参数校验失败和关键边界情况。提交 PR 前必须执行 `mvn clean verify`。

## 提交与 Pull Request 规范

当前提交历史较少，且同时存在中文和英文消息。后续使用简短、明确的命令式提交，可选用范围前缀，例如 `feat(document): 增加文档上传接口` 或 `fix(rag): 拒绝空检索结果`。每次提交只处理一个主题。PR 应说明问题、解决方案、测试结果、配置变化及所属路线阶段，并关联相关 Issue。API 变更需提供请求和响应示例；只有可见 UI 变化才需要截图。

## 安全与配置

禁止提交 API Key、生产密码、私有文档或用户数据。`compose.yml` 中固定的 PostgreSQL 凭据仅限本地开发；其他环境的敏感配置必须通过环境变量或密钥服务注入。日志中不得记录凭据、完整文档内容或敏感 Prompt。
