import { requestJson } from './http'
import type { KnowledgeSpace, KnowledgeSpaceInput } from '../types/api'

const baseUrl = '/api/knowledge-spaces'

export function listKnowledgeSpaces(): Promise<KnowledgeSpace[]> {
  return requestJson<KnowledgeSpace[]>(baseUrl)
}

export function createKnowledgeSpace(input: KnowledgeSpaceInput): Promise<KnowledgeSpace> {
  return requestJson<KnowledgeSpace>(baseUrl, jsonRequest('POST', input))
}

export function updateKnowledgeSpace(id: string, input: KnowledgeSpaceInput): Promise<KnowledgeSpace> {
  return requestJson<KnowledgeSpace>(`${baseUrl}/${id}`, jsonRequest('PUT', input))
}

export function deleteKnowledgeSpace(id: string): Promise<void> {
  return requestJson<void>(`${baseUrl}/${id}`, { method: 'DELETE' })
}

function jsonRequest(method: string, input: KnowledgeSpaceInput): RequestInit {
  return {
    method,
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input),
  }
}
