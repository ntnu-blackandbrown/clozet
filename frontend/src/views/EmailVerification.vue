<template>
  <div class="email-verification">
    <h2>E-postverifisering</h2>
    <p v-if="message">{{ message }}</p>
    <p v-else>Verifiserer...</p>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/api/axios'
import { useAuthStore } from '@/stores/AuthStore'

const route = useRoute()
const router = useRouter()
const message = ref('')
const authStore = useAuthStore()

onMounted(async () => {
  const token = route.query.token
  if (token && typeof token === 'string') {
    try {
      const response = await axios.get(`/api/users/verify?token=${token}`, { withCredentials: true })
      message.value = response.data

      // Hent innlogget brukerinfo, om nødvendig
      await authStore.fetchCurrentUser()

      // Etter en kort forsinkelse, omdiriger til dashboard
      setTimeout(() => {
        router.push('/dashboard')
      }, 1500)
    } catch (error: any) {
      message.value = error.response?.data || 'Verifisering feilet'
    }
  } else {
    message.value = 'Ingen token funnet.'
  }
})
</script>

<style scoped>
.email-verification {
  text-align: center;
  margin-top: 50px;
}
</style>
