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

// Stub vue-router's useRoute and useRouter
const pushSpy = vi.fn()
const replaceSpy = vi.fn()
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
  props: ['chats', 'activeId'],
  template: `<div class="messages-sidebar-stub">
    <div class="chats" v-for="chat in chats" :key="chat.id">{{ chat.receiverName }}</div>
  </div>`,
}

const ChatAreaStub = {
  name: 'ChatArea',
  props: ['activeChat', 'messages', 'contact'],
  template: `<div class="chat-area-stub">
    <div class="active-chat">{{ activeChat }}</div>
    <div class="chat-messages">{{ messages ? messages.length : 0 }}</div>
    <div class="contact-name">{{ contact ? contact.receiverName : '' }}</div>
  </div>`,
}

// Sample data for testing
const sampleConversations = [
  {
    id: 1,
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
    id: 2,
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
  mockedAxios.get.mockImplementation((url: string) => {
    if (url === '/api/conversations') {
      return Promise.resolve({ data: sampleConversations })
    }
    if (url === `/api/messages/2`) {
      return Promise.resolve({ data: sampleMessagesForChat2 })
    }
    return Promise.resolve({ data: {} })
  })

  // For axios.post (if needed)
  mockedAxios.post.mockResolvedValue({ data: {} })
})

describe('MessagingContainer.vue', () => {
  /*it('fetches conversations and messages on mount and routes if activeChat exists', async () => {
    const wrapper = mount(MessagesView, {
      global: {
        stubs: {
          MessagesSidebar: MessagesSidebarStub,
          ChatArea: ChatAreaStub,
        },
      },
    })

    await flushPromises()
    // Extra wait to ensure asynchronous onMounted operations complete
    await new Promise(resolve => setTimeout(resolve, 10))

    // Check that conversations are set
    expect(wrapper.vm.chats).toEqual(sampleConversations)

    // Since activeChat (2) exists, it should fetch messages for chat 2.
    expect(wrapper.vm.chatMessages[2]).toEqual(sampleMessagesForChat2)

    // Because activeChat is present from route params, router.replace should NOT be called.
    expect(replaceSpy).not.toHaveBeenCalled()
  })*/

  it('routes to first conversation if no active chat is in route', async () => {
    // Override useRoute to simulate no valid chatId
    vi.mock('vue-router', () => ({
      useRoute: () => ({
        params: { chatId: 'NaN' },
      }),
      useRouter: () => ({
        push: pushSpy,
        replace: replaceSpy,
      }),
    }))

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
    expect(replaceSpy).toHaveBeenCalledWith(`/messages/${sampleConversations[0].id}`)
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

    // Simulate MessagesSidebar emitting "select-chat" with chat id 1
    await wrapper.findComponent(MessagesSidebarStub).vm.$emit('select-chat', 1)
    expect(pushSpy).toHaveBeenCalledWith(`/messages/1`)
  })

  it('handles new message correctly by posting and then fetching messages', async () => {
    const wrapper = mount(MessagesView, {
      global: {
        stubs: {
          MessagesSidebar: MessagesSidebarStub,
          ChatArea: ChatAreaStub,
        },
      },
    })

    await flushPromises()

    // Prepare a fake message payload:
    const newMessagePayload = {
      chatId: 2,
      message: {
        senderId: 1,
        receiverId: 2,
        content: 'New message',
      },
    }

    // Call handleNewMessage directly
    await wrapper.vm.handleNewMessage(newMessagePayload)
    await flushPromises()

    // Expect axios.post to have been called with the correct payload
    expect(mockedAxios.post).toHaveBeenCalledWith(
      '/api/messages',
      expect.objectContaining({
        senderId: '1',
        receiverId: '2',
        content: 'New message',
        timestamp: expect.any(String),
      }),
    )

    // Then axios.get should have been called to fetch messages for chat 2
    expect(mockedAxios.get).toHaveBeenCalledWith(`/api/messages/2`)

    // And chatMessages for chat id 2 should now equal sampleMessagesForChat2
    expect(wrapper.vm.chatMessages[2]).toEqual(sampleMessagesForChat2)
  })

  it('logs error when posting message fails', async () => {
    const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
    mockedAxios.post.mockRejectedValue(new Error('Post failed'))

    const wrapper = mount(MessagesView, {
      global: {
        stubs: {
          MessagesSidebar: MessagesSidebarStub,
          ChatArea: ChatAreaStub,
        },
      },
    })

    await flushPromises()
    await wrapper.vm.handleNewMessage({
      chatId: 2,
      message: { senderId: 1, receiverId: 2, content: 'Test error' },
    })
    await flushPromises()
    expect(console.error).toHaveBeenCalledWith('Failed to send message:', expect.any(Error))
    errorSpy.mockRestore()
  })
})
