<script setup>
import { ref } from 'vue'
import { useAuthStore } from '@/stores/AuthStore'

const authStore = useAuthStore()
const username = ref('')
const password = ref('')
const errorMessage = ref('')
const isLoading = ref(false)

const emit = defineEmits(['login-success', 'login-error'])

async function handleLogin() {
  if (!username.value || !password.value) {
    errorMessage.value = 'Brukernavn og passord må fylles ut'
    return
  }

  errorMessage.value = ''
  isLoading.value = true

  try {
    const result = await authStore.login(username.value, password.value)
    if (result.success) {
      emit('login-success')
    } else {
      errorMessage.value = 'Innlogging feilet. Sjekk brukernavn og passord.'
      emit('login-error', errorMessage.value)
    }
  } catch (error) {
    errorMessage.value = 'En feil oppstod under innlogging'
    emit('login-error', errorMessage.value)
  } finally {
    isLoading.value = false
  }
}
</script>

<template>
  <form @submit.prevent="handleLogin" class="login-form">
    <div class="form-group">
      <label for="username">Brukernavn</label>
      <input
        type="text"
        id="username"
        v-model="username"
        required
        autocomplete="username"
      />
    </div>

    <div class="form-group">
      <label for="password">Passord</label>
      <input
        type="password"
        id="password"
        v-model="password"
        required
        autocomplete="current-password"
      />
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <button type="submit" :disabled="isLoading" class="login-button">
      {{ isLoading ? 'Logger inn...' : 'Logg inn' }}
    </button>
  </form>
</template>

<style scoped>
.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  width: 100%;
  max-width: 400px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

label {
  font-weight: 500;
}

input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 0.25rem;
}

.error-message {
  color: #e53e3e;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}

.login-button {
  padding: 0.5rem 1rem;
  background-color: #333;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-top: 0.5rem;
}

.login-button:hover:not(:disabled) {
  background-color: #444;
}

.login-button:disabled {
  background-color: #999;
  cursor: not-allowed;
}
</style>