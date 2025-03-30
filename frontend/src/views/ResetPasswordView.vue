<template>
  <div class="reset-password-container">
    <h2>Tilbakestill passord</h2>
    <p class="description" v-if="!tokenError">
      Oppgi ditt nye passord nedenfor.
    </p>

    <form @submit.prevent="resetPassword" class="reset-password-form" v-if="!tokenError && !resetSuccess">
      <div class="form-group">
        <label for="newPassword">Nytt passord</label>
        <input
          type="password"
          id="newPassword"
          v-model="newPassword"
          required
          placeholder="Skriv inn nytt passord"
          :disabled="isSubmitting"
        />
      </div>

      <div class="form-group">
        <label for="confirmPassword">Bekreft nytt passord</label>
        <input
          type="password"
          id="confirmPassword"
          v-model="confirmPassword"
          required
          placeholder="Skriv inn passordet på nytt"
          :disabled="isSubmitting"
        />
        <p class="password-error" v-if="passwordsDoNotMatch">
          Passordene stemmer ikke overens
        </p>
      </div>

      <div class="form-actions">
        <button type="submit" class="submit-button" :disabled="isSubmitting || passwordsDoNotMatch">
          {{ isSubmitting ? 'Oppdaterer...' : 'Oppdater passord' }}
        </button>
      </div>
    </form>

    <div v-if="tokenError" class="token-error">
      <p>{{ tokenError }}</p>
      <router-link to="/forgot-password" class="back-link">Gå til glemt passord</router-link>
    </div>

    <div v-if="resetSuccess" class="reset-success">
      <p>Passordet ditt har blitt oppdatert.</p>
      <router-link to="/login" class="login-link">Gå til innlogging</router-link>
    </div>

    <div v-if="errorMessage && !tokenError" class="error-message">
      {{ errorMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const token = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const isSubmitting = ref(false)
const tokenError = ref('')
const errorMessage = ref('')
const resetSuccess = ref(false)

const passwordsDoNotMatch = computed(() => {
  return newPassword.value && confirmPassword.value && newPassword.value !== confirmPassword.value
})

onMounted(() => {
  // Get token from query string
  token.value = route.query.token

  if (!token.value) {
    tokenError.value = 'Ugyldig tilbakestillingslenke. Vennligst be om en ny tilbakestillingslenke.'
  }
})

async function resetPassword() {
  if (passwordsDoNotMatch.value) return
  if (!newPassword.value || !confirmPassword.value) return

  isSubmitting.value = true
  errorMessage.value = ''

  try {
    const response = await axios.post(
      `/api/auth/reset-password?token=${token.value}&newPassword=${encodeURIComponent(newPassword.value)}`,
      {},
      { withCredentials: true }
    )

    resetSuccess.value = true

    // Redirect to login after 3 seconds
    setTimeout(() => {
      router.push('/login')
    }, 3000)
  } catch (error) {
    errorMessage.value = error.response?.data || 'Det oppstod en feil under tilbakestillingen. Vennligst prøv igjen.'
    console.error('Password reset error:', error)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.reset-password-container {
  max-width: 480px;
  margin: 40px auto;
  padding: 24px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 16px;
  color: #333;
  text-align: center;
}

.description {
  margin-bottom: 24px;
  color: #666;
  text-align: center;
}

.reset-password-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label {
  font-weight: 500;
  color: #333;
}

input {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

input:focus {
  border-color: #333;
  outline: none;
}

.password-error {
  color: #c62828;
  font-size: 14px;
  margin-top: 4px;
}

.form-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 8px;
}

.submit-button {
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

.submit-button:hover:not(:disabled) {
  background-color: #444;
}

.submit-button:disabled {
  background-color: #aaa;
  cursor: not-allowed;
}

.token-error,
.reset-success,
.error-message {
  text-align: center;
  padding: 16px;
  border-radius: 4px;
  margin: 16px 0;
}

.token-error {
  background-color: #ffebee;
  color: #c62828;
}

.reset-success {
  background-color: #e6f7e6;
  color: #2e7d32;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
}

.back-link,
.login-link {
  display: block;
  margin-top: 16px;
  color: #666;
  text-decoration: none;
}

.back-link:hover,
.login-link:hover {
  text-decoration: underline;
}

.login-link {
  color: #2e7d32;
  font-weight: 500;
}
</style>
