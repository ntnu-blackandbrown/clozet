import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '../UserStore'
import axios from 'axios'
import { beforeEach, describe, it, expect, vi, type Mock } from 'vitest'

vi.mock('axios')
const mockedAxios = axios as unknown as { post: Mock }

describe('UserStore', () => {
  beforeEach(() => {
    // Reset Pinia and clear localStorage before each test.
    setActivePinia(createPinia())
    localStorage.clear()
  })

  it('should initialize with correct state from localStorage', () => {
    localStorage.setItem('user_identificator', 'testId')
    localStorage.setItem('user_token', 'testToken')
    const store = useUserStore()
    expect(store.savedName).toBe('')
    expect(store.savedIdentificator).toBe('testId')
    expect(store.savedToken).toBe('testToken')
  })

  it('should call axios.post and log response on successful registration', async () => {
    const store = useUserStore()
    const responseData = { data: { id: 1 } }
    mockedAxios.post.mockResolvedValue(responseData)
    const consoleLogSpy = vi.spyOn(console, 'log').mockImplementation(() => {})

    await store.handleRegister('user', 'user@example.com', 'Password1', 'First', 'Last')

    expect(mockedAxios.post).toHaveBeenCalledWith('http://localhost:8080/api/users', {
      email: 'user@example.com',
      username: 'user',
      password: 'Password1',
      firstName: 'First',
      lastName: 'Last',
      role: 'user',
    })
    expect(consoleLogSpy).toHaveBeenCalledWith(responseData)
    consoleLogSpy.mockRestore()
  })

  it('should handle errors when registration fails', async () => {
    const store = useUserStore()
    const error = new Error('Registration failed')
    mockedAxios.post.mockRejectedValue(error)
    const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})

    await store.handleRegister('user', 'user@example.com', 'Password1', 'First', 'Last')

    expect(consoleErrorSpy).toHaveBeenCalledWith('Error while registering user:', error)
    consoleErrorSpy.mockRestore()
  })
})
