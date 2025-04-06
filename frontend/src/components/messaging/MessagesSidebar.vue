<script setup lang="ts">
import { computed, ref, onMounted } from 'vue'
import axios from 'axios'
import type { Message, Conversation } from '@/types/messaging'

interface ItemDTO {
  id: number
  images: { imageUrl: string }[]
}

const props = defineProps<{
  conversations: Conversation[]
  activeConversationId: number
  receiverUsernames: Map<number, string>
}>()

const emit = defineEmits(['select-chat'])

const itemImages = ref<Map<number, string>>(new Map())

const activeConversation = computed(() => {
  return props.conversations.find((conversation) => conversation.id === props.activeConversationId)
})

const getLatestMessage = (conversation: Conversation) => {
  if (!conversation.listOfMessages || conversation.listOfMessages.length === 0) {
    return 'No messages yet'
  }
  // Sort messages by createdAt in descending order and get the first one
  const sortedMessages = [...conversation.listOfMessages].sort(
    (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
  )
  return sortedMessages[0].content
}

const getReceiverUsername = (conversation: Conversation) => {
  console.log(`Getting username for conversation ${conversation.id}:`)
  const numericReceiverId = Number(conversation.receiverId)
  console.log(`- receiverId: ${numericReceiverId}`)
  console.log(`- receiverName: ${conversation.receiverName}`)

  // Try to get username from the map using numeric ID
  const usernameFromMap = props.receiverUsernames.get(numericReceiverId)
  console.log(`- username from map: ${usernameFromMap}`)

  // Use fallbacks in order: map username -> conversation receiverName -> Unknown User
  const username = usernameFromMap || conversation.receiverName || 'Unknown User'
  console.log(`- final username: ${username}`)
  return username
}

onMounted(async () => {
  // Fetch images for all items in conversations
  for (const conversation of props.conversations) {
    try {
      const response = await axios.get<ItemDTO>(`/api/items/${conversation.itemId}`)
      if (response.data.images && response.data.images.length > 0) {
        itemImages.value.set(conversation.itemId, response.data.images[0].imageUrl)
      }
    } catch (error) {
      console.error(`Failed to fetch images for item ${conversation.itemId}:`, error)
    }
  }
})
</script>

<template>
  <div class="messages-sidebar">
    <div class="messages-header">
      <h1>
        Messages <span class="message-count">{{ conversations.length }}</span>
      </h1>
      <button class="new-message-btn">
        <i class="fas fa-pen"></i>
      </button>
    </div>

    <div class="search-bar">
      <i class="fas fa-search"></i>
      <input type="text" placeholder="Search..." />
    </div>

    <div class="messages-list">
      <div
        v-for="conversation in conversations"
        :key="conversation.id"
        class="chat-item"
        :class="{ active: conversation.id === activeConversationId }"
        @click="$emit('select-chat', conversation.id)"
      >
        <div class="chat-avatar">
          <img
            v-if="itemImages.get(conversation.itemId)"
            :src="itemImages.get(conversation.itemId)"
            :alt="getReceiverUsername(conversation)"
          />
        </div>
        <div class="chat-info">
          <div class="chat-name">{{ getReceiverUsername(conversation) }}</div>
          <div class="chat-preview">{{ getLatestMessage(conversation) }}</div>
        </div>
        <div class="chat-meta">
          <div class="chat-time">{{ conversation.latestMessageTimestamp }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.messages-sidebar {
  width: 350px;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
}

.messages-header {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e5e7eb;
}

.messages-header h1 {
  font-size: 24px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.message-count {
  background: #e5e7eb;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 14px;
}

.new-message-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  background: transparent;
  cursor: pointer;
}

.search-bar {
  padding: 16px 20px;
  position: relative;
  border-bottom: 1px solid #e5e7eb;
}

.search-bar input {
  width: 80%;
  padding: 12px 12px 12px 40px;
  border: none;
  border-radius: 24px;
  font-size: 14px;
  background: #f3f4f6;
  outline: none;
}

.search-bar input::placeholder {
  color: #6b7280;
}

.search-bar i {
  position: absolute;
  left: 35px;
  top: 50%;
  transform: translateY(-50%);
  color: #6b7280;
  font-size: 14px;
}

.messages-list {
  flex: 1;
  overflow-y: auto;
}

.chat-item {
  display: flex;
  padding: 16px 20px;
  gap: 12px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-bottom: 1px solid #f3f4f6;
}

.chat-item:hover {
  background-color: #f3f4f6;
}

.chat-item.active {
  background-color: #eff6ff;
}

.chat-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #e5e7eb;
  overflow: hidden;
}

.chat-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.chat-info {
  flex: 1;
}

.chat-name {
  font-weight: 600;
  margin-bottom: 4px;
}

.chat-preview {
  font-size: 14px;
  color: #6b7280;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 200px;
}

.chat-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.chat-time {
  font-size: 12px;
  color: #6b7280;
}

.unread-badge {
  background: #4f46e5;
  color: white;
  padding: 2px 6px;
  border-radius: 12px;
  font-size: 12px;
}
</style>
