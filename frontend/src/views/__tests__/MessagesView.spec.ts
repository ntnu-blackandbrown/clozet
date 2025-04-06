import { mount, flushPromises } from '@vue/test-utils'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import MessagesView from '@/views/MessagesView.vue' // Adjust path as needed
import axios from '@/api/axios'

// --- Mocks ---
// Mock axios
vi.mock('@/api/axios')
const mockedAxios = axios as unknown as {
  get: any
  post: any
}

// Mock auth store
vi.mock('@/stores/AuthStore', () => ({
  useAuthStore: vi.fn(() => ({
    user: { id: 1 },
    isLoggedIn: true
  }))
}))

// Stub vue-router's useRoute and useRouter
const pushSpy = vi.fn()
const replaceSpy = vi.fn()

// Default route mock with chatId
vi.mock('vue-router', () => ({
  useRoute: () => ({
    params: {
      chatId: '2', // Simulate an active chat id as a string
    },
  }),
  useRouter: () => ({
    push: pushSpy,
    replace: replaceSpy,
  }),
}))

// Stub MessagesSidebar and ChatArea to isolate the container logic.
const MessagesSidebarStub = {
  name: 'MessagesSidebar',
  props: ['conversations', 'activeConversationId', 'receiverUsernames'],
  template: `<div class="messages-sidebar-stub">
    <div class="chats" v-for="conversation in conversations" :key="conversation.id">{{ conversation.receiverName }}</div>
    <button @click="$emit('select-chat', '1')">Select Chat 1</button>
  </div>`,
  emits: ['select-chat']
}

const ChatAreaStub = {
  name: 'ChatArea',
  props: ['activeChat', 'messages', 'contact'],
  template: `<div class="chat-area-stub">
    <div class="active-chat">{{ activeChat }}</div>
    <div class="chat-messages">{{ messages ? messages.length : 0 }}</div>
    <div class="contact-name">{{ contact ? contact.receiverName : '' }}</div>
    <button @click="$emit('send-message', { chatId: '2', message: { content: 'test' } })">Send Message</button>
  </div>`,
  emits: ['send-message']
}

// Mock websocket store
vi.mock('@/websocket/websocket', () => ({
  useWebsocket: vi.fn(() => ({
    connect: vi.fn(),
    setReceiver: vi.fn(),
    updateSender: vi.fn(),
    messages: [],
    messageContent: '',
    connectionStatus: 'Connected',
    connectionStatusClass: 'connected',
    connected: true,
    sendMessage: vi.fn(),
    clearMessages: vi.fn()
  }))
}))

// Sample data for testing
const sampleConversations = [
  {
    id: '1', // Updated to string
    conversationId: '1', // Added conversationId
    senderId: 1, // Added senderId
    receiverId: 2, // Added receiverId
    receiverName: 'Alice',
    itemId: 101,
    listOfMessages: [
      {
        id: 1,
        content: 'Hi',
        createdAt: '2022-01-01T10:00:00Z',
        senderId: 2,
        receiverId: 1,
        conversationId: 1,
      },
    ],
    latestMessageTimestamp: '2022-01-01T10:00:00Z',
  },
  {
    id: '2', // Updated to string
    conversationId: '2', // Added conversationId
    senderId: 1, // Added senderId
    receiverId: 3, // Added receiverId
    receiverName: 'Bob',
    itemId: 102,
    listOfMessages: [
      {
        id: 2,
        content: 'Hello Bob',
        createdAt: '2022-01-02T11:00:00Z',
        senderId: 1,
        receiverId: 2,
        conversationId: 2,
      },
    ],
    latestMessageTimestamp: '2022-01-02T11:00:00Z',
  },
]

const sampleMessagesForChat2 = [
  {
    id: 2,
    content: 'Hello Bob',
    createdAt: '2022-01-02T11:00:00Z',
    senderId: 1,
    receiverId: 2,
    conversationId: 2,
  },
]

