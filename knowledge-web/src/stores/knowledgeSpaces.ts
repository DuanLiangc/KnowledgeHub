import { computed, shallowRef } from 'vue'
import { defineStore } from 'pinia'
import {
  createKnowledgeSpace,
  deleteKnowledgeSpace,
  listKnowledgeSpaces,
  updateKnowledgeSpace,
} from '../api/knowledgeSpaces'
import type { KnowledgeSpace, KnowledgeSpaceInput } from '../types/api'

export const useKnowledgeSpacesStore = defineStore('knowledge-spaces', () => {
  const spaces = shallowRef<KnowledgeSpace[]>([])
  const selectedId = shallowRef<string | null>(null)
  const loading = shallowRef(false)

  const selectedSpace = computed(() =>
    spaces.value.find((space) => space.id === selectedId.value) ?? null,
  )

  async function load(): Promise<void> {
    loading.value = true
    try {
      spaces.value = await listKnowledgeSpaces()
      if (!selectedId.value || !spaces.value.some((space) => space.id === selectedId.value)) {
        selectedId.value = spaces.value[0]?.id ?? null
      }
    } finally {
      loading.value = false
    }
  }

  async function create(input: KnowledgeSpaceInput): Promise<void> {
    const created = await createKnowledgeSpace(input)
    spaces.value = [...spaces.value, created]
    selectedId.value = created.id
  }

  async function update(id: string, input: KnowledgeSpaceInput): Promise<void> {
    const updated = await updateKnowledgeSpace(id, input)
    spaces.value = spaces.value.map((space) => space.id === id ? updated : space)
  }

  async function remove(id: string): Promise<void> {
    await deleteKnowledgeSpace(id)
    spaces.value = spaces.value.filter((space) => space.id !== id)
    if (selectedId.value === id) {
      selectedId.value = spaces.value[0]?.id ?? null
    }
  }

  return { spaces, selectedId, selectedSpace, loading, load, create, update, remove }
})
