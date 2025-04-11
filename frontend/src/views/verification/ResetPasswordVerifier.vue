<template>
  <div class="verification-page">
    <h1>Password Reset Verification</h1>
    <p class="instructions">
      We're verifying your password reset token. After verification, you'll be able to create a new password.
    </p>
    <TokenVerifier
      verifyEndpoint="/api/auth/reset-password/validate"
      redirectPath="/reset-password"
      :includeTokenInRedirect="true"
    />
  </div>
</template>

<script setup>
import TokenVerifier from '@/components/TokenVerifier.vue'
</script>

<style scoped>
.verification-page {
  max-width: 600px;
  margin: 2rem auto;
  padding: 1rem;
  text-align: center;
}
h1 {
  margin-bottom: 1rem;
}
.instructions {
  margin-bottom: 2rem;
  color: #6c757d;
}
</style>
