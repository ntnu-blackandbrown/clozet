import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from '../api/axios'

interface UserInfo {
  id: number
  username: string
  role: string
  email?: string
  firstName?: string
  lastName?: string
}

export const useAuthStore = defineStore('auth', () => {
  const isLoggedIn = ref(false)
  const userId = ref<number | null>(null)
  const username = ref<string | null>(null)
  const role = ref<string | null>(null)
  const userDetails = ref<UserInfo | null>(null)

  async function login(username: string, password: string) {
    try {
      await axios.post('/api/auth/login', { username, password })
      await fetchUserInfo()
      return { success: true }
    } catch (error) {
      console.error('Login failed:', error)
      return { success: false, error }
    }
  }

  async function logout() {
    try {
      await axios.post('/api/auth/logout')
      resetState()
      return { success: true }
    } catch (error) {
      console.error('Logout failed:', error)
      return { success: false, error }
    }
  }

  async function fetchUserInfo() {
    try {
      const response = await axios.get('/api/me')
      isLoggedIn.value = true
      userId.value = response.data.id
      username.value = response.data.username
      role.value = response.data.role
      userDetails.value = response.data
      return { success: true, user: response.data }
    } catch (error) {
      resetState()
      return { success: false, error }
    }
  }

  // Legg til dette etter login-funksjonen
  async function register(userData: any) {
    try {
      // Bruk samme axios-instans som med login
      await axios.post('/api/users/register', userData)
      // Logg inn automatisk etter vellykket registrering
      const loginResult = await login(userData.username, userData.password)
      return loginResult
    } catch (error) {
      console.error('Registration failed:', error)
      return { success: false, error }
    }
  }

  // Husk å inkludere register i return-objektet
  return {
    isLoggedIn,
    userId,
    username,
    role,
    userDetails,
    login,
    logout,
    register, // Legg til denne linjen
    fetchUserInfo,
    resetState,
  }

  function resetState() {
    isLoggedIn.value = false
    userId.value = null
    username.value = null
    role.value = null
    userDetails.value = null
  }

  return {
    isLoggedIn,
    userId,
    username,
    role,
    userDetails,
    login,
    logout,
    fetchUserInfo,
    resetState,
  }
})
