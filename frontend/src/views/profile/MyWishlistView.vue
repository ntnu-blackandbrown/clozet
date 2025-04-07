<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from '@/api/axios.ts'
import ProductList from '@/components/product/ProductList.vue'
import type { Product } from '@/types/product'
import { useAuthStore } from '@/stores/AuthStore'
import { useRoute } from 'vue-router'

const authStore = useAuthStore()
const items = ref<Product[]>([])
const userId = authStore.user?.id
const route = useRoute()
const initialProductId = ref<number | null>(null)

// Fetch image URL for a single item
const fetchImageForItem = async (item: any): Promise<Product> => {
  try {
    const imagesResponse = await axios.get(`api/images/item/${item.id}`)
    const images = imagesResponse.data

    return {
      ...item,
      // Set the image URL to the first image if available, otherwise null
      image: images && images.length > 0 ? images[0].imageUrl : '/default-product-image.jpg'
    }
  } catch (error) {
    console.error(`Failed to fetch images for item ${item.id}:`, error)
    return {
      ...item,
      image: '/default-product-image.jpg'
    }
  }
}

onMounted(async () => {
  try {
    // First get user's favorites
    const favResponse = await axios.get(`api/favorites/user/${userId}`)
    const favorites = favResponse.data

    // Then get marketplace items
    const marketPlaceResponse = await axios.get(`api/marketplace/items`)
    const allItems = marketPlaceResponse.data

    // Filter items to only include those that exist in favorites
    const filteredItems = allItems.filter((item: Product) =>
      favorites.some((fav: any) => fav.itemId === item.id)
    )

    // Fetch images for filtered items
    const itemsWithImages = await Promise.all(
      filteredItems.map((item: any) => fetchImageForItem(item))
    )

    items.value = itemsWithImages

    // Check if there's a product ID in the URL
    if (route.params.id) {
      initialProductId.value = parseInt(route.params.id as string)
    }
  } catch (error) {
    console.error('Failed to fetch wishlist:', error)
    items.value = [] // fallback to empty list
  }
})
</script>

<template>
  <div class="my-wishlist-container">
    <h2>My Wishlist</h2>
    <ProductList :items="items" route-base-path="/profile/wishlist/" :initial-product-id="initialProductId" />
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
