<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from '@/api/axios.ts'
import type { Product } from '@/types/product'
import ProductList from '@/components/product/ProductList.vue'
import { useAuthStore } from '@/stores/AuthStore'
import { useRoute } from 'vue-router'

const items = ref<Product[]>([])
const authStore = useAuthStore()
const route = useRoute()
const initialProductId = ref<number | null>(null)

onMounted(async () => {
  try {
    const response = await axios.get(`api/items/seller/${authStore.user?.id}`)
    items.value = response.data

    // Check if there's a product ID in the URL
    if (route.params.id) {
      initialProductId.value = parseInt(route.params.id as string)
    }
  } catch (error) {
    console.error('Failed to fetch items:', error)
    items.value = [] // fallback to empty list
  }
})
</script>

<template>
  <div>
    <h1>My posts</h1>
    <ProductList :items="items" route-base-path="/profile/posts/" :initial-product-id="initialProductId" />
  </div>
</template>
