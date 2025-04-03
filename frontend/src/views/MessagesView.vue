<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import MessagesSidebar from '../components/messaging/MessagesSidebar.vue'
import ChatArea from '../components/messaging/ChatArea.vue'
import axios from '@/api/axios'

const router = useRouter()
const route = useRoute()

const chats = ref([])
const chatMessages = ref({})

const activeChat = computed(() => {
  const chatId = parseInt(route.params.chatId)
  return isNaN(chatId) ? null : chatId
})

const handleChatSelect = (chatId) => {
  router.push(`/messages/${chatId}`)
}

const handleNewMessage = async ({ chatId, message }) => {
  if (!chatMessages.value[chatId]) {
    chatMessages.value[chatId] = []
  }

  try {
    // Send message to backend
    const response = await axios.post('/api/messages', {
      senderId: message.senderId.toString(),
      receiverId: message.receiverId.toString(),
      content: message.content,
      timestamp: new Date().toISOString()
    })

    //after a successful message, update the chat by fetching the latest messages
    const mssgResponse = await axios.get(`/api/messages/${chatId}`)
    chatMessages.value[chatId] = mssgResponse.data
  } catch (error) {
    console.error('Failed to send message:', error)
    // You might want to show an error notification to the user here
  }
}

// Set initial chat if none selected and fetch conversations
onMounted(async () => {
  try {
    // Fetch all conversations
    const response = await axios.get('/api/conversations')
    chats.value = response.data

    // If there are conversations and no active chat, set the first one as active
    if (chats.value.length > 0 && !activeChat.value) {
      router.replace(`/messages/${chats.value[0].id}`)
    }

    // Fetch messages for the active chat if one is selected
    if (activeChat.value) {
      const mssgResponse = await axios.get(`/api/messages/${activeChat.value}`)
      chatMessages.value[activeChat.value] = mssgResponse.data
    }
  } catch (error) {
    console.error('Failed to fetch conversations:', error)
  }
})
</script>

<template>
  <div class="messages-container">
    <!-- Left sidebar with messages list -->
    <MessagesSidebar
      :conversations="chats"
      :activeConversationId="activeChat"
      @select-chat="handleChatSelect"
    />
    <!-- Right chat area -->
    <ChatArea
      :active-chat="activeChat"
      :messages="chatMessages[activeChat] || []"
      :contact="chats.find((chat) => chat.id === activeChat)"
      @send-message="handleNewMessage"
    />
  </div>
</template>

<style scoped>
.messages-container {
  display: flex;
  height: calc(100vh - 64px); /* Subtract header height */
  background: #ffffff;
}
</style>
