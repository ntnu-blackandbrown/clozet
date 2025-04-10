<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import LoginRegisterModal from '@/views/LoginRegisterView.vue'
import { RouterView, useRouter, useRoute } from 'vue-router'
import { useAuthStore } from './stores/AuthStore'
import { useFavoritesStore } from './stores/FavoritesStore'
import Footer from '@/components/layout/Footer.vue'
import LanguageSwitcher from '@/components/LanguageSwitcher.vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const favoritesStore = useFavoritesStore()
const showLoginModal = ref(false)
const statusMessage = ref('')
const isLoading = ref(false)
const initialAuthMode = ref('login') // Default to login mode
const mobileMenuOpen = ref(false) // Track mobile menu state

// Computed properties
const isLoggedIn = computed(() => authStore.isLoggedIn)
const userDetails = computed(() => authStore.userDetails)

// Load user info on app start
onMounted(async () => {
  console.log('🚀 App mounted, checking authentication status')

  // Check if user is already authenticated via cookies (silently)
  try {
    const isAuthenticated = await authStore.silentRefresh()
    console.log(`📝 Authentication check result: ${isAuthenticated ? 'Authenticated' : 'Not authenticated'}`)

    // Initialize favorites if user is authenticated
    if (isAuthenticated) {
      console.log('🔄 User is authenticated, initializing favorites')
      // Add small delay to ensure token is ready
      setTimeout(() => {
        favoritesStore.initializeFavorites()
      }, 500)
    }
  } catch (error) {
    console.error('❌ Error during authentication check:', error)
  }

  // Check if we should show the login/register modal based on the route
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
    // Close mobile menu when navigating
    mobileMenuOpen.value = false
  },
)

async function logout() {
  await authStore.logout()
  router.push('/')
}

const handleLoginClick = () => {
  showLoginModal.value = true
  initialAuthMode.value = 'login'
  router.replace('/login')
}

const handleCloseAuthModal = () => {
  showLoginModal.value = false
  router.replace('/')
}

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}
</script>

<template>
  <div :class="{ blurred: showLoginModal }">
    <a href="#main-content" class="skip-link sr-only sr-only-focusable">{{ $t('common.skipToContent') }}</a>
    <header class="main-header" role="banner">
      <div class="header-content">
        <div class="header-left">
          <RouterLink to="/" class="logo-container" :aria-label="$t('navigation.home')">
            <img src="@/assets/light-green.png" alt="Clozet Logo" class="logo-image" />
          </RouterLink>

          <!-- Hamburger menu button -->
          <button
            class="hamburger-menu-btn"
            @click="toggleMobileMenu"
            :aria-label="$t('common.toggleMenu')"
            :aria-expanded="mobileMenuOpen"
            :class="{ 'is-active': mobileMenuOpen }"
          >
            <span class="hamburger-bar"></span>
            <span class="hamburger-bar"></span>
            <span class="hamburger-bar"></span>
          </button>
        </div>

        <!-- Navigation menu -->
        <nav class="main-nav" :class="{ 'mobile-menu-open': mobileMenuOpen }" aria-label="Main Navigation">
          <template v-if="userDetails?.role === 'ADMIN'">
            <RouterLink to="/admin" class="nav-link admin-link" aria-label="Admin Dashboard"
              >{{ $t('navigation.adminDashboard') }}</RouterLink
            >
          </template>
          <template v-else>
            <RouterLink v-if="isLoggedIn" to="/profile" :aria-label="$t('navigation.profile')"
              >{{ $t('navigation.profile') }}</RouterLink
            >
            <RouterLink v-if="isLoggedIn" to="/messages" :aria-label="$t('navigation.messages')"
              >{{ $t('navigation.messages') }}</RouterLink
            >
            <RouterLink v-if="isLoggedIn" to="/create-product" :aria-label="$t('navigation.sellItems')"
              >{{ $t('navigation.sellItems') }}</RouterLink
            >
          </template>

          <!-- Mobile-only auth section -->
          <div class="mobile-auth-section">
            <LanguageSwitcher />
            <button v-if="isLoggedIn" @click="logout" class="logout-btn" :aria-label="$t('common.logout')">
              {{ $t('common.logout') }}
            </button>
            <button v-else @click="handleLoginClick" class="login-btn" :aria-label="$t('common.login')">
              {{ $t('common.login') }}
            </button>
          </div>
        </nav>

        <!-- Desktop auth section -->
        <div class="auth-section">
          <LanguageSwitcher />
          <button v-if="isLoggedIn" @click="logout" class="logout-btn" :aria-label="$t('common.logout')">
            {{ $t('common.logout') }}
          </button>
          <button v-else @click="handleLoginClick" class="login-btn" :aria-label="$t('common.login')">
            {{ $t('common.login') }}
          </button>
        </div>
      </div>
    </header>
    <main id="main-content" role="main">
      <RouterView />
    </main>
    <Footer />
  </div>

  <LoginRegisterModal
    v-if="showLoginModal"
    @close="handleCloseAuthModal"
    :initialMode="initialAuthMode"
  />
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

