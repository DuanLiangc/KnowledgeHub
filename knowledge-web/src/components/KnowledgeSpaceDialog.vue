<script setup lang="ts">
import { computed, reactive, shallowRef, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { KnowledgeSpace, KnowledgeSpaceInput } from '../types/api'

const props = defineProps<{
  modelValue: boolean
  space: KnowledgeSpace | null
  saving: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  save: [input: KnowledgeSpaceInput]
}>()

const form = reactive<KnowledgeSpaceInput>({ name: '', description: '' })
const formRef = shallowRef<FormInstance>()
const isEditing = computed(() => props.space !== null)

const rules: FormRules<KnowledgeSpaceInput> = {
  name: [
    { required: true, message: '请输入知识空间名称', trigger: 'blur' },
    { max: 100, message: '名称不能超过 100 个字符', trigger: 'blur' },
  ],
  description: [
    { max: 500, message: '描述不能超过 500 个字符', trigger: 'blur' },
  ],
}

watch(
  () => [props.modelValue, props.space] as const,
  ([visible, space]) => {
    if (visible) {
      form.name = space?.name ?? ''
      form.description = space?.description ?? ''
    }
  },
)

async function submit(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (valid) {
    emit('save', { name: form.name.trim(), description: form.description.trim() })
  }
}
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="isEditing ? '编辑知识空间' : '新建知识空间'"
    width="480px"
    align-center
    @update:model-value="emit('update:modelValue', $event)"
  >
    <p class="dialog-intro">
      知识空间用于组织同一业务范围内的文档。后续上传、检索和权限都会以它为边界。
    </p>
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="空间名称" prop="name">
        <el-input v-model="form.name" maxlength="100" placeholder="例如：研发技术文档" />
      </el-form-item>
      <el-form-item label="用途说明" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          maxlength="500"
          show-word-limit
          placeholder="说明这个空间将收录哪些知识"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" :loading="saving" @click="submit">
        {{ isEditing ? '保存修改' : '创建空间' }}
      </el-button>
    </template>
  </el-dialog>
</template>
