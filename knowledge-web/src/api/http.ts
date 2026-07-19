import type { ApiError } from '../types/api'

export class ApiRequestError extends Error {
  constructor(
    message: string,
    readonly code = 'REQUEST_FAILED',
    readonly details: Record<string, string> = {},
  ) {
    super(message)
    this.name = 'ApiRequestError'
  }
}

export async function requestJson<T>(url: string, options?: RequestInit): Promise<T> {
  const response = await fetch(url, options)

  if (!response.ok) {
    const error = await readApiError(response)
    throw new ApiRequestError(error.message, error.code, error.details)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json() as Promise<T>
}

async function readApiError(response: Response): Promise<ApiError> {
  try {
    return await response.json() as ApiError
  } catch {
    return {
      code: `HTTP_${response.status}`,
      message: `请求失败，HTTP 状态码：${response.status}`,
    }
  }
}
