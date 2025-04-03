<script setup lang="ts">
import { ref, computed } from 'vue'
import MessageInput from './MessageInput.vue'

interface Message {
  id: number
  content: string
  createdAt: string
  senderId: number
  receiverId: number
  conversationId: number
}

interface Conversation {
  id: number
  receiverName: string
  itemId: number
  listOfMessages: Message[]
  latestMessageTimestamp: string
}

const props = defineProps<{
  activeConversationId: number
  conversations: Conversation[]
}>()

const emit = defineEmits(['send-message'])

const activeConversation = computed(() => {
  return props.conversations.find((conversation) => conversation.id === props.activeConversationId)
})

const handleSendMessage = (text: string) => {
  if (!activeConversation.value) return

  emit('send-message', {
    conversationId: props.activeConversationId,
    message: {
      content: text,
      createdAt: new Date().toISOString(),
      senderId: 1, // This should be replaced with actual user ID
      receiverId: activeConversation.value.id,
      conversationId: props.activeConversationId
    },
  })
}
</script>

<template>
  <div class="chat-area">
    <div class="chat-header">
      <div class="chat-contact">
        <div class="contact-avatar"></div>
        <div class="contact-info">
          <div class="contact-name">{{ activeConversation?.receiverName || 'Select a chat' }}</div>
        </div>
      </div>
      <div class="chat-actions" v-if="activeConversation">
        <button class="action-btn">
          <i class="fas fa-ellipsis-v"></i>
        </button>
      </div>
    </div>

    <div class="chat-messages" v-if="activeConversation?.listOfMessages">
      <template v-for="(message, index) in activeConversation.listOfMessages" :key="message.id">
        <!-- Add date divider if it's the first message or if the date changes -->
        <div v-if="index === 0" class="date-divider">Today</div>

        <div class="message" :class="{ sent: message.senderId === 1, received: message.senderId !== 1 }">
          <!-- Text message -->
          <div class="message-content">
            {{ message.content }}
          </div>

          <div class="message-time">{{ new Date(message.createdAt).toLocaleTimeString('en-US', {
            hour: '2-digit',
            minute: '2-digit',
            hour12: false,
          }) }}</div>
          <div v-if="message.senderId === 1" class="message-status">Sent</div>
        </div>
      </template>
    </div>
    <div v-else class="no-chat-selected">Select a chat to start messaging</div>

    <MessageInput v-if="activeConversation" @send-message="handleSendMessage" />
  </div>
</template>


<style scoped>
.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
}

.chat-header {
  padding: 12px 24px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-contact {
  display: flex;
  align-items: center;
  gap: 12px;
}

.contact-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #e5e7eb;
}

.contact-name {
  font-weight: 600;
}

.chat-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  background: white;
  cursor: pointer;
}

.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.no-chat-selected {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  font-size: 16px;
}

.date-divider {
  text-align: center;
  color: #6b7280;
  font-size: 14px;
  margin: 24px 0;
}

.message {
  max-width: 70%;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.message.received {
  align-self: flex-start;
}

.message.sent {
  align-self: flex-end;
}

.message-content {
  background: #f3f4f6;
  padding: 12px 16px;
  border-radius: 12px;
  border-top-left-radius: 4px;
}

.message.sent .message-content {
  background: #4f46e5;
  color: white;
  border-top-left-radius: 12px;
  border-top-right-radius: 4px;
}

.message-time {
  font-size: 12px;
  color: #6b7280;
  align-self: flex-end;
}

.message-status {
  font-size: 12px;
  color: #6b7280;
}

.message-file {
  background: #f3f4f6;
  padding: 12px 16px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.file-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.file-name {
  font-weight: 500;
}

.file-size {
  font-size: 12px;
  color: #6b7280;
}

.message-audio {
  background: #4f46e5;
  padding: 12px 16px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  color: white;
}

.play-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 2px solid white;
  background: transparent;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.audio-wave {
  flex: 1;
  height: 24px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
}

.audio-duration {
  font-size: 14px;
}
</style>
