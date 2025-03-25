import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

export const useUserStore = defineStore('user', () => {
  const savedName = ref('')
  const savedIdentificator = ref(localStorage.getItem('user_identificator') || '')
  const savedToken = ref(localStorage.getItem('user_token') || '')

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
        role: "user"
      });
      console.log(response)
    } catch (error) {
      console.error('Error while registering user:', error)
    }
  }

  return {
    savedName,
    savedIdentificator,
    savedToken,
    handleRegister,
  }
})
