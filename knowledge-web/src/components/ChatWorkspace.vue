<script setup lang="ts">
import { computed, nextTick, shallowRef } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCloseFilled, Document, Promotion, VideoPause } from '@element-plus/icons-vue'
import { streamChat } from '../api/chat'
import type { ChatMetrics } from '../types/api'

interface ChatMessage {
  id: number
  role: 'user' | 'assistant'
  content: string
  status?: 'streaming' | 'completed' | 'stopped' | 'error'
}

const props = defineProps<{
  spaceName: string | null
  serviceOnline: boolean
}>()

const emit = defineEmits<{
  metrics: [value: ChatMetrics]
}>()

const messages = shallowRef<ChatMessage[]>([])
const question = shallowRef('')
const generating = shallowRef(false)
const messageList = shallowRef<HTMLElement | null>(null)
let messageSequence = 0
let activeRequest: AbortController | null = null

const canSend = computed(() =>
  props.serviceOnline && !generating.value && question.value.trim().length > 0,
)

async function send(): Promise<void> {
  const content = question.value.trim()
  if (!content || !canSend.value) {
    return
  }

  const userMessage: ChatMessage = { id: ++messageSequence, role: 'user', content }
  const assistantMessage: ChatMessage = {
    id: ++messageSequence,
    role: 'assistant',
    content: '',
    status: 'streaming',
  }
  messages.value = [...messages.value, userMessage, assistantMessage]
  question.value = ''
  generating.value = true
  activeRequest = new AbortController()
  await scrollToBottom()

  try {
    await streamChat(
      content,
      {
        onDelta: (text) => {
          assistantMessage.content += text
          messages.value = [...messages.value]
          void scrollToBottom()
        },
        onCompleted: (metrics) => {
          assistantMessage.status = 'completed'
          emit('metrics', metrics)
        },
        onError: (error) => {
          assistantMessage.status = 'error'
          assistantMessage.content = error.message
          messages.value = [...messages.value]
        },
      },
      activeRequest.signal,
    )
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      assistantMessage.status = 'stopped'
      if (!assistantMessage.content) {
        assistantMessage.content = '本次生成已停止。'
      }
    } else {
      assistantMessage.status = 'error'
      assistantMessage.content = error instanceof Error ? error.message : '模型连接失败，请稍后重试。'
      ElMessage.error(assistantMessage.content)
    }
  } finally {
    messages.value = [...messages.value]
    generating.value = false
    activeRequest = null
    await scrollToBottom()
  }
}

function stop(): void {
  activeRequest?.abort()
}

function handleKeydown(event: KeyboardEvent): void {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    void send()
  }
}

async function scrollToBottom(): Promise<void> {
  await nextTick()
  if (messageList.value) {
    messageList.value.scrollTop = messageList.value.scrollHeight
  }
}
</script>

<template>
  <main class="chat-workspace">
    <section class="workspace-context">
      <div>
        <span class="section-kicker">AI 问答台</span>
        <h2>{{ spaceName ?? '请选择知识空间' }}</h2>
      </div>
      <div class="stage-badge">
        <span class="stage-dot" />
        阶段 2 · 基础模型对话
      </div>
    </section>

    <section ref="messageList" class="message-list" aria-live="polite">
      <div v-if="messages.length === 0" class="welcome-panel">
        <div class="welcome-symbol" aria-hidden="true">
          <span>问</span>
          <i />
        </div>
        <span class="section-kicker">从一个问题开始</span>
        <h3>让企业知识变得可询问</h3>
        <p>
          当前用于验证模型调用和流式输出。文档检索尚未接入，因此回答不会使用左侧知识空间中的内容。
        </p>
        <div class="capability-grid">
          <div>
            <el-icon><Promotion /></el-icon>
            <span>实时流式回答</span>
          </div>
          <div>
            <el-icon><Document /></el-icon>
            <span>RAG 引用待接入</span>
          </div>
        </div>
      </div>

      <article
        v-for="message in messages"
        :key="message.id"
        class="message-row"
        :class="`message-row--${message.role}`"
      >
        <div v-if="message.role === 'assistant'" class="assistant-avatar">K</div>
        <div class="message-bubble" :class="{ 'is-error': message.status === 'error' }">
          <span class="message-role">{{ message.role === 'user' ? '你' : 'Knowledge Hub' }}</span>
          <p>{{ message.content }}<span v-if="message.status === 'streaming'" class="typing-cursor" /></p>
          <small v-if="message.status === 'stopped'">已停止生成</small>
          <small v-else-if="message.status === 'error'"><el-icon><CircleCloseFilled /></el-icon> 请检查模型配置</small>
        </div>
      </article>
    </section>

    <section class="composer-shell">
      <div class="composer-label">
        <span>{{ serviceOnline ? '模型服务已连接' : '后端服务未连接' }}</span>
        <span>{{ question.length }}/4000</span>
      </div>
      <div class="composer">
        <textarea
          v-model="question"
          maxlength="4000"
          rows="2"
          :disabled="generating"
          placeholder="输入问题，Enter 发送，Shift + Enter 换行"
          aria-label="输入问题"
          @keydown="handleKeydown"
        />
        <el-button v-if="generating" class="send-button" circle @click="stop">
          <el-icon><VideoPause /></el-icon>
        </el-button>
        <el-button v-else class="send-button" type="primary" circle :disabled="!canSend" @click="send">
          <el-icon><Promotion /></el-icon>
        </el-button>
      </div>
      <p class="composer-note">模型可能生成不准确的信息；企业知识接入后仍需核对引用来源。</p>
    </section>
  </main>
</template>
