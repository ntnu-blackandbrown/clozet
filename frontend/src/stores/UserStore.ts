import { defineStore } from 'pinia'
import axios from 'axios' // Import standard axios
import type { AxiosError } from 'axios'
export const useUserStore = defineStore('user', {
  state: () => ({
    message: '',
  }),
  actions: {
    async register(username: string, email: string, password: string): Promise<{
      success: boolean;
      message: unknown
    }> {
      try {
        const response = await axios.post(
          '/api/auth/register',
          { username, email, password }
        )
        this.message = response.data
        return { success: true, message: response.data }
      } catch (error: unknown) {
        console.error('Registration error:', error)
        const axiosError = error as AxiosError
        const errorMessage = axiosError.response?.data || 'Registration failed'
        this.message = errorMessage
        return { success: false, message: errorMessage }
      }
    },
  },
})
