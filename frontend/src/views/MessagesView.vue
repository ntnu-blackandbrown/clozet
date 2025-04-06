<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import MessagesSidebar from '../components/messaging/MessagesSidebar.vue'
import ChatArea from '../components/messaging/ChatArea.vue'
import axios from '@/api/axios'
import { useAuthStore } from '@/stores/AuthStore'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const chats = ref([])
const chatMessages = ref({})
const receiverDetails = ref({})
const receiverUsernames = ref(new Map())

const activeChat = computed(() => {
  const chatId = parseInt(route.params.chatId)
  return isNaN(chatId) ? null : chatId
})

const handleChatSelect = (chatId) => {
  router.push(`/messages/${chatId}`)
}

const fetchReceiverDetails = async (receiverId) => {
  try {
    console.log(`Fetching details for receiver ID: ${receiverId}`)
    // Convert receiverId to number to ensure consistent type
    const numericReceiverId = Number(receiverId)
    const response = await axios.get(`/api/users/${numericReceiverId}`)

    if (response.data) {
      receiverDetails.value = response.data
      // Make sure we're using the numeric ID as the key
      receiverUsernames.value.set(numericReceiverId, response.data.usernameOrEmail || response.data.username || response.data.firstName)
      console.log(`Stored username for receiver ${numericReceiverId}:`, response.data)
      console.log('Current receiverUsernames map:', Object.fromEntries(receiverUsernames.value))
      return response.data
    } else {
      console.warn(`No data received for receiver ID ${numericReceiverId}`)
      return null
    }
  } catch (error) {
    console.error(`Failed to fetch receiver details for ID ${receiverId}:`, error.response || error)
    return null
  }
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
      timestamp: new Date().toISOString(),
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
    const response = await axios.get('/api/conversations', {
      params: {
        userId: authStore.user?.id?.toString() || ''
      }
    })

    // Log the entire response data structure
    console.log('Full response data:', response.data)

    // Log each conversation's details
    response.data.forEach((conversation, index) => {
      console.log(`Conversation ${index + 1}:`, {
        id: conversation.id,
        receiverId: conversation.receiverId,
        senderId: conversation.senderId,
        receiverName: conversation.receiverName,
        latestMessage: conversation.latestMessage
      })
    })

    chats.value = response.data
    console.log('Updated chats value:', chats.value)
    console.log('Initial receiverUsernames map:', Object.fromEntries(receiverUsernames.value))

    // Fetch receiver details for all conversations
    for (const conversation of chats.value) {
      if (conversation.receiverId) {
        console.log(`Processing conversation ${conversation.id} with receiverId ${conversation.receiverId}`)
        await fetchReceiverDetails(conversation.receiverId)
      } else {
        console.warn(`Conversation ${conversation.id} has no receiverId`)
      }
    }

    console.log('Final receiverUsernames map:', Object.fromEntries(receiverUsernames.value))

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
