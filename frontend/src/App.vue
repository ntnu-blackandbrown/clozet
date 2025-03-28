<script setup>
import { ref } from 'vue'
import LoginRegisterModal from './components/modals/LoginRegisterModal.vue'
import ProductList from './components/ProductList.vue'
import { RouterView } from 'vue-router'
import { useUserStore } from './stores/UserStore'

const userStore = useUserStore()
const showLoginModal = ref(false)
const testRegistrationStatus = ref('')

// Funksjon for hurtigregistrering med testdata
const testRegister = async () => {
  try {
    testRegistrationStatus.value = 'Registrerer testbruker...'

    const testUser = {
      username: `testuser${Math.floor(Math.random() * 10000)}`,
      email: `testuser${Math.floor(Math.random() * 10000)}@example.com`,
      password: 'Password123',
      firstName: 'Test',
      lastName: 'User'
    }

    await userStore.handleRegister(
      testUser.username,
      testUser.email,
      testUser.password,
      testUser.firstName,
      testUser.lastName
    )

    testRegistrationStatus.value = 'Suksess! Testbruker registrert'
    setTimeout(() => {
      testRegistrationStatus.value = ''
    }, 3000)
  } catch (error) {
    testRegistrationStatus.value = 'Feil ved registrering'
    console.error('Error in test registration:', error)
  }
}
</script>

<template>
  <div :class="{ blurred: showLoginModal }">
    <header>
      <img alt="Vue logo" class="logo" src="@/assets/logo.png" width="125" height="125" />

        <h1>Welcome to Clozet!</h1>
        <nav>
          <RouterLink to="/product-display">Product Display</RouterLink>
        </nav>
        <div class="auth-buttons">
          <button @click="showLoginModal = true" class="login-button">Login / Register</button>
          <button @click="testRegister" class="test-button">Test Registrering</button>
          <span v-if="testRegistrationStatus" class="status-message">{{ testRegistrationStatus }}</span>
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

.test-button {
  padding: 0.5rem 1rem;
  background-color: #4CAF50;
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
  margin-left: 10px;
  color: #333;
}

main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}
</style>
