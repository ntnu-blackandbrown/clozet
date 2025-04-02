import { defineStore } from 'pinia'
import axios from 'axios'
import type { AxiosError } from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    message: '',
    user: null as never | null,
  }),
  actions: {
    async register(username: string, email: string, password: string): Promise<{
      success: boolean;
      message: unknown
    }> {
      try {
        await axios.post('/api/auth/register', {
          username,
          email,
          password
        });
        this.message = 'Registration successful!'
        return { success: true, message: 'Registration successful!' }
      } catch (error: unknown) {
        console.error('Registration error:', error)
        const axiosError = error as AxiosError

        // Fix for TypeScript error: ensure errorMessage is always a string
        let errorMessage: string

        if (axiosError.response?.data) {
          if (typeof axiosError.response.data === 'object') {
            // Convert object to string
            errorMessage = JSON.stringify(axiosError.response.data)
          } else {
            // Use as string
            errorMessage = String(axiosError.response.data)
          }
        } else {
          errorMessage = 'Registration failed. Please try again.'
        }

        this.message = errorMessage
        return { success: false, message: errorMessage }
      }
    },

    getUserDetails() {
      return this.user
    }
  },
})
