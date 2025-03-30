<!-- EmailVerification.vue -->
<template>
  <div>
    <h2>E-postverifisering</h2>
    <p v-if="message">{{ message }}</p>
    <p v-else>Verifiserer ...</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/api/axios'
// Hvis du har en store for å hente brukerinfo, importer den
import { useAuthStore } from '@/stores/AuthStore'

const route = useRoute()
const router = useRouter()
const message = ref('')
const authStore = useAuthStore()

onMounted(async () => {
  const token = route.query.token
  if (!token || typeof token !== 'string') {
    message.value = 'Ugyldig eller manglende token i URL.'
    return
  }
  try {
    const response = await axios.get(`/api/users/verify?token=${token}`, {
      // Viktig dersom du bruker cookies for JWT
      withCredentials: true
    })
    message.value = response.data

    // Hent brukerinfo fra server (om du har et endepunkt for "meg selv")
    await authStore.fetchCurrentUser()

    // Naviger brukeren til dashboard
    router.push('/dashboard')
  } catch (error: any) {
    message.value = 'Verifisering feilet: ' + (error.response?.data || error.message)
  }
})
</script>
