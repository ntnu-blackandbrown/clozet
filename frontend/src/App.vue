<script setup>
import { ref, computed } from 'vue'
import LoginRegisterModal from '@/views/LoginRegisterView.vue'
import ProductList from '@/views/ProductListView.vue'
import { RouterView } from 'vue-router'
import { useUserStore } from './stores/UserStore'

const userStore = useUserStore()
const showLoginModal = ref(false)
const testRegistrationStatus = ref('')

// Beregnet egenskap som viser om brukeren er logget inn
const currentUser = computed(() => userStore.currentUser)
const isLoggedIn = computed(() => userStore.isLoggedIn)

// Funksjon for hurtigregistrering med testdata
const testRegister = async () => {
  try {
    testRegistrationStatus.value = 'Registrerer testbruker...'

    const testUser = {
      username: `testuser${Math.floor(Math.random() * 10000)}`,
      email: `testuser${Math.floor(Math.random() * 10000)}@example.com`,
      password: 'Password123',
      firstName: 'Test',
      lastName: 'User',
    }

    const registerResult = await userStore.handleRegister(
      testUser.username,
      testUser.email,
      testUser.password,
      testUser.firstName,
      testUser.lastName,
    )

    if (registerResult.success && registerResult.user) {
      testRegistrationStatus.value = 'Registrering vellykket! Logger inn...'

      // Automatisk innlogging etter registrering
      const loginResult = await userStore.handleLogin(testUser.username, testUser.password)

      if (loginResult.success) {
        testRegistrationStatus.value = `Logget inn som ${loginResult.user.username}`
        setTimeout(() => {
          testRegistrationStatus.value = ''
        }, 5000)
      } else {
        testRegistrationStatus.value = 'Registrering OK, men innlogging feilet'
      }
    } else {
      testRegistrationStatus.value = 'Registrering feilet'
      setTimeout(() => {
        testRegistrationStatus.value = ''
      }, 3000)
    }
  } catch (error) {
    testRegistrationStatus.value = 'Feil ved registrering'
    console.error('Error in test registration:', error)
  }
}

// Logge ut
const logout = () => {
  userStore.logout()
  testRegistrationStatus.value = 'Logget ut'
  setTimeout(() => {
    testRegistrationStatus.value = ''
  }, 3000)
}
</script>

<template>
  <div :class="{ blurred: showLoginModal }">
    <header>
      <img alt="Vue logo" class="logo" src="@/assets/logo.png" width="125" height="125" />

      <h1>Welcome to Clozet!</h1>
      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/profile">Profile</RouterLink>
        <RouterLink to="/messages">Messages</RouterLink>
      </nav>

      <div class="auth-section">
        <!-- Brukerinfo når logget inn -->
        <div v-if="isLoggedIn" class="user-info">
          <span class="welcome-msg"
            >Hei, {{ currentUser?.firstName || currentUser?.username }}!</span
          >
          <button @click="logout" class="logout-button">Logg ut</button>
        </div>

        <!-- Knapper for innlogging/registrering når ikke logget inn -->
        <div v-else class="auth-buttons">
          <button @click="showLoginModal = true" class="login-button">Login / Register</button>
          <button @click="testRegister" class="test-button">Test Registrering</button>
        </div>

        <!-- Statusmelding -->
        <span v-if="testRegistrationStatus" class="status-message">{{
          testRegistrationStatus
        }}</span>
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

.test-button:hover {
  background-color: #45a049;
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}
</style>
