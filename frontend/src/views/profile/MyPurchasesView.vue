<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from '@/api/axios.ts'
import ProductList from '@/components/product/ProductList.vue'
import type { Product } from '@/types/product'
import { useAuthStore } from '@/stores/AuthStore'

const authStore = useAuthStore()

const items = ref<Product[]>([])

const fetchImageForItem = async (item: any): Promise<Product> => {
  try {
    const imagesResponse = await axios.get(`api/images/item/${item.id}`)
    const images = imagesResponse.data

    return {
      ...item,
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
    const response = await axios.get(`api/transactions/${authStore.user?.id}`)
    const purchasedItems = response.data

    // Fetch additional details from the marketplace if necessary
    const marketPlaceResponse = await axios.get('api/marketplace/items')
    const allItems = marketPlaceResponse.data

    const completeItems = purchasedItems.map((purchase: any) => {
      const item = allItems.find((marketItem: Product) => marketItem.id === purchase.itemId)
      return item ? { ...purchase, ...item } : purchase
    })

    // Fetch images for complete items
    const itemsWithImages = await Promise.all(
      completeItems.map((item: any) => fetchImageForItem(item))
    )

    items.value = itemsWithImages
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
