<script setup>
import Badge from '@/components/utils/Badge.vue'
import { useRouter, useRoute } from 'vue-router'
import ProductListView from '@/views/user/ProductListView.vue'
import { useAuthStore } from '@/stores/AuthStore'
import LoginRegisterModal from '@/views/LoginRegisterView.vue'
import { ref, onMounted, watch, computed, nextTick } from 'vue'
import { CategoryService } from '@/api/services/CategoryService'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const showLoginModal = ref(false)
const initialAuthMode = ref('login') // Default to login mode
const searchQuery = ref('')
const debouncedSearchQuery = ref('')
const topCategories = ref([])
const isLoadingCategories = ref(false)
const categoryError = ref(null)
const productListSection = ref(null)

// Simple debounce for search input
let debounceTimer = null
const handleSearchInput = (e) => {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    debouncedSearchQuery.value = e.target.value
  }, 300)
}

// Check if we should show the login/register modal based on the route
onMounted(async () => {
  if (route.path === '/login' || route.path === '/register') {
    showLoginModal.value = true
    initialAuthMode.value = route.path === '/login' ? 'login' : 'register'
  }
  await fetchTopCategories()
})

// Watch for route changes to handle login/register routes
watch(
  () => route.path,
  (newPath) => {
    if (newPath === '/login' || newPath === '/register') {
      showLoginModal.value = true
      initialAuthMode.value = newPath === '/login' ? 'login' : 'register'
    } else {
      showLoginModal.value = false
    }
  },
)

const handleCreatePost = () => {
  if (authStore.isLoggedIn) {
    router.push('/create-product')
  } else {
    showLoginModal.value = true
    initialAuthMode.value = 'login'
    router.replace('/login')
  }
}

const handleCloseAuthModal = () => {
  showLoginModal.value = false
  router.replace('/')
}

// Watch for changes in the route to handle product ID
watch(
  () => route.params.id,
  (newId) => {
    if (newId) {
      // If there's a product ID in the URL, we'll handle it in the ProductListView
      // The ProductListView will show the modal for this product
    }
  },
  { immediate: true },
)

const clearSearch = () => {
  searchQuery.value = ''
  debouncedSearchQuery.value = ''
}

const fetchTopCategories = async () => {
  isLoadingCategories.value = true
  categoryError.value = null

  try {
    const response = await CategoryService.getTopCategories()
    topCategories.value = response.data
    console.log('Fetched top categories:', response.data)
  } catch (error) {
    console.error('Error fetching top categories:', error)
    categoryError.value = 'Failed to load categories'
    topCategories.value = []
  } finally {
    isLoadingCategories.value = false
  }
}

// Function to handle category badge click
const handleCategoryClick = async (categoryName) => {
  // Update the route first
  await router.push({ path: '/', query: { category: categoryName } })

  // Scroll to the product list section after the DOM updates
  await nextTick()
  if (productListSection.value) {
    productListSection.value.scrollIntoView({ behavior: 'smooth' })
  }
}

// Computed property to handle the case where no categories are returned
const displayCategories = computed(() => {
  if (topCategories.value && topCategories.value.length > 0) {
    return topCategories.value
  }
  // Fallback categories if none are returned from API
  return [
    { id: 1, name: 'Tops' },
    { id: 2, name: 'Bottoms' },
    { id: 3, name: 'Dresses' },
    { id: 4, name: 'Accessories' },
  ]
})
</script>

<template>
  <div class="home-container">
    <div class="content-section">
      <div class="hero-section">
        <h1>Welcome to Clozet!</h1>
        <h3>The new way to shop for clothes</h3>

        <div class="search-create-container">
          <div class="search-wrapper">
            <input
              class="search-bar"
              type="text"
              placeholder="Search for a product..."
              v-model="searchQuery"
              @input="handleSearchInput"
              aria-label="Search for products"
            />

            <button
              v-if="searchQuery"
              @click="clearSearch"
              class="clear-search-btn"
              aria-label="Clear search"
            >
              Clear
            </button>
            <!-- Inline SVG icon -->
            <svg
              class="search-icon"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              aria-hidden="true"
            >
              <circle cx="11" cy="11" r="8" />
              <line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
          </div>
          <div class="create-post-btn">
            <button @click="handleCreatePost" aria-label="Create a new post">Create a post!</button>
          </div>
        </div>
      </div>

      <div class="categories-section">
        <h4>
          Popular Categories
          <span v-if="isLoadingCategories" class="loading-indicator">(Loading...)</span>
        </h4>
        <div v-if="categoryError" class="error-message">{{ categoryError }}</div>
        <div class="badge-container">
          <Badge
            v-for="category in displayCategories"
            :key="category.id"
            type="category"
            :name="category.name"
            @click="handleCategoryClick(category.name)"
            :aria-label="`Browse ${category.name} category`"
          />
        </div>
      </div>
    </div>
    <div class="image-section">
      <img src="@/assets/images/homepage.png" alt="Clozet Homepage" class="homepage-image" />
    </div>
  </div>
  <div class="featured-section" ref="productListSection">
    <div class="featured-products">
      <ProductListView :search-query="debouncedSearchQuery" />
    </div>
  </div>
  <LoginRegisterModal
    v-if="showLoginModal"
    @close="handleCloseAuthModal"
    :customTitle="'Please login to create a post'"
    :initialMode="initialAuthMode"
  />
