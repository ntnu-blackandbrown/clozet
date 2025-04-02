import { defineStore } from 'pinia'
import axios from 'axios' // Import standard axios
import type { AxiosError } from 'axios'

interface User {
  id: number
  username: string
  email: string
  firstName?: string
  lastName?: string
  active: boolean
  role: string
}

interface LoginResponse {
  success: boolean
  message?: string
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as User | null,
    token: null as string | null,
  }),
  getters: {
    isLoggedIn: (state) => !!state.user,
    userDetails: (state) => state.user,
  },
  actions: {
    async login(username: string, password: string): Promise<{ success: boolean; message: unknown }> {
      try {
        await axios.post('/api/auth/login', { username, password })

        // Fetch user data after successful login
        await this.fetchUserInfo()
        return {message: undefined, success: true }
      } catch (error: unknown) {
        console.error('Login error:', error)
        const axiosError = error as AxiosError
        return {
          success: false,
          message: axiosError.response?.data || 'Login failed'
        }
      }
    },

    async fetchUserInfo(): Promise<User | null> {
      try {
        const response = await axios.get('/api/me')
        this.user = response.data
        return response.data
      } catch (error) {
        console.error('Error fetching user info:', error)
        this.user = null
        return null
      }
    },

    async logout(): Promise<{ success: boolean; message: unknown }> {
      try {
        await axios.post('/api/auth/logout', {})
        this.user = null
        this.token = null
        return {message: undefined, success: true }
      } catch (error: unknown) {
        console.error('Logout error:', error)
        const axiosError = error as AxiosError
        return {
          success: false,
          message: axiosError.response?.data || 'Logout failed'
        }
      }
    },
  },
})
