import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as any,
    token: null as string | null,
  }),
  actions: {
    async login(username: string, password: string) {
      try {
        await axios.post('/api/auth/login', { username, password }, { withCredentials: true })
        // Etter login henter vi den innloggede brukerens data
        await this.fetchCurrentUser()
        return { success: true }
      } catch (error) {
        throw error
      }
    },
    async fetchCurrentUser() {
      try {
        const response = await axios.get('/api/me', { withCredentials: true })
        this.user = response.data
        return response.data
      } catch (error) {
        this.user = null
        throw error
      }
    },
    logout() {
      this.user = null
      this.token = null
    },
  },
})
