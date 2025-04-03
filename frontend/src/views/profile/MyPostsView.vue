<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from '@/api/axios.ts'
import type { Product } from '@/types/product'

const items = ref<Product[]>([])

const props = defineProps<{
  userId: number
}>()

onMounted(async () => {
  try {
    const response = await axios.get(`api/items/seller/${props.userId}`)
    items.value = response.data
  } catch (error) {
    console.error('Failed to fetch items:', error)
    items.value = [] // fallback to empty list
  }
})
</script>

<template>
  <div>
    <h1>My posts</h1>
    <ProductList :items="items" />
  </div>
</template>
