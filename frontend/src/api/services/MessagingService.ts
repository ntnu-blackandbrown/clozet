import axios from '../axios'

export const MessagingService = {
  /**
   * Get all conversations for a user
   */
  getUserConversations: (userId: number) => {
    return axios.get('/api/conversations', {
      params: {
        userId,
      },
    })
  },

  /**
   * Get messages for a specific conversation
   */
  getConversationMessages: (conversationId: string, page = 0, size = 20) => {
    return axios.get('/api/messages', {
      params: {
        conversationId,
        page,
        size,
      },
    })
  },

  /**
   * Send a new message
   */
  sendMessage: (messageData: any) => {
    return axios.post('/api/messages', messageData)
  },

  /**
   * Create a new conversation
   */
  createConversation: (conversationData: any) => {
    return axios.post('/api/conversations', conversationData)
  },

  /**
   * Archive a conversation for a user
   */
  archiveConversation: (conversationId: string, userId: number) => {
    return axios.post(`/api/conversations/${conversationId}/archive`, null, {
      params: {
        userId,
      },
    })
  },
}

export default MessagingService
