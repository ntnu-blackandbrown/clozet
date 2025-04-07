<script setup>
import { ref, computed, onMounted, watch } from 'vue'
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
const itemDetails = ref({}) // Map of itemId -> item details

// Pagination state
const messagePage = ref(1)
const messagePageSize = ref(20)
const isLoadingMore = ref(false)
const hasMoreMessages = ref(true)

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
 * Fetch details for an item by its ID
 */
const fetchItemDetails = async (itemId) => {
  try {
    const response = await axios.get(`/api/items/${itemId}`)
    itemDetails.value[itemId] = response.data
    return response.data
  } catch (error) {
    console.error(`Failed to fetch details for item ${itemId}:`, error)
    return null
  }
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

      // Clear WebSocket messages first to avoid duplicates
      websocket.clearMessages();

      // Then set the receiver
      websocket.setReceiver(selectedConversation.receiverId.toString())
    } else {
      console.error('Selected conversation has no receiverId')
    }

    // Reset pagination state
    messagePage.value = 1
    hasMoreMessages.value = true
    chatMessages.value[chatId] = []

    // Fetch first page of chat messages
    await loadMessages(chatId, true)

    if (selectedConversation.receiverId) {
      await fetchReceiverDetails(selectedConversation.receiverId)

      // Fetch item details if we have an itemId
      if (selectedConversation.itemId && !itemDetails.value[selectedConversation.itemId]) {
        await fetchItemDetails(selectedConversation.itemId)
      }

      // Mark messages from this receiver as read
      setTimeout(() => {
        websocket.markAllAsRead();
      }, 1000); // Small delay to ensure WebSocket connection is ready
    }
  } catch (error) {
    console.error('Failed to fetch messages for conversation:', error)
  }
}

/**
 * Load messages for the active chat with pagination
 * @param {string} chatId - The ID of the chat
 * @param {boolean} reset - Whether to reset existing messages
 */
const loadMessages = async (chatId, reset = false) => {
  if (isLoadingMore.value || !hasMoreMessages.value) return

  try {
    isLoadingMore.value = true

    const selectedConversation = findConversationByChatId(chatId)
    if (!selectedConversation) return

    // Fetch chat messages with pagination
    const mssgResponse = await axios.get('/api/messages', {
      params: {
        senderId: authStore.user?.id?.toString(),
        receiverId: selectedConversation.receiverId?.toString(),
        page: messagePage.value,
        size: messagePageSize.value
      }
    })

    // Ensure messages are sorted by timestamp
    const sortedMessages = mssgResponse.data.sort((a, b) =>
      new Date(a.timestamp || a.createdAt) - new Date(b.timestamp || b.createdAt)
    );

    // If no messages returned, we've reached the end
    if (sortedMessages.length === 0) {
      hasMoreMessages.value = false
      isLoadingMore.value = false
      return
    }

    // Update pagination
    messagePage.value += 1

    // Store messages
    if (reset) {
      chatMessages.value[chatId] = sortedMessages
    } else {
      // Prepend older messages
      chatMessages.value[chatId] = [...sortedMessages, ...chatMessages.value[chatId]]
    }

  } catch (error) {
    console.error('Failed to fetch messages:', error)
  } finally {
    isLoadingMore.value = false
  }
}

/**
 * Load more messages when user scrolls to top of message history
 */
