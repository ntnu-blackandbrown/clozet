<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import axios from '@/api/axios.ts'
import type { Product, ProductDisplay } from '@/types/product'
import ProductList from '@/components/product/ProductList.vue'
import { useRoute } from 'vue-router'

const props = defineProps({
  searchQuery: {
    type: String,
    default: ''
  }
})

const route = useRoute()
const items = ref<Product[]>([])
const detailedItems = ref<Map<number, ProductDisplay>>(new Map())
const initialProductId = ref<number | null>(null)
const isLoadingDetails = ref(false)

// Fetch detailed item information for a specific item
const fetchItemDetails = async (itemId: number) => {
  try {
    const response = await axios.get(`api/items/${itemId}`)
    const detailedItem = response.data
    detailedItems.value.set(itemId, detailedItem)
    return detailedItem
  } catch (error) {
    console.error(`Failed to fetch details for item ${itemId}:`, error)
    return null
  }
}

onMounted(async () => {
  try {
    const response = await axios.get('api/marketplace/items')
    items.value = response.data

    // Check if there's a product ID in the URL
    if (route.params.id) {
      const productId = parseInt(route.params.id as string)
      if (!isNaN(productId)) {
        initialProductId.value = productId
        // Fetch details for the initial product
        await fetchItemDetails(productId)
      }
    }
  } catch (error) {
    console.error('Failed to fetch items:', error)
    items.value = [] // fallback to empty list
  }
})

// Watch for changes in search query - fetch details if needed
watch(
  () => props.searchQuery,
  async (newQuery) => {
    // If search query is substantial, fetch details for all visible items
    if (newQuery && newQuery.trim().length > 2 && !isLoadingDetails.value) {
      isLoadingDetails.value = true

      // Fetch details for all items that don't already have details
      const itemsNeedingDetails = items.value.filter(item => !detailedItems.value.has(item.id))

      // Limit to first 10 items to avoid overloading the server
      const itemsToFetch = itemsNeedingDetails.slice(0, 10)

      if (itemsToFetch.length > 0) {
        await Promise.all(itemsToFetch.map(item => fetchItemDetails(item.id)))
      }

      isLoadingDetails.value = false
    }
  }
)

// Filter items based on search query
const filteredItems = computed(() => {
  if (!props.searchQuery.trim()) {
    return items.value
  }

  const query = props.searchQuery.toLowerCase().trim()

  return items.value.filter(item => {
    // Basic search on all items using the fields always available
    const basicMatch =
      item.title?.toLowerCase().includes(query) ||
      item.category?.toLowerCase().includes(query) ||
      item.location?.toLowerCase().includes(query);

    if (basicMatch) return true;

    // Advanced search on items that have detailed information
    const detailedItem = detailedItems.value.get(item.id);
    if (detailedItem) {
      return (
        detailedItem.brand?.toLowerCase().includes(query) ||
        detailedItem.color?.toLowerCase().includes(query) ||
        detailedItem.condition?.toLowerCase().includes(query) ||
        detailedItem.size?.toLowerCase().includes(query) ||
        detailedItem.longDescription?.toLowerCase().includes(query) ||
        detailedItem.sellerName?.toLowerCase().includes(query) ||
        detailedItem.shippingOptionName?.toLowerCase().includes(query)
      );
    }

    return false;
  });
})

// Watch for changes in the route to handle product ID
watch(
  () => route.params.id,
  async (newId) => {
    if (newId) {
      const productId = parseInt(newId as string)
      if (!isNaN(productId)) {
        initialProductId.value = productId
        // Fetch details for the new product
        if (!detailedItems.value.has(productId)) {
          await fetchItemDetails(productId)
        }
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
    <div v-if="isLoadingDetails" class="loading-indicator">
      Loading additional product details...
    </div>
    <ProductList :items="filteredItems" :initial-product-id="initialProductId" />
  </div>
</template>

<style scoped>
.loading-indicator {
  text-align: center;
  color: #666;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}
</style>
