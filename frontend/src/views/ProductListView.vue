<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import axios from '@/api/axios.ts'
import type { Product, ProductDisplay } from '@/types/product'
import ProductList from '@/components/product/ProductList.vue'
import { useRoute, useRouter } from 'vue-router'

const props = defineProps({
  searchQuery: {
    type: String,
    default: ''
  }
})

const route = useRoute()
const router = useRouter()
const items = ref<Product[]>([])
const detailedItems = ref<Map<number, ProductDisplay>>(new Map())
const initialProductId = ref<number | null>(null)
const isLoadingDetails = ref(false)

// Filter states
const selectedLocation = ref<string>('')
const selectedShippingOption = ref<string>('')
const selectedCategory = ref<string>('')

// Unique filter options
const locations = computed(() => {
  const uniqueLocations = new Set(items.value.map(item => item.location).filter(Boolean))
  return Array.from(uniqueLocations)
})

const categories = computed(() => {
  const uniqueCategories = new Set(items.value.map(item => item.category).filter(Boolean))
  return Array.from(uniqueCategories)
})

const shippingOptions = computed(() => {
  const uniqueOptions = new Set(
    Array.from(detailedItems.value.values())
      .map(item => item.shippingOptionName)
      .filter(Boolean)
  )
  return Array.from(uniqueOptions)
})

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

// Function to fetch details for all items
const fetchAllItemDetails = async () => {
  isLoadingDetails.value = true
  const itemsNeedingDetails = items.value.filter(item => !detailedItems.value.has(item.id))
  await Promise.all(itemsNeedingDetails.map(item => fetchItemDetails(item.id)))
  isLoadingDetails.value = false
}

onMounted(async () => {
  try {
    const response = await axios.get('api/marketplace/items')
    items.value = response.data

    // Fetch details for all items to populate shipping options
    await fetchAllItemDetails()

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

// Updated filtering logic
const filteredItems = computed(() => {
  let filtered = items.value

  // Apply search query filter
  if (props.searchQuery.trim()) {
    const query = props.searchQuery.toLowerCase().trim()
    filtered = filtered.filter(item => {
      const basicMatch =
        item.title?.toLowerCase().includes(query) ||
        item.category?.toLowerCase().includes(query) ||
        item.location?.toLowerCase().includes(query)

      if (basicMatch) return true

      const detailedItem = detailedItems.value.get(item.id)
      if (detailedItem) {
        return (
          detailedItem.brand?.toLowerCase().includes(query) ||
          detailedItem.color?.toLowerCase().includes(query) ||
          detailedItem.condition?.toLowerCase().includes(query) ||
          detailedItem.size?.toLowerCase().includes(query) ||
          detailedItem.longDescription?.toLowerCase().includes(query) ||
          detailedItem.sellerName?.toLowerCase().includes(query) ||
          detailedItem.shippingOptionName?.toLowerCase().includes(query)
        )
      }
      return false
    })
  }

  // Apply location filter
  if (selectedLocation.value) {
    filtered = filtered.filter(item => item.location === selectedLocation.value)
  }

  // Apply category filter
  if (selectedCategory.value) {
    filtered = filtered.filter(item => item.category === selectedCategory.value)
  }

  // Apply shipping option filter
  if (selectedShippingOption.value) {
    filtered = filtered.filter(item => {
      const detailedItem = detailedItems.value.get(item.id)
      return detailedItem?.shippingOptionName === selectedShippingOption.value
    })
  }

  return filtered
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

// Watch for query parameter changes
watch(
  () => route.query,
  (newQuery) => {
    // Update filters based on query parameters
    if (newQuery.category) {
      selectedCategory.value = newQuery.category as string
    }
    if (newQuery.location) {
      selectedLocation.value = newQuery.location as string
    }
    if (newQuery.shipping) {
      selectedShippingOption.value = newQuery.shipping as string
    }
  },
  { immediate: true }
)

// Update URL when filters change
watch([selectedCategory, selectedLocation, selectedShippingOption], ([category, location, shipping]) => {
  const query: Record<string, string> = {}

  if (category) query.category = category
  if (location) query.location = location
  if (shipping) query.shipping = shipping

  // Replace the current URL with the new query parameters
  router.replace({ query })
}, { deep: true })

// Watch for shipping filter changes
watch(
  selectedShippingOption,
  async (newShippingOption) => {
    if (newShippingOption) {
      await fetchAllItemDetails()
    }
  }
)

// Reset filters function
const resetFilters = () => {
  selectedLocation.value = ''
  selectedShippingOption.value = ''
  selectedCategory.value = ''
  // Clear URL query parameters
  router.replace({ query: {} })
}
</script>

<template>
  <div>
    <h1>Browse products</h1>

    <!-- Filters section -->
    <div class="filters-container">
      <div class="filter-group">
        <label for="location">Location:</label>
        <select id="location" v-model="selectedLocation">
          <option value="">All locations</option>
          <option v-for="location in locations" :key="location" :value="location">
            {{ location }}
          </option>
        </select>
      </div>

      <div class="filter-group">
        <label for="category">Category:</label>
        <select id="category" v-model="selectedCategory">
          <option value="">All categories</option>
          <option v-for="category in categories" :key="category" :value="category">
            {{ category }}
          </option>
        </select>
      </div>

      <div class="filter-group">
        <label for="shipping">Shipping option:</label>
        <select id="shipping" v-model="selectedShippingOption">
          <option value="">All shipping options</option>
          <option v-for="option in shippingOptions" :key="option" :value="option">
            {{ option }}
          </option>
        </select>
      </div>

      <button class="reset-button" @click="resetFilters">
        Reset filters
      </button>
    </div>

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

.filters-container {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 2rem;
  padding: 1rem;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.filter-group label {
  font-weight: 500;
  color: #333;
}

.filter-group select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  min-width: 200px;
  background-color: white;
}

.reset-button {
  padding: 0.5rem 1rem;
  background-color: #e0e0e0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
  align-self: flex-end;
}

.reset-button:hover {
  background-color: #d0d0d0;
}
</style>
