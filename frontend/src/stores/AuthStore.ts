import { defineStore } from 'pinia'
import type { AxiosError } from 'axios'
import { computed, ref } from 'vue'
import { AuthService } from '@/api/services/AuthService'

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

// Add interface for error response
interface ErrorResponse {
  message: string;
  [key: string]: any;
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const loading = ref(false)
  const isLoggedIn = computed(() => !!user.value)
  const userDetails = computed(() => user.value)

  const login = async (usernameOrEmail: string, password: string) => {
    try {
      console.log('🔐 Login attempt:', usernameOrEmail)
      loading.value = true
      const response = await AuthService.login(usernameOrEmail, password)
      console.log('✅ Login successful')

      // On successful login, fetch user details
      await fetchUserInfo()

      return { success: true, message: 'Login successful', data: response.data }
    } catch (error: unknown) {
      console.error('❌ Login error:', error)
      const axiosError = error as AxiosError
      return {
        success: false,
        message: (axiosError.response?.data as ErrorResponse)?.message || 'Invalid credentials',
        error: axiosError
      }
    } finally {
      loading.value = false
    }
  }

  const fetchUserInfo = async () => {
    try {
      console.log('👤 Fetching user info')
      loading.value = true
      const response = await AuthService.getCurrentUser()
      user.value = response.data
      console.log('👤 User info fetched successfully:', user.value)
      return response.data
    } catch (error) {
      const axiosError = error as AxiosError
      if (axiosError.response && axiosError.response.status === 401) {
        console.log('⚠️ Unauthorized when fetching user info, clearing user state')
        user.value = null
      } else {
        console.error('❌ Error fetching user info:', error)
        user.value = null
      }
      return null
    } finally {
      loading.value = false
    }
  }

  const logout = async () => {
    try {
      console.log('🚪 Logging out')
      loading.value = true
      await AuthService.logout()
      user.value = null
      console.log('🚪 Logout successful')
      return { success: true, message: 'Logout successful' }
    } catch (error) {
      console.error('❌ Logout error:', error)
      // Don't clear user state on logout failure
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
      console.log('📝 Registering new user:', username)
      loading.value = true
      const response = await AuthService.register(username, password, email, firstName, lastName)
      console.log('✅ Registration successful')
      return { success: true, message: 'Registration successful', data: response.data }
    } catch (error) {
      console.error('❌ Registration error:', error)
      const axiosError = error as AxiosError
      return {
        success: false,
        message: (axiosError.response?.data as ErrorResponse)?.message || 'Registration failed',
        error: axiosError
      }
    } finally {
      loading.value = false
    }
  }

  const checkAuth = async () => {
    // Check if user is already logged in by fetching current user
    try {
      console.log('🔒 Checking authentication status')
      if (!user.value) {
        await fetchUserInfo()
      }
      const authenticated = !!user.value
      console.log(`🔒 Authentication status: ${authenticated ? 'Authenticated' : 'Not authenticated'}`)
      return authenticated
    } catch (error) {
      console.log('🔒 Authentication check failed, user not authenticated')
      return false
    }
  }

  const silentRefresh = async () => {
    // For use on page refresh - tries to refresh token without showing errors
    try {
      console.log('🔄 Silent refresh attempt')

      // First try to fetch user info with current token
      const userResult = await fetchUserInfo().catch(() => null)
      if (userResult) {
        console.log('✅ Silent refresh successful - valid token found')
        return true
      }

      // If that fails, try to refresh the token
      console.log('⏳ Trying token refresh during silent refresh')
      await AuthService.refreshToken()

      // Try to fetch user info again
      await fetchUserInfo()
      return !!user.value
    } catch (error) {
      console.log('ℹ️ Silent refresh failed - user not authenticated')
      user.value = null
      return false

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
    loading,
    login,
    fetchUserInfo,
    logout,
    register,
    checkAuth,
    silentRefresh
    deleteUser,
  }
})
