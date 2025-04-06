export interface Message {
  id: number
  content: string
  createdAt: string
  senderId: number
  receiverId: number
  conversationId: number
}

export interface Conversation {
  id: string
  conversationId?: string
  senderId: number
  receiverId: number
  receiverName: string
  itemId: number
  listOfMessages: Message[]
  latestMessageTimestamp: string
  lastMessage?: string
}
