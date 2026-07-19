# 阶段 2：LLM 与模型调用基础

## 1. 本阶段做了什么

阶段 2 的目标不是马上构建 RAG，而是先完成一个最小、可理解的模型调用闭环：用户通过 HTTP 提交问题，应用调用模型，再以普通 JSON 或 SSE 流的形式返回答案。

目前提供两个接口：

| 接口 | 返回方式 | 适用场景 |
| --- | --- | --- |
| `POST /api/chat` | 等模型生成完毕后一次性返回 JSON | 后台任务、短答案、接口调试 |
| `POST /api/chat/stream` | 模型每生成一小段文字就发送一个 SSE 事件 | 聊天页面、长答案、降低用户等待感 |

阶段 2 使用 JDK `HttpClient` 直接调用 OpenAI Responses API，暂不使用 Spring AI。这样可以先看懂请求体、认证和 SSE 事件。阶段 3 替换为 Spring AI 时，应用层的 `ChatModelPort` 不需要改变。

## 2. 一次普通问答如何流动

```text
POST /api/chat
  -> ChatController：校验 HTTP 参数并转换响应
  -> ChatService：添加系统指令，编排问答用例
  -> ChatModelPort：应用层定义的模型调用接口
  -> OpenAiResponsesClient：构造 HTTP 请求并调用 Responses API
  -> ChatResult：答案、模型名称、Token 用量和耗时
  -> ChatResponse：返回给客户端的 JSON
```

这里最重要的是依赖方向：`ChatService` 只认识 `ChatModelPort`，不知道 OpenAI 的 URL、API Key 或 JSON 格式。因此将来更换模型供应商时，核心用例不必跟着修改。

## 3. 模型请求包含什么

当前发送给 `/v1/responses` 的核心字段如下：

```json
{
  "model": "gpt-5.6-luna",
  "instructions": "你是企业知识库助手。请准确、清晰地回答用户问题。",
  "input": "什么是 RAG？",
  "max_output_tokens": 1024,
  "store": false,
  "stream": true
}
```

- `model`：要调用的模型，由 `OPENAI_MODEL` 配置。
- `instructions`：系统指令，约束模型角色和行为。
- `input`：本阶段只有一个 User 问题，尚未保存多轮 Assistant 历史。
- `max_output_tokens`：限制最大输出长度，避免失控的耗时和费用。
- `store: false`：要求服务端不要为后续检索保存本次响应。
- `stream`：当前上游统一使用 `true`。流式接口立即转发文本片段；普通接口在服务端收集全部片段后一次性返回 JSON。这也兼容只正确实现 SSE 的模型网关。

`temperature` 和 `top_p` 本阶段没有主动设置，使用模型默认行为。不要为了“参数齐全”同时随意调整两者；后续应结合固定评估集验证效果。

## 4. Token、耗时与费用

Token 是模型处理文本的基本计量单位，不等同于 Java 字符数。输入问题、系统指令和未来的 RAG 上下文都会消耗输入 Token；模型答案消耗输出 Token。

接口返回以下用量：

```json
{
  "inputTokens": 20,
  "outputTokens": 30,
  "totalTokens": 50
}
```

实际费用由“输入 Token 数 × 输入单价 + 输出 Token 数 × 输出单价”决定，不应只看总 Token。当前代码记录模型名、Token 用量和耗时，但不会记录 API Key、用户问题或模型答案，避免敏感信息进入日志。

## 5. SSE 流式响应

SSE 是服务器通过一个持续打开的 HTTP 连接向浏览器发送事件。客户端必须使用 `curl -N` 或支持 SSE 的前端代码，`-N` 用来关闭 curl 的输出缓冲。

本项目向客户端发送三类事件：

```text
event:delta
data:你

event:completed
data:{"model":"...","usage":{...},"elapsedMillis":123}
```

- `delta`：本次新增的文本片段，客户端按顺序拼接。
- `completed`：生成结束，携带模型、Token 和总耗时。
- `error`：流式处理失败，携带稳定错误码和可读说明。

读取上游流是阻塞操作。项目使用 Java 21 虚拟线程执行它：每个流式请求可以拥有一个易理解的阻塞任务，又不会像大量平台线程那样昂贵。虚拟线程提高并发承载能力，但不会让模型本身响应得更快。

## 6. 配置与错误处理

API Key 只通过环境变量提供，不写入代码或 Git：

```bash
export OPENAI_API_KEY='你的_API_Key'
export OPENAI_MODEL='gpt-5.6-luna'
```

可选配置包括 `OPENAI_BASE_URL`、`OPENAI_MODEL`、连接超时、请求超时和最大输出 Token。未配置 Key 时应用仍能启动，知识空间接口不受影响：

- 普通问答返回 HTTP `503` 和错误码 `MODEL_NOT_CONFIGURED`。
- 流式问答建立 SSE 后发送 `error` 事件。
- 模型服务拒绝请求或不可用时，普通接口返回 HTTP `502`。

## 7. 测试策略

自动化测试绝不调用真实模型，因此不会消耗 Token：

- `ChatServiceTest` 使用 Fake Model 验证应用层编排。
- `OpenAiResponsesClientTest` 启动本地模拟 HTTP 服务，验证 SSE 转发、聚合和错误解析。
- `ChatControllerTest` 验证参数校验、状态码、中文编码和 SSE 事件。

运行全部检查：

```bash
mvn clean verify
```

这能证明代码逻辑和协议解析正确，但不能代替真实环境验证。本阶段已使用真实配置分别完成普通请求和流式请求验收，并验证 Token 与耗时能够返回。

## 8. 推荐代码阅读顺序

1. `ChatController`：观察普通 HTTP 与 SSE 接口的区别。
2. `ChatService`：观察系统指令如何加入用例。
3. `ChatModelPort`：理解应用层如何隔离模型供应商。
4. `OpenAiResponsesClient`：观察请求构造、响应解析和错误处理。
5. `ChatConfiguration`：观察 Spring 如何选择实现并装配对象。
6. 三组测试：理解如何在不花费 Token 的情况下验证模型客户端。
