import { ref } from 'vue'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

const serverUrl = ref('http://localhost:8080/ws')
const sender = ref('buyer1')
const receiver = ref('seller1')
const messageContent = ref('')
const logs = ref([])

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
  updateConnectionStatus('connecting', 'Connecting...')
  log(`Attempting to connect to ${serverUrl.value}...`)

  const socket = new SockJS(serverUrl.value)
  stompClient = Stomp.over(socket)
  stompClient.debug = (str) => log(`STOMP Debug: ${str}`, 'info')

  stompClient.connect(
    {},
    (frame) => {
      connected.value = true
      updateConnectionStatus('connected', 'Connected')
      log(`Connected: ${frame}`, 'message-received')
      subscribeToTopics()
    },
    (error) => {
      connected.value = false
      updateConnectionStatus('disconnected', 'Connection Failed')
      log(`Connection error: ${error}`, 'error')
    },
  )

  stompClient.heartbeat.outgoing = 20000
  stompClient.heartbeat.incoming = 20000
}

function subscribeToTopics() {
  stompClient.subscribe('/topic/messages', (msg) => {
    try {
      const message = JSON.parse(msg.body)
      messageCount++
      log(
        `Received message #${messageCount}:<br>ID: ${message.id}<br>From: ${message.senderId}<br>To: ${message.receiverId}<br>Content: ${message.content}<br>Time: ${message.createdAt}`,
        'message-received',
      )
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
      log(
        `Message updated:<br>ID: ${message.id}<br>Content: ${message.content}`,
        'message-received',
      )
    } catch (e) {
      log(`Error parsing update: ${e.message}`, 'error')
    }
  })
}

function disconnect() {
  if (stompClient && connected.value) {
    stompClient.disconnect(() => {
      log('Disconnected')
      updateConnectionStatus('disconnected', 'Disconnected')
    })
    connected.value = false
  }
}

function sendMessage() {
  if (!connected.value || !stompClient) {
    log('Not connected. Connect first.', 'error')
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
    createdAt: new Date().toISOString(),
  }

  stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(msg))
  log(
    `Sent:<br>From: ${msg.senderId}<br>To: ${msg.receiverId}<br>Content: ${msg.content}`,
    'message-sent',
  )
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
