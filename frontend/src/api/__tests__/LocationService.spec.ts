import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from '@/api/axios'
import LocationService from '@/api/services/LocationService'

// Mock the axios module using Vitest’s jest-compatible mocking
vi.mock('../axios', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

describe('LocationService', () => {
  beforeEach(() => {
    // Reset mocks between tests
    vi.clearAllMocks()
  })

  describe('getAllLocations', () => {
    it('should call axios.get with the correct URL and return a response', async () => {
      const expectedResponse = { data: ['Location1', 'Location2'] }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await LocationService.getAllLocations()

      expect(axios.get).toHaveBeenCalledWith('/api/locations')
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.get', async () => {
      const error = new Error('Error retrieving locations')
      ;(axios.get as any).mockRejectedValue(error)

      await expect(LocationService.getAllLocations()).rejects.toThrow('Error retrieving locations')
      expect(axios.get).toHaveBeenCalledWith('/api/locations')
    })
  })

  describe('getLocationById', () => {
    const locationId = 42
    it('should call axios.get with the correct URL and return a response', async () => {
      const expectedResponse = { data: { id: locationId, name: 'Test Location' } }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await LocationService.getLocationById(locationId)

      expect(axios.get).toHaveBeenCalledWith(`/api/locations/${locationId}`)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.get', async () => {
      const error = new Error('Location not found')
      ;(axios.get as any).mockRejectedValue(error)

      await expect(LocationService.getLocationById(locationId)).rejects.toThrow(
        'Location not found',
      )
      expect(axios.get).toHaveBeenCalledWith(`/api/locations/${locationId}`)
    })
  })

  describe('createLocation', () => {
    const locationData = { name: 'New Location', address: '123 Main St' }
    it('should call axios.post with the correct URL and payload and return a response', async () => {
      const expectedResponse = { data: { id: 1, ...locationData } }
      ;(axios.post as any).mockResolvedValue(expectedResponse)

      const result = await LocationService.createLocation(locationData)

      expect(axios.post).toHaveBeenCalledWith('/api/locations', locationData)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.post', async () => {
      const error = new Error('Create failed')
      ;(axios.post as any).mockRejectedValue(error)

      await expect(LocationService.createLocation(locationData)).rejects.toThrow('Create failed')
      expect(axios.post).toHaveBeenCalledWith('/api/locations', locationData)
    })
  })

  describe('updateLocation', () => {
    const locationId = 5
    const locationData = { name: 'Updated Location', address: '456 Side St' }
    it('should call axios.put with the correct URL and payload and return a response', async () => {
      const expectedResponse = { data: { id: locationId, ...locationData } }
      ;(axios.put as any).mockResolvedValue(expectedResponse)

      const result = await LocationService.updateLocation(locationId, locationData)

      expect(axios.put).toHaveBeenCalledWith(`/api/locations/${locationId}`, locationData)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.put', async () => {
      const error = new Error('Update failed')
      ;(axios.put as any).mockRejectedValue(error)

      await expect(LocationService.updateLocation(locationId, locationData)).rejects.toThrow(
        'Update failed',
      )
      expect(axios.put).toHaveBeenCalledWith(`/api/locations/${locationId}`, locationData)
    })
  })

  describe('deleteLocation', () => {
    const locationId = 10
    it('should call axios.delete with the correct URL and return a response', async () => {
      const expectedResponse = { data: { success: true } }
      ;(axios.delete as any).mockResolvedValue(expectedResponse)

      const result = await LocationService.deleteLocation(locationId)

      expect(axios.delete).toHaveBeenCalledWith(`/api/locations/${locationId}`)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.delete', async () => {
      const error = new Error('Delete failed')
      ;(axios.delete as any).mockRejectedValue(error)

      await expect(LocationService.deleteLocation(locationId)).rejects.toThrow('Delete failed')
      expect(axios.delete).toHaveBeenCalledWith(`/api/locations/${locationId}`)
    })
  })
})
