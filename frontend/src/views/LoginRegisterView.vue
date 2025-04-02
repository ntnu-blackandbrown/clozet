<template>
  <div class="login-register-container">
    <div class="tab-selector">
      <button
        @click="showLogin = true"
        :class="{ active: showLogin }"
      >
        Logg inn
      </button>
      <button
        @click="showLogin = false"
        :class="{ active: !showLogin }"
      >
        Registrer
      </button>
    </div>

    <div v-if="showLogin" class="form-container">
      <h2>Logg inn</h2>
      <form @submit.prevent="login">
        <div class="form-group">
          <label for="login-username">Brukernavn</label>
          <input
            type="text"
            id="login-username"
            v-model="loginUsername"
            required
            autocomplete="username"
          />
        </div>
        <div class="form-group">
          <label for="login-password">Passord</label>
          <input
            type="password"
            id="login-password"
            v-model="loginPassword"
            required
            autocomplete="current-password"
          />
        </div>
        <div v-if="loginError" class="error-message">{{ loginError }}</div>
        <button type="submit" class="submit-btn">Logg inn</button>
      </form>
    </div>

    <div v-else class="form-container">
      <h2>Registrer deg</h2>
      <form @submit.prevent="register">
        <div class="form-group">
          <label for="register-username">Brukernavn</label>
          <input
            type="text"
            id="register-username"
            v-model="registerUsername"
            required
            autocomplete="username"
          />
        </div>
        <div class="form-group">
          <label for="register-email">E-post</label>
          <input
            type="email"
            id="register-email"
            v-model="registerEmail"
            required
            autocomplete="email"
          />
        </div>
        <div class="form-group">
          <label for="register-password">Passord</label>
          <input
            type="password"
            id="register-password"
            v-model="registerPassword"
            required
            autocomplete="new-password"
          />
        </div>
        <button type="submit" class="submit-btn">Registrer</button>
      </form>
      <div v-if="registerMessage" class="message" :class="{ 'success': registerSuccess, 'error': !registerSuccess }">
        {{ registerMessage }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import type { AxiosError } from 'axios'

const showLogin = ref(true)
const loginUsername = ref('')
const loginPassword = ref('')
const registerUsername = ref('')
const registerEmail = ref('')
const registerPassword = ref('')
const registerMessage = ref('')
const registerSuccess = ref(false)
const loginError = ref('')

const router = useRouter()

const login = async () => {
  try {
    const response = await axios.post('/api/auth/login', {
      username: loginUsername.value,
      password: loginPassword.value
    })

    // Clear any previous error
    loginError.value = ''

    // Fetch user data after successful login
    const userResponse = await axios.get('/api/me')
    if (userResponse.data) {
      // Redirect to home page instead of non-existent dashboard
      router.push('/')
    }
  } catch (error: unknown) {
    console.error('Login error:', error)
    const axiosError = error as AxiosError

    // Handle response data properly to fix the TypeScript error
    if (axiosError.response?.data) {
      if (typeof axiosError.response.data === 'object') {
        // Convert object to string if needed
        loginError.value = JSON.stringify(axiosError.response.data)
      } else {
        // Use as string
        loginError.value = String(axiosError.response.data)
      }
    } else {
      loginError.value = 'Innlogging feilet. Sjekk brukernavn og passord.'
    }
  }
}

const register = async () => {
  try {
    registerMessage.value = ''
    const response = await axios.post(
      '/api/auth/register',
      {
        username: registerUsername.value,
        email: registerEmail.value,
        password: registerPassword.value
      }
    )

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
  } catch (error: unknown) {
    registerSuccess.value = false
    console.error('Registration error:', error)
    const axiosError = error as AxiosError

    // Handle response data properly to fix the TypeScript error
    if (axiosError.response?.data) {
      if (typeof axiosError.response.data === 'object') {
        // Convert object to string if needed
        registerMessage.value = JSON.stringify(axiosError.response.data)
      } else {
        // Use as string
        registerMessage.value = String(axiosError.response.data)
      }
    } else {
      registerMessage.value = 'Registrering feilet. Vennligst prøv igjen.'
    }
  }
}
</script>

<style scoped>
.login-register-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.tab-selector {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.tab-selector button {
  flex: 1;
  padding: 10px;
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-selector button.active {
  border-bottom: 2px solid #4f46e5;
  color: #4f46e5;
  font-weight: bold;
}

.form-container {
  padding: 10px 0;
}

h2 {
  margin-bottom: 20px;
  color: #333;
  font-size: 1.5rem;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  color: #555;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.submit-btn {
  width: 100%;
  padding: 12px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}

.submit-btn:hover {
  background: #4338ca;
}

.error-message {
  color: #ef4444;
  margin-bottom: 15px;
}

.message {
  padding: 10px;
  border-radius: 4px;
  margin-top: 15px;
}

.success {
  background-color: #dcfce7;
  color: #166534;
}

.error {
  background-color: #fee2e2;
  color: #b91c1c;
}
</style>
