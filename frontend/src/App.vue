<script setup>
import { ref } from 'vue'
import LoginRegisterModal from './components/modals/LoginRegisterModal.vue'
import ProductList from './components/ProductList.vue'
import { RouterView } from 'vue-router'

const showLoginModal = ref(false)
</script>

<template>
  <div :class="{ blurred: showLoginModal }">
    <header>
      <img alt="Vue logo" class="logo" src="@/assets/logo.png" width="125" height="125" />

        <h1>Welcome to Clozet!</h1>
        <nav>
          <RouterLink to="/product-display">Product Display</RouterLink>
        </nav>
        <button @click="showLoginModal = true" class="login-button">Login / Register</button>
    </header>
    <main>
      <RouterView />
    </main>
  </div>

  <LoginRegisterModal v-if="showLoginModal" @close="showLoginModal = false" />
</template>

<style scoped>
header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  border-bottom: 1px solid #e5e7eb;
}

.blurred {
  filter: blur(5px);
  transition: filter 0.3s ease;
  pointer-events: none;
  user-select: none;
}

.login-button {
  padding: 0.5rem 1rem;
  background-color: #333;
  color: white;
  border: none;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.login-button:hover {
  background-color: #444;
}

main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}
</style>
