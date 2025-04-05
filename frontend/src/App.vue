<script setup>
import { ref, computed, onMounted } from 'vue'
import LoginRegisterModal from '@/views/LoginRegisterView.vue'
import { RouterView, useRouter } from 'vue-router'
import { useAuthStore } from './stores/AuthStore'
import Footer from '@/components/layout/Footer.vue'

const router = useRouter()
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
  router.push('/')
}
</script>

<template>
  <div :class="{ blurred: showLoginModal }">
    <header class="main-header">
      <div class="header-content">
        <div class="header-left">
          <RouterLink to="/" class="logo-container">
            <img src="@/assets/light-green.png" alt="Clozet Logo" class="logo-image" />
          </RouterLink>
          <nav class="main-nav">
            <RouterLink v-if="isLoggedIn" to="/profile">Profile</RouterLink>
            <RouterLink v-if="isLoggedIn" to="/messages">Messages</RouterLink>
          </nav>
        </div>

        <div class="auth-section">
          <!-- User info when logged in -->
          <div v-if="isLoggedIn" class="user-info">
            <span class="welcome-msg"
              >Hei, {{ userDetails?.firstName || userDetails?.username }}!</span
            >
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

<style>
/* Global styles */
:root {
  --color-conch: #c3d7cc;
  --color-limed-spruce: #3a4951;
  --color-summer-green: #96bb7c;
  --color-smalt-blue: #507dbc;
  --color-white: #ffffff;

  /* Secondary shades */
  --color-conch-light: #d1e1d8;
  --color-conch-dark: #b4c7be;
  --color-limed-spruce-light: #485a64;
  --color-limed-spruce-dark: #2c383e;
  --color-summer-green-light: #a7c791;
  --color-summer-green-dark: #85a96d;
  --color-smalt-blue-light: #6b8fc5;
  --color-smalt-blue-dark: #456ba3;

  /* Spacing */
  --spacing-xs: 0.25rem;
  --spacing-sm: 0.5rem;
  --spacing-md: 1rem;
  --spacing-lg: 1.5rem;
  --spacing-xl: 2rem;

  /* Transitions */
  --transition-smooth: all 0.3s ease;
  --transition-bounce: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);

  /* Borders & Shadows */
  --border-radius-sm: 4px;
  --border-radius: 8px;
  --border-radius-lg: 12px;
  --box-shadow-light: 0 2px 4px rgba(45, 56, 58, 0.05);
  --box-shadow-medium: 0 4px 6px rgba(45, 56, 58, 0.1);
  --box-shadow-large: 0 8px 16px rgba(45, 56, 58, 0.1);
}

/* Reset & Base Styles */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family:
    'Inter',
    -apple-system,
    BlinkMacSystemFont,
    'Segoe UI',
    Roboto,
    sans-serif;
  color: var(--color-limed-spruce);
  background-color: var(--color-conch);
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* Global Component Styles */
.btn {
  padding: var(--spacing-sm) var(--spacing-lg);
  border: none;
  border-radius: var(--border-radius);
  font-weight: 500;
  font-size: 0.95rem;
  cursor: pointer;
  transition: var(--transition-smooth);
}

.btn-primary {
  background-color: var(--color-summer-green);
  color: var(--color-white);
}

.btn-primary:hover {
  background-color: var(--color-summer-green-dark);
  transform: translateY(-1px);
  box-shadow: var(--box-shadow-medium);
}

.btn-secondary {
  background-color: var(--color-smalt-blue);
  color: var(--color-white);
}

.btn-secondary:hover {
  background-color: var(--color-smalt-blue-dark);
  transform: translateY(-1px);
  box-shadow: var(--box-shadow-medium);
}

.btn-outline {
  background-color: transparent;
  border: 2px solid var(--color-summer-green);
  color: var(--color-summer-green);
}

.btn-outline:hover {
  background-color: var(--color-summer-green);
  color: var(--color-white);
}

/* Form Styles */
.form-control {
  background-color: var(--color-white);
  border: 1px solid var(--color-conch-dark);
  border-radius: var(--border-radius);
  padding: var(--spacing-sm) var(--spacing-md);
  transition: var(--transition-smooth);
}

.form-control:focus {
  outline: none;
  border-color: var(--color-limed-spruce);
  box-shadow: 0 0 0 3px rgba(55, 75, 74, 0.2);
}

