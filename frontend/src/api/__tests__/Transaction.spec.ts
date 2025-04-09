import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from '@/api/axios'
import TransactionService from '@/api/services/TransactionService'

// Mock axios using Vitest’s jest-compatible mocks
vi.mock('../axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}))

describe('TransactionService', () => {
  beforeEach(() => {
    // Clear mocks between tests
    vi.clearAllMocks()
  })

  describe('getAllTransactions', () => {
    it('should call axios.get with the correct URL and return a response', async () => {
      const expectedResponse = { data: ['trans1', 'trans2'] }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await TransactionService.getAllTransactions()

      expect(axios.get).toHaveBeenCalledWith('/api/transactions')
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.get', async () => {
      const error = new Error('Error retrieving transactions')
      ;(axios.get as any).mockRejectedValue(error)

      await expect(TransactionService.getAllTransactions()).rejects.toThrow('Error retrieving transactions')
      expect(axios.get).toHaveBeenCalledWith('/api/transactions')
    })
  })

  describe('getBuyerTransactions', () => {
    const buyerId = 123

    it('should call axios.get with the correct URL and return a response', async () => {
      const expectedResponse = { data: ['buyerTrans1', 'buyerTrans2'] }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await TransactionService.getBuyerTransactions(buyerId)

      expect(axios.get).toHaveBeenCalledWith(`/api/transactions/buyer/${buyerId}`)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.get for buyer transactions', async () => {
      const error = new Error('Buyer transactions error')
      ;(axios.get as any).mockRejectedValue(error)

      await expect(TransactionService.getBuyerTransactions(buyerId)).rejects.toThrow('Buyer transactions error')
      expect(axios.get).toHaveBeenCalledWith(`/api/transactions/buyer/${buyerId}`)
    })
  })

  describe('getSellerTransactions', () => {
    const sellerId = 456

    it('should call axios.get with the correct URL and return a response', async () => {
      const expectedResponse = { data: ['sellerTrans1', 'sellerTrans2'] }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await TransactionService.getSellerTransactions(sellerId)

      expect(axios.get).toHaveBeenCalledWith(`/api/transactions/seller/${sellerId}`)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.get for seller transactions', async () => {
      const error = new Error('Seller transactions error')
      ;(axios.get as any).mockRejectedValue(error)

      await expect(TransactionService.getSellerTransactions(sellerId)).rejects.toThrow('Seller transactions error')
      expect(axios.get).toHaveBeenCalledWith(`/api/transactions/seller/${sellerId}`)
    })
  })

  describe('createPurchase', () => {
    it('should call axios.post with the correct URL and payload and return a response', async () => {
      const transactionData = { buyerId: 123, sellerId: 456, amount: 99.99 }
      const expectedResponse = { data: { id: 1, ...transactionData } }
      ;(axios.post as any).mockResolvedValue(expectedResponse)

      const result = await TransactionService.createPurchase(transactionData)

      expect(axios.post).toHaveBeenCalledWith('/api/transactions/purchase', transactionData)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.post when creating a purchase', async () => {
      const transactionData = { buyerId: 123, sellerId: 456, amount: 99.99 }
      const error = new Error('Create purchase failed')
      ;(axios.post as any).mockRejectedValue(error)

      await expect(TransactionService.createPurchase(transactionData)).rejects.toThrow('Create purchase failed')
      expect(axios.post).toHaveBeenCalledWith('/api/transactions/purchase', transactionData)
    })
  })
})
