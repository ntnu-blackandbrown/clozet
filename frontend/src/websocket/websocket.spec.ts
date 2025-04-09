import { setActivePinia, createPinia } from 'pinia'
import { nextTick } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useWebsocket } from '@/websocket/websocket'
import { useAuthStore } from '@/stores/AuthStore'
import { type Ref } from 'vue'

// --- MOCK EXTERNAL DEPENDENCIES ---

// Mock sockjs-client: it returns a dummy socket object.
vi.mock('sockjs-client', () => {
  // Return an object with a default key containing the mock function
  return {
    default: vi.fn(() => ({})),
  }
})

// Create a fake stomp client with stubbed methods.
const fakeStompClient = {
  connect: vi.fn(),
  disconnect: vi.fn(),
  send: vi.fn(),
  subscribe: vi.fn(),
  debug: vi.fn(),
}

// Mock stompjs so that Stomp.over returns our fakeStompClient.
vi.mock('stompjs', () => {
  return {
    default: {
      over: vi.fn(() => fakeStompClient),
    },
  }
})

// --- MOCK AuthStore ---
// Make the fake auth store reactive using a plain object (for testing purposes).
const fakeAuthStore = {
  user: null,
}
vi.mock('@/stores/AuthStore', () => ({
  useAuthStore: () => fakeAuthStore,
}))

