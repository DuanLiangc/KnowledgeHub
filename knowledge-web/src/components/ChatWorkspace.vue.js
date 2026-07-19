import { computed, nextTick, shallowRef } from 'vue';
import { ElMessage } from 'element-plus';
import { CircleCloseFilled, Document, Promotion, VideoPause } from '@element-plus/icons-vue';
import { streamChat } from '../api/chat';
const props = defineProps();
const emit = defineEmits();
const messages = shallowRef([]);
const question = shallowRef('');
const generating = shallowRef(false);
const messageList = shallowRef(null);
let messageSequence = 0;
let activeRequest = null;
const canSend = computed(() => props.serviceOnline && !generating.value && question.value.trim().length > 0);
async function send() {
    const content = question.value.trim();
    if (!content || !canSend.value) {
        return;
    }
    const userMessage = { id: ++messageSequence, role: 'user', content };
    const assistantMessage = {
        id: ++messageSequence,
        role: 'assistant',
        content: '',
        status: 'streaming',
    };
    messages.value = [...messages.value, userMessage, assistantMessage];
    question.value = '';
    generating.value = true;
    activeRequest = new AbortController();
    await scrollToBottom();
    try {
        await streamChat(content, {
            onDelta: (text) => {
                assistantMessage.content += text;
                messages.value = [...messages.value];
                void scrollToBottom();
            },
            onCompleted: (metrics) => {
                assistantMessage.status = 'completed';
                emit('metrics', metrics);
            },
            onError: (error) => {
                assistantMessage.status = 'error';
                assistantMessage.content = error.message;
                messages.value = [...messages.value];
            },
        }, activeRequest.signal);
    }
    catch (error) {
        if (error instanceof DOMException && error.name === 'AbortError') {
            assistantMessage.status = 'stopped';
            if (!assistantMessage.content) {
                assistantMessage.content = '本次生成已停止。';
            }
        }
        else {
            assistantMessage.status = 'error';
            assistantMessage.content = error instanceof Error ? error.message : '模型连接失败，请稍后重试。';
            ElMessage.error(assistantMessage.content);
        }
    }
    finally {
        messages.value = [...messages.value];
        generating.value = false;
        activeRequest = null;
        await scrollToBottom();
    }
}
function stop() {
    activeRequest?.abort();
}
function handleKeydown(event) {
    if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault();
        void send();
    }
}
async function scrollToBottom() {
    await nextTick();
    if (messageList.value) {
        messageList.value.scrollTop = messageList.value.scrollHeight;
    }
}
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_elements;
let __VLS_components;
let __VLS_directives;
__VLS_asFunctionalElement(__VLS_elements.main, __VLS_elements.main)({
    ...{ class: "chat-workspace" },
});
__VLS_asFunctionalElement(__VLS_elements.section, __VLS_elements.section)({
    ...{ class: "workspace-context" },
});
__VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({});
__VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({
    ...{ class: "section-kicker" },
});
__VLS_asFunctionalElement(__VLS_elements.h2, __VLS_elements.h2)({});
(__VLS_ctx.spaceName ?? '请选择知识空间');
// @ts-ignore
[spaceName,];
__VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
    ...{ class: "stage-badge" },
});
__VLS_asFunctionalElement(__VLS_elements.span)({
    ...{ class: "stage-dot" },
});
__VLS_asFunctionalElement(__VLS_elements.section, __VLS_elements.section)({
    ref: "messageList",
    ...{ class: "message-list" },
    'aria-live': "polite",
});
/** @type {typeof __VLS_ctx.messageList} */ ;
// @ts-ignore
[messageList,];
if (__VLS_ctx.messages.length === 0) {
    // @ts-ignore
    [messages,];
    __VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
        ...{ class: "welcome-panel" },
    });
    __VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
        ...{ class: "welcome-symbol" },
        'aria-hidden': "true",
    });
    __VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({});
    __VLS_asFunctionalElement(__VLS_elements.i)({});
    __VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({
        ...{ class: "section-kicker" },
    });
    __VLS_asFunctionalElement(__VLS_elements.h3, __VLS_elements.h3)({});
    __VLS_asFunctionalElement(__VLS_elements.p, __VLS_elements.p)({});
    __VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
        ...{ class: "capability-grid" },
    });
    __VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({});
    const __VLS_0 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_1 = __VLS_asFunctionalComponent(__VLS_0, new __VLS_0({}));
    const __VLS_2 = __VLS_1({}, ...__VLS_functionalComponentArgsRest(__VLS_1));
    const { default: __VLS_4 } = __VLS_3.slots;
    const __VLS_5 = {}.Promotion;
    /** @type {[typeof __VLS_components.Promotion, ]} */ ;
    // @ts-ignore
    Promotion;
    // @ts-ignore
    const __VLS_6 = __VLS_asFunctionalComponent(__VLS_5, new __VLS_5({}));
    const __VLS_7 = __VLS_6({}, ...__VLS_functionalComponentArgsRest(__VLS_6));
    var __VLS_3;
    __VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({});
    __VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({});
    const __VLS_10 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_11 = __VLS_asFunctionalComponent(__VLS_10, new __VLS_10({}));
    const __VLS_12 = __VLS_11({}, ...__VLS_functionalComponentArgsRest(__VLS_11));
    const { default: __VLS_14 } = __VLS_13.slots;
    const __VLS_15 = {}.Document;
    /** @type {[typeof __VLS_components.Document, ]} */ ;
    // @ts-ignore
    Document;
    // @ts-ignore
    const __VLS_16 = __VLS_asFunctionalComponent(__VLS_15, new __VLS_15({}));
    const __VLS_17 = __VLS_16({}, ...__VLS_functionalComponentArgsRest(__VLS_16));
    var __VLS_13;
    __VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({});
}
for (const [message] of __VLS_getVForSourceType((__VLS_ctx.messages))) {
    // @ts-ignore
    [messages,];
    __VLS_asFunctionalElement(__VLS_elements.article, __VLS_elements.article)({
        key: (message.id),
        ...{ class: "message-row" },
        ...{ class: (`message-row--${message.role}`) },
    });
    if (message.role === 'assistant') {
        __VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
            ...{ class: "assistant-avatar" },
        });
    }
    __VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
        ...{ class: "message-bubble" },
        ...{ class: ({ 'is-error': message.status === 'error' }) },
    });
    __VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({
        ...{ class: "message-role" },
    });
    (message.role === 'user' ? '你' : 'Knowledge Hub');
    __VLS_asFunctionalElement(__VLS_elements.p, __VLS_elements.p)({});
    (message.content);
    if (message.status === 'streaming') {
        __VLS_asFunctionalElement(__VLS_elements.span)({
            ...{ class: "typing-cursor" },
        });
    }
    if (message.status === 'stopped') {
        __VLS_asFunctionalElement(__VLS_elements.small, __VLS_elements.small)({});
    }
    else if (message.status === 'error') {
        __VLS_asFunctionalElement(__VLS_elements.small, __VLS_elements.small)({});
        const __VLS_20 = {}.ElIcon;
        /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
        // @ts-ignore
        ElIcon;
        // @ts-ignore
        const __VLS_21 = __VLS_asFunctionalComponent(__VLS_20, new __VLS_20({}));
        const __VLS_22 = __VLS_21({}, ...__VLS_functionalComponentArgsRest(__VLS_21));
        const { default: __VLS_24 } = __VLS_23.slots;
        const __VLS_25 = {}.CircleCloseFilled;
        /** @type {[typeof __VLS_components.CircleCloseFilled, ]} */ ;
        // @ts-ignore
        CircleCloseFilled;
        // @ts-ignore
        const __VLS_26 = __VLS_asFunctionalComponent(__VLS_25, new __VLS_25({}));
        const __VLS_27 = __VLS_26({}, ...__VLS_functionalComponentArgsRest(__VLS_26));
        var __VLS_23;
    }
}
__VLS_asFunctionalElement(__VLS_elements.section, __VLS_elements.section)({
    ...{ class: "composer-shell" },
});
__VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
    ...{ class: "composer-label" },
});
__VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({});
(__VLS_ctx.serviceOnline ? '模型服务已连接' : '后端服务未连接');
// @ts-ignore
[serviceOnline,];
__VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({});
(__VLS_ctx.question.length);
// @ts-ignore
[question,];
__VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
    ...{ class: "composer" },
});
__VLS_asFunctionalElement(__VLS_elements.textarea)({
    ...{ onKeydown: (__VLS_ctx.handleKeydown) },
    value: (__VLS_ctx.question),
    maxlength: "4000",
    rows: "2",
    disabled: (__VLS_ctx.generating),
    placeholder: "输入问题，Enter 发送，Shift + Enter 换行",
    'aria-label': "输入问题",
});
// @ts-ignore
[question, handleKeydown, generating,];
if (__VLS_ctx.generating) {
    // @ts-ignore
    [generating,];
    const __VLS_30 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_31 = __VLS_asFunctionalComponent(__VLS_30, new __VLS_30({
        ...{ 'onClick': {} },
        ...{ class: "send-button" },
        circle: true,
    }));
    const __VLS_32 = __VLS_31({
        ...{ 'onClick': {} },
        ...{ class: "send-button" },
        circle: true,
    }, ...__VLS_functionalComponentArgsRest(__VLS_31));
    let __VLS_34;
    let __VLS_35;
    const __VLS_36 = ({ click: {} },
        { onClick: (__VLS_ctx.stop) });
    const { default: __VLS_37 } = __VLS_33.slots;
    // @ts-ignore
    [stop,];
    const __VLS_38 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_39 = __VLS_asFunctionalComponent(__VLS_38, new __VLS_38({}));
    const __VLS_40 = __VLS_39({}, ...__VLS_functionalComponentArgsRest(__VLS_39));
    const { default: __VLS_42 } = __VLS_41.slots;
    const __VLS_43 = {}.VideoPause;
    /** @type {[typeof __VLS_components.VideoPause, ]} */ ;
    // @ts-ignore
    VideoPause;
    // @ts-ignore
    const __VLS_44 = __VLS_asFunctionalComponent(__VLS_43, new __VLS_43({}));
    const __VLS_45 = __VLS_44({}, ...__VLS_functionalComponentArgsRest(__VLS_44));
    var __VLS_41;
    var __VLS_33;
}
else {
    const __VLS_48 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_49 = __VLS_asFunctionalComponent(__VLS_48, new __VLS_48({
        ...{ 'onClick': {} },
        ...{ class: "send-button" },
        type: "primary",
        circle: true,
        disabled: (!__VLS_ctx.canSend),
    }));
    const __VLS_50 = __VLS_49({
        ...{ 'onClick': {} },
        ...{ class: "send-button" },
        type: "primary",
        circle: true,
        disabled: (!__VLS_ctx.canSend),
    }, ...__VLS_functionalComponentArgsRest(__VLS_49));
    let __VLS_52;
    let __VLS_53;
    const __VLS_54 = ({ click: {} },
        { onClick: (__VLS_ctx.send) });
    const { default: __VLS_55 } = __VLS_51.slots;
    // @ts-ignore
    [canSend, send,];
    const __VLS_56 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_57 = __VLS_asFunctionalComponent(__VLS_56, new __VLS_56({}));
    const __VLS_58 = __VLS_57({}, ...__VLS_functionalComponentArgsRest(__VLS_57));
    const { default: __VLS_60 } = __VLS_59.slots;
    const __VLS_61 = {}.Promotion;
    /** @type {[typeof __VLS_components.Promotion, ]} */ ;
    // @ts-ignore
    Promotion;
    // @ts-ignore
    const __VLS_62 = __VLS_asFunctionalComponent(__VLS_61, new __VLS_61({}));
    const __VLS_63 = __VLS_62({}, ...__VLS_functionalComponentArgsRest(__VLS_62));
    var __VLS_59;
    var __VLS_51;
}
__VLS_asFunctionalElement(__VLS_elements.p, __VLS_elements.p)({
    ...{ class: "composer-note" },
});
/** @type {__VLS_StyleScopedClasses['chat-workspace']} */ ;
/** @type {__VLS_StyleScopedClasses['workspace-context']} */ ;
/** @type {__VLS_StyleScopedClasses['section-kicker']} */ ;
/** @type {__VLS_StyleScopedClasses['stage-badge']} */ ;
/** @type {__VLS_StyleScopedClasses['stage-dot']} */ ;
/** @type {__VLS_StyleScopedClasses['message-list']} */ ;
/** @type {__VLS_StyleScopedClasses['welcome-panel']} */ ;
/** @type {__VLS_StyleScopedClasses['welcome-symbol']} */ ;
/** @type {__VLS_StyleScopedClasses['section-kicker']} */ ;
/** @type {__VLS_StyleScopedClasses['capability-grid']} */ ;
/** @type {__VLS_StyleScopedClasses['message-row']} */ ;
/** @type {__VLS_StyleScopedClasses['assistant-avatar']} */ ;
/** @type {__VLS_StyleScopedClasses['message-bubble']} */ ;
/** @type {__VLS_StyleScopedClasses['is-error']} */ ;
/** @type {__VLS_StyleScopedClasses['message-role']} */ ;
/** @type {__VLS_StyleScopedClasses['typing-cursor']} */ ;
/** @type {__VLS_StyleScopedClasses['composer-shell']} */ ;
/** @type {__VLS_StyleScopedClasses['composer-label']} */ ;
/** @type {__VLS_StyleScopedClasses['composer']} */ ;
/** @type {__VLS_StyleScopedClasses['send-button']} */ ;
/** @type {__VLS_StyleScopedClasses['send-button']} */ ;
/** @type {__VLS_StyleScopedClasses['composer-note']} */ ;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            CircleCloseFilled: CircleCloseFilled,
            Document: Document,
            Promotion: Promotion,
            VideoPause: VideoPause,
            messages: messages,
            question: question,
            generating: generating,
            messageList: messageList,
            canSend: canSend,
            send: send,
            stop: stop,
            handleKeydown: handleKeydown,
        };
    },
    __typeEmits: {},
    __typeProps: {},
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
    __typeEmits: {},
    __typeProps: {},
});
; /* PartiallyEnd: #4569/main.vue */
