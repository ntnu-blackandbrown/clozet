<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import axios from '@/api/axios.ts'
import type { Product } from '@/types/product'
import ProductList from '@/components/product/ProductList.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const items = ref<Product[]>([])
const initialProductId = ref<number | null>(null)

onMounted(async () => {
  try {
    const response = await axios.get('api/marketplace/items')
    items.value = response.data

    // Check if there's a product ID in the URL
    if (route.params.id) {
      const productId = parseInt(route.params.id as string)
      if (!isNaN(productId)) {
        initialProductId.value = productId
      }
    }
  } catch (error) {
    console.error('Failed to fetch items:', error)
    items.value = [] // fallback to empty list
  }
})

// Watch for changes in the route to handle product ID
watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      const productId = parseInt(newId as string)
      if (!isNaN(productId)) {
        initialProductId.value = productId
      }
    } else {
      initialProductId.value = null
    }
  },
  { immediate: true },
)
</script>

<template>
  <div>
    <h1>Browse products</h1>
    <ProductList :items="items" :initial-product-id="initialProductId" />
  </div>
</template>
