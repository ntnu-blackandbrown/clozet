<script setup>
import { ref } from 'vue'
import LoginRegisterModal from './components/LoginRegisterModal.vue'

const showModal = ref(false)
</script>

<template>

  <div :class="{blurred: showModal}">
    <h1>Welcome to Clozet!</h1>
    <button @click="showModal = true">Login / Register</button>

  </div>

  <LoginRegisterModal v-if="showModal" @close="showModal = false" />
</template>

<style scoped>
.blurred {
  filter: blur(5px);
  transition: filter 0.3s ease;
  pointer-events: none;
  user-select: none;
}
</style>
