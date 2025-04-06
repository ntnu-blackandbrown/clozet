<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import MessagesSidebar from '../components/messaging/MessagesSidebar.vue'
import ChatArea from '../components/messaging/ChatArea.vue'
import axios from '@/api/axios'
import { useAuthStore } from '@/stores/AuthStore'
import { useWebsocket } from '@/websocket/websocket'

// Core stores and router
const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const websocket = useWebsocket()

// Reactive state
const chats = ref([]) // All conversations the user is in
const chatMessages = ref({}) // Messages grouped by chatId
const receiverDetails = ref({}) // Active receiver's details
const receiverUsernames = ref(new Map()) // Map of receiverId -> username

// Format timestamp for display
const formatTime = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

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

    console.log('Selected conversation:', selectedConversation)
    console.log('Receiver ID:', selectedConversation.receiverId)

    // Set the receiver ID in the WebSocket store
    if (selectedConversation.receiverId) {
      console.log('Setting receiver ID:', selectedConversation.receiverId.toString())
      websocket.setReceiver(selectedConversation.receiverId.toString())
    } else {
      console.error('Selected conversation has no receiverId')
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
    // Update the sender ID in the WebSocket store
    websocket.updateSender()

    // Step 1: Load all conversations for the logged-in user
    const response = await axios.get('/api/conversations', {
      params: {
        userId: authStore.user?.id?.toString() || ''
      }
    })

    chats.value = response.data
    console.log('Loaded conversations:', chats.value)

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
      console.log('Loading first chat:', firstChat)
      router.replace(`/messages/${firstChat.id}`)

      // Set the receiver ID in the WebSocket store
      if (firstChat.receiverId) {
        console.log('Setting receiver ID for first chat:', firstChat.receiverId.toString())
        websocket.setReceiver(firstChat.receiverId.toString())
      } else {
        console.error('First chat has no receiverId')
      }

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
        console.log('Loading active chat:', activeConversation)

        // Set the receiver ID in the WebSocket store
        if (activeConversation.receiverId) {
          console.log('Setting receiver ID for active chat:', activeConversation.receiverId.toString())
          websocket.setReceiver(activeConversation.receiverId.toString())
        } else {
          console.error('Active chat has no receiverId')
        }

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

// Add this function to log the conversations
const logConversations = () => {
  console.log('Current conversations:', JSON.stringify(chats.value, null, 2))
}

</script>


<template>
  <div :class="['status', websocket.connectionStatusClass]">Status: {{ websocket.connectionStatus }}</div>
  <h2>Messages</h2>

  <div class="websocket-controls">
    <button @click="websocket.connect">Connect</button>
    <button @click="websocket.disconnect">Disconnect</button>
    <button @click="websocket.checkConnection">Check Connection</button>
    <button @click="logConversations">Log Conversations</button>
  </div>

  <div class="websocket-debug">
    <h3>WebSocket Debug Log</h3>
    <div id="log">
      <div v-for="(msg, index) in websocket.logs" :key="index" :class="msg.type" v-html="msg.text"></div>
    </div>
  </div>

  <div class="websocket-messages">
    <h3>WebSocket Messages</h3>
    <div class="messages-container">
      <div v-for="(msg, index) in websocket.messages" :key="index" :class="['message', msg.type === 'sent' ? 'message-sent' : 'message-received']">
        <div class="message-header">
          <span class="message-sender">From: {{ msg.senderId }}</span>
          <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
        </div>
        <div class="message-content">{{ msg.content }}</div>
      </div>
    </div>
  </div>

<div class="controls">
      <h3>Send Message</h3>
      <div class="form-group">
        <label>Message:</label>
        <textarea v-model="websocket.messageContent" @keypress.enter.prevent="websocket.sendMessage"></textarea>
      </div>
      <button @click="websocket.sendMessage" :disabled="!websocket.connected">Send Message</button>
    <button @click="websocket.pingServer" :disabled="!websocket.connected">Ping Server</button>
  </div>
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

.websocket-controls {
  margin-bottom: 15px;
}

.websocket-controls button {
  margin-right: 10px;
  padding: 8px 15px;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.websocket-controls button:hover {
  background-color: #e0e0e0;
}

.websocket-debug, .websocket-messages {
  margin-bottom: 20px;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 15px;
}

.websocket-messages .messages-container {
  height: 300px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  padding: 10px;
}

.message {
  max-width: 80%;
  margin-bottom: 10px;
  padding: 10px;
  border-radius: 8px;
}

.message-sent {
  align-self: flex-end;
  background-color: #e3f2fd;
  border-left: 4px solid #2196f3;
}

.message-received {
  align-self: flex-start;
  background-color: #f1f8e9;
  border-left: 4px solid #8bc34a;
}

.message-header {
  display: flex;
  justify-content: space-between;
  font-size: 0.8em;
  margin-bottom: 5px;
  color: #666;
}

.message-content {
  word-break: break-word;
}

#log {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 10px;
  margin-bottom: 15px;
  background-color: #f9f9f9;
}

#log div {
  margin-bottom: 8px;
  padding: 8px;
  border-radius: 4px;
  word-break: break-word;
}

.message-sent {
  background-color: #e3f2fd;
  border-left: 4px solid #2196f3;
}

.message-received {
  background-color: #f1f8e9;
  border-left: 4px solid #8bc34a;
}

.error {
  background-color: #ffebee;
  border-left: 4px solid #f44336;
  color: #d32f2f;
}

.info {
  background-color: #f5f5f5;
  border-left: 4px solid #9e9e9e;
}

.status {
  padding: 8px 12px;
  margin-bottom: 15px;
  border-radius: 4px;
  font-weight: bold;
}

.connected {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.disconnected {
  background-color: #ffebee;
  color: #c62828;
}

.connecting {
  background-color: #fff8e1;
  color: #ff8f00;
}
</style>
