import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from '@/api/axios'
import FavoritesService from '@/api/services/FavoritesService'

// Mock the axios module using Vitest’s jest-compatible mocking
vi.mock('../axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    delete: vi.fn(),
  },
}))

describe('FavoritesService', () => {
  beforeEach(() => {
    // Reset all mocks before each test to avoid state sharing between tests
    vi.clearAllMocks()
  })

  describe('getUserFavorites', () => {
    it('should call axios.get with the correct URL and return a response', async () => {
      const userId = 123
      const expectedUrl = `api/favorites/user/${userId}`
      const expectedResponse = { data: ['favorite1', 'favorite2'] }

      // Set up the mock to resolve to the expected response
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await FavoritesService.getUserFavorites(userId)

      // Verify axios.get was called correctly and the function returns the expected value
      expect(axios.get).toHaveBeenCalledWith(expectedUrl)
      expect(result).toBe(expectedResponse)
    })

    it('should propagate errors from axios.get', async () => {
      const userId = 123
      const expectedUrl = `api/favorites/user/${userId}`
      const error = new Error('Network Error')

      ;(axios.get as any).mockRejectedValue(error)

      await expect(FavoritesService.getUserFavorites(userId)).rejects.toThrow('Network Error')
      expect(axios.get).toHaveBeenCalledWith(expectedUrl)
    })
  })

  describe('addFavorite', () => {
    it('should call axios.post with the correct URL and payload and return a response', async () => {
      const userId = 10
      const itemId = 20
      const expectedUrl = 'api/favorites'
      const expectedPayload = { userId, itemId }
      const expectedResponse = { data: { id: 1, userId, itemId } }

      ;(axios.post as any).mockResolvedValue(expectedResponse)

      const result = await FavoritesService.addFavorite(userId, itemId)

      // Check the correct URL and payload were used in the post call
      expect(axios.post).toHaveBeenCalledWith(expectedUrl, expectedPayload)
      expect(result).toBe(expectedResponse)
    })

    it('should propagate errors from axios.post', async () => {
      const userId = 10
      const itemId = 20
      const error = new Error('Post failed')

      ;(axios.post as any).mockRejectedValue(error)

      await expect(FavoritesService.addFavorite(userId, itemId)).rejects.toThrow('Post failed')
      expect(axios.post).toHaveBeenCalledWith('api/favorites', { userId, itemId })
    })
  })

  describe('removeFavorite', () => {
    it('should call axios.delete with the correct URL and return a response', async () => {
      const favoriteId = 5
      const expectedUrl = `api/favorites/${favoriteId}`
      const expectedResponse = { data: { success: true } }

      ;(axios.delete as any).mockResolvedValue(expectedResponse)

      const result = await FavoritesService.removeFavorite(favoriteId)

      // Verify axios.delete was called correctly with the expected URL
      expect(axios.delete).toHaveBeenCalledWith(expectedUrl)
      expect(result).toBe(expectedResponse)
    })

    it('should propagate errors from axios.delete', async () => {
      const favoriteId = 5
      const expectedUrl = `api/favorites/${favoriteId}`
      const error = new Error('Delete failed')

      ;(axios.delete as any).mockRejectedValue(error)

      await expect(FavoritesService.removeFavorite(favoriteId)).rejects.toThrow('Delete failed')
      expect(axios.delete).toHaveBeenCalledWith(expectedUrl)
    })
  })
})
