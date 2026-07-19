<script setup lang="ts">
import { computed, shallowRef } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { EditPen, FolderOpened, Plus, Refresh, Search, Tickets } from '@element-plus/icons-vue'
import { ApiRequestError } from '../api/http'
import { useKnowledgeSpacesStore } from '../stores/knowledgeSpaces'
import type { KnowledgeSpace, KnowledgeSpaceInput } from '../types/api'
import KnowledgeSpaceDialog from './KnowledgeSpaceDialog.vue'

const store = useKnowledgeSpacesStore()
const keyword = shallowRef('')
const dialogVisible = shallowRef(false)
const editingSpace = shallowRef<KnowledgeSpace | null>(null)
const saving = shallowRef(false)

const filteredSpaces = computed(() => {
  const normalizedKeyword = keyword.value.trim().toLocaleLowerCase()
  if (!normalizedKeyword) {
    return store.spaces
  }
  return store.spaces.filter((space) =>
    `${space.name} ${space.description}`.toLocaleLowerCase().includes(normalizedKeyword),
  )
})

function openCreateDialog(): void {
  editingSpace.value = null
  dialogVisible.value = true
}

function openEditDialog(space: KnowledgeSpace): void {
  editingSpace.value = space
  dialogVisible.value = true
}

async function save(input: KnowledgeSpaceInput): Promise<void> {
  saving.value = true
  try {
    if (editingSpace.value) {
      await store.update(editingSpace.value.id, input)
      ElMessage.success('知识空间已更新')
    } else {
      await store.create(input)
      ElMessage.success('知识空间已创建')
    }
    dialogVisible.value = false
  } catch (error) {
    ElMessage.error(toMessage(error))
  } finally {
    saving.value = false
  }
}

async function remove(space: KnowledgeSpace): Promise<void> {
  try {
    await ElMessageBox.confirm(
      `确定删除“${space.name}”吗？当前阶段只删除空间记录。`,
      '删除知识空间',
      { confirmButtonText: '删除', cancelButtonText: '取消', type: 'warning' },
    )
    await store.remove(space.id)
    ElMessage.success('知识空间已删除')
  } catch (error) {
    if (error !== 'cancel' && error !== 'close') {
      ElMessage.error(toMessage(error))
    }
  }
}

async function reload(): Promise<void> {
  try {
    await store.load()
  } catch (error) {
    ElMessage.error(toMessage(error))
  }
}

function toMessage(error: unknown): string {
  return error instanceof ApiRequestError || error instanceof Error
    ? error.message
    : '操作失败，请稍后重试'
}
</script>

<template>
  <aside class="knowledge-sidebar">
    <div class="sidebar-heading">
      <div>
        <span class="section-kicker">知识目录</span>
        <h2>知识空间</h2>
      </div>
      <el-tooltip content="刷新列表" placement="bottom">
        <el-button circle text :loading="store.loading" aria-label="刷新列表" @click="reload">
          <el-icon><Refresh /></el-icon>
        </el-button>
      </el-tooltip>
    </div>

    <el-input v-model="keyword" :prefix-icon="Search" clearable placeholder="搜索空间" />

    <div v-loading="store.loading" class="space-list">
      <div
        v-for="space in filteredSpaces"
        :key="space.id"
        class="space-card"
        :class="{ 'is-active': store.selectedId === space.id }"
        role="button"
        tabindex="0"
        @click="store.selectedId = space.id"
        @keydown.enter="store.selectedId = space.id"
        @keydown.space.prevent="store.selectedId = space.id"
      >
        <span class="space-icon"><el-icon><FolderOpened /></el-icon></span>
        <span class="space-copy">
          <strong>{{ space.name }}</strong>
          <small>{{ space.description || '尚未填写用途说明' }}</small>
        </span>
        <el-dropdown trigger="click" @click.stop>
          <el-button class="space-menu" text circle aria-label="空间操作">•••</el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item :icon="EditPen" @click="openEditDialog(space)">编辑</el-dropdown-item>
              <el-dropdown-item divided @click="remove(space)">删除</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <div v-if="!store.loading && filteredSpaces.length === 0" class="sidebar-empty">
        <el-icon><Tickets /></el-icon>
        <strong>{{ keyword ? '没有匹配的空间' : '还没有知识空间' }}</strong>
        <span>{{ keyword ? '换个关键词试试' : '先创建一个空间来组织文档' }}</span>
      </div>
    </div>

    <el-button class="create-space-button" type="primary" :icon="Plus" @click="openCreateDialog">
      新建知识空间
    </el-button>

    <KnowledgeSpaceDialog
      v-model="dialogVisible"
      :space="editingSpace"
      :saving="saving"
      @save="save"
    />
  </aside>
</template>
