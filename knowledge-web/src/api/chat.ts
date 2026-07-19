import type { ChatMetrics, ChatStreamError } from '../types/api'

interface StreamHandlers {
  onDelta: (text: string) => void
  onCompleted: (metrics: ChatMetrics) => void
  onError: (error: ChatStreamError) => void
}

export async function streamChat(
  question: string,
  handlers: StreamHandlers,
  signal: AbortSignal,
): Promise<void> {
  const response = await fetch('/api/chat/stream', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ question }),
    signal,
  })

  if (!response.ok || !response.body) {
    throw new Error(`无法建立流式连接，HTTP 状态码：${response.status}`)
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''

  while (true) {
    const { value, done } = await reader.read()
    buffer += decoder.decode(value, { stream: !done }).replaceAll('\r\n', '\n')

    const eventBlocks = buffer.split('\n\n')
    buffer = eventBlocks.pop() ?? ''
    eventBlocks.forEach((block) => dispatchEvent(block, handlers))

    if (done) {
      if (buffer.trim()) {
        dispatchEvent(buffer, handlers)
      }
      break
    }
  }
}

function dispatchEvent(block: string, handlers: StreamHandlers): void {
  let eventName = 'message'
  const dataLines: string[] = []

  for (const line of block.split('\n')) {
    if (line.startsWith('event:')) {
      eventName = line.slice('event:'.length).trim()
    } else if (line.startsWith('data:')) {
      dataLines.push(line.slice('data:'.length).trimStart())
    }
  }

  const data = dataLines.join('\n')
  if (!data) {
    return
  }

  if (eventName === 'delta') {
    handlers.onDelta(parseSseText(data))
  } else if (eventName === 'completed') {
    handlers.onCompleted(JSON.parse(data) as ChatMetrics)
  } else if (eventName === 'error') {
    handlers.onError(JSON.parse(data) as ChatStreamError)
  }
}

function parseSseText(data: string): string {
  try {
    const parsed = JSON.parse(data) as unknown
    return typeof parsed === 'string' ? parsed : data
  } catch {
    return data
  }
}
