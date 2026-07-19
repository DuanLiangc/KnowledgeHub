<script setup lang="ts">
import { Connection, DataAnalysis, Timer } from '@element-plus/icons-vue'
import type { ChatMetrics } from '../types/api'

defineProps<{
  serviceOnline: boolean
  checkingService: boolean
  metrics: ChatMetrics | null
}>()
</script>

<template>
  <header class="app-header">
    <div class="brand">
      <div class="brand-mark" aria-hidden="true">
        <span>K</span>
        <i />
      </div>
      <div>
        <p class="brand-eyebrow">企业知识中枢</p>
        <h1>Knowledge Hub</h1>
      </div>
    </div>

    <div class="runtime-strip" aria-label="运行状态">
      <div class="runtime-item">
        <el-icon><Connection /></el-icon>
        <div>
          <span>后端服务</span>
          <strong :class="serviceOnline ? 'is-online' : 'is-offline'">
            {{ checkingService ? '检查中' : serviceOnline ? '运行中' : '未连接' }}
          </strong>
        </div>
      </div>

      <div class="runtime-item">
        <el-icon><DataAnalysis /></el-icon>
        <div>
          <span>当前模型</span>
          <strong>{{ metrics?.model ?? '等待首次调用' }}</strong>
        </div>
      </div>

      <div class="runtime-item runtime-item--compact">
        <el-icon><Timer /></el-icon>
        <div>
          <span>最近用量</span>
          <strong>
            {{ metrics ? `${metrics.usage.totalTokens} Token · ${metrics.elapsedMillis} ms` : '暂无数据' }}
          </strong>
        </div>
      </div>
    </div>
  </header>
</template>
