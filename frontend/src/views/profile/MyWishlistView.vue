<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from '@/api/axios.ts'
import ProductList from '@/components/product/ProductList.vue'
import type { Product } from '@/types/product'
import { useAuthStore } from '@/stores/AuthStore'

const authStore = useAuthStore()
const items = ref<Product[]>([])
const userId = authStore.user?.id

onMounted(async () => {
  try {
    const response = await axios.get(`api/favorites/user/${userId}`)
    items.value = response.data
  } catch (error) {
    console.error('Failed to fetch wishlist:', error)
    items.value = [] // fallback to empty list
  }
  console.log(items.value)
})
</script>

<template>
  <div class="my-wishlist-container">
    <h2>My Wishlist</h2>
    <ProductList :items="items" />
  </div>
</template>

<style scoped>
.my-wishlist-container {
  padding: 2rem;
}

.my-wishlist-container h2 {
  margin-bottom: 2rem;
  color: #333;
  font-size: 1.5rem;
}

@media (max-width: 768px) {
  .my-wishlist-container {
    padding: 1rem;
  }
}
</style>
