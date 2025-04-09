import { ref } from 'vue'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'
import { useAuthStore } from '@/stores/AuthStore'
import { defineStore } from 'pinia'

// Message interface
interface Message {
  id: string;
  senderId: string | number;
  receiverId: string | number;
  content: string;
  timestamp?: string;
  createdAt?: string;
  type?: string;
  status?: string;
}

// Log interface
interface LogEntry {
  text: string;
  type: string;
}

export const useWebsocket = defineStore('websocket', () => {
  const serverUrl = ref('http://localhost:8080/ws')

  const authStore = useAuthStore()
  // Initialize sender as null and update it when needed
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

  //Will be set by a function
  const receiver = ref<number>(0)
  const setReceiver = (id: number) => {
    console.log('Setting receiver ID:', id, typeof id)
    receiver.value = id
    console.log('Receiver ID after setting:', receiver.value, typeof receiver.value)
  }

  const messageContent = ref('')
  const logs = ref<LogEntry[]>([])
  const messages = ref<Message[]>([])

  // New state for enhanced messaging features
  const deliveredMessages = ref(new Set<string>())
  const readMessages = ref(new Set<string>())
  const failedMessages = ref(new Set<string>())
  const typingUsers = ref(new Map<string | number, { timestamp: number, timeoutId: NodeJS.Timeout | null }>())
  const isTyping = ref(false)
  const typingTimeout = ref<NodeJS.Timeout | null>(null)
  const pendingMessages = ref<Message[]>([]) // For offline queueing

  let stompClient: Stomp.Client | null = null
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
    // Update sender before connecting
    updateSender()

    console.log('Attempting to connect to WebSocket server...')
    updateConnectionStatus('connecting', 'Connecting...')
    log(`Attempting to connect to ${serverUrl.value}...`)

    const socket = new SockJS(serverUrl.value)
    stompClient = Stomp.over(socket)
    stompClient.debug = (str: string) => log(`STOMP Debug: ${str}`, 'info')

    stompClient.connect(
      {},
      (frame: any) => {
        connected.value = true
        updateConnectionStatus('connected', 'Connected')
        log(`Connected: ${frame}`, 'message-received')
        subscribeToTopics()

        // Process any pending offline messages
        processPendingMessages()
      },
      (error: any) => {
        connected.value = false
        updateConnectionStatus('disconnected', 'Connection Failed')
        log(`Connection error: ${error}`, 'error')
      },
    )
  }

  function disconnect() {
    if (stompClient) {
      stompClient.disconnect()
      updateConnectionStatus('disconnected', 'Disconnected')
      log('Disconnected from WebSocket')
    }
  }

  function subscribeToTopics() {
    if (!stompClient) return;

    stompClient.subscribe('/topic/messages', (msg: any) => {
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

          // Automatically send delivery confirmation
          sendDeliveryConfirmation(message.id)
        }
      } catch (e: unknown) {
        const error = e as Error
        log(`Error parsing message: ${error.message}`, 'error')
      }
    })

    stompClient.subscribe('/topic/messages.read', (msg: any) => {
      try {
        const message = JSON.parse(msg.body)
        log(`Message read:<br>ID: ${message.id}<br>Read: ${message.read}`, 'message-received')

        // Add to read messages set
        readMessages.value.add(message.id)
      } catch (e: unknown) {
        const error = e as Error
        log(`Error parsing read status: ${error.message}`, 'error')
      }
    })

    stompClient.subscribe('/topic/messages.delivered', (msg: any) => {
      try {
        const { messageId } = JSON.parse(msg.body)
        log(`Message delivered: ${messageId}`, 'message-received')

        // Mark message as delivered
        deliveredMessages.value.add(messageId)
      } catch (e: unknown) {
        const error = e as Error
        log(`Error parsing delivery status: ${error.message}`, 'error')
      }
    })

    stompClient.subscribe('/topic/messages.typing', (msg: any) => {
      try {
        const { userId, isTyping: userIsTyping } = JSON.parse(msg.body)

        // Only process if message is from the current receiver
        if (userId === receiver.value) {
          if (userIsTyping) {
            // Set typing status with timeout to automatically clear after 3 seconds
            const now = Date.now()

            // Clear existing timeout if any
            if (typingUsers.value.has(userId) && typingUsers.value.get(userId)?.timeoutId) {
              clearTimeout(typingUsers.value.get(userId)?.timeoutId as NodeJS.Timeout)
            }

            // Set new timeout
            const timeoutId = setTimeout(() => {
              typingUsers.value.delete(userId)
            }, 3000)

            // Update typing users map
            typingUsers.value.set(userId, { timestamp: now, timeoutId })
          } else {
            // Remove typing status
            typingUsers.value.delete(userId)
          }
        }
      } catch (e: unknown) {
        const error = e as Error
        log(`Error parsing typing status: ${error.message}`, 'error')
      }
    })

    stompClient.subscribe('/topic/messages.update', (msg: any) => {
      try {
        const message = JSON.parse(msg.body)
        log(
          `Message updated:<br>ID: ${message.id}<br>Content: ${message.content}`,
          'message-received',
        )
      } catch (e: unknown) {
        const error = e as Error
        log(`Error parsing update: ${error.message}`, 'error')
      }
    })
  }

  function sendDeliveryConfirmation(messageId: string) {
    if (connected.value && stompClient) {
      const confirmation = {
        messageId,
        receiverId: sender.value,
        timestamp: new Date().toISOString(),
      }
      stompClient.send('/app/chat.confirmDelivery', {}, JSON.stringify(confirmation))
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

    // Generate a client ID for this message
    const clientMessageId = `client-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`

    const msg: Message = {
      id: clientMessageId, // Include client ID
      senderId: sender.value,
      receiverId: receiver.value,
      content: messageContent.value,
      createdAt: timestamp,
    }

    // Add to local state immediately
    messages.value.push({
      ...msg,
      type: 'sent',
      status: 'sending',
    })

    // Clear typing indicator
    sendTypingStatus(false)
    if (typingTimeout.value) {
      clearTimeout(typingTimeout.value)
      typingTimeout.value = null
    }
    isTyping.value = false

    // If not connected, queue the message for later
    if (!connected.value || !stompClient) {
      log('Not connected. Message queued for later.', 'warning')
      pendingMessages.value.push(msg)
      failedMessages.value.add(clientMessageId)
      return
    }

    // Send via WebSocket
    stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(msg))
    log(
      `Sent:<br>From: ${msg.senderId}<br>To: ${msg.receiverId}<br>Content: ${msg.content}`,
      'message-sent',
    )
    messageContent.value = ''
  }

  // Retry sending a failed message
  function retryMessage(messageId: string) {
    const messageIndex = messages.value.findIndex((m) => m.id === messageId)
    if (messageIndex === -1) return

    const message = messages.value[messageIndex]

    // Update status
    messages.value[messageIndex] = { ...message, status: 'sending' }

    // If connected, send message
    if (connected.value && stompClient) {
      stompClient.send(
        '/app/chat.sendMessage',
        {},
        JSON.stringify({
          id: message.id,
          senderId: message.senderId,
          receiverId: message.receiverId,
          content: message.content,
          createdAt: message.timestamp || message.createdAt,
        }),
      )

      // Remove from failed messages
      failedMessages.value.delete(messageId)
    } else {
      // If not connected, keep in failed state
      messages.value[messageIndex] = { ...message, status: 'failed' }
    }
  }

  // Process any pending messages when connection is established
  function processPendingMessages() {
    if (!connected.value || !stompClient) return

    const messagesToSend = [...pendingMessages.value]
    pendingMessages.value = []

    messagesToSend.forEach((msg) => {
      if (stompClient) {
        stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(msg))
      }
      failedMessages.value.delete(msg.id)

      // Update message status
      const messageIndex = messages.value.findIndex((m) => m.id === msg.id)
      if (messageIndex !== -1) {
        messages.value[messageIndex] = { ...messages.value[messageIndex], status: 'sent' }
      }
    })

    if (messagesToSend.length > 0) {
      log(`Sent ${messagesToSend.length} queued messages`, 'info')
    }
  }

  // Send typing status
  function sendTypingStatus(isTyping: boolean) {
    if (connected.value && stompClient && sender.value && receiver.value) {
      const status = {
        userId: sender.value,
        receiverId: receiver.value,
        isTyping,
        timestamp: new Date().toISOString(),
      }
      stompClient.send('/app/chat.typing', {}, JSON.stringify(status))
    }
  }

  // Handle user typing
  function handleTyping() {
    if (!isTyping.value) {
      isTyping.value = true
      sendTypingStatus(true)
    }

    // Clear existing timeout
    if (typingTimeout.value) {
      clearTimeout(typingTimeout.value)
    }

    // Set new timeout to stop typing indicator after 3 seconds of inactivity
    typingTimeout.value = setTimeout(() => {
      isTyping.value = false
      sendTypingStatus(false)
    }, 3000)
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

  // Clear WebSocket messages when changing chats
  function clearMessages() {
    messages.value = []
    log('Cleared WebSocket messages', 'info')
  }

  // Mark messages as read for a specific sender
  function markMessagesAsRead(senderId: string) {
    if (connected.value && stompClient) {
      const readStatus = {
        senderId: senderId,
        receiverId: sender.value,
        read: true,
        timestamp: new Date().toISOString(),
      }
      stompClient.send('/app/chat.markRead', {}, JSON.stringify(readStatus))
      log(`Marked messages from ${senderId} as read`, 'info')
    }
  }

  // Mark all unread messages as read for the current chat
  function markAllAsRead() {
    if (!connected.value || !stompClient || !receiver.value) return

    // Get messages from current receiver that need to be marked as read
    const unreadMessages = messages.value.filter(
      (m) => m.senderId === receiver.value && !readMessages.value.has(m.id),
    )

    // Mark each message as read
    unreadMessages.forEach((msg) => {
      readMessages.value.add(msg.id)

      const readStatus = {
        messageId: msg.id,
        senderId: receiver.value,
        receiverId: sender.value,
        read: true,
        timestamp: new Date().toISOString(),
      }

      if (stompClient) {
        stompClient.send('/app/chat.markRead', {}, JSON.stringify(readStatus))
      }
    })

    if (unreadMessages.length > 0) {
      log(`Marked ${unreadMessages.length} messages as read`, 'info')
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
    isReceiverTyping: (receiverId: string | number) => typingUsers.value.has(receiverId),
  }
})
