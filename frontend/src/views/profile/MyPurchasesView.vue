<script setup lang="ts">
import { ref, onMounted } from 'vue'
import ProductList from '@/components/product/ProductList.vue'
import type { Product } from '@/types/product'
import { useAuthStore } from '@/stores/AuthStore'
import { ProductService } from '@/api/services/ProductService'
import { TransactionService } from '@/api/services/TransactionService'
const authStore = useAuthStore()

const items = ref<Product[]>([])
const loading = ref(true)

const fetchImageForItem = async (item: any): Promise<Product> => {
  try {
    const imagesResponse = await ProductService.getItemImages(item.id)
    const images = imagesResponse.data

    return {
      ...item,
      image: images && images.length > 0 ? images[0].imageUrl : '/default-product-image.jpg',
      isAvailable: false, // Set isAvailable to false since these are purchased items
    }
  } catch (error) {
    console.error(`Failed to fetch images for item ${item.id}:`, error)
    return {
      ...item,
      image: '/default-product-image.jpg',
      isAvailable: false, // Set isAvailable to false since these are purchased items
    }
  }
}

const fetchItemById = async (itemId: number): Promise<Product | null> => {
  try {
    // Get item details directly from the items API
    const response = await ProductService.getItemById(itemId)
    const item = response.data

    return {
      id: item.id,
      title: item.title || 'Unknown Item',
      price: item.price || 0,
      category: item.category?.name || 'Unknown',
      location: item.location?.name || 'Unknown',
      vippsPaymentEnabled: item.vippsPaymentEnabled || false,
      wishlisted: false, // We don't have this info for purchased items
      image: '/default-product-image.jpg', // Will be populated by fetchImageForItem
      isAvailable: false, // Set isAvailable to false since these are purchased items
    }
  } catch (error) {
    console.error(`Failed to fetch item ${itemId}:`, error)
    // Create a placeholder item with minimal data
    return {
      id: itemId,
      title: `Purchased Item #${itemId}`,
      price: 0,
      category: 'Unknown',
      location: 'Unknown',
      vippsPaymentEnabled: false,
      wishlisted: false,
      image: '/default-product-image.jpg',
      isAvailable: false, // Set isAvailable to false since these are purchased items
    }
  }
}

onMounted(async () => {
  try {
    loading.value = true

    // Fetch user's transactions
    const response = await TransactionService.getBuyerTransactions(authStore.user?.id as number)
    const purchasedTransactions = response.data
    console.log('Purchased Transactions:', purchasedTransactions)
    console.log('Transactions count:', purchasedTransactions.length)

    if (!purchasedTransactions || purchasedTransactions.length === 0) {
      items.value = []
      loading.value = false
      return
    }

    // Get unique item IDs to avoid duplicates
    const uniqueItemIds = [
      ...new Set(purchasedTransactions.map((transaction: any) => transaction.itemId)),
    ]
    console.log('Unique Item IDs:', uniqueItemIds)

    // Fetch each item individually
    const fetchPromises = uniqueItemIds.map((itemId) => fetchItemById(Number(itemId)))
    const fetchedItems = await Promise.all(fetchPromises)

    // Filter out any null items (failed fetches) and ensure we have valid items
    const validItems = fetchedItems.filter(Boolean) as Product[]
    console.log('Valid items found:', validItems.length)

    // Fetch images for all valid items
    const itemsWithImages = await Promise.all(
      validItems.map((item: Product) => fetchImageForItem(item)),
    )

    console.log('Final items with images:', itemsWithImages.length)
    items.value = itemsWithImages
  } catch (error) {
    console.error('Failed to fetch purchases:', error)
    items.value = [] // fallback to empty list
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="my-purchases-container">
    <h2>My Purchases</h2>
    <div v-if="loading" class="loading">Loading your purchases...</div>
    <div v-else-if="items.length === 0" class="no-items">You have no purchases yet</div>
    <ProductList v-else :items="items" />
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

.loading,
.no-items {
  text-align: center;
  padding: 2rem;
  color: #666;
  font-size: 1.1rem;
}

@media (max-width: 768px) {
  .my-purchases-container {
    padding: 1rem;
  }
}
</style>
