<template>
  <div class="login-register-container">
    <div class="tab-selector">
      <button
        @click="showLogin = true"
        :class="{ 'active': showLogin }"
      >
        Logg inn
      </button>
      <button
        @click="showLogin = false"
        :class="{ 'active': !showLogin }"
      >
        Registrer deg
      </button>
    </div>

    <div v-if="showLogin" class="form-container">
      <h2>Logg inn</h2>
      <form @submit.prevent="login">
        <div class="form-group">
          <label for="loginUsername">Brukernavn</label>
          <input
            type="text"
            id="loginUsername"
            v-model="loginUsername"
            placeholder="Brukernavn"
            required
          />
        </div>
        <div class="form-group">
          <label for="loginPassword">Passord</label>
          <input
            type="password"
            id="loginPassword"
            v-model="loginPassword"
            placeholder="Passord"
            required
          />
        </div>
        <div class="forgot-password">
          <router-link to="/forgot-password">Glemt passord?</router-link>
        </div>
        <button type="submit" class="submit-button">Logg inn</button>
      </form>
      <div v-if="loginError" class="error">{{ loginError }}</div>
    </div>

    <div v-else class="form-container">
      <h2>Registrer deg</h2>
      <form @submit.prevent="register">
        <div class="form-group">
          <label for="registerUsername">Brukernavn</label>
          <input
            type="text"
            id="registerUsername"
            v-model="registerUsername"
            placeholder="Velg et brukernavn"
            required
          />
        </div>
        <div class="form-group">
          <label for="registerEmail">E-post</label>
          <input
            type="email"
            id="registerEmail"
            v-model="registerEmail"
            placeholder="Din e-postadresse"
            required
          />
        </div>
        <div class="form-group">
          <label for="registerPassword">Passord</label>
          <input
            type="password"
            id="registerPassword"
            v-model="registerPassword"
            placeholder="Velg et passord"
            required
          />
        </div>
        <button type="submit" class="submit-button">Registrer</button>
      </form>
      <div v-if="registerMessage" class="message" :class="{ 'success': registerSuccess, 'error': !registerSuccess }">
        {{ registerMessage }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/AuthStore'
import { useUserStore } from '@/stores/UserStore'
import { useRouter } from 'vue-router'
import apiClient from '@/api/axios'

const showLogin = ref(true)
const loginUsername = ref('')
const loginPassword = ref('')
const registerUsername = ref('')
const registerEmail = ref('')
const registerPassword = ref('')
const registerMessage = ref('')
const registerSuccess = ref(false)
const loginError = ref('')

const authStore = useAuthStore()
const userStore = useUserStore()
const router = useRouter()

const login = async () => {
  try {
    const result = await authStore.login(loginUsername.value, loginPassword.value)
    if (result.success) {
      router.push('/dashboard')
    }
  } catch (error: any) {
    loginError.value = error.response?.data || 'Innlogging feilet. Sjekk brukernavn og passord.'
  }
}

const register = async () => {
  try {
    registerMessage.value = ''
    const result = await userStore.register(
      registerUsername.value,
      registerEmail.value,
      registerPassword.value
    )

    if (result.success) {
      registerSuccess.value = true
      registerMessage.value = 'Registrering vellykket! Vennligst sjekk e-posten din for å verifisere kontoen.'

      // Reset form fields
      registerUsername.value = ''
      registerEmail.value = ''
      registerPassword.value = ''

      // After 2 seconds, redirect to verify-info page
      setTimeout(() => {
        router.push('/verify-info')
      }, 2000)
    } else {
      registerSuccess.value = false
      registerMessage.value = result.message
    }
  } catch (error: any) {
    registerSuccess.value = false
    registerMessage.value = error.response?.data || 'Registrering feilet. Vennligst prøv igjen.'
  }
}
</script>

<style scoped>
.login-register-container {
  max-width: 480px;
  margin: 40px auto;
  padding: 24px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tab-selector {
  display: flex;
  margin-bottom: 24px;
  border-bottom: 1px solid #ddd;
}

.tab-selector button {
  flex: 1;
  padding: 12px;
  background: none;
  border: none;
  font-size: 16px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-selector button.active {
  color: #333;
  border-bottom: 2px solid #333;
}

h2 {
  margin-bottom: 24px;
  color: #333;
  text-align: center;
}

.form-group {
  margin-bottom: 16px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

input:focus {
  border-color: #333;
  outline: none;
}

.forgot-password {
  text-align: right;
  margin-bottom: 16px;
}

.forgot-password a {
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.forgot-password a:hover {
  text-decoration: underline;
}

.submit-button {
  width: 100%;
  padding: 12px;
  background-color: #333;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-button:hover {
  background-color: #444;
}

.error {
  margin-top: 16px;
  padding: 12px;
  background-color: #ffebee;
  color: #c62828;
  border-radius: 4px;
}

.message {
  margin-top: 16px;
  padding: 12px;
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
</style>