/* Skip Link */
.skip-link {
  position: absolute;
  top: -40px;
  left: 0;
  padding: 8px;
  background-color: var(--color-white);
  color: var(--color-limed-spruce);
  z-index: 1000;
  transition: top 0.3s ease;
}

.skip-link:focus {
  top: 0;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

.sr-only-focusable:focus {
  position: static;
  width: auto;
  height: auto;
  padding: 0;
  margin: 0;
  overflow: visible;
  clip: auto;
  white-space: normal;
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
}

.logo-container {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  text-decoration: none;
  transition: var(--transition-bounce);
  margin-right: var(--spacing-xl);
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

/* Hamburger Menu Styles */
.hamburger-menu-btn {
  display: none;
  flex-direction: column;
  justify-content: space-between;
  width: 30px;
  height: 21px;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
  z-index: 110;
}

.hamburger-bar {
  width: 100%;
  height: 3px;
  background-color: var(--color-white);
  border-radius: 10px;
  transition: var(--transition-smooth);
}

/* Mobile Navigation Styles */
.mobile-auth-section {
  display: none;
}

.main-nav {
  display: flex;
  gap: var(--spacing-xl);
  margin-right: auto;
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

.main-nav a:hover,
.main-nav a:focus {
  color: var(--color-white);
  opacity: 1;
  outline: none;
}

.main-nav a:focus {
  text-decoration: underline;
  outline: 2px solid white;
  outline-offset: 2px;
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
  gap: var(--spacing-md);
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

.login-btn {
  background-color: #f1e7ca; /* Parchment color */
  color: var(--color-limed-spruce);
  padding: var(--spacing-sm) var(--spacing-lg);
  border: none; /* Remove border to match logout button */
  border-radius: var(--border-radius);
  font-weight: 500;
  font-size: 0.95rem;
  cursor: pointer;
  transition: var(--transition-bounce);
}

.login-btn:hover {
  background-color: #e8ddb8; /* Slightly darker shade on hover */
  color: var(--color-limed-spruce);
  transform: translateY(-2px);
  box-shadow: var(--box-shadow-medium);
}

.logout-btn {
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

.logout-btn:hover {
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
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    position: relative;
  }

  .header-left {
    flex-direction: row;
    width: auto;
    gap: var(--spacing-md);
    justify-content: space-between;
    width: 100%;
  }

  .hamburger-menu-btn {
    display: flex;
    z-index: 120;
  }

  .main-nav {
    position: fixed;
    top: 0;
    right: -100%;
    height: 100vh;
    width: 75%;
    max-width: 300px;
    background-color: var(--color-limed-spruce);
    padding: 80px 20px 20px;
    flex-direction: column;
    gap: var(--spacing-lg);
    z-index: 100;
    transition: right 0.3s ease;
    overflow-y: auto;
    box-shadow: -5px 0 15px rgba(0, 0, 0, 0.2);
  }

  .main-nav.mobile-menu-open {
    right: 0;
  }

  .main-nav a {
    font-size: 1.3rem;
    padding: var(--spacing-md) 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    width: 100%;
  }

  .main-nav a::after {
    display: none;
  }

  .auth-section {
    display: none;
  }

  .mobile-auth-section {
    display: block;
    margin-top: var(--spacing-xl);
  }

  .mobile-auth-section button {
    width: 100%;
    padding: var(--spacing-md);
    margin-top: var(--spacing-md);
    font-size: 1.1rem;
  }

  /* Overlay when menu is open */
  body:has(.mobile-menu-open) {
    overflow: hidden;
  }

  /* Add an overlay when menu is open */
  .main-nav.mobile-menu-open::before {
    content: "";
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    z-index: -1;
  }

  .grid {
    grid-template-columns: 1fr;
  }

  .mobile-auth-section .language-switcher {
    display: block !important;
    width: 100%;
    margin: 15px 0;
  }
}

/* Animation for hamburger to X */
.hamburger-menu-btn.is-active .hamburger-bar:nth-child(1) {
  transform: translateY(9px) rotate(45deg);
}

.hamburger-menu-btn.is-active .hamburger-bar:nth-child(2) {
  opacity: 0;
}

.hamburger-menu-btn.is-active .hamburger-bar:nth-child(3) {
  transform: translateY(-9px) rotate(-45deg);
}

/* Ensure the language switcher is visible */
.auth-section .language-switcher {
  display: inline-block !important;
  margin-right: 10px;
}
</style>
