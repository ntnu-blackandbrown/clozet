import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from '@/api/axios'
import MessagingService from '@/api/services/MessagingService'

// Mock the axios module using Vitest’s jest-compatible mock
vi.mock('../axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}))

describe('MessagingService', () => {
  beforeEach(() => {
    // Clear mock calls between tests
    vi.clearAllMocks()
  })

  describe('getUserConversations', () => {
    it('should call axios.get with the correct URL and parameters and return a response', async () => {
      const userId = 1
      const expectedResponse = { data: ['conversation1', 'conversation2'] }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await MessagingService.getUserConversations(userId)

      expect(axios.get).toHaveBeenCalledWith('/api/conversations', { params: { userId } })
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.get', async () => {
      const userId = 1
      const error = new Error('Network error')
      ;(axios.get as any).mockRejectedValue(error)

      await expect(MessagingService.getUserConversations(userId)).rejects.toThrow('Network error')
      expect(axios.get).toHaveBeenCalledWith('/api/conversations', { params: { userId } })
    })
  })

  describe('getConversationMessages', () => {
    it('should call axios.get with the correct URL and default parameters and return a response', async () => {
      const conversationId = 'conv1'
      const expectedResponse = { data: ['message1', 'message2'] }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await MessagingService.getConversationMessages(conversationId)

      // Default page is 0 and default size is 20
      expect(axios.get).toHaveBeenCalledWith('/api/messages', {
        params: { conversationId, page: 0, size: 20 },
      })
      expect(result).toEqual(expectedResponse)
    })

    it('should call axios.get with custom page and size parameters and return a response', async () => {
      const conversationId = 'conv2'
      const page = 2
      const size = 50
      const expectedResponse = { data: ['messageA', 'messageB'] }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await MessagingService.getConversationMessages(conversationId, page, size)

      expect(axios.get).toHaveBeenCalledWith('/api/messages', {
        params: { conversationId, page, size },
      })
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.get', async () => {
      const conversationId = 'conv1'
      const error = new Error('Fetch error')
      ;(axios.get as any).mockRejectedValue(error)

      await expect(MessagingService.getConversationMessages(conversationId)).rejects.toThrow(
        'Fetch error',
      )
      expect(axios.get).toHaveBeenCalledWith('/api/messages', {
        params: { conversationId, page: 0, size: 20 },
      })
    })
  })

  describe('sendMessage', () => {
    it('should call axios.post with the correct URL and message data and return a response', async () => {
      const messageData = { text: 'Hello, world!' }
      const expectedResponse = { data: { id: 'msg1', ...messageData } }
      ;(axios.post as any).mockResolvedValue(expectedResponse)

      const result = await MessagingService.sendMessage(messageData)

      expect(axios.post).toHaveBeenCalledWith('/api/messages', messageData)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.post', async () => {
      const messageData = { text: 'Hello, world!' }
      const error = new Error('Send failed')
      ;(axios.post as any).mockRejectedValue(error)

      await expect(MessagingService.sendMessage(messageData)).rejects.toThrow('Send failed')
      expect(axios.post).toHaveBeenCalledWith('/api/messages', messageData)
    })
  })

  describe('createConversation', () => {
    it('should call axios.post with the correct URL and conversation data and return a response', async () => {
      const conversationData = { title: 'New Conversation' }
      const expectedResponse = { data: { id: 'conv1', ...conversationData } }
      ;(axios.post as any).mockResolvedValue(expectedResponse)

      const result = await MessagingService.createConversation(conversationData)

      expect(axios.post).toHaveBeenCalledWith('/api/conversations', conversationData)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.post', async () => {
      const conversationData = { title: 'New Conversation' }
      const error = new Error('Create failed')
      ;(axios.post as any).mockRejectedValue(error)

      await expect(MessagingService.createConversation(conversationData)).rejects.toThrow(
        'Create failed',
      )
      expect(axios.post).toHaveBeenCalledWith('/api/conversations', conversationData)
    })
  })
})
