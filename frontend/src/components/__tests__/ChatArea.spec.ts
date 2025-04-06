import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import ChatArea from '@/components/messaging/ChatArea.vue'
import type { Message, Conversation } from '@/types/messaging'

// Mock AuthStore
vi.mock('@/stores/AuthStore', () => ({
  useAuthStore: vi.fn(() => ({
    user: { id: 1 },
    isLoggedIn: true
  }))
}))

// Sample test data
const sampleMessage: Message = {
  id: 1,
  content: 'Hello there!',
  createdAt: '2022-01-01T10:00:00Z',
  senderId: 2,
  receiverId: 1,
  conversationId: 123,
}

const sampleConversation: Conversation = {
  id: '123',
  conversationId: '123',
  senderId: 1,
  receiverId: 2,
  receiverName: 'Alice',
  itemId: 10,
  listOfMessages: [sampleMessage],
  latestMessageTimestamp: '2022-01-01T10:00:00Z',
}

// Create a stub for MessageInput that emits "send-message" with a test text when its button is clicked.
const MessageInputStub = {
  name: 'MessageInput',
  template: `<button class="stub-button" @click="$emit('send-message', 'Test message')">Send</button>`,
}

describe('ChatArea.vue', () => {
  it('renders header with "Select a chat" when no active chat/contact is provided', async () => {
    // When activeChat is null and contact is undefined, the header should show "Select a chat"
    const wrapper = mount(ChatArea, {
      props: {
        activeChat: null,
        messages: [],
        contact: undefined,
      },
      global: {
        stubs: {
          // MessageInput is not rendered if activeChat is falsy.
          MessageInput: true,
        },
      },
    })
    // Check the header's contact-name text.
    const contactName = wrapper.find('.contact-name')
    expect(contactName.exists()).toBe(true)
    expect(contactName.text()).toBe('Select a chat')
  })

  it('renders header with contact name when activeChat and contact are provided', async () => {
    const wrapper = mount(ChatArea, {
      props: {
        activeChat: '123',
        messages: [sampleMessage],
        contact: sampleConversation,
      },
      global: {
        stubs: {
          MessageInput: true,
        },
      },
    })
    const contactName = wrapper.find('.contact-name')
    expect(contactName.exists()).toBe(true)
    expect(contactName.text()).toBe('Alice')
  })

  it('renders messages with a date divider and correct message classes', async () => {
    const wrapper = mount(ChatArea, {
      props: {
        activeChat: '123',
        messages: [sampleMessage],
        contact: sampleConversation,
      },
      global: {
        stubs: {
          MessageInput: true,
        },
      },
    })
    await flushPromises()
    const divider = wrapper.find('.date-divider')
    expect(divider.exists()).toBe(true)
    expect(divider.text()).toBe('Today')

    const messageDivs = wrapper.findAll('.message')
    expect(messageDivs.length).toBe(1)
    const messageDiv = messageDivs[0]
    // Since sampleMessage.senderId is 2 (not 1), it should have the "received" class.
    expect(messageDiv.classes()).toContain('received')
    expect(messageDiv.text()).toContain('Hello there!')
  })

  it('emits "send-message" event with correct payload when MessageInput emits send', async () => {
    const wrapper = mount(ChatArea, {
      props: {
        activeChat: '123',
        messages: [],
        contact: sampleConversation,
      },
      global: {
        stubs: {
          // Use our custom stub for MessageInput.
          MessageInput: MessageInputStub,
        },
      },
    })
    const inputStub = wrapper.findComponent({ name: 'MessageInput' })
    expect(inputStub.exists()).toBe(true)
    await inputStub.trigger('click')
    await flushPromises()
    const emitted = wrapper.emitted('send-message')
    expect(emitted).toBeTruthy()
    const payload = emitted![0][0] as {
      chatId: string
      message: { content: string; senderId: number; receiverId: number; timestamp: string }
    }
    expect(payload.chatId).toBe('123')
    expect(payload.message.content).toBe('Test message')
    expect(payload.message.senderId).toBe(1)
    expect(payload.message.receiverId).toBe(2)
    expect(typeof payload.message.timestamp).toBe('string')
  })

  it('does not emit "send-message" when activeChat or contact is missing', async () => {
    // Case 1: activeChat is null → MessageInput is not rendered.
    const wrapper1 = mount(ChatArea, {
      props: {
        activeChat: null,
        messages: [],
        contact: sampleConversation,
      },
      global: {
        stubs: {
          MessageInput: MessageInputStub,
        },
      },
    })
    expect(wrapper1.findComponent({ name: 'MessageInput' }).exists()).toBe(false)
    expect(wrapper1.emitted('send-message')).toBeFalsy()

    // Case 2: contact is undefined → even if activeChat is provided, handleSendMessage returns early.
    const wrapper2 = mount(ChatArea, {
      props: {
        activeChat: '123',
        messages: [],
        contact: undefined,
      },
      global: {
        stubs: {
          MessageInput: MessageInputStub,
        },
      },
    })
    const inputStub2 = wrapper2.findComponent({ name: 'MessageInput' })
    expect(inputStub2.exists()).toBe(true)
    await inputStub2.trigger('click')
    await flushPromises()
    expect(wrapper2.emitted('send-message')).toBeFalsy()
  })
})
