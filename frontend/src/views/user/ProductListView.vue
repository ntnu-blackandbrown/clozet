<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import type { Product, ProductDisplay } from '@/types/product'
import ProductList from '@/components/product/ProductList.vue'
import { useRoute, useRouter } from 'vue-router'
import { ProductService } from '@/api/services/ProductService'
const props = defineProps({
  searchQuery: {
    type: String,
    default: '',
  },
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

// Pagination state
const currentPage = ref(1)
const itemsPerPage = ref(12) // Adjust as needed

// Unique filter options
const locations = computed(() => {
  const uniqueLocations = new Set(items.value.map((item) => item.location).filter(Boolean))
  return Array.from(uniqueLocations)
})

const categories = computed(() => {
  const uniqueCategories = new Set(items.value.map((item) => item.category).filter(Boolean))
  return Array.from(uniqueCategories)
})

const shippingOptions = computed(() => {
  const uniqueOptions = new Set(
    Array.from(detailedItems.value.values())
      .map((item) => item.shippingOptionName)
      .filter(Boolean),
  )
  return Array.from(uniqueOptions)
})

// Fetch detailed item information for a specific item
const fetchItemDetails = async (itemId: number) => {
  try {
    const response = await ProductService.getItemById(itemId)
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
  const itemsNeedingDetails = items.value.filter((item) => !detailedItems.value.has(item.id))
  await Promise.all(itemsNeedingDetails.map((item) => fetchItemDetails(item.id)))
  isLoadingDetails.value = false
}

onMounted(async () => {
  try {
    const response = await ProductService.getAllItems()
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
    // Set current page from query params if available
    if (route.query.page) {
      const page = parseInt(route.query.page as string);
      if (!isNaN(page) && page > 0) {
        currentPage.value = page;
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
  async (newQuery, oldQuery) => {
    // Reset to page 1 when search query changes
    if (newQuery !== oldQuery) {
        currentPage.value = 1;
    }

    // If search query is substantial, fetch details for all visible items
    if (newQuery && newQuery.trim().length > 2 && !isLoadingDetails.value) {
      isLoadingDetails.value = true

      // Fetch details for all items that don't already have details
      const itemsNeedingDetails = items.value.filter((item) => !detailedItems.value.has(item.id))

      // Limit to first 10 items to avoid overloading the server
      const itemsToFetch = itemsNeedingDetails.slice(0, 10)

      if (itemsToFetch.length > 0) {
        await Promise.all(itemsToFetch.map((item) => fetchItemDetails(item.id)))
      }

      isLoadingDetails.value = false
    }
  },
)

// Updated filtering logic
const filteredItems = computed(() => {
  let filtered = items.value

  // Apply search query filter
  if (props.searchQuery.trim()) {
    const query = props.searchQuery.toLowerCase().trim()
    filtered = filtered.filter((item) => {
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
    filtered = filtered.filter((item) => item.location === selectedLocation.value)
  }

  // Apply category filter
  if (selectedCategory.value) {
    filtered = filtered.filter((item) => item.category === selectedCategory.value)
  }

  // Apply shipping option filter
  if (selectedShippingOption.value) {
    filtered = filtered.filter((item) => {
      const detailedItem = detailedItems.value.get(item.id)
      return detailedItem?.shippingOptionName === selectedShippingOption.value
    })
  }

  return filtered
})

// Compute total pages
const totalPages = computed(() => {
  return Math.ceil(filteredItems.value.length / itemsPerPage.value)
})

// Compute paginated items
const paginatedItems = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage.value
  const end = start + itemsPerPage.value
  return filteredItems.value.slice(start, end)
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
    let filtersChanged = false;
    if (newQuery.category && newQuery.category !== selectedCategory.value) {
        selectedCategory.value = newQuery.category as string;
        filtersChanged = true;
    } else if (!newQuery.category && selectedCategory.value) {
        selectedCategory.value = '';
        filtersChanged = true;
    }

    if (newQuery.location && newQuery.location !== selectedLocation.value) {
        selectedLocation.value = newQuery.location as string;
        filtersChanged = true;
    } else if (!newQuery.location && selectedLocation.value) {
        selectedLocation.value = '';
        filtersChanged = true;
    }

    if (newQuery.shipping && newQuery.shipping !== selectedShippingOption.value) {
        selectedShippingOption.value = newQuery.shipping as string;
        filtersChanged = true;
    } else if (!newQuery.shipping && selectedShippingOption.value) {
        selectedShippingOption.value = '';
        filtersChanged = true;
    }

    // Update page based on query parameter
    const pageQuery = newQuery.page ? parseInt(newQuery.page as string) : 1;
    if (!isNaN(pageQuery) && pageQuery > 0 && pageQuery !== currentPage.value) {
      currentPage.value = pageQuery;
    } else if (!newQuery.page && currentPage.value !== 1) {
        // If page query is removed, reset to 1 unless it was already 1
        currentPage.value = 1;
    }

    // If filters changed, reset page to 1 (unless page is explicitly set in the same query update)
    // This check might need refinement depending on desired behavior when filters and page change simultaneously
    if (filtersChanged && (!newQuery.page || pageQuery === 1)) {
      // Only reset if page wasn't explicitly set to something other than 1
      if (currentPage.value !== 1) {
         // Update URL to reflect page reset due to filter change
         updateUrlQuery();
      }
      currentPage.value = 1;
    }

  },
  { immediate: true, deep: true }, // Use deep watch for query object
)

// Function to update URL query parameters
const updateUrlQuery = () => {
    const query: Record<string, string> = {}

    if (selectedCategory.value) query.category = selectedCategory.value
    if (selectedLocation.value) query.location = selectedLocation.value
    if (selectedShippingOption.value) query.shipping = selectedShippingOption.value
    if (currentPage.value > 1) query.page = String(currentPage.value) // Add page to query only if > 1

    // Use router.replace to avoid adding history entries for filter/page changes
    router.replace({ query: Object.keys(query).length > 0 ? query : {} })
}

// Update URL when filters or page change
watch(
  [selectedCategory, selectedLocation, selectedShippingOption, currentPage],
  () => {
      updateUrlQuery();
  },
  { deep: true },
)

// Watch for shipping filter changes
watch(selectedShippingOption, async (newShippingOption) => {
  if (newShippingOption) {
    await fetchAllItemDetails()
  }
})

// Reset filters function
const resetFilters = () => {
  selectedLocation.value = ''
  selectedShippingOption.value = ''
  selectedCategory.value = ''
  currentPage.value = 1 // Reset page on filter reset
  // updateUrlQuery will be called by the watcher
}

// Pagination methods
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const goToPage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
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

      <button class="reset-button" @click="resetFilters">Reset filters</button>
    </div>

    <div v-if="isLoadingDetails" class="loading-indicator">
      Loading additional product details...
    </div>

    <!-- Product List -->
    <ProductList :items="paginatedItems" :initial-product-id="initialProductId" />

    <!-- Pagination Controls -->
     <div v-if="totalPages > 1" class="pagination-controls">
      <button @click="prevPage" :disabled="currentPage === 1" class="page-button">
        Previous
      </button>
      <span class="page-info">Page {{ currentPage }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages" class="page-button">
        Next
      </button>

       <!-- Optional: Page number buttons -->
       <!--
       <div class="page-numbers">
         <button
           v-for="page in totalPages"
           :key="page"
           @click="goToPage(page)"
           :class="{ 'page-button': true, 'active': currentPage === page }"
           :disabled="currentPage === page"
         >
           {{ page }}
         </button>
       </div>
       -->
    </div>
     <div v-else-if="filteredItems.length === 0 && !isLoadingDetails" class="no-results">
        No products found matching your criteria.
     </div>
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
  margin-left: auto;
}

.reset-button:hover {
  background-color: #d0d0d0;
}

/* Pagination Styles */
.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  margin-bottom: 1rem;
  gap: 0.5rem;
}

.page-button {
  padding: 0.5rem 1rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.page-button:hover:not(:disabled) {
  background-color: #45a049;
}

.page-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  opacity: 0.7;
}

.page-info {
  margin: 0 0.5rem;
  color: #555;
  font-weight: 500;
}

.page-numbers {
    margin-top: 0.5rem;
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 0.25rem;
}

.page-numbers .page-button {
    min-width: 30px;
    background-color: #f0f0f0;
    color: #333;
}
.page-numbers .page-button:hover:not(:disabled) {
     background-color: #e0e0e0;
}

.page-numbers .page-button.active {
    background-color: #4CAF50;
    color: white;
    font-weight: bold;
    cursor: default;
}
.page-numbers .page-button:disabled:not(.active) {
    background-color: #f0f0f0;
    opacity: 0.5;
}

.no-results {
    text-align: center;
    color: #888;
    margin-top: 2rem;
    font-style: italic;
}

/* Responsive adjustments */
@media (max-width: 600px) {
    .reset-button {
        margin-left: 0;
        align-self: center;
        margin-top: 1rem;
    }

    .pagination-controls {
        flex-wrap: wrap;
        justify-content: center;
        gap: 0.75rem;
    }
     .page-info {
         width: 100%;
         text-align: center;
         margin: 0.5rem 0;
     }
}
</style>
