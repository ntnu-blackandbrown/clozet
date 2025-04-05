<template>
  <div class="token-verifier">
  <p v-if="loading">Validerer token...</p>
  <p v-if="error" class="error">{{ errorMessage }}</p>
  </div>
  </template>

<script setup>
  import { ref, onMounted } from 'vue'
  import { useRoute, useRouter } from 'vue-router'
  import axios from '@/api/axios'

  const route = useRoute()
  const router = useRouter()

  const token = ref(route.query.token || '')
  const loading = ref(false)
  const error = ref(false)
  const errorMessage = ref('')

  onMounted(async () => {
    if (!token.value) {
      error.value = true
      errorMessage.value = 'Token mangler i URL.'
      return
    }

    loading.value = true
    try {
      await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/auth/verify?token=${token.value}`)
      // Hvis OK → redirect til nytt passord-skjema med token
      router.push({ path: '/', query: { token: token.value } })
    } catch (err) {
      error.value = true
      errorMessage.value = 'Token er ugyldig eller utløpt.'
    } finally {
      loading.value = false
    }
  })
  </script>

  <style scoped>
  .token-verifier {
    max-width: 600px;
    margin: auto;
    padding: 1rem;
  }
  .error {
    color: red;
  }
  </style>