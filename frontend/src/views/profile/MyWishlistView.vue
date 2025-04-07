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
    // First get user's favorites
    const favResponse = await axios.get(`api/favorites/user/${userId}`)
    const favorites = favResponse.data

    // Then get marketplace items
    const marketPlaceResponse = await axios.get(`api/marketplace/items`)
    const allItems = marketPlaceResponse.data

    // Filter items to only include those that exist in favorites
    items.value = allItems.filter((item: Product) =>
      favorites.some((fav: any) => fav.itemId === item.id)
    )
  } catch (error) {
    console.error('Failed to fetch wishlist:', error)
    items.value = [] // fallback to empty list
  }
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
