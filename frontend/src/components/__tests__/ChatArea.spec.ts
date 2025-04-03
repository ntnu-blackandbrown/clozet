import { mount, flushPromises } from '@vue/test-utils'
import { vi, describe, it, expect } from 'vitest'
import MessageChatArea from '@/components/messaging/ChatArea.vue' // adjust path if needed

describe('MessageChatArea.vue', () => {
  // Define two sample conversations
  const conversationActive = {
    id: 100,
    receiverName: 'Test Receiver',
    itemId: 200,
    listOfMessages: [
      {
        id: 1,
        content: 'First message',
        createdAt: '2022-01-01T10:00:00Z',
        senderId: 2,
        receiverId: 1,
        conversationId: 100,
      },
      {
        id: 2,
        content: 'Second message',
        createdAt: '2022-01-01T11:00:00Z',
        senderId: 1,
        receiverId: 2,
        conversationId: 100,
      },
    ],
    latestMessageTimestamp: '2022-01-01T11:00:00Z',
  }

  const conversationInactive = {
    id: 101,
    receiverName: 'Another Receiver',
    itemId: 201,
    listOfMessages: [],
    latestMessageTimestamp: '2022-01-02T09:00:00Z',
  }

  const conversations = [conversationActive, conversationInactive]

  it('renders "Select a chat to start messaging" when no active conversation', async () => {
    const wrapper = mount(MessageChatArea, {
      props: {
        activeConversationId: 999, // No conversation matches
        conversations,
      },
      global: {
        stubs: {
          MessageInput: true,
        },
      },
    })

    // The no-chat-selected block should be rendered
    const noChat = wrapper.find('.no-chat-selected')
    expect(noChat.exists()).toBe(true)
    expect(noChat.text()).toContain('Select a chat to start messaging')
  })

  it('renders active conversation header with receiver name', async () => {
    const wrapper = mount(MessageChatArea, {
      props: {
        activeConversationId: 100,
        conversations,
      },
      global: {
        stubs: {
          MessageInput: true,
        },
      },
    })

    // Check that the header displays the active conversation's receiverName
    const contactName = wrapper.find('.contact-name')
    expect(contactName.exists()).toBe(true)
    expect(contactName.text()).toBe('Test Receiver')
  })

  it('renders chat messages with a date divider and proper message details', async () => {
    const wrapper = mount(MessageChatArea, {
      props: {
        activeConversationId: 100,
        conversations,
      },
      global: {
        stubs: {
          MessageInput: true,
        },
      },
    })

    await flushPromises()

    // Check that the chat messages container exists
    const chatMessages = wrapper.find('.chat-messages')
    expect(chatMessages.exists()).toBe(true)

    // The first message should render a date divider "Today"
    const divider = chatMessages.find('.date-divider')
    expect(divider.exists()).toBe(true)
    expect(divider.text()).toBe('Today')

    // Check that both messages are rendered
    const messageDivs = chatMessages.findAll('.message')
    expect(messageDivs.length).toBe(conversationActive.listOfMessages.length)

    // First message: senderId is 2 → should have class "received"
    const firstMessage = messageDivs[0]
    expect(firstMessage.classes()).toContain('received')
    expect(firstMessage.text()).toContain('First message')

    // Second message: senderId is 1 → should have class "sent" and show a status "Sent"
    const secondMessage = messageDivs[1]
    expect(secondMessage.classes()).toContain('sent')
    expect(secondMessage.text()).toContain('Second message')
    const status = secondMessage.find('.message-status')
    expect(status.exists()).toBe(true)
    expect(status.text()).toBe('Sent')
  })

  it('emits "send-message" event when MessageInput emits a send event', async () => {
    // Stub MessageInput with a button that emits a "send-message" event with a test message
    const MessageInputStub = {
      name: 'MessageInput',
      template: `<button class="stub-message-input" @click="$emit('send-message', 'Hello Chat')">Send</button>`,
    }

    const wrapper = mount(MessageChatArea, {
      props: {
        activeConversationId: 100,
        conversations,
      },
      global: {
        stubs: {
          MessageInput: MessageInputStub,
        },
      },
    })

    // Simulate clicking the MessageInput stub to send a message
    const btn = wrapper.find('.stub-message-input')
    await btn.trigger('click')

    // The component should emit a "send-message" event with the correct payload
    const emitted = wrapper.emitted('send-message')
    expect(emitted).toBeTruthy()
    const payload = emitted?.[0]?.[0]
    expect(payload).toEqual(
      expect.objectContaining({
        conversationId: 100,
        message: expect.objectContaining({
          content: 'Hello Chat',
          senderId: 1,
          receiverId: 100, // Based on handleSendMessage: receiverId is set to activeConversation.id
          conversationId: 100,
          createdAt: expect.any(String),
        }),
      }),
    )
  })
})
