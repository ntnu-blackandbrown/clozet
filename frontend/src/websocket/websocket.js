import { ref } from 'vue'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import { useAuthStore } from '@/stores/AuthStore'
import { defineStore } from 'pinia'
export const useWebsocket = defineStore('websocket', () => {
  const serverUrl = ref('http://localhost:8080/ws')

const authStore = useAuthStore()
// Initialize sender as null and update it when needed
const sender = ref(null)

// Update sender when auth store changes
const updateSender = () => {
  if (authStore.user && authStore.user.id) {
    sender.value = authStore.user.id.toString()
    console.log('Sender updated:', sender.value)
  } else {
    console.warn('User ID not available in auth store')
  }
}

//Will be set by a function
const receiver = ref(0)
const setReceiver = (id) => {
  console.log('Setting receiver ID:', id, typeof id)
  receiver.value = id
  console.log('Receiver ID after setting:', receiver.value, typeof receiver.value)
}

const messageContent = ref('')
const logs = ref([])
const messages = ref([])

let stompClient = null
let messageCount = 0
const connected = ref(false)
const connectionStatus = ref('Disconnected')
const connectionStatusClass = ref('disconnected')

function log(message, type = 'info') {
  const timestamp = new Date().toLocaleTimeString()
  logs.value.push({ text: `[${timestamp}] ${message}`, type })
  const logEl = document.getElementById('log')
  if (logEl) logEl.scrollTop = logEl.scrollHeight
}

function updateConnectionStatus(status, message) {
  connectionStatus.value = message
  connectionStatusClass.value = status
}

function connect() {
  // Update sender before connecting
  updateSender()

  console.log('Attempting to connect to WebSocket server...')
  updateConnectionStatus('connecting', 'Connecting...')
  log(`Attempting to connect to ${serverUrl.value}...`)

  const socket = new SockJS(serverUrl.value)
  stompClient = Stomp.over(socket)
  stompClient.debug = (str) => log(`STOMP Debug: ${str}`, 'info')

  stompClient.connect({}, (frame) => {
    connected.value = true
    updateConnectionStatus('connected', 'Connected')
    log(`Connected: ${frame}`, 'message-received')
    subscribeToTopics()
  }, (error) => {
    connected.value = false
    updateConnectionStatus('disconnected', 'Connection Failed')
    log(`Connection error: ${error}`, 'error')
  })
}

function disconnect() {
  if (stompClient) {
    stompClient.disconnect()
    updateConnectionStatus('disconnected', 'Disconnected')
    log('Disconnected from WebSocket')
  }
}

function subscribeToTopics() {
  stompClient.subscribe('/topic/messages', (msg) => {
    try {
      const message = JSON.parse(msg.body)
      messageCount++

      // Only add the message if it wasn't sent by the current user
      if (message.senderId !== sender.value) {
        messages.value.push({
          id: message.id,
          senderId: message.senderId,
          receiverId: message.receiverId,
          content: message.content,
          timestamp: message.createdAt,
          type: 'received'
        })

        log(`Received message #${messageCount}:<br>ID: ${message.id}<br>From: ${message.senderId}<br>To: ${message.receiverId}<br>Content: ${message.content}<br>Time: ${message.createdAt}`, 'message-received')
      }
    } catch (e) {
      log(`Error parsing message: ${e.message}`, 'error')
    }
  })

  stompClient.subscribe('/topic/messages.read', (msg) => {
    try {
      const message = JSON.parse(msg.body)
      log(`Message read:<br>ID: ${message.id}<br>Read: ${message.read}`, 'message-received')
    } catch (e) {
      log(`Error parsing read status: ${e.message}`, 'error')
    }
  })

  stompClient.subscribe('/topic/messages.update', (msg) => {
    try {
      const message = JSON.parse(msg.body)
      log(`Message updated:<br>ID: ${message.id}<br>Content: ${message.content}`, 'message-received')
    } catch (e) {
      log(`Error parsing update: ${e.message}`, 'error')
    }
  })
}


function sendMessage() {
  console.log('Attempting to send message...')
  console.log('Connection status:', connected.value)
  console.log('Sender ID:', sender.value, typeof sender.value)
  console.log('Receiver ID:', receiver.value, typeof receiver.value)
  console.log('Message content:', messageContent.value, typeof messageContent.value)

  if (!connected.value || !stompClient) {
    log('Not connected. Connect first.', 'error')
    return
  }

  // Check each field individually to identify which one is causing the issue
  if (!sender.value) {
    log('Sender ID is missing', 'error')
    return
  }
  if (!receiver.value) {
    log('Receiver ID is missing', 'error')
    return
  }
  if (!messageContent.value) {
    log('Message content is missing', 'error')
    return
  }

  if (!sender.value || !receiver.value || !messageContent.value) {
    log('Fill all fields', 'error')
    return
  }

  const msg = {
    senderId: sender.value,
    receiverId: receiver.value,
    content: messageContent.value,
    createdAt: new Date().toISOString()
  }

  stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(msg))

  messages.value.push({
    id: Date.now(),
    senderId: msg.senderId,
    receiverId: msg.receiverId,
    content: msg.content,
    timestamp: msg.createdAt,
    type: 'sent'
  })

  log(`Sent:<br>From: ${msg.senderId}<br>To: ${msg.receiverId}<br>Content: ${msg.content}`, 'message-sent')
  messageContent.value = ''
}

function pingServer() {
  if (connected.value && stompClient) {
    stompClient.send('/app/chat.ping', {}, '')
    log('Ping sent', 'message-sent')
  } else {
    log('Not connected', 'error')
  }
}

function checkConnection() {
  if (connected.value && stompClient) {
    stompClient.send('/app/chat.ping', {}, '')
    log('Connection check: Active', 'message-received')
  } else {
    log('Connection check: Not connected', 'error')
    updateConnectionStatus('disconnected', 'Not Connected')
  }
}


return {
  sender,
  receiver,
  setReceiver,
  messageContent,
  connect,
  disconnect,
  sendMessage,
  pingServer,
  checkConnection,
  logs,
  connectionStatus,
  connectionStatusClass,
  connected,
  messages,
  updateSender
}
})
