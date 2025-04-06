<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import MessagesSidebar from '../components/messaging/MessagesSidebar.vue'
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

// Helper function to get a chat ID (either conversationId or id)
const getChatId = (conversation) => {
  return conversation?.conversationId || conversation?.id;
}

// Helper function to find a conversation by chat ID
const findConversationByChatId = (chatId) => {
  return chats.value.find(chat =>
    (chat.conversationId === chatId) || (chat.id === chatId)
  );
}

/**
 * When a chat is selected from the sidebar:
 * - update the route
 * - set the receiver ID in WebSocket store
 * - fetch messages
 * - fetch receiver details if needed
 */
const handleChatSelect = async (chatId) => {
  console.log('Selecting chat:', chatId)
  try {
    // Validate chatId is defined and not 'undefined'
    if (!chatId || chatId === 'undefined') {
      console.error('Invalid chat ID:', chatId);
      return;
    }

    router.push(`/messages/${chatId}`)

    // Look for the conversation by conversationId OR id (depending on backend response)
    const selectedConversation = chats.value.find(chat =>
      (chat.conversationId === chatId) || (chat.id === chatId)
    )

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

      // Reset WebSocket messages to clear any old messages
      websocket.clearMessages();
    } else {
      console.error('Selected conversation has no receiverId')
    }

    // Fetch chat messages
    const mssgResponse = await axios.get('/api/messages', {
      params: {
        senderId: authStore.user?.id?.toString(),
        receiverId: selectedConversation.receiverId?.toString()
      }
    })

    // Ensure messages are sorted by timestamp
    const sortedMessages = mssgResponse.data.sort((a, b) =>
      new Date(a.timestamp) - new Date(b.timestamp)
    );

    // Store messages using the chatId from the parameter
    chatMessages.value[chatId] = sortedMessages;

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
 * Lifecycle hook: on mount
 * - initialize WebSocket connection
 * - fetch all user conversations
 * - fetch all receiver details
 * - load appropriate chat based on URL or first available
 */
onMounted(async () => {
  try {
    // Update the sender ID in the WebSocket store
    websocket.updateSender()

    // Connect to WebSocket immediately on mount
    console.log('Connecting to WebSocket on mount')
    websocket.connect()

    // Step 1: Load all conversations for the logged-in user
    const response = await axios.get('/api/conversations', {
      params: {
        userId: authStore.user?.id?.toString() || ''
      }
    })

    // Filter out duplicate conversations and conversations with self
    const uniqueConversations = [];
    const conversationKeys = new Set();

    response.data.forEach(convo => {
      // Skip conversations where sender and receiver are the same
      if (convo.senderId === convo.receiverId) {
        console.log('Skipping self-conversation:', convo);
        return;
      }

      // Create a unique key for each conversation (sorted IDs to handle both directions)
      const ids = [convo.senderId, convo.receiverId].sort().join('-');

      // Only add if we haven't seen this conversation before
      if (!conversationKeys.has(ids)) {
        conversationKeys.add(ids);

        // Ensure receiverId is always the other person, not the current user
        if (Number(convo.receiverId) === authStore.user?.id) {
          // Swap sender and receiver if needed
          const temp = convo.senderId;
          convo.senderId = convo.receiverId;
          convo.receiverId = temp;
        }

        uniqueConversations.push(convo);
      }
    });

    chats.value = uniqueConversations;
    console.log('Loaded unique conversations:', chats.value);

    // Step 2: Fetch receiver details for each conversation
    for (const convo of chats.value) {
      if (convo.receiverId) {
        await fetchReceiverDetails(convo.receiverId)
      } else {
        console.warn(`Conversation ${convo.conversationId || convo.id} has no receiverId`)
      }
    }

    // Step 3: Handle initial chat selection based on URL or first available
    if (chats.value.length > 0) {
      if (route.params.chatId && route.params.chatId !== 'undefined') {
        // If we have a valid chat ID in the route, select that chat
        await handleChatSelect(route.params.chatId)
      } else {
        // Otherwise, select the first chat and update the URL
        const firstChat = chats.value[0]
        // Use conversationId as the primary identifier, fall back to id
        const chatId = firstChat.conversationId || firstChat.id

        if (chatId) {
          // Instead of waiting for handleChatSelect to update the URL,
          // update it first to avoid 'undefined' in the URL
          router.replace(`/messages/${chatId}`)
          await handleChatSelect(chatId)
        } else {
          console.error('First chat is invalid:', firstChat)
        }
      }
    } else {
      console.log('No conversations available')
    }
  } catch (error) {
    console.error('Failed to fetch conversations:', error)
  }
})

// Log conversations for debugging
const logConversations = () => {
  console.log('Current conversations:', JSON.stringify(chats.value, null, 2))
}

// Filter WebSocket messages to only show those relevant to current chat
const filteredWebSocketMessages = computed(() => {
  if (!activeChat.value) return [];

  // Find conversation by either id or conversationId
  const currentChat = chats.value.find(chat =>
    (chat.conversationId === activeChat.value) || (chat.id === activeChat.value)
  );

  if (!currentChat) return [];

  // Get the current receiver ID
  const currentReceiverId = currentChat.receiverId?.toString();

  // Filter WebSocket messages to only include those from/to current receiver
  return websocket.messages.filter(msg => {
    // Only messages from the current receiver or to the current receiver
    return (msg.receiverId === currentReceiverId || msg.senderId === currentReceiverId);
  });
});
</script>

<template>
  <div class="messaging-container">
    <!-- Left sidebar with conversations list -->
    <MessagesSidebar
      :conversations="chats"
      :activeConversationId="activeChat"
      :receiver-usernames="receiverUsernames"
      @select-chat="handleChatSelect"
    />

    <!-- Right area with WebSocket chat functionality -->
    <div class="chat-content">
      <!-- WebSocket connection status -->
      <div :class="['status', websocket.connectionStatusClass]">Status: {{ websocket.connectionStatus }}</div>

      <!-- Chat header with active user info -->
      <div v-if="activeChat" class="chat-header">
        <h2>
          {{ receiverUsernames.get(findConversationByChatId(activeChat)?.receiverId) || 'Chat' }}
        </h2>
      </div>

      <!-- Message history area -->
      <div class="message-history" v-if="activeChat">
        <div class="messages-list">
          <!-- Historic messages from backend -->
          <div v-for="(msg, index) in chatMessages[activeChat]" :key="'chat-'+index"
               :class="['message', Number(msg.senderId) === authStore.user?.id ? 'message-sent' : 'message-received']">
            <div class="message-content">{{ msg.content }}</div>
            <div class="message-footer">
              <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
            </div>
          </div>

          <!-- WebSocket messages - filtered to only show those for the active conversation -->
          <div v-for="(msg, index) in filteredWebSocketMessages" :key="'ws-'+index"
               :class="['message', msg.type === 'sent' ? 'message-sent' : 'message-received']">
            <div class="message-content">{{ msg.content }}</div>
            <div class="message-footer">
              <span class="message-time">{{ formatTime(msg.timestamp) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Message input area -->
      <div class="message-input" v-if="activeChat">
        <div class="form-group">
          <textarea v-model="websocket.messageContent"
                   @keypress.enter.prevent="websocket.sendMessage"
                   placeholder="Type a message..."></textarea>
        </div>
        <button @click="websocket.sendMessage"
                :disabled="!websocket.connected">Send Message</button>
      </div>

      <!-- No chat selected state -->
      <div v-if="!activeChat" class="no-chat-selected">
        <h3>Select a conversation to start chatting</h3>
      </div>
    </div>
  </div>
</template>

<style scoped>
.messaging-container {
  display: flex;
  height: calc(100vh - 80px);
  background: #ffffff;
}

.chat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.chat-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.message-history {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20px;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 15px;
}

.messages-list {
  display: flex;
  flex-direction: column;
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

.message-input {
  display: flex;
  flex-direction: column;
}

.message-input textarea {
  width: 100%;
  min-height: 80px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 10px;
  resize: vertical;
}

.message-input button {
  align-self: flex-end;
  padding: 8px 20px;
  background-color: #1976d2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.message-input button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.no-chat-selected {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #666;
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
