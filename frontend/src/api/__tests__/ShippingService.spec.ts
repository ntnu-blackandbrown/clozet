import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from '@/api/axios'
import ShippingService from '@/api/services/ShippingService'

// Mock the axios module using Vitest's jest-compatible mocking
vi.mock('../axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

describe('ShippingService', () => {
  beforeEach(() => {
    // Clear all mock calls between tests
    vi.clearAllMocks()
  })

  describe('getAllShippingOptions', () => {
    it('should call axios.get with the correct endpoint and return a response', async () => {
      const expectedResponse = { data: ['option1', 'option2'] }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await ShippingService.getAllShippingOptions()

      expect(axios.get).toHaveBeenCalledWith('/api/shipping-options')
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.get', async () => {
      const error = new Error('Error retrieving shipping options')
      ;(axios.get as any).mockRejectedValue(error)

      await expect(ShippingService.getAllShippingOptions()).rejects.toThrow('Error retrieving shipping options')
      expect(axios.get).toHaveBeenCalledWith('/api/shipping-options')
    })
  })

  describe('getShippingOptionById', () => {
    it('should call axios.get with the correct endpoint and return a response', async () => {
      const shippingId = 1
      const expectedResponse = { data: { id: shippingId, name: 'Option A' } }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await ShippingService.getShippingOptionById(shippingId)

      expect(axios.get).toHaveBeenCalledWith(`/api/shipping-options/${shippingId}`)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.get', async () => {
      const shippingId = 1
      const error = new Error('Shipping option not found')
      ;(axios.get as any).mockRejectedValue(error)

      await expect(ShippingService.getShippingOptionById(shippingId)).rejects.toThrow('Shipping option not found')
      expect(axios.get).toHaveBeenCalledWith(`/api/shipping-options/${shippingId}`)
    })
  })

  describe('createShippingOption', () => {
    it('should call axios.post with the correct endpoint and payload and return a response', async () => {
      const shippingData = { name: 'New Option', cost: 9.99 }
      const expectedResponse = { data: { id: 1, ...shippingData } }
      ;(axios.post as any).mockResolvedValue(expectedResponse)

      const result = await ShippingService.createShippingOption(shippingData)

      expect(axios.post).toHaveBeenCalledWith('/api/shipping-options', shippingData)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.post', async () => {
      const shippingData = { name: 'New Option', cost: 9.99 }
      const error = new Error('Creation failed')
      ;(axios.post as any).mockRejectedValue(error)

      await expect(ShippingService.createShippingOption(shippingData)).rejects.toThrow('Creation failed')
      expect(axios.post).toHaveBeenCalledWith('/api/shipping-options', shippingData)
    })
  })

  describe('updateShippingOption', () => {
    it('should call axios.put with the correct endpoint and payload and return a response', async () => {
      const shippingId = 2
      const shippingData = { name: 'Updated Option', cost: 12.99 }
      const expectedResponse = { data: { id: shippingId, ...shippingData } }
      ;(axios.put as any).mockResolvedValue(expectedResponse)

      const result = await ShippingService.updateShippingOption(shippingId, shippingData)

      expect(axios.put).toHaveBeenCalledWith(`/api/shipping-options/${shippingId}`, shippingData)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.put', async () => {
      const shippingId = 2
      const shippingData = { name: 'Updated Option', cost: 12.99 }
      const error = new Error('Update failed')
      ;(axios.put as any).mockRejectedValue(error)

      await expect(ShippingService.updateShippingOption(shippingId, shippingData)).rejects.toThrow('Update failed')
      expect(axios.put).toHaveBeenCalledWith(`/api/shipping-options/${shippingId}`, shippingData)
    })
  })

  describe('deleteShippingOption', () => {
    it('should call axios.delete with the correct endpoint and return a response', async () => {
      const shippingId = 3
      const expectedResponse = { data: { success: true } }
      ;(axios.delete as any).mockResolvedValue(expectedResponse)

      const result = await ShippingService.deleteShippingOption(shippingId)

      expect(axios.delete).toHaveBeenCalledWith(`/api/shipping-options/${shippingId}`)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.delete', async () => {
      const shippingId = 3
      const error = new Error('Deletion failed')
      ;(axios.delete as any).mockRejectedValue(error)

      await expect(ShippingService.deleteShippingOption(shippingId)).rejects.toThrow('Deletion failed')
      expect(axios.delete).toHaveBeenCalledWith(`/api/shipping-options/${shippingId}`)
    })
  })
})
