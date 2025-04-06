import { defineStore } from 'pinia'
import axios from '@/api/axios' // axios
import type { AxiosError } from 'axios'
import { computed, ref } from 'vue'

interface User {
  id: number
  usernameOrEmail: string
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

  const login = async (usernameOrEmail: string, password: string) => {
    try {
      loading.value = true

      await axios.post('/api/auth/login', { usernameOrEmail, password })

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
    usernameOrEmail: string,
    password: string,
    email: string,
    firstName: string,
    lastName: string,
  ) => {
    console.log("In request register now, sending data to backend")
    try {
      loading.value = true
      await axios.post('/api/auth/register', { usernameOrEmail, password, email, firstName, lastName })
      return { success: true, message: 'Registration successful' }
      console.log("Registration successful")
    } catch (error) {
      console.error('Registration error:', error)
      return { success: false, message: 'Registration failed' }
      console.log("Registration failed")
    } finally {
      loading.value = false
    }
  }

  return {
    user,
    isLoggedIn,
    userDetails,
    login,
    fetchUserInfo,
    logout,
    register,
  }
})
