import { computed, shallowRef } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { EditPen, FolderOpened, Plus, Refresh, Search, Tickets } from '@element-plus/icons-vue';
import { ApiRequestError } from '../api/http';
import { useKnowledgeSpacesStore } from '../stores/knowledgeSpaces';
import KnowledgeSpaceDialog from './KnowledgeSpaceDialog.vue';
const store = useKnowledgeSpacesStore();
const keyword = shallowRef('');
const dialogVisible = shallowRef(false);
const editingSpace = shallowRef(null);
const saving = shallowRef(false);
const filteredSpaces = computed(() => {
    const normalizedKeyword = keyword.value.trim().toLocaleLowerCase();
    if (!normalizedKeyword) {
        return store.spaces;
    }
    return store.spaces.filter((space) => `${space.name} ${space.description}`.toLocaleLowerCase().includes(normalizedKeyword));
});
function openCreateDialog() {
    editingSpace.value = null;
    dialogVisible.value = true;
}
function openEditDialog(space) {
    editingSpace.value = space;
    dialogVisible.value = true;
}
async function save(input) {
    saving.value = true;
    try {
        if (editingSpace.value) {
            await store.update(editingSpace.value.id, input);
            ElMessage.success('知识空间已更新');
        }
        else {
            await store.create(input);
            ElMessage.success('知识空间已创建');
        }
        dialogVisible.value = false;
    }
    catch (error) {
        ElMessage.error(toMessage(error));
    }
    finally {
        saving.value = false;
    }
}
async function remove(space) {
    try {
        await ElMessageBox.confirm(`确定删除“${space.name}”吗？当前阶段只删除空间记录。`, '删除知识空间', { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' });
        await store.remove(space.id);
        ElMessage.success('知识空间已删除');
    }
    catch (error) {
        if (error !== 'cancel' && error !== 'close') {
            ElMessage.error(toMessage(error));
        }
    }
}
async function reload() {
    try {
        await store.load();
    }
    catch (error) {
        ElMessage.error(toMessage(error));
    }
}
function toMessage(error) {
    return error instanceof ApiRequestError || error instanceof Error
        ? error.message
        : '操作失败，请稍后重试';
}
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_elements;
let __VLS_components;
let __VLS_directives;
__VLS_asFunctionalElement(__VLS_elements.aside, __VLS_elements.aside)({
    ...{ class: "knowledge-sidebar" },
});
__VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
    ...{ class: "sidebar-heading" },
});
__VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({});
__VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({
    ...{ class: "section-kicker" },
});
__VLS_asFunctionalElement(__VLS_elements.h2, __VLS_elements.h2)({});
const __VLS_0 = {}.ElTooltip;
/** @type {[typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, typeof __VLS_components.ElTooltip, typeof __VLS_components.elTooltip, ]} */ ;
// @ts-ignore
ElTooltip;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent(__VLS_0, new __VLS_0({
    content: "刷新列表",
    placement: "bottom",
}));
const __VLS_2 = __VLS_1({
    content: "刷新列表",
    placement: "bottom",
}, ...__VLS_functionalComponentArgsRest(__VLS_1));
const { default: __VLS_4 } = __VLS_3.slots;
const __VLS_5 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
ElButton;
// @ts-ignore
const __VLS_6 = __VLS_asFunctionalComponent(__VLS_5, new __VLS_5({
    ...{ 'onClick': {} },
    circle: true,
    text: true,
    loading: (__VLS_ctx.store.loading),
    'aria-label': "刷新列表",
}));
const __VLS_7 = __VLS_6({
    ...{ 'onClick': {} },
    circle: true,
    text: true,
    loading: (__VLS_ctx.store.loading),
    'aria-label': "刷新列表",
}, ...__VLS_functionalComponentArgsRest(__VLS_6));
let __VLS_9;
let __VLS_10;
const __VLS_11 = ({ click: {} },
    { onClick: (__VLS_ctx.reload) });
