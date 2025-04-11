<template>
  <main class="not-found" role="main">
    <h1 id="error-title">404</h1>
    <h2 id="error-subtitle">Page Not Found</h2>
    <p>Sorry, the page you are looking for does not exist.</p>
    <router-link to="/" aria-label="Return to home page">Go back to Home</router-link>
  </main>
</template>

<script setup lang="ts">
// No specific script setup needed for this simple view
</script>

<style scoped>
.not-found {
  text-align: center;
  margin-top: 50px;
  padding: 2rem;
}

h1 {
  font-size: 5em;
  color: #ff6f61; /* Example color */
}

h2 {
  color: #555;
}

p {
  color: #777;
  margin: 1rem 0;
}

a {
  color: #007bff; /* Example link color */
  text-decoration: none;
  display: inline-block;
  padding: 0.5rem 1rem;
  background-color: #f8f9fa;
  border-radius: 4px;
  margin-top: 1rem;
  border: 1px solid #dee2e6;
  transition: all 0.2s ease;
}

a:hover {
  text-decoration: underline;
  background-color: #e9ecef;
}

a:focus {
  outline: 2px solid #007bff;
  outline-offset: 2px;
}
</style>
