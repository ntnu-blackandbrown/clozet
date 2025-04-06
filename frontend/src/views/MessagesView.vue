<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import MessagesSidebar from '../components/messaging/MessagesSidebar.vue'
import ChatArea from '../components/messaging/ChatArea.vue'
import axios from '@/api/axios'
import { useAuthStore } from '@/stores/AuthStore'

// Core stores and router
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// Reactive state
const chats = ref([]) // All conversations the user is in
const chatMessages = ref({}) // Messages grouped by chatId
const receiverDetails = ref({}) // Active receiver's details
const receiverUsernames = ref(new Map()) // Map of receiverId -> username

// Returns currently active chatId from route (or null)
const activeChat = computed(() => {
  const chatId = route.params.chatId
  return chatId || null
})

/**
 * When a chat is selected from the sidebar:
 * - update the route
 * - fetch messages
 * - fetch receiver details if needed
 */
const handleChatSelect = async (chatId) => {
  console.log('Selecting chat:', chatId)
  try {
    router.push(`/messages/${chatId}`)

    const selectedConversation = chats.value.find(chat => chat.id === chatId)
    if (!selectedConversation) {
      console.error('Selected conversation not found')
      return
    }

    const mssgResponse = await axios.get('/api/messages', {
      params: {
        senderId: authStore.user?.id?.toString(),
        receiverId: selectedConversation.receiverId?.toString()
      }
    })

    chatMessages.value[chatId] = mssgResponse.data

    if (selectedConversation.receiverId) {
      await fetchReceiverDetails(selectedConversation.receiverId)
    }
  } catch (error) {
    console.error('Failed to fetch messages for conversation:', error)
  }
}

/**
 * Fetch and store user details for a given receiverId.
 * Saves a readable name to the receiverUsernames map.
 */
const fetchReceiverDetails = async (receiverId) => {
  try {
    const numericReceiverId = Number(receiverId)
    const response = await axios.get(`/api/users/${numericReceiverId}`)

    if (response.data) {
      receiverDetails.value = response.data

      const readableName =
        response.data.usernameOrEmail ||
        response.data.username ||
        response.data.firstName

      receiverUsernames.value.set(numericReceiverId, readableName)
      return response.data
    } else {
      console.warn(`No data received for receiver ID ${numericReceiverId}`)
      return null
    }
  } catch (error) {
    console.error(`Failed to fetch receiver details for ID ${receiverId}:`, error)
    return null
  }
}

/**
 * Handles sending a new message to the backend.
 * Updates local chat message list after sending.
 */
const handleNewMessage = async ({ chatId, message }) => {
  if (!chatMessages.value[chatId]) {
    chatMessages.value[chatId] = []
  }

  try {
    await axios.post('/api/messages', {
      senderId: message.senderId.toString(),
      receiverId: message.receiverId.toString(),
      content: message.content,
      timestamp: new Date().toISOString(),
    })

    const selectedConversation = chats.value.find(chat => chat.id === chatId)
    if (selectedConversation) {
      const mssgResponse = await axios.get('/api/messages', {
        params: {
          senderId: authStore.user?.id?.toString(),
          receiverId: selectedConversation.receiverId?.toString()
        }
      })
      chatMessages.value[chatId] = mssgResponse.data
    }
  } catch (error) {
    console.error('Failed to send message:', error)
  }
}

/**
 * Lifecycle hook: on mount
 * - fetches all user conversations
 * - fetches all receiver details
 * - preloads messages for first conversation or active route
 */
onMounted(async () => {
  try {
    // Step 1: Load all conversations for the logged-in user
    const response = await axios.get('/api/conversations', {
      params: {
        userId: authStore.user?.id?.toString() || ''
      }
    })

    chats.value = response.data

    // Step 2: Fetch receiver details for each conversation
    for (const convo of chats.value) {
      if (convo.receiverId) {
        await fetchReceiverDetails(convo.receiverId)
      } else {
        console.warn(`Conversation ${convo.id} has no receiverId`)
      }
    }

    // Step 3: If on /messages and we have chats, load the first one
    if (chats.value.length > 0 && !route.params.chatId) {
      const firstChat = chats.value[0]
      router.replace(`/messages/${firstChat.id}`)

      const mssgResponse = await axios.get('/api/messages', {
        params: {
          senderId: authStore.user?.id?.toString(),
          receiverId: firstChat.receiverId?.toString()
        }
      })
      chatMessages.value[firstChat.id] = mssgResponse.data
    } else if (chats.value.length === 0) {
      console.log('No conversations available')
    }

    // Step 4: If navigating directly to a chat URL, load its messages
    if (activeChat.value) {
      const activeConversation = chats.value.find(chat => chat.id === activeChat.value)
      if (activeConversation) {
        const mssgResponse = await axios.get('/api/messages', {
          params: {
            senderId: authStore.user?.id?.toString(),
            receiverId: activeConversation.receiverId?.toString()
          }
        })
        chatMessages.value[activeChat.value] = mssgResponse.data
      }
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
      :receiver-usernames="receiverUsernames"
      @select-chat="handleChatSelect"
    />
    <!-- Right chat area -->
    <ChatArea
      :active-chat="activeChat"
      :messages="chatMessages[activeChat] || []"
      :contact="chats.find((chat) => chat.id === activeChat)"
      :receiver-details="receiverDetails"
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
