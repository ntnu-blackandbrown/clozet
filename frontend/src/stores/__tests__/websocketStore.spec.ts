// tests/websocket.spec.ts
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { useWebsocket } from '@/stores/websocketStore'
import { useAuthStore } from '@/stores/AuthStore'

// Define types for our mock STOMP client
interface MockStompClient {
  brokerURL: string;
  connected: boolean;
  activate: () => void;
  deactivate: () => void;
  publish: (options: any) => void;
  subscribe: (destination: string, callback: any) => { id: string };
}

// Mock @stomp/stompjs
let mockStompClientConfig: any = {}
let mockStompClient: MockStompClient

vi.mock('@stomp/stompjs', () => {
  return {
    Client: vi.fn().mockImplementation((config) => {
      // Store config for tests to access
      if (config) {
        mockStompClientConfig = config
      }
      return mockStompClient
    })
  }
})

describe('useWebsocket store', () => {
  let websocketStore: ReturnType<typeof useWebsocket>
  let authStore: ReturnType<typeof useAuthStore>

  beforeEach(() => {
    authStore = useAuthStore()

    // Mock an authenticated user
    authStore.user = { id: 123, usernameOrEmail: 'Test User', email: 'test@example.com', active: true, role: 'user' }

    // Prepare a fresh mockStompClient each test
    mockStompClient = {
      brokerURL: 'ws://localhost:8080/ws',
      connected: false,
      activate: vi.fn(function(this: MockStompClient) {
        // Simulate connection success
        this.connected = true
        // Call the onConnect callback if provided
        if (mockStompClientConfig.onConnect) {
          mockStompClientConfig.onConnect('mock-frame')
        }
      }),
      deactivate: vi.fn(function(this: MockStompClient) {
        this.connected = false
      }),
      publish: vi.fn(),
      subscribe: vi.fn().mockReturnValue({ id: 'mock-subscription-id' }),
    }

    // Reset config for each test
    mockStompClientConfig = {}

    // Create the store
    websocketStore = useWebsocket()
  })

  afterEach(() => {
    vi.clearAllMocks()
  })

  it('initializes with default values', () => {
    expect(websocketStore.connected).toBeDefined()
    expect(websocketStore.logs).toBeDefined()
    expect(websocketStore.messages).toBeDefined()
    expect(websocketStore.deliveredMessages).toBeDefined()
    expect(websocketStore.readMessages).toBeDefined()
    expect(websocketStore.failedMessages).toBeDefined()
    expect(websocketStore.typingUsers).toBeDefined()
    expect(websocketStore.isTyping).toBe(false)
    expect(websocketStore.pendingMessages).toBeDefined()
  })

  it('updates sender when auth store changes', () => {
    // Initially the store might have null
    websocketStore.updateSender()
    expect(websocketStore.sender).toBe('123') // from mocked authStore
  })

  it('sets receiver correctly', () => {
    expect(websocketStore.receiver).toBe(0)
    websocketStore.setReceiver(999)
    expect(websocketStore.receiver).toBe(999)
  })

  it('connects to websocket server successfully', () => {
    expect(websocketStore.connected).toBe(false)
    websocketStore.connect()

    // Because our mock calls onConnect, we should be "connected" now
    expect(websocketStore.connected).toBe(true)
    expect(websocketStore.connectionStatus).toBe('Connected')
  })

  it('disconnects from websocket server', () => {
    websocketStore.connect()
    expect(websocketStore.connected).toBe(true)

    websocketStore.disconnect()
    expect(websocketStore.connected).toBe(false)
    expect(websocketStore.connectionStatus).toBe('Disconnected')
  })

  it('logs messages', () => {
    const initialLength = websocketStore.logs.length
    // we exposed the `log` function, so we can call it
    websocketStore.log('test log', 'info')
    expect(websocketStore.logs.length).toBe(initialLength + 1)
    const lastLog = websocketStore.logs[websocketStore.logs.length - 1]
    expect(lastLog.text).toContain('test log')
    expect(lastLog.type).toBe('info')
  })

  it('sends a message when connected', () => {
    // Connect first
    websocketStore.connect()
    // Set up sender and receiver
    websocketStore.updateSender()
    websocketStore.setReceiver(456)
    // Provide some content
    websocketStore.messageContent = 'Hello, world!'

    // Attempt to send
    websocketStore.sendMessage()

    // We should see the message in local store with type 'sent'
    const lastMessage = websocketStore.messages.at(-1)
    expect(lastMessage?.content).toBe('Hello, world!')
    expect(lastMessage?.type).toBe('sent')
    expect(lastMessage?.status).toBe('sending')

    // Check that our mock stomp client did get a send
    expect(mockStompClient.publish).toHaveBeenCalled()
  })

  it('queues a message if not connected', () => {
    // do not connect
    websocketStore.updateSender()
    websocketStore.setReceiver(999)
    websocketStore.messageContent = 'Message while offline'

    websocketStore.sendMessage()

    // Should see the message in local store
    expect(websocketStore.messages).toHaveLength(1)
    const lastMessage = websocketStore.messages.at(-1)
    expect(lastMessage?.content).toBe('Message while offline')
    expect(lastMessage?.status).toBe('sending') // we set it immediately

    // But also see that it's in failedMessages + pending queue
    const [msg] = websocketStore.pendingMessages
    expect(msg.content).toBe('Message while offline')
    expect(websocketStore.failedMessages.size).toBe(1)
  })

  it('retries a failed message', () => {
    // Force a message into store
    const failedMsg = {
      id: 'client-123',
      senderId: '123',
      receiverId: 456,
      content: 'failed message',
      createdAt: new Date().toISOString(),
      type: 'sent',
      status: 'failed',
    }
    websocketStore.messages.push(failedMsg)
    websocketStore.failedMessages.add('client-123')

    // Connect so we can attempt retry
    websocketStore.connect()
    expect(websocketStore.connected).toBe(true)

    // Retry
    websocketStore.retryMessage('client-123')
    const updated = websocketStore.messages.find((m) => m.id === 'client-123')
    expect(updated?.status).toBe('sending')
    // The store sets it to 'sending' before the actual send
    expect(mockStompClient.publish).toHaveBeenCalled()
    expect(websocketStore.failedMessages.has('client-123')).toBe(false)
  })

  it('handles typing status', () => {
    websocketStore.connect()
    expect(websocketStore.isTyping).toBe(false)

    websocketStore.handleTyping()
    expect(websocketStore.isTyping).toBe(true)
    // The store sets a 3s timeout to revert isTyping to false
    // We won't wait 3s in the test, but we've confirmed it's set to true
  })

  

  it('marks all unread messages as read', () => {
    websocketStore.connect()
    websocketStore.updateSender() // => "123"
    websocketStore.setReceiver(999)

    // Add messages from 999
    const unread1 = {
      id: 'unread-msg-1',
      senderId: 999,
      receiverId: '123',
      content: 'hello1',
      type: 'received',
    }
    const unread2 = {
      id: 'unread-msg-2',
      senderId: 999,
      receiverId: '123',
      content: 'hello2',
      type: 'received',
    }
    websocketStore.messages.push(unread1, unread2)
    // Mark all as read
    websocketStore.markAllAsRead()

    expect(websocketStore.readMessages.has('unread-msg-1')).toBe(true)
    expect(websocketStore.readMessages.has('unread-msg-2')).toBe(true)
  })

  it('clears messages', () => {
    websocketStore.messages.push({
      id: 'test-clear',
      senderId: '123',
      receiverId: 999,
      content: 'should be cleared',
    })
    expect(websocketStore.messages.length).toBe(1)
    websocketStore.clearMessages()
    expect(websocketStore.messages.length).toBe(0)
  })
})
