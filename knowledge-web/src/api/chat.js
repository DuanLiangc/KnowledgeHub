export async function streamChat(question, handlers, signal) {
    const response = await fetch('/api/chat/stream', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ question }),
        signal,
    });
    if (!response.ok || !response.body) {
        throw new Error(`无法建立流式连接，HTTP 状态码：${response.status}`);
    }
    const reader = response.body.getReader();
    const decoder = new TextDecoder('utf-8');
    let buffer = '';
    while (true) {
        const { value, done } = await reader.read();
        buffer += decoder.decode(value, { stream: !done }).replaceAll('\r\n', '\n');
        const eventBlocks = buffer.split('\n\n');
        buffer = eventBlocks.pop() ?? '';
        eventBlocks.forEach((block) => dispatchEvent(block, handlers));
        if (done) {
            if (buffer.trim()) {
                dispatchEvent(buffer, handlers);
            }
            break;
        }
    }
}
function dispatchEvent(block, handlers) {
    let eventName = 'message';
    const dataLines = [];
    for (const line of block.split('\n')) {
        if (line.startsWith('event:')) {
            eventName = line.slice('event:'.length).trim();
        }
        else if (line.startsWith('data:')) {
            dataLines.push(line.slice('data:'.length).trimStart());
        }
    }
    const data = dataLines.join('\n');
    if (!data) {
        return;
    }
    if (eventName === 'delta') {
        handlers.onDelta(parseSseText(data));
    }
    else if (eventName === 'completed') {
        handlers.onCompleted(JSON.parse(data));
    }
    else if (eventName === 'error') {
        handlers.onError(JSON.parse(data));
    }
}
function parseSseText(data) {
    try {
        const parsed = JSON.parse(data);
        return typeof parsed === 'string' ? parsed : data;
    }
    catch {
        return data;
    }
}
