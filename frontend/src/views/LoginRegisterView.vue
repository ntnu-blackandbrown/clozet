<template>
  <div class="login-register-container">
    <div>
      <button @click="showLogin = true">Login</button>
      <button @click="showLogin = false">Register</button>
    </div>
    <div v-if="showLogin">
      <h2>Login</h2>
      <form @submit.prevent="login">
        <input type="text" v-model="loginUsername" placeholder="Username" required />
        <input type="password" v-model="loginPassword" placeholder="Password" required />
        <button type="submit">Login</button>
      </form>
      <div v-if="loginError" class="error">{{ loginError }}</div>
    </div>
    <div v-else>
      <h2>Register</h2>
      <form @submit.prevent="register">
        <input type="text" v-model="registerUsername" placeholder="Username" required />
        <input type="email" v-model="registerEmail" placeholder="Email" required />
        <input type="password" v-model="registerPassword" placeholder="Password" required />
        <button type="submit">Register</button>
      </form>
      <div v-if="registerMessage">{{ registerMessage }}</div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import { useAuthStore } from '@/stores/AuthStore'
import { useUserStore } from '@/stores/UserStore'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'LoginRegisterView',
  setup() {
    const showLogin = ref(true)
    const loginUsername = ref('')
    const loginPassword = ref('')
    const registerUsername = ref('')
    const registerEmail = ref('')
    const registerPassword = ref('')
    const registerMessage = ref('')
    const loginError = ref('')

    const authStore = useAuthStore()
    const userStore = useUserStore()
    const router = useRouter()

    const login = async () => {
      try {
        await authStore.login(loginUsername.value, loginPassword.value)
        router.push('/dashboard')
      } catch (error: any) {
        loginError.value = error.response?.data?.message || 'Login failed'
      }
    }

    const register = async () => {
      try {
        await userStore.register(registerUsername.value, registerEmail.value, registerPassword.value)
        // Etter registrering omdirigeres brukeren til en side som instruerer dem om å sjekke e-posten
        router.push('/verify-info')
      } catch (error: any) {
        registerMessage.value = error.response?.data?.message || 'Registration failed'
      }
    }

    return {
      showLogin,
      loginUsername,
      loginPassword,
      registerUsername,
      registerEmail,
      registerPassword,
      registerMessage,
      loginError,
      login,
      register,
    }
  },
})
</script>

<style scoped>
.login-register-container {
  max-width: 400px;
  margin: 0 auto;
  text-align: center;
}
.error {
  color: red;
}
</style>