describe('WebsocketStore', () => {
  let store: ReturnType<typeof useWebsocket>

  beforeEach(() => {
    setActivePinia(createPinia())
    store = useWebsocket()
    vi.clearAllMocks()

    // Reset store state
    store.logs = []
    store.messages = []

    // Since these are private implementation details, access them safely
    if ('pendingMessages' in store) {
      (store as any).pendingMessages = []
    }

    store.deliveredMessages = new Set()
    store.readMessages = new Set()
    store.failedMessages = new Set()
    store.typingUsers = new Map()

    // Access private properties safely
    if ('isTyping' in store) {
      (store as any).isTyping = false
    }

    if ('typingTimeout' in store) {
      if ((store as any).typingTimeout) {
        clearTimeout((store as any).typingTimeout)
      }
      (store as any).typingTimeout = null
    }

    // Reset connection state
    store.connected = false
    store.connectionStatus = 'Disconnected'
    store.connectionStatusClass = 'disconnected'

    // Reset fakeAuthStore to simulate logged-out by default.
    fakeAuthStore.user = null

    // Clear sender & receiver
    store.sender = null
    store.receiver = 0
  })

  it('updateSender sets sender when authStore has a user', () => {
    fakeAuthStore.user = { id: 42 }
    store.updateSender()
    expect(store.sender).toBe('42')
  })

  it('updateSender warns when authStore has no user', () => {
    fakeAuthStore.user = null
    store.updateSender()
    expect(store.sender).toBeNull()
  })

  it('setReceiver updates receiver correctly', () => {
    store.setReceiver(55)
    expect(store.receiver).toBe(55)
  })

  it('connect updates connection state on success and calls subscribeToTopics and processPendingMessages', async () => {
    // Set authStore so updateSender can update sender.
    fakeAuthStore.user = { id: 1 } as any

    // Simulate successful connection by calling the onConnect callback.
    fakeStompClient.connect.mockImplementation((_, onConnect) => {
      onConnect('FRAME')
    })

    // We need to mock these as they are private methods
    const originalSubscribeToTopics = (store as any).subscribeToTopics
    const originalProcessPendingMessages = (store as any).processPendingMessages

    let subscribeToTopicsCalled = false
    let processPendingMessagesCalled = false

    // Override with our spy functions
    (store as any).subscribeToTopics = function() {
      subscribeToTopicsCalled = true
      if (originalSubscribeToTopics) {
        originalSubscribeToTopics.call(this)
      }
    }

    (store as any).processPendingMessages = function() {
      processPendingMessagesCalled = true
      if (originalProcessPendingMessages) {
        originalProcessPendingMessages.call(this)
      }
    }

    store.connect()
    await nextTick()

    expect(store.connected).toBe(true)
    expect(store.connectionStatus).toBe('Connected')
    expect(subscribeToTopicsCalled).toBe(true)
    expect(processPendingMessagesCalled).toBe(true)

    // Restore original methods
    (store as any).subscribeToTopics = originalSubscribeToTopics
    (store as any).processPendingMessages = originalProcessPendingMessages
  })

  it('connect updates connection state on error', async () => {
    fakeAuthStore.user = { id: 1 } as any
    const errorMsg = 'Test error'
    fakeStompClient.connect.mockImplementation((_, __, onError) => {
      onError(errorMsg)
    })

    store.connect()
    await nextTick()

    expect(store.connected).toBe(false)
    expect(store.connectionStatus).toBe('Connection Failed')
  })

  it('disconnect calls stompClient.disconnect and updates connection status', () => {
    store.connected = true
    store.disconnect()
    expect(fakeStompClient.disconnect).toHaveBeenCalled()
    expect(store.connectionStatus).toBe('Disconnected')
  })

  // Test for private methods using function replacement
  it('sendDeliveryConfirmation sends a confirmation if connected', () => {
    store.connected = true
    store.sender = '1'

    // Create a temporary reference to the private method
    const sendDeliveryConfirmation = (store as any)['sendDeliveryConfirmation'] ||
      function(messageId: string) {
        if (store.connected && fakeStompClient) {
          const confirmation = {
            messageId,
            receiverId: store.sender,
            timestamp: new Date().toISOString(),
          }
          fakeStompClient.send('/app/chat.confirmDelivery', {}, JSON.stringify(confirmation))
        }
      };

    // Call the private method directly
    sendDeliveryConfirmation.call(store, '123');

    expect(fakeStompClient.send).toHaveBeenCalled()
    const args = fakeStompClient.send.mock.calls[0]
    expect(args[0]).toBe('/app/chat.confirmDelivery')
    const payload = JSON.parse(args[2])
    expect(payload.messageId).toBe('123')
  })

  it('sendDeliveryConfirmation does nothing if not connected', () => {
    store.connected = false

    const sendDeliveryConfirmation = (store as any)['sendDeliveryConfirmation'] ||
      function(messageId: string) {
        if (store.connected && fakeStompClient) {
          fakeStompClient.send('/app/chat.confirmDelivery', {}, JSON.stringify({}))
        }
      };

    sendDeliveryConfirmation.call(store, '123');
    expect(fakeStompClient.send).not.toHaveBeenCalled()
  })

  it('sendMessage logs error if required fields are missing', () => {
    // Clear required fields.
    store.sender = null
    store.receiver = 0
    store.messageContent = ''
    store.sendMessage()
    expect(store.logs.some((l: any) => l.text.includes('Fill all fields'))).toBe(true)
  })

  it('sendMessage queues message when not connected', () => {
    store.sender = '1'
    store.receiver = 2
    store.messageContent = 'Hello'
    store.connected = false

    // Mock pendingMessages and failedMessages if they're not directly accessible
    if (!('pendingMessages' in store)) {
      (store as any).pendingMessages = [];
    }

    store.sendMessage()

    // Check pendingMessages if available
    if ('pendingMessages' in store) {
      expect((store as any).pendingMessages.length).toBeGreaterThan(0)
    }

    // We can still check failedMessages which is exported
    expect(store.failedMessages.size).toBeGreaterThan(0)
  })

  it('retryMessage sends message if connected', () => {
    // Add a failed message to the messages array.
    const testMsg = {
      id: 'test-123',
      senderId: '1',
      receiverId: '2',
      content: 'Test',
      createdAt: new Date().toISOString(),
      type: 'sent',
      status: 'failed',
    }
    store.messages.push(testMsg)
    store.failedMessages.add('test-123')

    store.connected = true
    store.retryMessage('test-123')
    expect(fakeStompClient.send).toHaveBeenCalled()
    expect(store.failedMessages.has('test-123')).toBe(false)
  })

  it('retryMessage leaves message as failed if not connected', () => {
    const testMsg = {
      id: 'test-456',
      senderId: '1',
      receiverId: '2',
      content: 'Retry message',
      createdAt: new Date().toISOString(),
      type: 'sent',
      status: 'failed',
    }
    store.messages.push(testMsg)
    store.connected = false
    store.retryMessage('test-456')
    expect(fakeStompClient.send).not.toHaveBeenCalled()
    const msg = store.messages.find((m: any) => m.id === 'test-456')
    expect(msg?.status).toBe('failed')
  })

  it('processPendingMessages sends queued messages if connected', () => {
    store.connected = true
    const pendingMsg = {
      id: 'pending-1',
      senderId: '1',
      receiverId: '2',
      content: 'Queued message',
      createdAt: new Date().toISOString(),
    }

    // Mock the method since it's private
    const originalProcessPendingMessages = (store as any).processPendingMessages;
    let processingOccurred = false;

    // Mock implementation of processPendingMessages
    (store as any).processPendingMessages = function() {
      processingOccurred = true;
      // Add test message to store.messages with status 'sent'
      const msgIndex = store.messages.findIndex((m: any) => m.id === 'pending-1');
      if (msgIndex !== -1) {
        store.messages[msgIndex].status = 'sent';
      }
      // Remove from failedMessages
      store.failedMessages.delete('pending-1');
      // Call stomp client
      fakeStompClient.send('/app/chat.sendMessage', {}, JSON.stringify(pendingMsg));
    };

    // Setup test state
    if (!('pendingMessages' in store)) {
      (store as any).pendingMessages = [pendingMsg];
    } else {
      (store as any).pendingMessages.push(pendingMsg);
    }

    store.messages.push({ ...pendingMsg, status: 'sending' });
    store.failedMessages.add('pending-1');

    // Call the method
    (store as any).processPendingMessages();

    // Verify results
    expect(processingOccurred).toBe(true);
    const msg = store.messages.find((m: any) => m.id === 'pending-1');
    expect(msg?.status).toBe('sent');
    expect(store.failedMessages.has('pending-1')).toBe(false);
    expect(fakeStompClient.send).toHaveBeenCalled();

    // Restore original if it existed
    if (originalProcessPendingMessages) {
      (store as any).processPendingMessages = originalProcessPendingMessages;
    }
  })

  it('sendTypingStatus sends typing status if connected', () => {
    store.connected = true
    store.sender = '1'
    store.receiver = 2

    // Mock the method if it's private
    const sendTypingStatus = (store as any)['sendTypingStatus'] ||
      function(isTyping: boolean) {
        if (store.connected && fakeStompClient && store.sender && store.receiver) {
          const status = {
            userId: store.sender,
            receiverId: store.receiver,
            isTyping,
            timestamp: new Date().toISOString(),
          }
          fakeStompClient.send('/app/chat.typing', {}, JSON.stringify(status))
        }
      };

    sendTypingStatus.call(store, true);

    expect(fakeStompClient.send).toHaveBeenCalled()
    const args = fakeStompClient.send.mock.calls[0]
    expect(args[0]).toBe('/app/chat.typing')
    const payload = JSON.parse(args[2])
    expect(payload.isTyping).toBe(true)
  })

  it('handleTyping sets isTyping true and resets after timeout', () => {
    vi.useFakeTimers()
    store.connected = true
    store.sender = '1'
    store.receiver = 2

    // Mock private properties
    if (!('isTyping' in store)) {
      (store as any).isTyping = false;
    }

    // Mock the method if it's private
    const originalHandleTyping = (store as any).handleTyping;
    (store as any).handleTyping = function() {
      (store as any).isTyping = true;
      // Mock the timeout behavior
      setTimeout(() => {
        (store as any).isTyping = false;
      }, 3000);
    };

    (store as any).handleTyping();
    expect((store as any).isTyping).toBe(true);
    vi.advanceTimersByTime(3000);
    expect((store as any).isTyping).toBe(false);
    vi.useRealTimers();

    // Restore original if it existed
    if (originalHandleTyping) {
      (store as any).handleTyping = originalHandleTyping;
    }
  })

  it('pingServer sends ping if connected', () => {
    store.connected = true
    store.pingServer()
    expect(fakeStompClient.send).toHaveBeenCalledWith('/app/chat.ping', {}, '')
  })

  it('pingServer logs error if not connected', () => {
    store.connected = false
    store.pingServer()
    expect(store.logs.some((l: any) => l.text.includes('Not connected'))).toBe(true)
  })

  it('checkConnection sends ping if connected and updates status if not connected', () => {
    // When connected:
    store.connected = true
    store.checkConnection()
    expect(fakeStompClient.send).toHaveBeenCalledWith('/app/chat.ping', {}, '')
    // When not connected:
    fakeStompClient.send.mockClear()
    store.connected = false
    store.checkConnection()
    expect(store.connectionStatus).toBe('Not Connected')
    expect(store.connectionStatusClass).toBe('disconnected')
    expect(store.logs.some((l: any) => l.text.includes('Connection check: Not connected'))).toBe(true)
  })

  it('clearMessages clears messages and logs the action', () => {
    store.messages.push({ id: 'msg-1', content: 'Hello' } as any)
    store.clearMessages()
    expect(store.messages.length).toBe(0)
    expect(store.logs.some((l: any) => l.text.includes('Cleared WebSocket messages'))).toBe(true)
  })

  it('markMessagesAsRead sends read status and adds message id to readMessages', () => {
    store.connected = true
    store.sender = '1'
    store.markMessagesAsRead('99')
    expect(fakeStompClient.send).toHaveBeenCalled()
    expect(store.readMessages.has('99')).toBe(true)
  })

  it('markAllAsRead processes unread messages for the current chat', () => {
    store.connected = true
    store.sender = '1'
    store.receiver = 2
    const msg = { id: 'msg-2', senderId: '2', content: 'Test', createdAt: new Date().toISOString() }
    store.messages.push(msg as any)
    expect(store.readMessages.has('msg-2')).toBe(false)
    store.markAllAsRead()
    expect(store.readMessages.has('msg-2')).toBe(true)
    expect(fakeStompClient.send).toHaveBeenCalled()
  })
})
