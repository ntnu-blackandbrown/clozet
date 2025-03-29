<script setup>
import { ref, computed, onMounted } from 'vue'
import LoginRegisterModal from '@/views/LoginRegisterView.vue'
import { RouterView } from 'vue-router'
import { useAuthStore } from './stores/AuthStore'
import axios from '@/api/axios'

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

// Logout function
const logout = async () => {
  const result = await authStore.logout()
  if (result.success) {
    statusMessage.value = 'Logget ut'
    setTimeout(() => {
      statusMessage.value = ''
    }, 3000)
  }
}

// Security test function
async function testSecuritySetup() {
  isLoading.value = true
  statusMessage.value = 'Testing security setup...'

  try {
    // Generate random user data
    const randomStr = Math.floor(Math.random() * 10000)
    const testUser = {
      username: `testuser${randomStr}`,
      email: `testuser${randomStr}@example.com`,
      password: 'Password123!',
      firstName: 'Test',
      lastName: 'User',
      role: 'USER'
    }

    // Step 1: Register user
    statusMessage.value = 'Registrerer testbruker...'
    await axios.post('/api/users', testUser)

    // Step 2: Login with created user
    statusMessage.value = 'Logger inn med testbruker...'
    const loginResult = await authStore.login(testUser.username, testUser.password)

    if (loginResult.success) {
      statusMessage.value = `✅ Sikkerhetskonfigurasjon fungerer! Logget inn som ${testUser.username}`
    } else {
      statusMessage.value = '❌ Innlogging feilet etter registrering'
    }
  } catch (error) {
    console.error('Security test failed:', error)
    statusMessage.value = `❌ Sikkerhetstest feilet: ${error.response?.data?.message || error.message}`
  } finally {
    isLoading.value = false
    // Clear success message after 10 seconds
    if (statusMessage.value.includes('✅')) {
      setTimeout(() => {
        statusMessage.value = ''
      }, 10000)
    }
  }
}
</script>

<template>
  <div :class="{ blurred: showLoginModal }">
    <header>
      <img alt="Vue logo" class="logo" src="@/assets/logo.png" width="125" height="125" />

      <h1>Welcome to Clozet!</h1>
      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink v-if="isLoggedIn" to="/profile">Profile</RouterLink>
      </nav>

      <div class="auth-section">
        <!-- User info when logged in -->
        <div v-if="isLoggedIn" class="user-info">
          <span class="welcome-msg"
            >Hei, {{ userDetails?.firstName || userDetails?.username }}!</span
          >
          <button @click="logout" class="logout-button">Logg ut</button>
        </div>

        <!-- Login/register buttons when not logged in -->
        <div v-else class="auth-buttons">
          <button @click="showLoginModal = true" class="login-button">Login / Register</button>
          <button
            @click="testSecuritySetup"
            :disabled="isLoading"
            class="test-button"
          >
            {{ isLoading ? 'Tester...' : 'Test Sikkerhet' }}
          </button>
        </div>

        <!-- Status message -->
        <span v-if="statusMessage" class="status-message" :class="{
          'success': statusMessage.includes('✅'),
          'error': statusMessage.includes('❌')
        }">
          {{ statusMessage }}
        </span>
      </div>
    </header>
    <main>
      <RouterView />
    </main>
  </div>

  <LoginRegisterModal v-if="showLoginModal" @close="showLoginModal = false" />
</template>

<style scoped>
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  border-bottom: 1px solid #e5e7eb;
}

.blurred {
  filter: blur(5px);
  transition: filter 0.3s ease;
  pointer-events: none;
  user-select: none;
}

.auth-section {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.auth-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
}

.login-button {
  padding: 0.5rem 1rem;
  background-color: #333;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.login-button:hover {
  background-color: #444;
}

.logout-button {
  padding: 0.5rem 1rem;
  background-color: #666;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.logout-button:hover {
  background-color: #888;
}

.test-button {
  padding: 0.5rem 1rem;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.test-button:hover:not(:disabled) {
  background-color: #45a049;
}

.test-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.status-message {
  font-size: 0.85rem;
  margin-top: 8px;
  color: #333;
  padding: 4px 8px;
  border-radius: 4px;
}

.success {
  background-color: #e6f7e6;
  color: #2e7d32;
}

.error {
  background-color: #ffebee;
  color: #c62828;
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}
</style>