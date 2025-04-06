import { setActivePinia, createPinia } from 'pinia'
import { useAuthStore } from '@/stores/AuthStore'
import axios from '@/api/axios'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import type { Mock } from 'vitest'

vi.mock('@/api/axios')

// Add proper typing for mocked axios
const mockedAxios = axios as unknown as {
  get: Mock
  post: Mock
}

describe('AuthStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.resetAllMocks()
  })

  it('logs in successfully and fetches user info', async () => {
    const mockUser = {
      id: 1,
      usernameOrEmail: 'gizmo',
      email: 'gizmo@example.com',
      active: true,
      role: 'USER',
    }

    mockedAxios.post.mockResolvedValueOnce({}) // /login
    mockedAxios.get.mockResolvedValueOnce({ data: mockUser }) // /me

    const store = useAuthStore()
    const result = await store.login('gizmo', 'pass1234')

    expect(result.success).toBe(true)
    expect(store.user).toEqual(mockUser)
    expect(store.isLoggedIn).toBe(true)
  })

  it('returns error on failed login', async () => {
    mockedAxios.post.mockRejectedValueOnce({ response: { data: 'Invalid credentials' } })

    const store = useAuthStore()
    const result = await store.login('fail', 'wrong')

    expect(result.success).toBe(false)
    expect(result.message).toBe('Invalid credentials')
    expect(store.user).toBe(null)
  })

  it('fetches user info and sets user', async () => {
    const mockUser = { id: 2, username: 'user2', email: 'u2@mail.com', active: true, role: 'ADMIN' }
    mockedAxios.get.mockResolvedValueOnce({ data: mockUser })

    const store = useAuthStore()
    await store.fetchUserInfo()

    expect(store.user).toEqual(mockUser)
  })

  it('sets user to null on failed fetchUserInfo', async () => {
    mockedAxios.get.mockRejectedValueOnce(new Error('Fetch error'))

    const store = useAuthStore()
    await store.fetchUserInfo()

    expect(store.user).toBe(null)
  })

  it('logs out and clears user + token', async () => {
    mockedAxios.post.mockResolvedValueOnce({}) // /logout

    const store = useAuthStore()
    store.user = { id: 99, usernameOrEmail: 'logoutUser', email: '', active: true, role: 'USER' }
    const result = await store.logout()

    expect(result.success).toBe(true)
    expect(store.user).toBe(null)
  })

  it('handles logout failure gracefully', async () => {
    mockedAxios.post.mockRejectedValueOnce(new Error('Logout failed'))

    const store = useAuthStore()
    store.user = { id: 99, usernameOrEmail: 'logoutUser', email: '', active: true, role: 'USER' }
    const result = await store.logout()

    expect(result.success).toBe(false)
    expect(result.message).toBe('Logout failed')
    expect(store.user).toEqual({
      id: 99,
      usernameOrEmail: 'logoutUser',
      email: '',
      active: true,
      role: 'USER',
    })
  })

  it('registers a new user successfully', async () => {
    mockedAxios.post.mockResolvedValueOnce({})

    const store = useAuthStore()
    const result = await store.register('new', 'StrongPass123', 'n@mail.com', 'N', 'User')

    expect(result.success).toBe(true)
  })

  it('fails registration on error', async () => {
    mockedAxios.post.mockRejectedValueOnce(new Error('Register error'))

    const store = useAuthStore()
    const result = await store.register('fail', 'fail', 'fail', 'fail', 'fail')

    expect(result.success).toBe(false)
  })

  it('isLoggedIn returns false when no user', () => {
    const store = useAuthStore()
    expect(store.isLoggedIn).toBe(false)
  })

  it('userDetails reflects user state', async () => {
    const store = useAuthStore()
    store.user = { id: 1, usernameOrEmail: 'test', email: 'test@test.com', active: true, role: 'USER' }
    expect(store.userDetails).toEqual(store.user)
  })
})
