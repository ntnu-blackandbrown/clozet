<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/AuthStore'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const isMobileMenuOpen = ref(false)

const isAdmin = computed(() => {
  return authStore.userDetails?.role === 'ADMIN'
})

onMounted(() => {
  // Check if user is admin, if not redirect to home
  if (!isAdmin.value) {
    router.push('/')
  }
})

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}
</script>

<template>
  <div class="admin-container">
    <!-- Hamburger button for mobile -->
    <button class="admin-hamburger-btn" @click="toggleMobileMenu">
      <span v-if="!isMobileMenuOpen">☰</span>
      <span v-else>✕</span>
    </button>

    <!-- Admin sidebar navigation -->
    <nav class="admin-sidebar" :class="{ 'is-open': isMobileMenuOpen }">
      <h2 class="admin-title">Admin Dashboard</h2>

      <div class="admin-nav-links">
        <RouterLink
          to="/admin/overview"
          class="admin-nav-link"
          :class="{ active: route.path === '/admin/overview' }"
          @click="isMobileMenuOpen = false"
        >
          <span class="admin-nav-icon">📊</span>
          Overview
        </RouterLink>

        <RouterLink
          to="/admin/categories"
          class="admin-nav-link"
          :class="{ active: route.path === '/admin/categories' }"
           @click="isMobileMenuOpen = false"
       >
          <span class="admin-nav-icon">🏷️</span>
          Categories
        </RouterLink>

        <RouterLink
          to="/admin/locations"
          class="admin-nav-link"
          :class="{ active: route.path === '/admin/locations' }"
           @click="isMobileMenuOpen = false"
       >
          <span class="admin-nav-icon">📍</span>
          Locations
        </RouterLink>

        <RouterLink
          to="/admin/shipping"
          class="admin-nav-link"
          :class="{ active: route.path === '/admin/shipping' }"
           @click="isMobileMenuOpen = false"
       >
          <span class="admin-nav-icon">🚚</span>
          Shipping Options
        </RouterLink>

        <RouterLink
          to="/admin/users"
          class="admin-nav-link"
          :class="{ active: route.path === '/admin/users' }"
           @click="isMobileMenuOpen = false"
       >
          <span class="admin-nav-icon">👥</span>
          User Management
        </RouterLink>

        <RouterLink
          to="/admin/transactions"
          class="admin-nav-link"
          :class="{ active: route.path === '/admin/transactions' }"
           @click="isMobileMenuOpen = false"
       >
          <span class="admin-nav-icon">💰</span>
          Transactions
        </RouterLink>
      </div>

      <div class="admin-sidebar-footer">
        <RouterLink to="/" class="admin-back-link" @click="isMobileMenuOpen = false"> Return to Site </RouterLink>
      </div>
    </nav>

    <!-- Admin content area -->
    <main class="admin-content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.admin-container {
  display: flex;
  min-height: calc(100vh - 64px);
  background-color: var(--color-alabaster);
  position: relative;
}

.admin-hamburger-btn {
  display: none;
  position: absolute;
  top: 1rem;
  left: 1rem;
  z-index: 1100;
  background: var(--color-limed-spruce);
  color: var(--color-white);
  border: none;
  padding: 0.5rem 0.75rem;
  border-radius: 0.375rem;
  cursor: pointer;
  font-size: 1.5rem;
  line-height: 1;
}

.admin-sidebar {
  width: 280px;
  background-color: var(--color-limed-spruce);
  color: var(--color-white);
  display: flex;
  flex-direction: column;
  padding: 1.5rem 1rem;
  position: sticky;
  top: 0;
  height: 100vh;
  transition: transform 0.3s ease-in-out;
  z-index: 1000;
}

.admin-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-nav-links {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  flex: 1;
}

.admin-nav-link {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  border-radius: 0.5rem;
  transition: all 0.2s ease;
}

.admin-nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--color-white);
}

.admin-nav-link.active {
  background-color: rgba(255, 255, 255, 0.15);
  color: var(--color-white);
  font-weight: 500;
}

.admin-nav-icon {
  margin-right: 0.75rem;
  font-size: 1.25rem;
}

.admin-sidebar-footer {
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-back-link {
  display: block;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  padding: 0.75rem 1rem;
  border-radius: 0.5rem;
  transition: all 0.2s ease;
  text-align: center;
}

.admin-back-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--color-white);
}

.admin-content {
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .admin-container {
    flex-direction: column;
    min-height: 100vh;
    padding-top: 4rem;
  }

  .admin-hamburger-btn {
    display: block;
  }

  .admin-sidebar {
    position: fixed;
    top: 0;
    left: 0;
    bottom: 0;
    width: 280px;
    height: 100%;
    transform: translateX(-100%);
    padding-top: 4rem;
    overflow-y: auto;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.2);
  }

  .admin-sidebar.is-open {
    transform: translateX(0);
  }

  .admin-content {
    padding: 1rem;
    margin-left: 0;
  }
}
</style>
