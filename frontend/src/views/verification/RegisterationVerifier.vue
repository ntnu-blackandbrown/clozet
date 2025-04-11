<template>
  <div class="verification-page">
    <h1>E-post verifisering</h1>
    <p class="instructions">
      Vi bekrefter e-postadressen din. Du vil bli videresendt til innloggingssiden hvis verifiseringen er vellykket.
    </p>
    <TokenVerifier
      verifyEndpoint="/api/auth/verify"
      redirectPath="/login"
      :includeTokenInRedirect="false"
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
