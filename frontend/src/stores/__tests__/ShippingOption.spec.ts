import { setActivePinia, createPinia } from 'pinia'
import { describe, it, beforeEach, expect, vi } from 'vitest'
import { useShippingOptionStore } from '@/stores/ShippingOption'
import { ShippingService } from '@/api/services/ShippingService'

// Mock the ShippingService so we can control its responses.
vi.mock('@/api/services/ShippingService', () => ({
  ShippingService: {
    getAllShippingOptions: vi.fn(),
  },
}))

describe('useShippingOptionStore', () => {
  let store: ReturnType<typeof useShippingOptionStore>

  beforeEach(() => {
    // Create a fresh Pinia instance before each test.
    setActivePinia(createPinia())
    store = useShippingOptionStore()
    vi.clearAllMocks()
  })

  describe('fetchShippingOptions', () => {
    it('should fetch and set shipping options on success', async () => {
      // Arrange: set up a fake API response.
      const mockShippingOptions = [
        { id: 1, name: 'Location A' },
        { id: 2, name: 'Location B' },
      ]
      ;(ShippingService.getAllShippingOptions as any).mockResolvedValue({
        data: mockShippingOptions,
      })

      // Act: call the store action.
      await store.fetchShippingOptions()

      // Assert: ensure the service was called, and the state is updated accordingly.
      expect(ShippingService.getAllShippingOptions).toHaveBeenCalled()
      expect(store.shippingOptions).toEqual(mockShippingOptions)
      expect(store.error).toBeNull()
      expect(store.loading).toBe(false)
    })

    it('should set error message if fetching locations fails', async () => {
      // Arrange: set up the API to reject.
      const error = new Error('Network error')
      ;(ShippingService.getAllShippingOptions as any).mockRejectedValue(error)

      // Act: call the store action.
      await store.fetchShippingOptions()

      // Assert: check the error and state after the call.
      expect(ShippingService.getAllShippingOptions).toHaveBeenCalled()
      expect(store.shippingOptions).toEqual([])
      expect(store.error).toBe('Failed to fetch shipping options')
      expect(store.loading).toBe(false)
    })
  })
})
