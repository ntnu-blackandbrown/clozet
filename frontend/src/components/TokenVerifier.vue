<template>
  <div class="token-verifier">
    <p v-if="loading">Validerer token...</p>
    <p v-if="error" class="error">{{ errorMessage }}</p>
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

onMounted(async () => {
  if (!token.value) {
    error.value = true
    errorMessage.value = 'Token mangler i URL.'
    return
  }

  loading.value = true
  try {
    await AuthService.verifyToken(props.verifyEndpoint, token.value)

    // Redirect with or without token based on prop
    if (props.includeTokenInRedirect) {
      router.push({ path: props.redirectPath, query: { token: token.value } })
    } else {
      router.push(props.redirectPath)
    }
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
