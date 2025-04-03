<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from '@/api/axios.ts'
import ProductList from '@/components/product/ProductList.vue'
import type { Product } from '@/types/product'

const items = ref<Product[]>([])

onMounted(async () => {
  try {
    const response = await axios.get('api/items/purchases')
    items.value = response.data
  } catch (error) {
    console.error('Failed to fetch purchases:', error)
    items.value = [] // fallback to empty list
  }
})
</script>

<template>
  <div class="my-purchases-container">
    <h2>My Purchases</h2>
    <ProductList :items="items" />
  </div>
</template>

<style scoped>
.my-purchases-container {
  padding: 2rem;
}

.my-purchases-container h2 {
  margin-bottom: 2rem;
  color: #333;
  font-size: 1.5rem;
}

@media (max-width: 768px) {
  .my-purchases-container {
    padding: 1rem;
  }
}
</style>
