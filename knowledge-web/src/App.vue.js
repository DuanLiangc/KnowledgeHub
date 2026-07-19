import { onMounted, shallowRef } from 'vue';
import { ElMessage } from 'element-plus';
import AppHeader from './components/AppHeader.vue';
import ChatWorkspace from './components/ChatWorkspace.vue';
import KnowledgeSidebar from './components/KnowledgeSidebar.vue';
import { useKnowledgeSpacesStore } from './stores/knowledgeSpaces';
const store = useKnowledgeSpacesStore();
const serviceOnline = shallowRef(false);
const checkingService = shallowRef(true);
const metrics = shallowRef(null);
onMounted(async () => {
    await Promise.all([checkService(), loadSpaces()]);
});
async function checkService() {
    checkingService.value = true;
    try {
        const response = await fetch('/actuator/health');
        serviceOnline.value = response.ok;
    }
    catch {
        serviceOnline.value = false;
    }
    finally {
        checkingService.value = false;
    }
}
async function loadSpaces() {
    try {
        await store.load();
    }
    catch {
        ElMessage.error('无法加载知识空间，请确认后端已在 8080 端口启动');
    }
}
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_elements;
let __VLS_components;
let __VLS_directives;
__VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
    ...{ class: "app-shell" },
});
/** @type {[typeof AppHeader, ]} */ ;
// @ts-ignore
const __VLS_0 = __VLS_asFunctionalComponent(AppHeader, new AppHeader({
    serviceOnline: (__VLS_ctx.serviceOnline),
    checkingService: (__VLS_ctx.checkingService),
    metrics: (__VLS_ctx.metrics),
}));
const __VLS_1 = __VLS_0({
    serviceOnline: (__VLS_ctx.serviceOnline),
    checkingService: (__VLS_ctx.checkingService),
    metrics: (__VLS_ctx.metrics),
}, ...__VLS_functionalComponentArgsRest(__VLS_0));
// @ts-ignore
[serviceOnline, checkingService, metrics,];
__VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
    ...{ class: "workspace-layout" },
});
/** @type {[typeof KnowledgeSidebar, ]} */ ;
// @ts-ignore
const __VLS_4 = __VLS_asFunctionalComponent(KnowledgeSidebar, new KnowledgeSidebar({}));
const __VLS_5 = __VLS_4({}, ...__VLS_functionalComponentArgsRest(__VLS_4));
/** @type {[typeof ChatWorkspace, ]} */ ;
// @ts-ignore
const __VLS_8 = __VLS_asFunctionalComponent(ChatWorkspace, new ChatWorkspace({
    ...{ 'onMetrics': {} },
    spaceName: (__VLS_ctx.store.selectedSpace?.name ?? null),
    serviceOnline: (__VLS_ctx.serviceOnline),
}));
const __VLS_9 = __VLS_8({
    ...{ 'onMetrics': {} },
    spaceName: (__VLS_ctx.store.selectedSpace?.name ?? null),
    serviceOnline: (__VLS_ctx.serviceOnline),
}, ...__VLS_functionalComponentArgsRest(__VLS_8));
let __VLS_11;
let __VLS_12;
const __VLS_13 = ({ metrics: {} },
    { onMetrics: (...[$event]) => {
            __VLS_ctx.metrics = $event;
            // @ts-ignore
            [serviceOnline, metrics, store,];
        } });
var __VLS_10;
/** @type {__VLS_StyleScopedClasses['app-shell']} */ ;
/** @type {__VLS_StyleScopedClasses['workspace-layout']} */ ;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            AppHeader: AppHeader,
            ChatWorkspace: ChatWorkspace,
            KnowledgeSidebar: KnowledgeSidebar,
            store: store,
            serviceOnline: serviceOnline,
            checkingService: checkingService,
            metrics: metrics,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
