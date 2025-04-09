import { setActivePinia, createPinia } from 'pinia'
import { describe, it, beforeEach, expect, vi } from 'vitest'
import { useLocationStore } from '@/stores/Location'
import { LocationService } from '@/api/services/LocationService'

// Mock the LocationService so we can control its responses.
vi.mock('@/api/services/LocationService', () => ({
  LocationService: {
    getAllLocations: vi.fn(),
  },
}))

describe('useLocationStore', () => {
  let store: ReturnType<typeof useLocationStore>

  beforeEach(() => {
    // Create a fresh Pinia instance before each test.
    setActivePinia(createPinia())
    store = useLocationStore()
    vi.clearAllMocks()
  })

  describe('fetchLocations', () => {
    it('should fetch and set locations on success', async () => {
      // Arrange: set up a fake API response.
      const mockLocations = [
        { id: 1, name: 'Location A' },
        { id: 2, name: 'Location B' },
      ]
      ;(LocationService.getAllLocations as any).mockResolvedValue({ data: mockLocations })

      // Act: call the store action.
      await store.fetchLocations()

      // Assert: ensure the service was called, and the state is updated accordingly.
      expect(LocationService.getAllLocations).toHaveBeenCalled()
      expect(store.locations).toEqual(mockLocations)
      expect(store.error).toBeNull()
      expect(store.loading).toBe(false)
    })

    it('should set error message if fetching locations fails', async () => {
      // Arrange: set up the API to reject.
      const error = new Error('Network error')
      ;(LocationService.getAllLocations as any).mockRejectedValue(error)

      // Act: call the store action.
      await store.fetchLocations()

      // Assert: check the error and state after the call.
      expect(LocationService.getAllLocations).toHaveBeenCalled()
      expect(store.locations).toEqual([])
      expect(store.error).toBe('Failed to fetch locations')
      expect(store.loading).toBe(false)
    })
  })
})
