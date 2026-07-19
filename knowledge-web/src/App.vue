<script setup lang="ts">
import { onMounted, shallowRef } from 'vue'
import { ElMessage } from 'element-plus'
import AppHeader from './components/AppHeader.vue'
import ChatWorkspace from './components/ChatWorkspace.vue'
import KnowledgeSidebar from './components/KnowledgeSidebar.vue'
import { useKnowledgeSpacesStore } from './stores/knowledgeSpaces'
import type { ChatMetrics } from './types/api'

const store = useKnowledgeSpacesStore()
const serviceOnline = shallowRef(false)
const checkingService = shallowRef(true)
const metrics = shallowRef<ChatMetrics | null>(null)

onMounted(async () => {
  await Promise.all([checkService(), loadSpaces()])
})

async function checkService(): Promise<void> {
  checkingService.value = true
  try {
    const response = await fetch('/actuator/health')
    serviceOnline.value = response.ok
  } catch {
    serviceOnline.value = false
  } finally {
    checkingService.value = false
  }
}

async function loadSpaces(): Promise<void> {
  try {
    await store.load()
  } catch {
    ElMessage.error('无法加载知识空间，请确认后端已在 8080 端口启动')
  }
}
</script>

<template>
  <div class="app-shell">
    <AppHeader
      :service-online="serviceOnline"
      :checking-service="checkingService"
      :metrics="metrics"
    />
    <div class="workspace-layout">
      <KnowledgeSidebar />
      <ChatWorkspace
        :space-name="store.selectedSpace?.name ?? null"
        :service-online="serviceOnline"
        @metrics="metrics = $event"
      />
    </div>
  </div>
</template>