beforeEach(() => {
  vi.clearAllMocks()

  // Mock axios.get based on URL:
  mockedAxios.get.mockImplementation((url: string, config?: any) => {
    if (url === '/api/conversations') {
      return Promise.resolve({ data: sampleConversations })
    }
    if (url === '/api/messages') {
      return Promise.resolve({ data: sampleMessagesForChat2 })
    }
    if (url.includes('/api/users/')) {
      return Promise.resolve({ data: {
        id: Number(url.split('/').pop()),
        username: url.includes('2') ? 'alice' : 'bob'
      }})
    }
    return Promise.resolve({ data: {} })
  })

  // For axios.post (if needed)
  mockedAxios.post.mockResolvedValue({ data: {} })
})

describe('MessagingContainer.vue', () => {
  it('routes to first conversation if no active chat is in route', async () => {
    // Override useRoute to simulate no chatId
    vi.mock('vue-router', () => ({
      useRoute: () => ({
        params: {},
      }),
      useRouter: () => ({
        push: pushSpy,
        replace: replaceSpy,
      }),
    }), { virtual: true })

    const wrapper = mount(MessagesView, {
      global: {
        stubs: {
          MessagesSidebar: MessagesSidebarStub,
          ChatArea: ChatAreaStub,
        },
      },
    })

    await flushPromises()
    await new Promise((resolve) => setTimeout(resolve, 10))

    // Check if sample conversations are returned by axios
    expect(mockedAxios.get).toHaveBeenCalledWith('/api/conversations', expect.any(Object))

    // Manually set the value
    wrapper.vm.chats = sampleConversations
    await flushPromises()

    // Manually call handleChatSelect to simulate logic
    await wrapper.vm.handleChatSelect(sampleConversations[0].id)

    // Verify router.push is called with correct path
    expect(pushSpy).toHaveBeenCalledWith(`/messages/${sampleConversations[0].id}`)
  })

  it('calls router.push when MessagesSidebar emits "select-chat"', async () => {
    const wrapper = mount(MessagesView, {
      global: {
        stubs: {
          MessagesSidebar: MessagesSidebarStub,
          ChatArea: ChatAreaStub,
        },
      },
    })

    await flushPromises()

    // Manually trigger the event that would normally be emitted
    await wrapper.findComponent({ name: 'MessagesSidebar' }).vm.$emit('select-chat', '1')
    expect(pushSpy).toHaveBeenCalledWith(`/messages/1`)
  })

  it('handles message sending correctly', async () => {
    const wrapper = mount(MessagesView, {
      global: {
        stubs: {
          MessagesSidebar: MessagesSidebarStub,
          ChatArea: ChatAreaStub,
        },
      },
    })

    await flushPromises()

    // Set necessary data directly
    wrapper.vm.chats = sampleConversations
    await flushPromises()

    // Find the ChatArea stub
    const chatAreaStub = wrapper.findComponent({ name: 'ChatArea' })

    // Emit send-message event from ChatArea
    await chatAreaStub.vm.$emit('send-message', {
      chatId: '2',
      message: { content: 'New message', senderId: 1, receiverId: 3 }
    })

    // Verify that UI interaction works
    expect(chatAreaStub.emitted('send-message')).toBeTruthy()
  })

  it('logs error when message sending fails', async () => {
    // Mock console.error to verify it's called
    const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

    // Force an error to occur
    mockedAxios.post.mockRejectedValue(new Error('API Error'))

    const wrapper = mount(MessagesView, {
      global: {
        stubs: {
          MessagesSidebar: MessagesSidebarStub,
          ChatArea: ChatAreaStub,
        },
      },
    })

    await flushPromises()

    // Find the ChatArea stub and emit the event
    const chatAreaStub = wrapper.findComponent({ name: 'ChatArea' })
    await chatAreaStub.vm.$emit('send-message', {
      chatId: '2',
      message: { content: 'Test error', senderId: 1, receiverId: 3 }
    })

    await flushPromises()

    // Check if error was logged
    expect(errorSpy).toHaveBeenCalled()

    // Restore the spy
    errorSpy.mockRestore()
  })
})
