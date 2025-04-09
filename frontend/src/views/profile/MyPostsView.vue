<script setup lang="ts">
import { ref, onMounted } from 'vue'
import type { Product } from '@/types/product'
import ProductList from '@/components/product/ProductList.vue'
import { useAuthStore } from '@/stores/AuthStore'
import { useRoute } from 'vue-router'
import { ProductService } from '@/api/services/ProductService'
const items = ref<Product[]>([])
const authStore = useAuthStore()
const route = useRoute()
const initialProductId = ref<number | null>(null)

// Fetch image URL for a single item
const fetchImageForItem = async (item: any): Promise<Product> => {
  try {
    const imagesResponse = await ProductService.getItemImages(item.id)
    const images = imagesResponse.data

    return {
      ...item,
      // Set the image URL to the first image if available, otherwise null
      image: images && images.length > 0 ? images[0].imageUrl : '/default-product-image.jpg',
      isAvailable: item.available, // Map from backend's 'available' to frontend's 'isAvailable'
    }
  } catch (error) {
    console.error(`Failed to fetch images for item ${item.id}:`, error)
    return {
      ...item,
      image: '/default-product-image.jpg',
      isAvailable: item.available,
    }
  }
}

onMounted(async () => {
  try {
    const response = await ProductService.getItemsBySeller(authStore.user?.id as number)
    const fetchedItems = response.data
     ('Fetched items from backend:', fetchedItems)

    // Fetch images for all items
    const itemsWithImages = await Promise.all(
      fetchedItems.map((item: any) => fetchImageForItem(item)),
    )
     ('Items with images and availability:', itemsWithImages)

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
    <h1 id="my-posts-title">My posts</h1>
    <div v-if="items.length === 0" role="status" aria-live="polite" class="empty-state">
      You have no posts yet
    </div>
    <ProductList
      v-else
      :items="items"
      route-base-path="/profile/posts/"
      :initial-product-id="initialProductId"
      aria-labelledby="my-posts-title"
    />
  </div>
</template>
