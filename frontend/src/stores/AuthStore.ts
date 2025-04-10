import { defineStore } from 'pinia'
import type { AxiosError } from 'axios'
import { computed, ref } from 'vue'
import { AuthService } from '@/api/services/AuthService'

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

  const loading = ref(false)

  const isLoggedIn = computed(() => !!user.value)
  const userDetails = computed(() => user.value)

  const login = async (usernameOrEmail: string, password: string) => {
    try {
      loading.value = true

      await AuthService.login(usernameOrEmail, password)

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

      const response = await AuthService.getCurrentUser()

      user.value = response.data
      return response.data
    } catch (error) {
      const axiosError = error as AxiosError
      if (axiosError.response && axiosError.response.status === 401) {
        user.value = null
      } else {
        console.error('Error fetching user info:', error)
        user.value = null
      }
      return null
    } finally {
      loading.value = false
    }
  }

  const logout = async () => {
    try {
      await AuthService.logout()
      user.value = null
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
    try {
      loading.value = true
      await AuthService.register(usernameOrEmail, password, email, firstName, lastName)
      return { success: true, message: 'Registration successful' }
    } catch (error) {
      console.error('Registration error:', error)
      return { success: false, message: 'Registration failed' }
    } finally {
      loading.value = false
    }
  }

  const deleteUser = async () => {
    try {
      console.log('Deleting user:', user.value?.id)
      await AuthService.deleteUser(user.value?.id?.toString() ?? '')
    } catch (error) {
      console.error('Delete user error:', error)
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
    deleteUser,
  }
}, {
  persist: {
    paths: ['user'],
  },
} as any)
