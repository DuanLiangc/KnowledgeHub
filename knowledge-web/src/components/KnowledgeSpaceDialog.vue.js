import { computed, reactive, shallowRef, watch } from 'vue';
const props = defineProps();
const emit = defineEmits();
const form = reactive({ name: '', description: '' });
const formRef = shallowRef();
const isEditing = computed(() => props.space !== null);
const rules = {
    name: [
        { required: true, message: '请输入知识空间名称', trigger: 'blur' },
        { max: 100, message: '名称不能超过 100 个字符', trigger: 'blur' },
    ],
    description: [
        { max: 500, message: '描述不能超过 500 个字符', trigger: 'blur' },
    ],
};
watch(() => [props.modelValue, props.space], ([visible, space]) => {
    if (visible) {
        form.name = space?.name ?? '';
        form.description = space?.description ?? '';
    }
});
async function submit() {
    const valid = await formRef.value?.validate().catch(() => false);
    if (valid) {
        emit('save', { name: form.name.trim(), description: form.description.trim() });
    }
}
debugger; /* PartiallyEnd: #3632/scriptSetup.vue */
const __VLS_ctx = {};
let __VLS_elements;
let __VLS_components;
let __VLS_directives;
const __VLS_0 = {}.ElDialog;
/** @type {[typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, typeof __VLS_components.ElDialog, typeof __VLS_components.elDialog, ]} */ ;
// @ts-ignore
ElDialog;
// @ts-ignore
const __VLS_1 = __VLS_asFunctionalComponent(__VLS_0, new __VLS_0({
    ...{ 'onUpdate:modelValue': {} },
    modelValue: (__VLS_ctx.modelValue),
    title: (__VLS_ctx.isEditing ? '编辑知识空间' : '新建知识空间'),
    width: "480px",
    alignCenter: true,
}));
const __VLS_2 = __VLS_1({
    ...{ 'onUpdate:modelValue': {} },
    modelValue: (__VLS_ctx.modelValue),
    title: (__VLS_ctx.isEditing ? '编辑知识空间' : '新建知识空间'),
    width: "480px",
    alignCenter: true,
}, ...__VLS_functionalComponentArgsRest(__VLS_1));
let __VLS_4;
let __VLS_5;
const __VLS_6 = ({ 'update:modelValue': {} },
    { 'onUpdate:modelValue': (...[$event]) => {
            __VLS_ctx.emit('update:modelValue', $event);
            // @ts-ignore
            [modelValue, isEditing, emit,];
        } });
