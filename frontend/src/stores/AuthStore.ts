import { defineStore } from 'pinia'
import axios from 'axios' // Import standard axios
import type { AxiosError } from 'axios'
import { computed, ref } from 'vue'

interface User {
  id: number
  username: string
  email: string
  firstName?: string
  lastName?: string
  active: boolean
  role: string
  phoneNumber?: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)

  const loading = ref(false)
  const error = ref<string | null>(null)

  const isLoggedIn = computed(() => !!user.value)
  const userDetails = computed(() => user.value)

  const login = async (username: string, password: string) => {
    try {
      loading.value = true

      await axios.post('/api/auth/login', { username, password })

      await fetchUserInfo()
      return { success: true, message: 'Login successful' }
    } catch (error: unknown) {
      console.error('Login error:', error)
      const axiosError = error as AxiosError
      return { success: false, message: axiosError.response?.data || 'Login failed' }
    } finally {
      loading.value = false
    }
  }

  const fetchUserInfo = async () => {
    try {
      loading.value = true
      const response = await axios.get('/api/me')
      user.value = response.data
      return response.data
    } catch (error) {
      console.error('Error fetching user info:', error)
      user.value = null
      return null
    } finally {
      loading.value = false
    }
  }

  const logout = async () => {
    try {
      await axios.post('/api/auth/logout', {})
      user.value = null
      token.value = null
      return { success: true, message: 'Logout successful' }
    } catch (error) {
      console.error('Logout error:', error)
      return { success: false, message: 'Logout failed' }
    } finally {
      loading.value = false
    }
  }

  const register = async (
    username: string,
    password: string,
    email: string,
    firstName: string,
    lastName: string,
  ) => {
    try {
      loading.value = true
      await axios.post('/api/auth/register', { username, password, email, firstName, lastName })
      return { success: true, message: 'Registration successful' }
    } catch (error) {
      console.error('Registration error:', error)
      return { success: false, message: 'Registration failed' }
    } finally {
      loading.value = false
    }
  }
})