/* Card Styles */
.card {
  background-color: var(--color-white);
  border-radius: var(--border-radius);
  box-shadow: var(--box-shadow-light);
  transition: var(--transition-smooth);
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: var(--box-shadow-medium);
}

/* Layout Components */
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-header {
  background-color: var(--color-limed-spruce);
  box-shadow: var(--box-shadow-light);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-md) var(--spacing-xl);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
}

.logo-container {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  text-decoration: none;
  transition: var(--transition-bounce);
}

.logo-container:hover {
  transform: translateY(-2px);
}

.logo-image {
  width: 64px;
  height: 64px;
  object-fit: contain;
}

.logo {
  font-size: 2.5rem;
  font-weight: 600;
  color: var(--color-white);
  margin: 0;
  letter-spacing: -0.02em;
}

.main-nav {
  display: flex;
  gap: var(--spacing-xl);
}

.main-nav a {
  text-decoration: none;
  color: var(--color-white);
  font-weight: 500;
  font-size: 1.2rem;
  padding: var(--spacing-sm) 0;
  position: relative;
  transition: var(--transition-smooth);
  opacity: 0.9;
}

.main-nav a:hover {
  color: var(--color-white);
  opacity: 1;
}

.main-nav a::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 2px;
  background-color: var(--color-white);
  transition: var(--transition-smooth);
}

.main-nav a:hover::after {
  width: 100%;
}

.auth-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
}

.welcome-msg {
  font-weight: 500;
  color: var(--color-white);
  font-size: 0.95rem;
  opacity: 0.9;
}

.login-button {
  background-color: var(--color-white);
  color: var(--color-limed-spruce);
  padding: var(--spacing-sm) var(--spacing-lg);
  border: 2px solid var(--color-limed-spruce);
  border-radius: var(--border-radius);
  font-weight: 500;
  font-size: 0.95rem;
  cursor: pointer;
  transition: var(--transition-bounce);
}

.login-button:hover {
  background-color: var(--color-limed-spruce);
  color: var(--color-white);
  transform: translateY(-2px);
  box-shadow: var(--box-shadow-medium);
}

.logout-button {
  background-color: #f1e7ca;
  color: var(--color-limed-spruce);
  padding: var(--spacing-sm) var(--spacing-lg);
  border: none;
  border-radius: var(--border-radius);
  font-weight: 500;
  font-size: 0.95rem;
  cursor: pointer;
  transition: var(--transition-bounce);
}

.logout-button:hover {
  background-color: #e8ddb8; /* Slightly darker shade */
  color: var(--color-limed-spruce);
  transform: translateY(-2px);
  box-shadow: var(--box-shadow-medium);
}

main {
  flex: 1;
  max-width: 1200px;
  margin: var(--spacing-xl) auto;
  padding: 0 var(--spacing-xl);
  width: 100%;
}

/* Grid Layout */
.grid {
  display: grid;
  gap: var(--spacing-lg);
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
}

/* Animation Classes */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Utility Classes */
.blurred {
  filter: blur(4px);
  pointer-events: none;
  user-select: none;
}

.text-center {
  text-align: center;
}
.text-right {
  text-align: right;
}
.text-left {
  text-align: left;
}

.mt-1 {
  margin-top: var(--spacing-sm);
}
.mt-2 {
  margin-top: var(--spacing-md);
}
.mt-3 {
  margin-top: var(--spacing-lg);
}
.mt-4 {
  margin-top: var(--spacing-xl);
}

.mb-1 {
  margin-bottom: var(--spacing-sm);
}
.mb-2 {
  margin-bottom: var(--spacing-md);
}
.mb-3 {
  margin-bottom: var(--spacing-lg);
}
.mb-4 {
  margin-bottom: var(--spacing-xl);
}

/* Responsive Design */
@media (max-width: 768px) {
  .header-content {
    padding: var(--spacing-md);
    flex-direction: column;
    gap: var(--spacing-lg);
  }

  .header-left {
    flex-direction: column;
    gap: var(--spacing-lg);
    width: 100%;
    align-items: center;
  }

  .main-nav {
    width: 100%;
    justify-content: center;
  }

  .auth-section {
    width: 100%;
    justify-content: center;
  }

  .grid {
    grid-template-columns: 1fr;
  }
}
</style>
