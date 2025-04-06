import { mount, flushPromises } from '@vue/test-utils'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import MessagesSidebar from '@/components/messaging/MessagesSidebar.vue' // adjust path as needed
import axios from 'axios'

// Mock axios to intercept image fetches
vi.mock('axios')
const mockedAxios = axios as unknown as { get: any }

describe('MessagesSidebar.vue', () => {
  // Sample conversations prop
  const conversations = [
    {
      id: '1',
      conversationId: '1',
      senderId: 2,
      receiverId: 1,
      receiverName: 'Alice',
      itemId: 101,
      listOfMessages: [
        {
          id: 1,
          content: 'Hello',
          createdAt: '2022-01-01T10:00:00Z',
          senderId: 2,
          receiverId: 1,
          conversationId: 1,
        },
        {
          id: 2,
          content: 'How are you?',
          createdAt: '2022-01-01T11:00:00Z',
          senderId: 1,
          receiverId: 2,
          conversationId: 1,
        },
      ],
      latestMessageTimestamp: '2022-01-01T11:00:00Z',
    },
    {
      id: '2',
      conversationId: '2',
      senderId: 1,
      receiverId: 3,
      receiverName: 'Bob',
      itemId: 102,
      listOfMessages: [],
      latestMessageTimestamp: '2022-01-02T09:00:00Z',
    },
  ]

  const activeConversationId = '1'

  // Create a Map for receiverUsernames prop
  const receiverUsernames = new Map([
    [1, 'Alice'],
    [3, 'Bob']
  ])

  beforeEach(() => {
    vi.clearAllMocks()
    // For each conversation, simulate axios.get response based on itemId
    mockedAxios.get.mockImplementation((url: string) => {
      if (url === '/api/items/101') {
        return Promise.resolve({ data: { images: [{ imageUrl: 'http://example.com/101.jpg' }] } })
      } else if (url === '/api/items/102') {
        return Promise.resolve({ data: { images: [] } })
      }
      return Promise.reject(new Error('Not Found'))
    })
  })

  it('renders header with correct message count', async () => {
    const wrapper = mount(MessagesSidebar, {
      props: {
        conversations,
        activeConversationId,
        receiverUsernames
      },
      global: {
        stubs: {
          // Stub the modal to avoid interfering with header tests
          ProductDisplayModal: {
            name: 'ProductDisplayModal',
            props: ['productId'],
            template: '<div class="modal-stub">Product ID: {{ productId }}</div>',
          },
          // Stub RouterLink if used
          RouterLink: {
            template: '<a class="router-link"><slot /></a>',
          },
        },
      },
    })
    await flushPromises()
    // Header should include "Messages" and display the count (2)
    expect(wrapper.find('h1').text()).toContain('Messages')
    expect(wrapper.find('h1').text()).toContain(`${conversations.length}`)
  })

  it('renders chat items with proper info and active class', async () => {
    const wrapper = mount(MessagesSidebar, {
      props: {
        conversations,
        activeConversationId,
        receiverUsernames
      },
      global: {
        stubs: {
          ProductDisplayModal: true,
          RouterLink: true,
        },
      },
    })
    await flushPromises()

    const chatItems = wrapper.findAll('.chat-item')
    expect(chatItems.length).toBe(conversations.length)

    // For Alice (conversation id 1): expect active class and correct latest message
    const aliceItem = chatItems.find((item) => item.text().includes('Alice'))
    expect(aliceItem).toBeTruthy()
    expect(aliceItem?.classes()).toContain('active')
    // Latest message should be "How are you?" (since it's the newest)
    expect(aliceItem?.text()).toContain('How are you?')

    // For Bob (conversation id 2): no messages so should display fallback text
    const bobItem = chatItems.find((item) => item.text().includes('Bob'))
    expect(bobItem).toBeTruthy()
    expect(bobItem?.text()).toContain('No messages yet')
  })

  it('fetches item images and renders avatar when available', async () => {
    const wrapper = mount(MessagesSidebar, {
      props: {
        conversations,
        activeConversationId,
        receiverUsernames
      },
      global: {
        stubs: {
          ProductDisplayModal: true,
          RouterLink: true,
        },
      },
    })
    await flushPromises()

    // For Alice (itemId 101), axios returns an image – check for an <img> element with correct src
    const aliceItem = wrapper.findAll('.chat-item').find((item) => item.text().includes('Alice'))
    expect(aliceItem).toBeTruthy()
    const img = aliceItem?.find('img')
    expect(img?.exists()).toBe(true)
    expect(img?.attributes('src')).toBe('http://example.com/101.jpg')

    // For Bob (itemId 102), no images should be rendered
    const bobItem = wrapper.findAll('.chat-item').find((item) => item.text().includes('Bob'))
    expect(bobItem).toBeTruthy()
    const imgBob = bobItem?.find('img')
    expect(imgBob?.exists()).toBe(false)
  })

  it('emits "select-chat" event with correct id when a chat item is clicked', async () => {
    const wrapper = mount(MessagesSidebar, {
      props: {
        conversations,
        activeConversationId,
        receiverUsernames
      },
      global: {
        stubs: {
          ProductDisplayModal: true,
          RouterLink: true,
        },
      },
    })
    await flushPromises()
    const chatItems = wrapper.findAll('.chat-item')
    // Click on Bob's chat item (id 2)
    const bobItem = chatItems.find((item) => item.text().includes('Bob'))
    expect(bobItem).toBeTruthy()
    await bobItem?.trigger('click')
    // Verify the "select-chat" event is emitted with conversation id 2
    expect(wrapper.emitted('select-chat')).toBeTruthy()
    const emitted = wrapper.emitted('select-chat') as any[]
    expect(emitted[0]).toEqual(['2'])
  })

  /*  it('opens and closes ProductDisplayModal when openProductModal is called', async () => {
    // For testing the modal, ensure your component exposes openProductModal via defineExpose({ openProductModal })
    const wrapper = mount(MessagesSidebar, {
      props: {
        conversations,
        activeConversationId,
        receiverUsernames
      },
      global: {
        stubs: {
          ProductDisplayModal: {
            name: 'ProductDisplayModal',
            props: ['productId'],
            template: '<div class="modal-stub">Product ID: {{ productId }}</div>',
          },
          RouterLink: true,
        },
      },
    })
    await flushPromises()

    // Initially, the modal should not be rendered
    expect(wrapper.findComponent({ name: 'ProductDisplayModal' }).exists()).toBe(false)

    // Ensure openProductModal is exposed; if not, throw an error to remind you to add defineExpose({ openProductModal })
    if (typeof wrapper.vm.openProductModal !== 'function') {
      throw new Error('openProductModal is not exposed. Please add defineExpose({ openProductModal }) in your component.')
    }

    // Call openProductModal with a sample product id
    wrapper.vm.openProductModal('999')
    await flushPromises()

    // Now the modal should be rendered with the correct productId
    const modal = wrapper.findComponent({ name: 'ProductDisplayModal' })
    expect(modal.exists()).toBe(true)
    expect(modal.props('productId')).toBe('999')

    // Simulate closing the modal by emitting "close"
    modal.vm.$emit('close')
    await flushPromises()

    expect(wrapper.findComponent({ name: 'ProductDisplayModal' }).exists()).toBe(false)
    })*/
})
