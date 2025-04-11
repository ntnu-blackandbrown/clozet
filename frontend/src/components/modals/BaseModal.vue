<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  modalTitle: {
    type: String,
    default: 'Modal Dialog',
  },
  modalId: {
    type: String,
    default: () => `modal-${Math.random().toString(36).substr(2, 9)}`,
  },
  hideCloseButton: {
    type: Boolean,
    default: false,
  },
  maxWidth: {
    type: String,
    default: '900px',
  },
  padding: {
    type: String,
    default: '2rem',
  },
})

const modalIdRef = ref(props.modalId)
const titleId = ref(`${modalIdRef.value}-title`)
const emit = defineEmits(['close'])

// Store the element that had focus before the modal was opened
const previouslyFocused = ref(null)

const close = () => {
  emit('close')
  // Return focus to the element that had focus before the modal was opened
  if (previouslyFocused.value) {
    previouslyFocused.value.focus()
  }
}

const handleKeydown = (event) => {
  if (event.key === 'Escape') {
    close()
  }
}

onMounted(() => {
  // Store the element that had focus before the modal was opened
  previouslyFocused.value = document.activeElement
  // Set focus to the modal container
  setTimeout(() => {
    const modalContainer = document.getElementById(modalIdRef.value)
    if (modalContainer) {
      modalContainer.focus()
    }
  }, 50)

  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <div class="backdrop" @click.self="close" aria-hidden="true">
    <div
      class="container"
      :style="{ maxWidth: maxWidth, padding: padding }"
      role="dialog"
      aria-modal="true"
      :aria-labelledby="titleId"
      :id="modalIdRef"
      tabindex="-1"
    >
      <button v-if="!hideCloseButton" class="close-button" @click="close" aria-label="Close modal">
        ×
      </button>
      <div :id="titleId" class="visually-hidden">{{ modalTitle }}</div>
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

.visually-hidden {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

@media (max-width: 768px) {
  .container {
    padding: 1.5rem;
    width: 95%;
  }
}
</style>
