export interface ApiError {
  code: string
  message: string
  details?: Record<string, string>
}

export interface KnowledgeSpace {
  id: string
  name: string
  description: string
  createdAt: string
}

export interface KnowledgeSpaceInput {
  name: string
  description: string
}

export interface TokenUsage {
  inputTokens: number
  outputTokens: number
  totalTokens: number
}

export interface ChatMetrics {
  model: string
  usage: TokenUsage
  elapsedMillis: number
}

export interface ChatStreamError {
  code: string
  message: string
}
