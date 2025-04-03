<template>
  <div class="message-input-container">
    <div class="message-input-wrapper">
      <input
        type="text"
        v-model="messageText"
        placeholder="Type a message..."
        @keyup.enter="handleSend"
      />
      <button class="send-button" :class="{ active: messageText.trim() }" @click="handleSend">
        <i class="fas fa-paper-plane"></i>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const emit = defineEmits<{
  (e: 'send-message', text: string): void
}>()

const messageText = ref('')

const handleSend = () => {
  const text = messageText.value.trim()
  if (text) {
    emit('send-message', text)
    messageText.value = ''
  }
}
</script>

<style scoped>
.message-input-container {
  padding: 16px 24px;
  border-top: 1px solid #e5e7eb;
  background: white;
}

.message-input-wrapper {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f3f4f6;
  border-radius: 24px;
  padding: 8px 16px;
}

input {
  flex: 1;
  border: none;
  background: transparent;
  padding: 8px 0;
  font-size: 14px;
  outline: none;
}

input::placeholder {
  color: #6b7280;
}

.send-button {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: transparent;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.send-button.active {
  background: #4f46e5;
  color: white;
}

.send-button:hover.active {
  background: #4338ca;
}
</style>
