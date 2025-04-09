import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from '@/api/axios'
import UserService from '@/api/services/UserService'

// Mock the axios module using Vitest's jest-compatible mocking
vi.mock('../axios', () => ({
  default: {
    get: vi.fn(),
    put: vi.fn(),
  },
}))

describe('UserService', () => {
  beforeEach(() => {
    // Clear all mocks before each test
    vi.clearAllMocks()
  })

  describe('getAllUsers', () => {
    it('should call axios.get with the correct URL and return the expected response', async () => {
      const expectedResponse = {
        data: [
          { id: 1, name: 'User1' },
          { id: 2, name: 'User2' },
        ],
      }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await UserService.getAllUsers()

      expect(axios.get).toHaveBeenCalledWith('/api/users')
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.get', async () => {
      const error = new Error('Error fetching users')
      ;(axios.get as any).mockRejectedValue(error)

      await expect(UserService.getAllUsers()).rejects.toThrow('Error fetching users')
      expect(axios.get).toHaveBeenCalledWith('/api/users')
    })
  })

  describe('getUserById', () => {
    it('should call axios.get with the correct URL and return a response', async () => {
      const userId = 42
      const expectedResponse = { data: { id: 42, name: 'User42' } }
      ;(axios.get as any).mockResolvedValue(expectedResponse)

      const result = await UserService.getUserById(userId)

      expect(axios.get).toHaveBeenCalledWith(`/api/users/${userId}`)
      expect(result).toEqual(expectedResponse)
    })

    it('should propagate errors from axios.get when fetching a user by ID', async () => {
      const userId = 42
      const error = new Error('User not found')
      ;(axios.get as any).mockRejectedValue(error)

      await expect(UserService.getUserById(userId)).rejects.toThrow('User not found')
      expect(axios.get).toHaveBeenCalledWith(`/api/users/${userId}`)
    })
  })

  describe('updateUser', () => {
    it("should call axios.put with the correct URL and payload to update a user's information", async () => {
      const userId = 10
      const userData = { name: 'Updated Name', email: 'updated@example.com' }
      const expectedResponse = { data: { id: 10, ...userData } }
      ;(axios.put as any).mockResolvedValue(expectedResponse)

      const result = await UserService.updateUser(userId, userData)

      expect(axios.put).toHaveBeenCalledWith(`/api/users/${userId}`, userData)
      expect(result).toEqual(expectedResponse)
    })

    it("should propagate errors from axios.put when updating a user's information", async () => {
      const userId = 10
      const userData = { name: 'Updated Name', email: 'updated@example.com' }
      const error = new Error('Update failed')
      ;(axios.put as any).mockRejectedValue(error)

      await expect(UserService.updateUser(userId, userData)).rejects.toThrow('Update failed')
      expect(axios.put).toHaveBeenCalledWith(`/api/users/${userId}`, userData)
    })
  })

  describe('updateUserRole', () => {
    it("should call axios.put with the correct URL and payload to update a user's role", async () => {
      const userId = 20
      const role = 'admin'
      const expectedResponse = { data: { id: 20, role } }
      ;(axios.put as any).mockResolvedValue(expectedResponse)

      const result = await UserService.updateUserRole(userId, role)

      expect(axios.put).toHaveBeenCalledWith(`/api/users/${userId}`, { role })
      expect(result).toEqual(expectedResponse)
    })

    it("should propagate errors from axios.put when updating a user's role", async () => {
      const userId = 20
      const role = 'admin'
      const error = new Error('Role update failed')
      ;(axios.put as any).mockRejectedValue(error)

      await expect(UserService.updateUserRole(userId, role)).rejects.toThrow('Role update failed')
      expect(axios.put).toHaveBeenCalledWith(`/api/users/${userId}`, { role })
    })
  })

  describe('toggleUserStatus', () => {
    it("should call axios.put with the correct URL and payload to toggle a user's active status", async () => {
      const userId = 30
      const isActive = false
      const expectedResponse = { data: { id: 30, active: isActive } }
      ;(axios.put as any).mockResolvedValue(expectedResponse)

      const result = await UserService.toggleUserStatus(userId, isActive)

      expect(axios.put).toHaveBeenCalledWith(`/api/users/${userId}`, { active: isActive })
      expect(result).toEqual(expectedResponse)
    })

    it("should propagate errors from axios.put when toggling a user's active status", async () => {
      const userId = 30
      const isActive = true
      const error = new Error('Status update failed')
      ;(axios.put as any).mockRejectedValue(error)

      await expect(UserService.toggleUserStatus(userId, isActive)).rejects.toThrow(
        'Status update failed',
      )
      expect(axios.put).toHaveBeenCalledWith(`/api/users/${userId}`, { active: isActive })
    })
  })
})
