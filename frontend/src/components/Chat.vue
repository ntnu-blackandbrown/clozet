<template>
  <div class="chat-container">
    <h2>Chat</h2>
    <div class="messages-container">
      <div v-for="(msg, index) in messages" 
           :key="index" 
           :class="['message', msg.senderId === currentUserId ? 'sent' : 'received']">
        <div class="message-header">
          <strong>{{ msg.senderId }}</strong>
          <span class="timestamp">{{ formatTime(msg.createdAt) }}</span>
        </div>
        <p class="message-content">{{ msg.content }}</p>
        <div class="message-status" v-if="msg.senderId === currentUserId">
          <small>{{ msg.read ? 'Read' : 'Sent' }}</small>
        </div>
      </div>
    </div>
    <div class="message-input">
      <input 
        v-model="newMessage" 
        placeholder="Write a message..." 
        @keyup.enter="sendMessage"
      />
      <button @click="sendMessage">Send</button>
    </div>
  </div>
</template>

<script>
import webSocketService from '@/services/webSocketService';
import apiService from '@/services/apiService';

export default {
  name: 'Chat',
  props: {
    currentUserId: {
      type: String,
      default: 'user123' // Replace with real user ID from authentication
    },
    receiverId: {
      type: String,
      default: 'seller456' // Replace with the recipient's ID
    }
  },
  data() {
    return {
      messages: [],
      newMessage: '',
      subscriptions: [],
      connected: false
    };
  },
  methods: {
    connectWebSocket() {
      webSocketService.connect(() => {
        this.connected = true;
        console.log('WebSocket connected successfully');
        
        // Subscribe to main messages topic
        this.subscriptions.push(
          webSocketService.subscribe('/topic/messages', (message) => {
            // Only add messages relevant to this conversation
            if (this.isMessageForCurrentConversation(message)) {
              this.messages.push(message);
            }
          })
        );
        
        // Subscribe to read status updates
        this.subscriptions.push(
          webSocketService.subscribe('/topic/messages.read', (message) => {
            if (this.isMessageForCurrentConversation(message)) {
              this.updateMessageReadStatus(message);
            }
          })
        );
        
        // Load message history
        this.loadMessages();
      }, error => {
        console.error('Could not connect to WebSocket:', error);
      });
    },
    
    loadMessages() {
      // Load message history from the API
      apiService.getMessages()
        .then(response => {
          const allMessages = response.data;
          // Filter messages for current conversation
          this.messages = allMessages.filter(msg => 
            this.isMessageForCurrentConversation(msg)
          );
        })
        .catch(error => {
          console.error('Error loading messages:', error);
        });
    },
    
    isMessageForCurrentConversation(message) {
      return (message.senderId === this.currentUserId && message.receiverId === this.receiverId) || 
             (message.senderId === this.receiverId && message.receiverId === this.currentUserId);
    },
    
    updateMessageReadStatus(updatedMessage) {
      const index = this.messages.findIndex(msg => msg.id === updatedMessage.id);
      if (index !== -1) {
        this.messages[index].read = updatedMessage.read;
      }
    },
    
    sendMessage() {
      if (this.newMessage.trim() !== '' && this.connected) {
        const messagePayload = {
          senderId: this.currentUserId,
          receiverId: this.receiverId,
          content: this.newMessage,
          createdAt: new Date().toISOString()
        };
        
        webSocketService.send("/app/chat.sendMessage", messagePayload);
        this.newMessage = '';
      }
    },
    
    formatTime(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
  },
  mounted() {
    this.connectWebSocket();
  },
  beforeDestroy() {
    // Clean up subscriptions
    this.subscriptions.forEach(subscription => {
      if (subscription) {
        subscription.unsubscribe();
      }
    });
    webSocketService.disconnect();
  }
};
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  max-height: 500px;
  border: 1px solid #e0e0e0;
  border-radius: 5px;
  margin-bottom: 10px;
}

.message {
  padding: 10px 15px;
  border-radius: 10px;
  max-width: 70%;
  word-wrap: break-word;
}

.sent {
  align-self: flex-end;
  background-color: #dcf8c6;
  margin-left: auto;
}

.received {
  align-self: flex-start;
  background-color: #f1f0f0;
  margin-right: auto;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
  font-size: 0.85em;
}

.timestamp {
  color: #999;
}

.message-content {
  margin: 0;
}

.message-status {
  text-align: right;
  color: #888;
  font-size: 0.75em;
  margin-top: 2px;
}

.message-input {
  display: flex;
  margin-top: 10px;
}

input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-right: 10px;
}

button {
  padding: 10px 20px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #45a049;
}
</style> 