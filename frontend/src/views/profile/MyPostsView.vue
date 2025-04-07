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

// Fetch image URL for a single item
const fetchImageForItem = async (item: any): Promise<Product> => {
  try {
    const imagesResponse = await axios.get(`api/images/item/${item.id}`)
    const images = imagesResponse.data

    return {
      ...item,
      // Set the image URL to the first image if available, otherwise null
      image: images && images.length > 0 ? images[0].imageUrl : '/default-product-image.jpg',
      isAvailable: item.available // Map from backend's 'available' to frontend's 'isAvailable'
    }
  } catch (error) {
    console.error(`Failed to fetch images for item ${item.id}:`, error)
    return {
      ...item,
      image: '/default-product-image.jpg',
      isAvailable: item.available
    }
  }
}

onMounted(async () => {
  try {
    const response = await axios.get(`api/items/seller/${authStore.user?.id}`)
    const fetchedItems = response.data
    console.log('Fetched items from backend:', fetchedItems)

    // Fetch images for all items
    const itemsWithImages = await Promise.all(
      fetchedItems.map((item: any) => fetchImageForItem(item))
    )
    console.log('Items with images and availability:', itemsWithImages)

    items.value = itemsWithImages

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
