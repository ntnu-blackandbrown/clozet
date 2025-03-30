<template>
  <div class="forgot-password-container">
    <h2>Glemt passord</h2>
    <p class="description">
      Skriv inn e-postadressen din, så sender vi deg en lenke for å tilbakestille passordet ditt.
    </p>

    <form @submit.prevent="requestReset" class="forgot-password-form">
      <div class="form-group">
        <label for="email">E-post</label>
        <input
          type="email"
          id="email"
          v-model="email"
          required
          placeholder="Din e-postadresse"
          :disabled="isSubmitting"
        />
      </div>

      <div class="form-actions">
        <button type="submit" class="submit-button" :disabled="isSubmitting">
          {{ isSubmitting ? 'Sender...' : 'Send tilbakestillingslenke' }}
        </button>
        <router-link to="/login" class="back-link">Tilbake til innlogging</router-link>
      </div>
    </form>

    <div v-if="message" class="message" :class="{ success: isSuccess, error: !isSuccess }">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'

const email = ref('')
const message = ref('')
const isSuccess = ref(false)
const isSubmitting = ref(false)

async function requestReset() {
  if (!email.value) return

  isSubmitting.value = true
  message.value = ''

  try {
    const response = await axios.post(
      `/api/auth/request-reset?email=${encodeURIComponent(email.value)}`,
      {},
      { withCredentials: true }
    )

    isSuccess.value = true
    message.value = 'Hvis e-postadressen er registrert hos oss, har du nå mottatt en e-post med instruksjoner for å tilbakestille passordet ditt.'
    email.value = '' // Clear the email field
  } catch (error) {
    isSuccess.value = false
    message.value = error.response?.data || 'Det oppstod en feil. Vennligst prøv igjen senere.'
    console.error('Reset password request error:', error)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.forgot-password-container {
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

.forgot-password-form {
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

.back-link {
  text-align: center;
  color: #666;
  text-decoration: none;
  font-size: 14px;
}

.back-link:hover {
  text-decoration: underline;
}

.message {
  margin-top: 24px;
  padding: 12px;
  border-radius: 4px;
  text-align: center;
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
