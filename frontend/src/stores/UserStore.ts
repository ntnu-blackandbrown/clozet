import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

interface User {
  username: string
  email: string
  firstName: string
  lastName: string
  [key: string]: any // For eventuelle ekstra felter
}

export const useUserStore = defineStore('user', () => {
  const currentUser = ref<User | null>(null)
  const isLoggedIn = ref(false)
  const savedIdentificator = ref(localStorage.getItem('user_identificator') || '')
  const savedToken = ref(localStorage.getItem('user_token') || '')
  const lastRegisteredUser = ref<User | null>(null)

  async function handleRegister(
    username: string,
    email: string,
    password: string,
    firstName: string,
    lastName: string,
  ) {
    try {
      const response = await axios.post('http://localhost:8080/api/users', {
        email: email,
        username: username,
        password: password,
        firstName: firstName,
        lastName: lastName,
        role: 'USER',
      })
      console.log(response)

      // Lagre informasjon om nylig registrert bruker
      if (response.data) {
        lastRegisteredUser.value = {
          username: response.data.username,
          email: response.data.email,
          firstName: response.data.firstName,
          lastName: response.data.lastName
        }
        return { success: true, user: response.data }
      }

      return { success: true, user: response.data }
    } catch (error) {
      console.error('Error while registering user:', error)
      return { success: false, error: error }
    }
  }

  async function handleLogin(identificator: string, password: string) {
    try {
      // Dette er bare en stub-implementasjon inntil backend har login-endepunkt
      // Senere vil dette endres til å kalle det faktiske login-endepunktet
      console.log('Login attempt with:', { identificator, password })

      // Simulerer vellykket innlogging for testing
      currentUser.value = {
        username: identificator,
        email: identificator.includes('@') ? identificator : `${identificator}@example.com`,
        firstName: 'Test',
        lastName: 'User'
      }

      isLoggedIn.value = true
      savedIdentificator.value = identificator
      localStorage.setItem('user_identificator', identificator)

      return { success: true, user: currentUser.value }
    } catch (error) {
      console.error('Error while logging in:', error)
      return { success: false, error: error }
    }
  }

  function logout() {
    currentUser.value = null
    isLoggedIn.value = false
    savedIdentificator.value = ''
    savedToken.value = ''
    localStorage.removeItem('user_identificator')
    localStorage.removeItem('user_token')
  }

  return {
    currentUser,
    isLoggedIn,
    savedIdentificator,
    savedToken,
    lastRegisteredUser,
    handleRegister,
    handleLogin,
    logout
  }
})
