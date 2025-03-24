<script setup lang="ts">
import { ref, computed } from 'vue'

const emit = defineEmits(['close'])

const isLogin = ref(true)
const identificator = ref('')
const password = ref('')
const firstName = ref('')
const lastName = ref('')
const email = ref('')
const phoneNumber = ref('')
const userName = ref('')
const formTitle = computed(() => isLogin.value ? 'Login' : 'Register')
const toggleText = computed(() => isLogin.value ? 'Need an account? Register' : 'Already have an account? Login')

const toggleForm = () => {
  isLogin.value = !isLogin.value
}

const submit = () => {
  if (isLogin.value){
    console.log('Login')
    //Call the login API
  } else {
    console.log('Register')
    //Call the register API
  }
  emit('close') // close the modal after submit
}

const close = () => {
  emit('close') //close when clicking outside the modal
}
</script>

<template>
  <div class = "backdrop" @click.self="close">
    <div class="container">
      <h2>{{ formTitle }}</h2>

      <!-- FORM CONTENT-->
       <form @submit.prevent='submit'>
        <input v-if="isLogin" v-model="identificator" placeholder="Email or Username" />
        <input v-if="!isLogin" v-model="userName" placeholder="Username" />
        <input v-if="!isLogin" v-model="firstName" placeholder="First Name" />
        <input v-if="!isLogin" v-model="lastName" placeholder="Last Name" />
        <input v-if="!isLogin" v-model="email" placeholder="Email" />
        <input v-if="!isLogin" v-model="phoneNumber" placeholder="Phone Number" />

        <input v-model="password" placeholder="Password" />
        <button type="submit">{{ isLogin ? 'Login' : 'Register' }}</button>
       </form>

       <!-- FORM SWITCH -->
        <p>
          <button class="toggle-form" @click="toggleForm">{{ toggleText }}</button>
        </p>
    </div>
  </div>
</template>

<style scoped>
.backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.6); /* dark overlay */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}

.container {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
}

input {
  width: 100%;
  margin: 0.5rem 0;
  padding: 0.5rem;
  font-size: 1rem;
}

button {
  padding: 0.6rem 1.2rem;
  font-size: 1rem;
  margin-top: 0.5rem;
}

.toggle-form {
  background: none;
  border: none;
  color: blue;
  text-decoration: underline;
  cursor: pointer;
  font-size: 0.8rem;
  display: block;
  margin: 0 auto;
}
</style>