</template>

<style scoped>
.home-container {
  display: flex;
  gap: var(--spacing-xl);
  align-items: flex-start;
  max-width: 1400px;
  margin: 0 auto;
  padding: var(--spacing-xl);
}

.content-section {
  flex: 1;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  padding-top: var(--spacing-xl);
}

.image-section {
  flex: 1;
  position: sticky;
  top: var(--spacing-xl);
  display: flex;
  justify-content: center;
  align-items: center;
  max-width: 500px;
  padding-top: var(--spacing-xl);
  margin: 0 auto;
}

.homepage-image {
  width: 100%;
  height: auto;
  max-height: 600px;
  object-fit: contain;
  border-radius: var(--border-radius-lg);
  display: block;
  margin: 0 auto;
}

.hero-section {
  text-align: left;
  margin-bottom: var(--spacing-xl);
}

.search-create-container {
  display: flex;
  gap: var(--spacing-md);
  margin-top: var(--spacing-xl);
  align-items: center;
}

.categories-section {
  background-color: var(--color-white);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--box-shadow-light);
  text-align: center;
}

.categories-section h4 {
  color: var(--color-limed-spruce);
  font-size: 1.1rem;
  font-weight: 500;
  margin-bottom: var(--spacing-md);
  text-align: center;
}

.featured-section {
  padding: var(--spacing-lg) 0;
}

.featured-section h4 {
  color: var(--color-limed-spruce);
  font-size: 1.1rem;
  font-weight: 500;
  margin-bottom: var(--spacing-lg);
  padding: 0 var(--spacing-lg);
}

.featured-products {
  width: 100%;
}

h1 {
  color: var(--color-limed-spruce);
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: var(--spacing-sm);
  letter-spacing: -0.02em;
  line-height: 1.2;
}

h3 {
  color: var(--color-limed-spruce);
  font-size: 1.5rem;
  font-weight: 400;
  margin-bottom: var(--spacing-md);
  opacity: 0.8;
  line-height: 1.4;
}

.badge-container {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
  justify-content: center;
}

.search-wrapper {
  flex: 1;
  position: relative;
}

.search-bar {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-lg);
  padding-right: 40px;
  font-size: 1rem;
  border: 2px solid #2d353f;
  border-radius: var(--border-radius-lg);
  background-color: var(--color-white);
  transition: var(--transition-smooth);
  color: var(--color-limed-spruce);
}

.search-bar:focus {
  outline: none;
  border-color: #2d353f;
  box-shadow: 0 0 0 3px rgba(45, 53, 63, 0.2);
}

.search-bar::placeholder {
  color: #9ca3af;
}

.search-icon {
  position: absolute;
  right: var(--spacing-md);
  top: 50%;
  width: 20px;
  height: 20px;
  transform: translateY(-50%);
  pointer-events: none;
  stroke: #2d353f;
  transition: var(--transition-smooth);
}

.create-post-btn {
  display: inline-block;
  vertical-align: middle;
}

.create-post-btn button {
  background-color: #2d353f;
  color: var(--color-white);
  border: none;
  padding: var(--spacing-md) var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition-bounce);
  box-shadow: var(--box-shadow-light);
  white-space: nowrap;
}

.create-post-btn button:hover {
  background-color: #343d48;
  transform: translateY(-2px);
  box-shadow: var(--box-shadow-medium);
}

.create-post-btn button:active {
  transform: translateY(0);
  background-color: #262d36;
  box-shadow: var(--box-shadow-light);
}

.clear-search-btn {
  background: none;
  border: none;
  color: #2d353f;
  cursor: pointer;
  font-size: 0.9rem;
  margin-left: 8px;
}

.loading-indicator {
  font-size: 0.8rem;
  color: #666;
  font-weight: normal;
  margin-left: 0.5rem;
}

.error-message {
  color: #e53e3e;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

@media (max-width: 1024px) {
  .home-container {
    flex-direction: column;
    padding: var(--spacing-md);
    align-items: center;
  }

  .content-section {
    gap: var(--spacing-lg);
    padding-top: 0;
  }

  .hero-section {
    text-align: center;
    margin-bottom: var(--spacing-lg);
  }

  .search-create-container {
    flex-direction: column;
    width: 100%;
  }

  .search-wrapper {
    width: 100%;
  }

  .create-post-btn {
    width: 100%;
  }

  .image-section {
    position: static;
    order: -1;
    padding-top: 0;
    margin-bottom: var(--spacing-lg);
    max-width: 100%;
    display: flex;
    justify-content: center;
  }

  .homepage-image {
    max-height: 50vh;
    margin: 0 auto;
    max-width: 90%;
  }
}

@media (max-width: 768px) {
  h1 {
    font-size: 2.5rem;
  }

  h3 {
    font-size: 1.25rem;
  }

  .categories-section,
  .featured-section {
    padding: var(--spacing-md);
  }
}
</style>
