<script setup>
import { defineEmits, ref } from 'vue'

const hideCloseButton = ref(false)
const maxWidth = ref('900px')
const padding = ref('2rem')

const emit = defineEmits(['close'])

const close = () => {
  emit('close')
}
</script>

<template>
  <div class="backdrop" @click.self="close">
    <div class="container" :style="{ maxWidth: maxWidth, padding: padding }">
      <button v-if="!hideCloseButton" class="close-button" @click="close">×</button>
      <slot></slot>
    </div>
  </div>
</template>

<style scoped>
.backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
  padding: 2rem;
  overflow-y: auto;
}

.container {
  background: white;
  padding: 2rem;
  border-radius: 16px;
  width: 90%;
  max-width: 900px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  margin: auto;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
}

.container::-webkit-scrollbar {
  width: 8px;
}

.container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.close-button {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
  z-index: 10;
  width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.close-button:hover {
  background-color: #f1f1f1;
  color: #333;
}

@media (max-width: 768px) {
  .container {
    padding: 1.5rem;
    width: 95%;
  }
}
</style>
