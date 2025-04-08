<template>
  <div class="not-found">
    <h1>404</h1>
    <h2>Page Not Found</h2>
    <p>Sorry, the page you are looking for does not exist.</p>
    <router-link to="/">Go back to Home</router-link>
  </div>
</template>

<script setup lang="ts">
// No specific script setup needed for this simple view
</script>

<style scoped>
.not-found {
  text-align: center;
  margin-top: 50px;
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
}

a {
  color: #007bff; /* Example link color */
  text-decoration: none;
}

a:hover {
  text-decoration: underline;
}
</style>