const { default: __VLS_12 } = __VLS_8.slots;
// @ts-ignore
[store, reload,];
const __VLS_13 = {}.ElIcon;
/** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
// @ts-ignore
ElIcon;
// @ts-ignore
const __VLS_14 = __VLS_asFunctionalComponent(__VLS_13, new __VLS_13({}));
const __VLS_15 = __VLS_14({}, ...__VLS_functionalComponentArgsRest(__VLS_14));
const { default: __VLS_17 } = __VLS_16.slots;
const __VLS_18 = {}.Refresh;
/** @type {[typeof __VLS_components.Refresh, ]} */ ;
// @ts-ignore
Refresh;
// @ts-ignore
const __VLS_19 = __VLS_asFunctionalComponent(__VLS_18, new __VLS_18({}));
const __VLS_20 = __VLS_19({}, ...__VLS_functionalComponentArgsRest(__VLS_19));
var __VLS_16;
var __VLS_8;
var __VLS_3;
const __VLS_23 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_24 = __VLS_asFunctionalComponent(__VLS_23, new __VLS_23({
    modelValue: (__VLS_ctx.keyword),
    prefixIcon: (__VLS_ctx.Search),
    clearable: true,
    placeholder: "搜索空间",
}));
const __VLS_25 = __VLS_24({
    modelValue: (__VLS_ctx.keyword),
    prefixIcon: (__VLS_ctx.Search),
    clearable: true,
    placeholder: "搜索空间",
}, ...__VLS_functionalComponentArgsRest(__VLS_24));
// @ts-ignore
[keyword, Search,];
__VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
    ...{ class: "space-list" },
});
__VLS_asFunctionalDirective(__VLS_directives.vLoading)(null, { ...__VLS_directiveBindingRestFields, value: (__VLS_ctx.store.loading) }, null, null);
// @ts-ignore
[store, vLoading,];
for (const [space] of __VLS_getVForSourceType((__VLS_ctx.filteredSpaces))) {
    // @ts-ignore
    [filteredSpaces,];
    __VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
        ...{ onClick: (...[$event]) => {
                __VLS_ctx.store.selectedId = space.id;
                // @ts-ignore
                [store,];
            } },
        ...{ onKeydown: (...[$event]) => {
                __VLS_ctx.store.selectedId = space.id;
                // @ts-ignore
                [store,];
            } },
        ...{ onKeydown: (...[$event]) => {
                __VLS_ctx.store.selectedId = space.id;
                // @ts-ignore
                [store,];
            } },
        key: (space.id),
        ...{ class: "space-card" },
        ...{ class: ({ 'is-active': __VLS_ctx.store.selectedId === space.id }) },
        role: "button",
        tabindex: "0",
    });
    // @ts-ignore
    [store,];
    __VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({
        ...{ class: "space-icon" },
    });
    const __VLS_28 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_29 = __VLS_asFunctionalComponent(__VLS_28, new __VLS_28({}));
    const __VLS_30 = __VLS_29({}, ...__VLS_functionalComponentArgsRest(__VLS_29));
    const { default: __VLS_32 } = __VLS_31.slots;
    const __VLS_33 = {}.FolderOpened;
    /** @type {[typeof __VLS_components.FolderOpened, ]} */ ;
    // @ts-ignore
    FolderOpened;
    // @ts-ignore
    const __VLS_34 = __VLS_asFunctionalComponent(__VLS_33, new __VLS_33({}));
    const __VLS_35 = __VLS_34({}, ...__VLS_functionalComponentArgsRest(__VLS_34));
    var __VLS_31;
    __VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({
        ...{ class: "space-copy" },
    });
    __VLS_asFunctionalElement(__VLS_elements.strong, __VLS_elements.strong)({});
    (space.name);
    __VLS_asFunctionalElement(__VLS_elements.small, __VLS_elements.small)({});
    (space.description || '尚未填写用途说明');
    const __VLS_38 = {}.ElDropdown;
    /** @type {[typeof __VLS_components.ElDropdown, typeof __VLS_components.elDropdown, typeof __VLS_components.ElDropdown, typeof __VLS_components.elDropdown, ]} */ ;
    // @ts-ignore
    ElDropdown;
    // @ts-ignore
    const __VLS_39 = __VLS_asFunctionalComponent(__VLS_38, new __VLS_38({
        ...{ 'onClick': {} },
        trigger: "click",
    }));
    const __VLS_40 = __VLS_39({
        ...{ 'onClick': {} },
        trigger: "click",
    }, ...__VLS_functionalComponentArgsRest(__VLS_39));
    let __VLS_42;
    let __VLS_43;
    const __VLS_44 = ({ click: {} },
        { onClick: () => { } });
    const { default: __VLS_45 } = __VLS_41.slots;
    const __VLS_46 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_47 = __VLS_asFunctionalComponent(__VLS_46, new __VLS_46({
        ...{ class: "space-menu" },
        text: true,
        circle: true,
        'aria-label': "空间操作",
    }));
    const __VLS_48 = __VLS_47({
        ...{ class: "space-menu" },
        text: true,
        circle: true,
        'aria-label': "空间操作",
    }, ...__VLS_functionalComponentArgsRest(__VLS_47));
    const { default: __VLS_50 } = __VLS_49.slots;
    var __VLS_49;
    {
        const { dropdown: __VLS_51 } = __VLS_41.slots;
        const __VLS_52 = {}.ElDropdownMenu;
        /** @type {[typeof __VLS_components.ElDropdownMenu, typeof __VLS_components.elDropdownMenu, typeof __VLS_components.ElDropdownMenu, typeof __VLS_components.elDropdownMenu, ]} */ ;
        // @ts-ignore
        ElDropdownMenu;
        // @ts-ignore
        const __VLS_53 = __VLS_asFunctionalComponent(__VLS_52, new __VLS_52({}));
        const __VLS_54 = __VLS_53({}, ...__VLS_functionalComponentArgsRest(__VLS_53));
        const { default: __VLS_56 } = __VLS_55.slots;
        const __VLS_57 = {}.ElDropdownItem;
        /** @type {[typeof __VLS_components.ElDropdownItem, typeof __VLS_components.elDropdownItem, typeof __VLS_components.ElDropdownItem, typeof __VLS_components.elDropdownItem, ]} */ ;
        // @ts-ignore
        ElDropdownItem;
        // @ts-ignore
        const __VLS_58 = __VLS_asFunctionalComponent(__VLS_57, new __VLS_57({
            ...{ 'onClick': {} },
            icon: (__VLS_ctx.EditPen),
        }));
        const __VLS_59 = __VLS_58({
            ...{ 'onClick': {} },
            icon: (__VLS_ctx.EditPen),
        }, ...__VLS_functionalComponentArgsRest(__VLS_58));
        let __VLS_61;
        let __VLS_62;
        const __VLS_63 = ({ click: {} },
            { onClick: (...[$event]) => {
                    __VLS_ctx.openEditDialog(space);
                    // @ts-ignore
                    [EditPen, openEditDialog,];
                } });
        const { default: __VLS_64 } = __VLS_60.slots;
        var __VLS_60;
        const __VLS_65 = {}.ElDropdownItem;
        /** @type {[typeof __VLS_components.ElDropdownItem, typeof __VLS_components.elDropdownItem, typeof __VLS_components.ElDropdownItem, typeof __VLS_components.elDropdownItem, ]} */ ;
        // @ts-ignore
        ElDropdownItem;
        // @ts-ignore
        const __VLS_66 = __VLS_asFunctionalComponent(__VLS_65, new __VLS_65({
            ...{ 'onClick': {} },
            divided: true,
        }));
        const __VLS_67 = __VLS_66({
            ...{ 'onClick': {} },
            divided: true,
        }, ...__VLS_functionalComponentArgsRest(__VLS_66));
        let __VLS_69;
        let __VLS_70;
        const __VLS_71 = ({ click: {} },
            { onClick: (...[$event]) => {
                    __VLS_ctx.remove(space);
                    // @ts-ignore
                    [remove,];
                } });
        const { default: __VLS_72 } = __VLS_68.slots;
        var __VLS_68;
        var __VLS_55;
    }
    var __VLS_41;
}
if (!__VLS_ctx.store.loading && __VLS_ctx.filteredSpaces.length === 0) {
    // @ts-ignore
    [store, filteredSpaces,];
    __VLS_asFunctionalElement(__VLS_elements.div, __VLS_elements.div)({
        ...{ class: "sidebar-empty" },
    });
    const __VLS_73 = {}.ElIcon;
    /** @type {[typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, typeof __VLS_components.ElIcon, typeof __VLS_components.elIcon, ]} */ ;
    // @ts-ignore
    ElIcon;
    // @ts-ignore
    const __VLS_74 = __VLS_asFunctionalComponent(__VLS_73, new __VLS_73({}));
    const __VLS_75 = __VLS_74({}, ...__VLS_functionalComponentArgsRest(__VLS_74));
    const { default: __VLS_77 } = __VLS_76.slots;
    const __VLS_78 = {}.Tickets;
    /** @type {[typeof __VLS_components.Tickets, ]} */ ;
    // @ts-ignore
    Tickets;
    // @ts-ignore
    const __VLS_79 = __VLS_asFunctionalComponent(__VLS_78, new __VLS_78({}));
    const __VLS_80 = __VLS_79({}, ...__VLS_functionalComponentArgsRest(__VLS_79));
    var __VLS_76;
    __VLS_asFunctionalElement(__VLS_elements.strong, __VLS_elements.strong)({});
    (__VLS_ctx.keyword ? '没有匹配的空间' : '还没有知识空间');
    // @ts-ignore
    [keyword,];
    __VLS_asFunctionalElement(__VLS_elements.span, __VLS_elements.span)({});
    (__VLS_ctx.keyword ? '换个关键词试试' : '先创建一个空间来组织文档');
    // @ts-ignore
    [keyword,];
}
const __VLS_83 = {}.ElButton;
/** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
// @ts-ignore
ElButton;
// @ts-ignore
const __VLS_84 = __VLS_asFunctionalComponent(__VLS_83, new __VLS_83({
    ...{ 'onClick': {} },
    ...{ class: "create-space-button" },
    type: "primary",
    icon: (__VLS_ctx.Plus),
}));
const __VLS_85 = __VLS_84({
    ...{ 'onClick': {} },
    ...{ class: "create-space-button" },
    type: "primary",
    icon: (__VLS_ctx.Plus),
}, ...__VLS_functionalComponentArgsRest(__VLS_84));
let __VLS_87;
let __VLS_88;
const __VLS_89 = ({ click: {} },
    { onClick: (__VLS_ctx.openCreateDialog) });
const { default: __VLS_90 } = __VLS_86.slots;
// @ts-ignore
[Plus, openCreateDialog,];
var __VLS_86;
/** @type {[typeof KnowledgeSpaceDialog, ]} */ ;
// @ts-ignore
const __VLS_91 = __VLS_asFunctionalComponent(KnowledgeSpaceDialog, new KnowledgeSpaceDialog({
    ...{ 'onSave': {} },
    modelValue: (__VLS_ctx.dialogVisible),
    space: (__VLS_ctx.editingSpace),
    saving: (__VLS_ctx.saving),
}));
const __VLS_92 = __VLS_91({
    ...{ 'onSave': {} },
    modelValue: (__VLS_ctx.dialogVisible),
    space: (__VLS_ctx.editingSpace),
    saving: (__VLS_ctx.saving),
}, ...__VLS_functionalComponentArgsRest(__VLS_91));
let __VLS_94;
let __VLS_95;
const __VLS_96 = ({ save: {} },
    { onSave: (__VLS_ctx.save) });
