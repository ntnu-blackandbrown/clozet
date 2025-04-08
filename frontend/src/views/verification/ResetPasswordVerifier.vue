<template>
  <TokenVerifier
    verifyEndpoint="/api/auth/reset-password/validate"
    redirectPath="/forgot-password"
    :includeTokenInRedirect="false"
  />
</template>

<script setup>
import TokenVerifier from '@/components/TokenVerifier.vue'
</script>
