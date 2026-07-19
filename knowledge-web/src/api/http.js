export class ApiRequestError extends Error {
    code;
    details;
    constructor(message, code = 'REQUEST_FAILED', details = {}) {
        super(message);
        this.code = code;
        this.details = details;
        this.name = 'ApiRequestError';
    }
}
export async function requestJson(url, options) {
    const response = await fetch(url, options);
    if (!response.ok) {
        const error = await readApiError(response);
        throw new ApiRequestError(error.message, error.code, error.details);
    }
    if (response.status === 204) {
        return undefined;
    }
    return response.json();
}
async function readApiError(response) {
    try {
        return await response.json();
    }
    catch {
        return {
            code: `HTTP_${response.status}`,
            message: `请求失败，HTTP 状态码：${response.status}`,
        };
    }
}