// @ts-ignore
[dialogVisible, editingSpace, saving, save,];
var __VLS_93;
/** @type {__VLS_StyleScopedClasses['knowledge-sidebar']} */ ;
/** @type {__VLS_StyleScopedClasses['sidebar-heading']} */ ;
/** @type {__VLS_StyleScopedClasses['section-kicker']} */ ;
/** @type {__VLS_StyleScopedClasses['space-list']} */ ;
/** @type {__VLS_StyleScopedClasses['space-card']} */ ;
/** @type {__VLS_StyleScopedClasses['is-active']} */ ;
/** @type {__VLS_StyleScopedClasses['space-icon']} */ ;
/** @type {__VLS_StyleScopedClasses['space-copy']} */ ;
/** @type {__VLS_StyleScopedClasses['space-menu']} */ ;
/** @type {__VLS_StyleScopedClasses['sidebar-empty']} */ ;
/** @type {__VLS_StyleScopedClasses['create-space-button']} */ ;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            EditPen: EditPen,
            FolderOpened: FolderOpened,
            Plus: Plus,
            Refresh: Refresh,
            Search: Search,
            Tickets: Tickets,
            KnowledgeSpaceDialog: KnowledgeSpaceDialog,
            store: store,
            keyword: keyword,
            dialogVisible: dialogVisible,
            editingSpace: editingSpace,
            saving: saving,
            filteredSpaces: filteredSpaces,
            openCreateDialog: openCreateDialog,
            openEditDialog: openEditDialog,
            save: save,
            remove: remove,
            reload: reload,
        };
    },
});
export default (await import('vue')).defineComponent({
    setup() {
        return {};
    },
});
; /* PartiallyEnd: #4569/main.vue */
