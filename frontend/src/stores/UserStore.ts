import { defineStore } from 'pinia'
import axios from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    message: '',
  }),
  actions: {
    async register(username: string, email: string, password: string) {
      try {
        const response = await axios.post(
          '/api/users/register',
          { username, email, password },
          { withCredentials: true }
        )
        this.message = response.data
        return response.data
      } catch (error) {
        throw error
      }
    },
  },
})
