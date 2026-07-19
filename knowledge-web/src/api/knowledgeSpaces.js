import { requestJson } from './http';
const baseUrl = '/api/knowledge-spaces';
export function listKnowledgeSpaces() {
    return requestJson(baseUrl);
}
export function createKnowledgeSpace(input) {
    return requestJson(baseUrl, jsonRequest('POST', input));
}
export function updateKnowledgeSpace(id, input) {
    return requestJson(`${baseUrl}/${id}`, jsonRequest('PUT', input));
}
export function deleteKnowledgeSpace(id) {
    return requestJson(`${baseUrl}/${id}`, { method: 'DELETE' });
}
function jsonRequest(method, input) {
    return {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(input),
    };
}
