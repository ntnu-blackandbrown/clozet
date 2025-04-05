<script setup>
import Badge from '@/components/utils/Badge.vue'
import { useRouter, useRoute } from 'vue-router'
import ProductListView from '@/views/ProductListView.vue'
import { useAuthStore } from '@/stores/AuthStore'
import LoginRegisterModal from '@/views/LoginRegisterView.vue'
import { ref, onMounted, watch } from 'vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const showLoginModal = ref(false)
const initialAuthMode = ref('login') // Default to login mode

// Check if we should show the login/register modal based on the route
onMounted(() => {
  if (route.path === '/login' || route.path === '/register') {
    showLoginModal.value = true
    initialAuthMode.value = route.path === '/login' ? 'login' : 'register'
  }
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
</script>

<template>
  <div class="home-container">
    <div class="content-section">
      <div class="hero-section">
        <h1>Welcome to Clozet!</h1>
        <h3>The new way to shop for clothes</h3>

        <div class="search-create-container">
          <div class="search-wrapper">
            <input class="search-bar" type="text" placeholder="Search for a product..." />
            <!-- Inline SVG icon -->
            <svg
              class="search-icon"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <circle cx="11" cy="11" r="8" />
              <line x1="21" y1="21" x2="16.65" y2="16.65" />
            </svg>
          </div>
          <div class="create-post-btn">
            <button @click="handleCreatePost">Create a post!</button>
          </div>
        </div>
      </div>

      <div class="categories-section">
        <h4>Popular Categories</h4>
        <div class="badge-container">
          <Badge type="category" name="Tops" />
          <Badge type="category" name="Bottoms" />
          <Badge type="category" name="Dresses" />
          <Badge type="category" name="Accessories" />
        </div>
      </div>
    </div>
    <div class="image-section">
      <img src="@/assets/images/homepage.png" alt="Clozet Homepage" class="homepage-image" />
    </div>
  </div>
  <div class="featured-section">
    <div class="featured-products">
      <ProductListView />
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
  align-items: flex-start;
  max-width: 500px;
  padding-top: var(--spacing-xl);
}

.homepage-image {
  width: 100%;
  height: auto;
  max-height: 600px;
  object-fit: contain;
  border-radius: var(--border-radius-lg);
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
}

.categories-section h4 {
  color: var(--color-limed-spruce);
  font-size: 1.1rem;
  font-weight: 500;
  margin-bottom: var(--spacing-md);
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

@media (max-width: 1024px) {
  .home-container {
    flex-direction: column;
    padding: var(--spacing-md);
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
  }

  .homepage-image {
    max-height: 50vh;
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
