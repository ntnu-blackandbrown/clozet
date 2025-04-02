import { defineStore } from 'pinia'


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
    async login(username: string, password: string): Promise<LoginResponse> {
      try {
        await apiClient.post('/api/auth/login',
          { username, password }
        )

        // Fetch user data after successful login
        await this.fetchUserInfo()
        return { success: true }
      } catch (error: any) {
        console.error('Login error:', error)
        return {
          success: false,
          message: error.response?.data || 'Login failed'
        }
      }
    },

    async fetchUserInfo(): Promise<User | null> {
      try {
        const response = await apiClient.get('/api/me')
        this.user = response.data
        return response.data
      } catch (error) {
        console.error('Error fetching user info:', error)
        this.user = null
        return null
      }
    },

    async logout(): Promise<LoginResponse> {
      try {
        await apiClient.post('/api/auth/logout', {})
        this.user = null
        this.token = null
        return { success: true }
      } catch (error: any) {
        console.error('Logout error:', error)
        return {
          success: false,
          message: error.response?.data || 'Logout failed'
        }
      }
    },
  },
})
