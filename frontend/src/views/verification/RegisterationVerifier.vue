<template>
  <TokenVerifier
    verifyEndpoint="/api/auth/verify"
    redirectPath="/"
    :includeTokenInRedirect="true"
  />
</template>

<script setup>
import TokenVerifier from '@/components/TokenVerifier.vue'
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
