import { defineStore } from 'pinia'

interface RegisterResponse {
  success: boolean
  message: string
}

export const useUserStore = defineStore('user', {
  state: () => ({
    message: '',
  }),
  actions: {
    async register(username: string, email: string, password: string): Promise<RegisterResponse> {
      try {
        const response = await apiClient.post(
          '/api/auth/register',
          { username, email, password }
        )
        this.message = response.data
        return { success: true, message: response.data }
      } catch (error: any) {
        console.error('Registration error:', error)
        const errorMessage = error.response?.data || 'Registration failed'
        this.message = errorMessage
        return { success: false, message: errorMessage }
      }
    },
  },
})