var __VLS_7 = {};
const { default: __VLS_8 } = __VLS_3.slots;
__VLS_asFunctionalElement(__VLS_elements.p, __VLS_elements.p)({
    ...{ class: "dialog-intro" },
});
const __VLS_9 = {}.ElForm;
/** @type {[typeof __VLS_components.ElForm, typeof __VLS_components.elForm, typeof __VLS_components.ElForm, typeof __VLS_components.elForm, ]} */ ;
// @ts-ignore
ElForm;
// @ts-ignore
const __VLS_10 = __VLS_asFunctionalComponent(__VLS_9, new __VLS_9({
    ref: "formRef",
    model: (__VLS_ctx.form),
    rules: (__VLS_ctx.rules),
    labelPosition: "top",
}));
const __VLS_11 = __VLS_10({
    ref: "formRef",
    model: (__VLS_ctx.form),
    rules: (__VLS_ctx.rules),
    labelPosition: "top",
}, ...__VLS_functionalComponentArgsRest(__VLS_10));
/** @type {typeof __VLS_ctx.formRef} */ ;
var __VLS_13 = {};
const { default: __VLS_15 } = __VLS_12.slots;
// @ts-ignore
[form, rules, formRef,];
const __VLS_16 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_17 = __VLS_asFunctionalComponent(__VLS_16, new __VLS_16({
    label: "空间名称",
    prop: "name",
}));
const __VLS_18 = __VLS_17({
    label: "空间名称",
    prop: "name",
}, ...__VLS_functionalComponentArgsRest(__VLS_17));
const { default: __VLS_20 } = __VLS_19.slots;
const __VLS_21 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_22 = __VLS_asFunctionalComponent(__VLS_21, new __VLS_21({
    modelValue: (__VLS_ctx.form.name),
    maxlength: "100",
    placeholder: "例如：研发技术文档",
}));
const __VLS_23 = __VLS_22({
    modelValue: (__VLS_ctx.form.name),
    maxlength: "100",
    placeholder: "例如：研发技术文档",
}, ...__VLS_functionalComponentArgsRest(__VLS_22));
// @ts-ignore
[form,];
var __VLS_19;
const __VLS_26 = {}.ElFormItem;
/** @type {[typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, typeof __VLS_components.ElFormItem, typeof __VLS_components.elFormItem, ]} */ ;
// @ts-ignore
ElFormItem;
// @ts-ignore
const __VLS_27 = __VLS_asFunctionalComponent(__VLS_26, new __VLS_26({
    label: "用途说明",
    prop: "description",
}));
const __VLS_28 = __VLS_27({
    label: "用途说明",
    prop: "description",
}, ...__VLS_functionalComponentArgsRest(__VLS_27));
const { default: __VLS_30 } = __VLS_29.slots;
const __VLS_31 = {}.ElInput;
/** @type {[typeof __VLS_components.ElInput, typeof __VLS_components.elInput, ]} */ ;
// @ts-ignore
ElInput;
// @ts-ignore
const __VLS_32 = __VLS_asFunctionalComponent(__VLS_31, new __VLS_31({
    modelValue: (__VLS_ctx.form.description),
    type: "textarea",
    rows: (4),
    maxlength: "500",
    showWordLimit: true,
    placeholder: "说明这个空间将收录哪些知识",
}));
const __VLS_33 = __VLS_32({
    modelValue: (__VLS_ctx.form.description),
    type: "textarea",
    rows: (4),
    maxlength: "500",
    showWordLimit: true,
    placeholder: "说明这个空间将收录哪些知识",
}, ...__VLS_functionalComponentArgsRest(__VLS_32));
// @ts-ignore
[form,];
var __VLS_29;
var __VLS_12;
{
    const { footer: __VLS_36 } = __VLS_3.slots;
    const __VLS_37 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_38 = __VLS_asFunctionalComponent(__VLS_37, new __VLS_37({
        ...{ 'onClick': {} },
    }));
    const __VLS_39 = __VLS_38({
        ...{ 'onClick': {} },
    }, ...__VLS_functionalComponentArgsRest(__VLS_38));
    let __VLS_41;
    let __VLS_42;
    const __VLS_43 = ({ click: {} },
        { onClick: (...[$event]) => {
                __VLS_ctx.emit('update:modelValue', false);
                // @ts-ignore
                [emit,];
            } });
    const { default: __VLS_44 } = __VLS_40.slots;
    var __VLS_40;
    const __VLS_45 = {}.ElButton;
    /** @type {[typeof __VLS_components.ElButton, typeof __VLS_components.elButton, typeof __VLS_components.ElButton, typeof __VLS_components.elButton, ]} */ ;
    // @ts-ignore
    ElButton;
    // @ts-ignore
    const __VLS_46 = __VLS_asFunctionalComponent(__VLS_45, new __VLS_45({
        ...{ 'onClick': {} },
        type: "primary",
        loading: (__VLS_ctx.saving),
    }));
    const __VLS_47 = __VLS_46({
        ...{ 'onClick': {} },
        type: "primary",
        loading: (__VLS_ctx.saving),
    }, ...__VLS_functionalComponentArgsRest(__VLS_46));
    let __VLS_49;
    let __VLS_50;
    const __VLS_51 = ({ click: {} },
        { onClick: (__VLS_ctx.submit) });
    const { default: __VLS_52 } = __VLS_48.slots;
    // @ts-ignore
    [saving, submit,];
    (__VLS_ctx.isEditing ? '保存修改' : '创建空间');
    // @ts-ignore
    [isEditing,];
    var __VLS_48;
}
var __VLS_3;
/** @type {__VLS_StyleScopedClasses['dialog-intro']} */ ;
// @ts-ignore
var __VLS_14 = __VLS_13;
var __VLS_dollars;
const __VLS_self = (await import('vue')).defineComponent({
    setup() {
        return {
            emit: emit,
            form: form,
            formRef: formRef,
            isEditing: isEditing,
            rules: rules,
            submit: submit,
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
