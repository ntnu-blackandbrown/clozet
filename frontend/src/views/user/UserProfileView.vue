<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import ProductDisplayModal from '@/components/modals/ProductDisplayModal.vue'

const route = useRoute()
const showProductModal = ref(false)
const selectedProductId = ref('')

const openProductModal = (productId) => {
  selectedProductId.value = productId
  showProductModal.value = true
}
</script>

<template>
  <div class="profile-container">
    <!-- Vertical Navigation Menu -->
    <nav class="profile-nav" aria-label="Profile navigation">
      <RouterLink
        to="/profile/settings"
        class="nav-link"
        :class="{ active: route.path === '/profile/settings' }"
        aria-current="route.path === '/profile/settings' ? 'page' : undefined"
      >
        Profile Settings
      </RouterLink>
      <RouterLink
        to="/profile/posts"
        class="nav-link"
        :class="{ active: route.path === '/profile/posts' }"
        aria-current="route.path === '/profile/posts' ? 'page' : undefined"
      >
        My Posts
      </RouterLink>
      <RouterLink
        to="/profile/wishlist"
        class="nav-link"
        :class="{ active: route.path === '/profile/wishlist' }"
        aria-current="route.path === '/profile/wishlist' ? 'page' : undefined"
      >
        My Wishlist
      </RouterLink>
      <RouterLink
        to="/profile/purchases"
        class="nav-link"
        :class="{ active: route.path === '/profile/purchases' }"
        aria-current="route.path === '/profile/purchases' ? 'page' : undefined"
      >
        My Purchases
      </RouterLink>
      <RouterLink
        to="/profile/change-password"
        class="nav-link"
        :class="{ active: route.path === '/profile/change-password' }"
        aria-current="route.path === '/profile/change-password' ? 'page' : undefined"
      >
        Change Password
      </RouterLink>
    </nav>

    <!-- Content Area -->
    <main class="profile-content" role="main">
      <RouterView />
    </main>

    <!-- Product Display Modal -->
    <ProductDisplayModal
      v-if="showProductModal"
      :productId="selectedProductId"
      @close="showProductModal = false"
    />
  </div>
</template>

<style scoped>
.profile-container {
  display: flex;
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  min-height: calc(100vh - 64px); /* Adjust based on your header height */
}

.profile-nav {
  width: 250px;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  position: sticky;
  top: 2rem;
  height: fit-content;
  background-color: white;
  padding: 1rem;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.nav-link {
  padding: 1rem 1.5rem;
  text-decoration: none;
  color: #4b5563;
  border-radius: 8px;
  transition: all 0.2s ease;
  font-weight: 500;
}

.nav-link:hover {
  background-color: #f3f4f6;
  color: #1f2937;
}

.nav-link.active {
  background-color: #e5e7eb;
  color: #111827;
}

.profile-content {
  flex: 1;
  background-color: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  min-height: 500px;
}

@media (max-width: 768px) {
  .profile-container {
    flex-direction: column;
    padding: 1rem;
  }

  .profile-nav {
    width: 100%;
    flex-direction: row;
    overflow-x: auto;
    padding: 0.75rem;
    position: relative;
    top: 0;
    margin-bottom: 1rem;
  }

  .nav-link {
    white-space: nowrap;
    padding: 0.75rem 1rem;
    font-size: 0.9rem;
  }

  .profile-content {
    padding: 1rem;
  }
}
</style>