const handleScrollToTop = async (event) => {
  const { scrollTop } = event.target
  if (scrollTop < 50 && !isLoadingMore.value && hasMoreMessages.value && activeChat.value) {
    await loadMessages(activeChat.value)
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

      // Step 2.5: Fetch item details if available
      if (convo.itemId) {
        await fetchItemDetails(convo.itemId)
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

// Add this computed property to deduplicate messages between WebSocket and API sources
const combinedMessages = computed(() => {
  if (!activeChat.value) return [];

  // Find the current conversation
  const currentConversation = chats.value.find(chat =>
    (chat.conversationId === activeChat.value) || (chat.id === activeChat.value)
  );

  if (!currentConversation) return [];

  const currentReceiverId = currentConversation.receiverId?.toString();
  const currentSenderId = authStore.user?.id?.toString();

  // Get API messages for the active chat
  const apiMessages = chatMessages.value[activeChat.value] || [];

  // Filter API messages to ensure they belong to the current conversation
  const filteredApiMessages = apiMessages.filter(msg =>
    (msg.senderId === currentSenderId && msg.receiverId === currentReceiverId) ||
    (msg.senderId === currentReceiverId && msg.receiverId === currentSenderId)
  );

  // Create a unique key for each API message for duplicate detection
  const messageKeys = new Set();
  filteredApiMessages.forEach(msg => {
    const key = `${msg.senderId}-${msg.receiverId}-${msg.content}-${msg.timestamp || msg.createdAt}`;
    messageKeys.add(key);
  });

  // Filter WebSocket messages to only include those that don't match API messages
  const uniqueWebSocketMessages = filteredWebSocketMessages.value.filter(wsMsg => {
    const key = `${wsMsg.senderId}-${wsMsg.receiverId}-${wsMsg.content}-${wsMsg.timestamp || wsMsg.createdAt}`;
    if (messageKeys.has(key)) {
      return false;
    }
    messageKeys.add(key);
    return true;
  });

  // Combine and sort all messages by timestamp
  const allMessages = [...filteredApiMessages, ...uniqueWebSocketMessages];
  return allMessages.sort((a, b) => {
    const timeA = new Date(a.timestamp || a.createdAt).getTime();
    const timeB = new Date(b.timestamp || b.createdAt).getTime();
    return timeA - timeB;
  });
});

/**
 * Get the username for a conversation's receiver
 */
const getReceiverUsername = (conversation) => {
  if (!conversation) return '';

  const numericReceiverId = Number(conversation.receiverId);
  const username = receiverUsernames.value.get(numericReceiverId);

  if (username) {
    return username;
  }

  // If we don't have the username in our map, try to fetch it
  if (numericReceiverId) {
    fetchReceiverDetails(numericReceiverId);
  }

  // Return receiver name from conversation as fallback
  return conversation.receiverName || 'Unknown User';
}

// Get active item details for the current chat
const activeItemDetails = computed(() => {
  if (!activeChat.value) return null;

  const currentChat = findConversationByChatId(activeChat.value);
  if (!currentChat || !currentChat.itemId) return null;

  return itemDetails.value[currentChat.itemId] || null;
});

// Function to handle Buy Item button click
const handleBuyItem = () => {
  if (!activeItemDetails.value) return;
  router.push(`/product/${activeItemDetails.value.id}`);
};
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
      <!-- Chat header with active user info -->
      <div v-if="activeChat" class="chat-header">
        <div class="user-info">
          <h2>
            {{ getReceiverUsername(findConversationByChatId(activeChat)) }}
          </h2>
          <div v-if="activeItemDetails" class="item-info">
            <span class="item-label">Item:</span>
            <span class="item-name">{{ activeItemDetails.title }}</span>
          </div>
        </div>
        <div v-if="activeItemDetails" class="header-actions">
          <button
            class="buy-button"
            @click="handleBuyItem"
            :disabled="activeItemDetails.sellerId === authStore.user?.id || !activeItemDetails.isAvailable"
          >
            Buy Item
          </button>
        </div>
      </div>

      <!-- Message history area -->
      <div class="message-history" v-if="activeChat" @scroll="handleScrollToTop">
        <!-- Loading indicator -->
        <div v-if="isLoadingMore" class="loading-indicator">
          <div class="loading-spinner"></div>
          <div>Loading older messages...</div>
        </div>

        <!-- End of message history indicator -->
        <div v-if="!hasMoreMessages && chatMessages[activeChat]?.length > 0" class="end-of-history">
          Beginning of conversation
        </div>

        <div class="messages-list">
          <!-- Combined messages from API and WebSocket, without duplicates -->
          <div v-for="(msg, index) in combinedMessages" :key="'msg-'+index"
               :class="['message', (Number(msg.senderId) === authStore.user?.id || msg.type === 'sent') ? 'message-sent' : 'message-received']">
            <div class="message-content">{{ msg.content }}</div>
            <div class="message-footer">
              <span class="message-time">{{ formatTime(msg.timestamp || msg.createdAt) }}</span>

              <!-- Message status indicators (only for sent messages) -->
              <span v-if="Number(msg.senderId) === authStore.user?.id || msg.type === 'sent'" class="message-status">
                <!-- Failed message with retry option -->
                <span v-if="websocket.failedMessages.has(msg.id)" class="message-failed" @click="websocket.retryMessage(msg.id)">
                  <i class="fas fa-exclamation-circle"></i>
                  <span class="retry-text">Tap to retry</span>
                </span>

                <!-- Sending indicator -->
                <span v-else-if="msg.status === 'sending'" class="message-sending">
                  <i class="fas fa-circle-notch fa-spin"></i>
                </span>

                <!-- Delivered indicator -->
                <span v-else-if="websocket.deliveredMessages.has(msg.id)" class="message-delivered">
                  <i class="fas fa-check"></i>
                </span>

                <!-- Read indicator -->
                <span v-else-if="websocket.readMessages.has(msg.id)" class="message-read">
                  <i class="fas fa-check-double"></i>
                </span>

                <!-- Sent indicator (default) -->
                <span v-else class="message-sent-indicator">
                  <i class="fas fa-check"></i>
                </span>
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Message input area -->
      <div class="message-input" v-if="activeChat">
        <!-- Typing indicator -->
        <div v-if="websocket.isReceiverTyping(findConversationByChatId(activeChat)?.receiverId)" class="typing-indicator">
          {{ getReceiverUsername(findConversationByChatId(activeChat)) }} is typing...
        </div>
        <div class="form-group">
          <textarea v-model="websocket.messageContent"
                   @keypress.enter.prevent="websocket.sendMessage"
                   @input="websocket.handleTyping"
                   placeholder="Type a message..."></textarea>
        </div>
        <button @click="websocket.sendMessage"
                :disabled="!websocket.messageContent.trim()">Send Message</button>
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.item-info {
  display: flex;
  align-items: center;
  margin-top: 4px;
  font-size: 0.9em;
}

.item-label {
  color: #666;
  margin-right: 6px;
  font-weight: 500;
}

.item-name {
  color: #1976d2;
  font-weight: 500;
}

.header-actions {
  display: flex;
}

.buy-button {
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 0.375rem;
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.buy-button:hover:not(:disabled) {
  background-color: #45a049;
}

.buy-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  opacity: 0.7;
}

.typing-indicator {
  color: #666;
  font-style: italic;
  font-size: 0.9em;
  animation: pulsate 1.5s infinite;
  margin-bottom: 8px;
  padding-left: 4px;
}

@keyframes pulsate {
  0% { opacity: 0.5; }
  50% { opacity: 1; }
  100% { opacity: 0.5; }
}

.message-history {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20px;
  border: 1px solid #eee;
  border-radius: 4px;
  padding: 15px;
}

.loading-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  color: #666;
  font-size: 0.9em;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  margin-right: 10px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.end-of-history {
  text-align: center;
  padding: 10px;
  font-size: 0.9em;
  color: #9e9e9e;
  margin-bottom: 10px;
  border-bottom: 1px dashed #e0e0e0;
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

.message-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.8em;
  margin-top: 5px;
  color: #666;
}

.message-status {
  display: flex;
  align-items: center;
}

.message-failed {
  color: #f44336;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.retry-text {
  margin-left: 4px;
  font-size: 0.8em;
}

.message-sending {
  color: #9e9e9e;
}

.message-delivered {
  color: #2196f3;
}

.message-read {
  color: #4caf50;
}

.message-sent-indicator {
  color: #9e9e9e;
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
</style>
