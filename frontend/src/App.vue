<script setup>
import { ref, computed, onMounted } from 'vue'
import LoginRegisterModal from '@/views/LoginRegisterView.vue'
import { RouterView } from 'vue-router'
import { useAuthStore } from './stores/AuthStore'

const authStore = useAuthStore()
const showLoginModal = ref(false)
const statusMessage = ref('')
const isLoading = ref(false)

// Computed properties
const isLoggedIn = computed(() => authStore.isLoggedIn)
const userDetails = computed(() => authStore.userDetails)

// Load user info on app start
onMounted(async () => {
  await authStore.fetchUserInfo()
})

async function logout() {
  await authStore.logout()
}

</script>

<template>
  <div :class="{ blurred: showLoginModal }">
    <header class="main-header">
      <div class="header-content">
        <div class="header-left">
          <RouterLink to="/" class="logo-container">
            <img src="@/assets/logo.png" alt="Clozet Logo" class="logo-image" />
            <h1 class="logo">Clozet</h1>
          </RouterLink>
          <nav class="main-nav">
            <RouterLink  to="/profile">Profile</RouterLink>
            <RouterLink to="/messages">Messages</RouterLink>
          </nav>
        </div>

        <div class="auth-section">
          <!-- User info when logged in -->
          <div v-if="isLoggedIn" class="user-info">
            <span class="welcome-msg">Hei, {{ userDetails?.firstName || userDetails?.username }}!</span>
            <button @click="logout" class="logout-button">Log Out</button>
          </div>

          <!-- Login/register buttons when not logged in -->
          <div v-else class="auth-buttons">
            <button @click="showLoginModal = true" class="login-button">Login / Register</button>
          </div>
        </div>
      </div>
    </header>
    <main>
      <RouterView />
    </main>
    <Footer />
  </div>

  <LoginRegisterModal v-if="showLoginModal" @close="showLoginModal = false" />
</template>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-header {
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-decoration: none;
  transition: opacity 0.2s ease;
}

.logo-container:hover {
  opacity: 0.8;
}

.logo-image {
  width: 40px;
  height: 40px;
  object-fit: contain;
}

.logo {
  font-size: 1.8rem;
  font-weight: 700;
  color: #333;
  margin: 0;
  letter-spacing: -0.5px;
}

.main-nav {
  display: flex;
  gap: 1.5rem;
}

.nav-link {
  text-decoration: none;
  color: #4b5563;
  font-weight: 500;
  padding: 0.5rem 0;
  position: relative;
  transition: color 0.2s ease;
}

.nav-link:hover {
  color: #333;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background-color: #333;
  transition: width 0.2s ease;
}

.nav-link:hover::after {
  width: 100%;
}

.auth-section {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 0.5rem;
}

.auth-buttons {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.login-button,
.logout-button,
.test-button {
  padding: 0.625rem 1.25rem;
  border: none;
  border-radius: 0.5rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.login-button {
  background-color: #333;
  color: white;
}

.login-button:hover {
  background-color: #444;
  transform: translateY(-1px);
}

.logout-button {
  background-color: #f3f4f6;
  color: #4b5563;
}

.logout-button:hover {
  background-color: #e5e7eb;
  transform: translateY(-1px);
}

.test-button {
  background-color: #4caf50;
  color: white;
}

.test-button:hover:not(:disabled) {
  background-color: #45a049;
  transform: translateY(-1px);
}

.test-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.status-message {
  font-size: 0.85rem;
  margin-top: 8px;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.welcome-msg {
  font-weight: 500;
  color: #333;
}

main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  width: 100%;
}

@media (max-width: 768px) {
  .header-content {
    padding: 1rem;
    flex-direction: column;
    gap: 1rem;
  }

  .header-left {
    flex-direction: column;
    gap: 1rem;
    width: 100%;
  }

  .logo-container {
    width: 100%;
    justify-content: center;
  }

  .logo-image {
    width: 32px;
    height: 32px;
  }

  .main-nav {
    width: 100%;
    justify-content: center;
  }

  .auth-section {
    width: 100%;
    align-items: center;
  }

  .auth-buttons {
    flex-direction: column;
    width: 100%;
  }

  .login-button,
  .logout-button,
  .test-button {
    width: 100%;
  }
}
</style>
