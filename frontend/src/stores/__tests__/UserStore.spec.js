import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../UserStore'
import axios from 'axios'

vi.mock('axios')

describe('UserStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    localStorage.clear()
    vi.clearAllMocks()
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('initializes with empty state when no local storage', () => {
    const store = useUserStore()
    expect(store.savedName).toBe('')
    expect(store.savedIdentificator).toBe('')
    expect(store.savedToken).toBe('')
  })

  it('initializes with values from localStorage', () => {
    localStorage.setItem('user_identificator', 'test-id')
    localStorage.setItem('user_token', 'test-token')

    const store = useUserStore()
    expect(store.savedIdentificator).toBe('test-id')
    expect(store.savedToken).toBe('test-token')
  })

  it('successfully registers a new user', async () => {
    const store = useUserStore()
    const mockResponse = {
      data: {
        id: 1,
        username: 'testuser',
        token: 'test-token',
        identificator: 'test-id'
      }
    }
    axios.post.mockResolvedValueOnce(mockResponse)

    await store.handleRegister(
      'testuser',
      'test@example.com',
      'password123',
      'Test',
      'User'
    )

    expect(axios.post).toHaveBeenCalledWith(
      'http://localhost:8080/api/users',
      {
        email: 'test@example.com',
        username: 'testuser',
        password: 'password123',
        firstName: 'Test',
        lastName: 'User',
        role: 'user'
      }
    )
    expect(axios.post).toHaveBeenCalledTimes(1)
  })

  it('handles registration error', async () => {
    const store = useUserStore()
    const mockError = new Error('Registration failed')
    axios.post.mockRejectedValueOnce(mockError)

    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

    await store.handleRegister(
      'testuser',
      'test@example.com',
      'password123',
      'Test',
      'User'
    )

    expect(consoleSpy).toHaveBeenCalledWith(
      'Error while registering user:',
      mockError
    )
    expect(axios.post).toHaveBeenCalledTimes(1)

    consoleSpy.mockRestore()
  })
})
