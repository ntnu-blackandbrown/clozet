// src/stores/Websocket.ts (or similar)
import { ref } from 'vue'
import { Client } from '@stomp/stompjs'
import { useAuthStore } from '@/stores/AuthStore'
import { defineStore } from 'pinia'

interface Message {
  id: string
  senderId: string | number
  receiverId: string | number
  content: string
  timestamp?: string
  createdAt?: string
  type?: string
  status?: string
}

interface LogEntry {
  text: string
  type: string
}

export const useWebsocket = defineStore('websocket', () => {
  const serverUrl = ref('http://localhost:8080/ws')
  const authStore = useAuthStore()
  const sender = ref<string | null>(null)

  // Update sender when auth store changes
  const updateSender = () => {
    if (authStore.user && authStore.user.id) {
      sender.value = authStore.user.id.toString()
      console.log('Sender updated:', sender.value)
    } else {
      console.warn('User ID not available in auth store')
    }
  }

  const receiver = ref<number>(0)
  const setReceiver = (id: number) => {
    console.log('Setting receiver ID:', id, typeof id)
    receiver.value = id
    console.log('Receiver ID after setting:', receiver.value, typeof receiver.value)
  }

  const messageContent = ref('')
  const logs = ref<LogEntry[]>([])
  const messages = ref<Message[]>([])

  // Enhanced messaging features
  const deliveredMessages = ref(new Set<string>())
  const readMessages = ref(new Set<string>())
  const failedMessages = ref(new Set<string>())
  const typingUsers = ref(
    new Map<string | number, { timestamp: number; timeoutId: NodeJS.Timeout | null }>(),
  )
  const isTyping = ref(false)
  const typingTimeout = ref<NodeJS.Timeout | null>(null)
  const pendingMessages = ref<Message[]>([]) // For offline queueing

  let stompClient: Client | null = null
  let messageCount = 0
  const connected = ref(false)
  const connectionStatus = ref('Disconnected')
  const connectionStatusClass = ref('disconnected')

  function log(message: string, type: string = 'info') {
    const timestamp = new Date().toLocaleTimeString()
    logs.value.push({ text: `[${timestamp}] ${message}`, type })
    const logEl = document.getElementById('log')
    if (logEl) logEl.scrollTop = logEl.scrollHeight
  }

  function updateConnectionStatus(status: string, message: string) {
    connectionStatus.value = message
    connectionStatusClass.value = status
  }

  function connect() {
    updateSender()
    console.log('Attempting to connect to WebSocket server...')
    updateConnectionStatus('connecting', 'Connecting...')
    log(`Attempting to connect to ${serverUrl.value}...`)

    stompClient = new Client({
      brokerURL: serverUrl.value.replace('http', 'ws'),
      debug: (str) => log(`STOMP Debug: ${str}`, 'info'),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: (frame) => {
        connected.value = true
        updateConnectionStatus('connected', 'Connected')
        log(`Connected: ${frame}`, 'message-received')
        subscribeToTopics()
        processPendingMessages() // process offline messages
      },
      onStompError: (error) => {
        connected.value = false
        updateConnectionStatus('disconnected', 'Connection Failed')
        log(`Connection error: ${error.headers?.message || JSON.stringify(error)}`, 'error')
      }
    })

    stompClient.activate()
  }

  function disconnect() {
    if (stompClient) {
      stompClient.deactivate()
      updateConnectionStatus('disconnected', 'Disconnected')
      log('Disconnected from WebSocket')
      connected.value = false
    }
  }

  function subscribeToTopics() {
    if (!stompClient || !stompClient.connected) return

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
            type: 'received',
          })
          log(
            `Received message #${messageCount}:<br>ID: ${message.id}<br>From: ${message.senderId}<br>To: ${message.receiverId}<br>Content: ${message.content}<br>Time: ${message.createdAt}`,
            'message-received',
          )
          sendDeliveryConfirmation(message.id)
        }
      } catch (e) {
        log(`Error parsing message: ${(e as Error).message}`, 'error')
      }
    })

    stompClient.subscribe('/topic/messages.read', (msg: any) => {
      try {
        const message = JSON.parse(msg.body)
        log(`Message read:<br>ID: ${message.id}<br>Read: ${message.read}`, 'message-received')
        readMessages.value.add(message.id)
      } catch (e) {
        log(`Error parsing read status: ${(e as Error).message}`, 'error')
      }
    })

    stompClient.subscribe('/topic/messages.delivered', (msg: any) => {
      try {
        const { messageId } = JSON.parse(msg.body)
        log(`Message delivered: ${messageId}`, 'message-received')
        deliveredMessages.value.add(messageId)
      } catch (e) {
        log(`Error parsing delivery status: ${(e as Error).message}`, 'error')
      }
    })

    stompClient.subscribe('/topic/messages.typing', (msg: any) => {
      try {
        const { userId, isTyping: userIsTyping } = JSON.parse(msg.body)
        if (userId === receiver.value) {
          if (userIsTyping) {
            const now = Date.now()
            // Clear existing timeout if any
            if (typingUsers.value.has(userId) && typingUsers.value.get(userId)?.timeoutId) {
              clearTimeout(typingUsers.value.get(userId)?.timeoutId as NodeJS.Timeout)
            }
            // Set new timeout
            const timeoutId = setTimeout(() => {
              typingUsers.value.delete(userId)
            }, 3000)
            typingUsers.value.set(userId, { timestamp: now, timeoutId })
          } else {
            typingUsers.value.delete(userId)
          }
        }
      } catch (e) {
        log(`Error parsing typing status: ${(e as Error).message}`, 'error')
      }
    })

    stompClient.subscribe('/topic/messages.update', (msg: any) => {
      try {
        const message = JSON.parse(msg.body)
        log(`Message updated:<br>ID: ${message.id}<br>Content: ${message.content}`, 'message-received')
      } catch (e) {
        log(`Error parsing update: ${(e as Error).message}`, 'error')
      }
    })
  }

  function sendDeliveryConfirmation(messageId: string) {
    if (connected.value && stompClient && stompClient.connected) {
      const confirmation = {
        messageId,
        receiverId: sender.value,
        timestamp: new Date().toISOString(),
      }
      stompClient.publish({
        destination: '/app/chat.confirmDelivery',
        body: JSON.stringify(confirmation)
      })
      log(`Sent delivery confirmation for message ${messageId}`, 'info')
    }
  }

  function sendMessage() {
    console.log('Attempting to send message...')
    console.log('Connection status:', connected.value)
    console.log('Sender ID:', sender.value, typeof sender.value)
    console.log('Receiver ID:', receiver.value, typeof receiver.value)
    console.log('Message content:', messageContent.value, typeof messageContent.value)

    if (!sender.value || !receiver.value || !messageContent.value) {
      log('Fill all fields', 'error')
      return
    }

    const timestamp = new Date().toISOString()
    const clientMessageId = `client-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`

    const msg: Message = {
      id: clientMessageId,
      senderId: sender.value,
      receiverId: receiver.value,
      content: messageContent.value,
      createdAt: timestamp,
    }

    messages.value.push({
      ...msg,
      type: 'sent',
      status: 'sending',
    })

    sendTypingStatus(false)
    if (typingTimeout.value) {
      clearTimeout(typingTimeout.value)
      typingTimeout.value = null
    }
    isTyping.value = false

    if (!connected.value || !stompClient) {
      log('Not connected. Message queued for later.', 'warning')
      pendingMessages.value.push(msg)
      failedMessages.value.add(clientMessageId)
      return
    }

    stompClient.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify(msg)
    })
    log(
      `Sent:<br>From: ${msg.senderId}<br>To: ${msg.receiverId}<br>Content: ${msg.content}`,
      'message-sent',
    )
    messageContent.value = ''
  }

  function retryMessage(messageId: string) {
    const messageIndex = messages.value.findIndex((m) => m.id === messageId)
    if (messageIndex === -1) return

    const message = messages.value[messageIndex]
    // set status to sending
    messages.value[messageIndex] = { ...message, status: 'sending' }

    if (connected.value && stompClient) {
      stompClient.publish({
        destination: '/app/chat.sendMessage',
        body: JSON.stringify({
          id: message.id,
          senderId: message.senderId,
          receiverId: message.receiverId,
          content: message.content,
          createdAt: message.timestamp || message.createdAt,
        })
      })
      failedMessages.value.delete(messageId)
    } else {
      messages.value[messageIndex] = { ...message, status: 'failed' }
    }
  }

  function processPendingMessages() {
    if (!connected.value || !stompClient) return
    const messagesToSend = [...pendingMessages.value]
    pendingMessages.value = []

    messagesToSend.forEach((msg) => {
      stompClient?.publish({
        destination: '/app/chat.sendMessage',
        body: JSON.stringify(msg)
      })
      failedMessages.value.delete(msg.id)
      // Mark as sent in local store
      const messageIndex = messages.value.findIndex((m) => m.id === msg.id)
      if (messageIndex !== -1) {
        messages.value[messageIndex] = { ...messages.value[messageIndex], status: 'sent' }
      }
    })

    if (messagesToSend.length > 0) {
      log(`Sent ${messagesToSend.length} queued messages`, 'info')
    }
  }

  function sendTypingStatus(isTypingStatus: boolean) {
    if (connected.value && stompClient && sender.value && receiver.value) {
      const status = {
        userId: sender.value,
        receiverId: receiver.value,
        isTyping: isTypingStatus,
      }
      stompClient.publish({
        destination: '/app/chat.typing',
        body: JSON.stringify(status)
      })
    }
  }

  function handleTyping() {
    if (!isTyping.value) {
      isTyping.value = true
      sendTypingStatus(true)
    }
    // Reset the timeout
    if (typingTimeout.value) {
      clearTimeout(typingTimeout.value)
    }
    typingTimeout.value = setTimeout(() => {
      isTyping.value = false
      sendTypingStatus(false)
    }, 2000)
  }

  function pingServer() {
    if (connected.value && stompClient) {
      stompClient.publish({
        destination: '/app/chat.ping',
        body: ''
      })
    }
  }

  function checkConnection() {
    if (!connected.value && stompClient) {
      log('Attempting to reconnect...', 'warning')
      connect()
    } else if (connected.value && stompClient) {
      stompClient.publish({
        destination: '/app/chat.ping',
        body: ''
      })
    }
  }

  function clearMessages() {
    messages.value = []
    messageCount = 0
  }

  function markMessagesAsRead(senderId: string) {
    if (!connected.value || !stompClient || !sender.value) return

    const unreadMessages = messages.value.filter(
      (m) => m.senderId === senderId && !readMessages.value.has(m.id),
    )

    if (unreadMessages.length === 0) return

    const readStatus = {
      senderId: sender.value,
      messages: unreadMessages.map((m) => m.id),
      timestamp: new Date().toISOString(),
    }

    stompClient.publish({
      destination: '/app/chat.markRead',
      body: JSON.stringify(readStatus)
    })

    // Optimistically mark as read locally
    unreadMessages.forEach((m) => readMessages.value.add(m.id))
  }

  function markAllAsRead() {
    if (messages.value.length === 0 || !sender.value || !connected.value || !stompClient) return

    const readStatus = {
      senderId: sender.value,
      messages: messages.value.map((m) => m.id),
      timestamp: new Date().toISOString(),
    }

    stompClient.publish({
      destination: '/app/chat.markRead',
      body: JSON.stringify(readStatus)
    })

    // Mark all as read locally
    messages.value.forEach((m) => readMessages.value.add(m.id))
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
    clearMessages,
    markMessagesAsRead,
    markAllAsRead,
    logs,
    connectionStatus,
    connectionStatusClass,
    connected,
    messages,
    updateSender,
    deliveredMessages,
    readMessages,
    failedMessages,
    typingUsers,
    retryMessage,
    handleTyping,
    isTyping,
    // So the test can check if a user is in 'typingUsers'
    isReceiverTyping: (receiverId: string | number) => typingUsers.value.has(receiverId),
    // Expose these so the test can reference them directly
    pendingMessages,
    log,
    processPendingMessages,
  }
})
