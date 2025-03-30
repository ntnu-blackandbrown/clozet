import { defineStore } from 'pinia'
import axios from 'axios'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as any, // Du kan definere en passende type for brukeren
    token: null as string | null,
  }),
  actions: {
    async login(username: string, password: string) {
      try {
        const response = await axios.post('/api/auth/login', { username, password }, {
          withCredentials: true
        })
        // Sett token og brukerdata hvis du får dette fra login
        this.token = response.data.token
        // Alternativt kan du kalle fetchCurrentUser for å få hele brukerobjektet
        await this.fetchCurrentUser()
        return { success: true }
      } catch (error) {
        throw error
      }
    },
    async fetchCurrentUser() {
      try {
        // Kall /api/me for å hente innlogget brukerdata
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
    }
  },
})
