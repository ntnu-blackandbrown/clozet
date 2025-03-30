<template>
  <div class="email-verification">
    <h2>E-postverifisering</h2>
    <div v-if="isLoading" class="loading">
      <p>Verifiserer din konto...</p>
    </div>
    <div v-else class="verification-result" :class="{ 'success': isSuccess, 'error': !isSuccess }">
      <p>{{ message }}</p>
      <div v-if="isSuccess" class="actions">
        <p>Du blir omdirigert til din konto...</p>
      </div>
      <div v-else class="actions">
        <router-link to="/login" class="login-link">Gå til innlogging</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { useAuthStore } from '@/stores/AuthStore'

const route = useRoute()
const router = useRouter()
const message = ref('')
const isLoading = ref(true)
const isSuccess = ref(false)
const authStore = useAuthStore()

onMounted(async () => {
  const token = route.query.token
  if (token && typeof token === 'string') {
    try {
      const response = await axios.get(`/api/auth/verify?token=${token}`, { withCredentials: true })
      message.value = response.data
      isSuccess.value = true

      // Fetch user info if necessary
      await authStore.fetchUserInfo()

      // After a short delay, redirect to dashboard
      setTimeout(() => {
        router.push('/dashboard')
      }, 2000)
    } catch (error: any) {
      console.error('Verification error:', error)
      message.value = error.response?.data || 'Verifisering feilet. Vennligst kontakt kundeservice.'
      isSuccess.value = false
    } finally {
      isLoading.value = false
    }
  } else {
    message.value = 'Ingen verifikasjonstoken funnet i URLen.'
    isSuccess.value = false
    isLoading.value = false
  }
})
</script>

<style scoped>
.email-verification {
  max-width: 480px;
  margin: 40px auto;
  padding: 24px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: center;
}

h2 {
  margin-bottom: 24px;
  color: #333;
}

.loading {
  padding: 20px;
  font-style: italic;
  color: #666;
}

.verification-result {
  padding: 24px;
  border-radius: 4px;
  margin-top: 16px;
}

.success {
  background-color: #e6f7e6;
  color: #2e7d32;
}

.error {
  background-color: #ffebee;
  color: #c62828;
}

.actions {
  margin-top: 24px;
}

.login-link {
  display: inline-block;
  padding: 10px 20px;
  background-color: #333;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.login-link:hover {
  background-color: #444;
}
</style>
