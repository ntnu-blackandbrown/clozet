import { defineStore } from 'pinia';
import axios from 'axios';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as any,
    token: null as string | null,
  }),
  actions: {
    async login(username: string, password: string) {
      try {
        const response = await axios.post('/api/users/login', { username, password });
        this.user = response.data;
        this.token = response.data.token;
        return response.data;
      } catch (error) {
        throw error;
      }
    },
    logout() {
      this.user = null;
      this.token = null;
    },
  },
});
