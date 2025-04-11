<template>
  <div class="token-verifier" role="status" aria-live="polite">
    <p v-if="loading" aria-busy="true">Validerer token...</p>
    <div v-if="error" class="error" role="alert">
      <h2>Verification failed</h2>
      <p>{{ errorMessage }}</p>
      <p class="suggestion">You can try:</p>
      <ul>
        <li>Check that you have clicked the entire link from your email</li>
        <li>Try to log in with your username and password directly</li>
        <li>Contact support if the problem persists</li>
      </ul>
      <button @click="navigateToLogin" class="button">Gå til innlogging</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { AuthService } from '@/api/services/AuthService'
const props = defineProps({
  verifyEndpoint: {
    type: String,
    required: true,
  },
  redirectPath: {
    type: String,
    required: true,
  },
  includeTokenInRedirect: {
    type: Boolean,
    default: false,
  },
})

const route = useRoute()
const router = useRouter()

const token = ref(route.query.token || '')
const loading = ref(false)
const error = ref(false)
const errorMessage = ref('')

const navigateToLogin = () => {
  router.push('/login')
}

onMounted(async () => {
  if (!token.value) {
    error.value = true
    errorMessage.value = 'Token mangler i URL.'
    return
  }

  loading.value = true
  try {
    await AuthService.verifyToken(token.value)

    if (props.includeTokenInRedirect) {
      router.push({ path: props.redirectPath, query: { token: token.value } })
    } else {
      router.push(props.redirectPath)
    }
  } catch (err) {
    console.error('Token verification failed:', err)
    error.value = true
    errorMessage.value = 'Token er ugyldig eller utløpt. Dette kan skyldes at serveren forventer at tokenet er i cookies, men det sendes som en URL parameter.'
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
  color: #842029;
  background-color: #f8d7da;
  border: 1px solid #f5c2c7;
  border-radius: 5px;
  padding: 1rem;
  margin-top: 1rem;
}
.error h2 {
  margin-top: 0;
}
.suggestion {
  margin-bottom: 0.5rem;
}
ul {
  margin-bottom: 1.5rem;
}
.button {
  background-color: #0d6efd;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 0.5rem 1rem;
  cursor: pointer;
}
.button:hover {
  background-color: #0b5ed7;
}
</style>
