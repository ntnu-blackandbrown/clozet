<script setup lang="ts">
import { ref, computed } from 'vue'
import { useField, useForm } from 'vee-validate'
import * as yup from 'yup'

const emit = defineEmits(['close'])

const isLogin = ref(true)

const formTitle = computed(() => isLogin.value ? 'Login' : 'Register')
const toggleText = computed(() => isLogin.value ? 'Need an account? Register' : 'Already have an account? Login')

// Login schema
const loginSchema = yup.object({
  identificator: yup.string().required('Email or username is required'),
  password: yup.string().required('Password is required')
})

// Register schema
const registerSchema = yup.object({
  userName: yup.string().required('Username is required'),
  firstName: yup.string().required('First name is required'),
  lastName: yup.string().required('Last name is required'),
  email: yup.string().email('Invalid email').required('Email is required'),
  phoneNumber: yup.string().matches(/^\+?[\d\s-]+$/, 'Invalid phone number'),
  password: yup.string()
    .required('Password is required')
    .min(8, 'Password must be at least 8 characters')
    .matches(/[A-Z]/, 'Password must contain at least one uppercase letter')
    .matches(/[a-z]/, 'Password must contain at least one lowercase letter')
    .matches(/[0-9]/, 'Password must contain at least one number')
})

// Use computed to switch between schemas
const currentSchema = computed(() => isLogin.value ? loginSchema : registerSchema)

const { handleSubmit, errors } = useForm({
  validationSchema: currentSchema
})

const { value: identificator, errorMessage: identificatorError } = useField('identificator')
const { value: password, errorMessage: passwordError } = useField('password')

const { value: userName, errorMessage: userNameError } = useField('userName')
const { value: firstName, errorMessage: firstNameError } = useField('firstName')
const { value: lastName, errorMessage: lastNameError } = useField('lastName')
const { value: email, errorMessage: emailError } = useField('email')
const { value: phoneNumber, errorMessage: phoneNumberError } = useField('phoneNumber')

const toggleForm = () => {
  isLogin.value = !isLogin.value
}

const submit = handleSubmit(async (values) => {
  console.log(values)
  try{
    if (isLogin.value){
    console.log('Login')
    //Call the login API
    //push to logged in home page
  } else {
    console.log('Register')
    //Call the register API
    //push to logged in home page
  }
  emit('close') // close the modal after submit
  } catch (error) {
    console.error('Error while submitting form:', error)
  }
})

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
        <input v-if="isLogin" type="text" v-model="identificator" placeholder="Email or Username" />
        <template v-if="!isLogin">
          <input v-model="userName" type="text" placeholder="Username" />
          <span class="error" id="userNameErrSpan">{{ userNameError }}</span>
          <input v-model="firstName" type="text" placeholder="First Name" />
          <span class="error" id="firstNameErrSpan">{{ firstNameError }}</span>
          <input v-model="lastName" type="text" placeholder="Last Name" />
          <span class="error" id="lastNameErrSpan">{{ lastNameError }}</span>
          <input v-model="email" type="email" placeholder="Email" />
          <span class="error" id="emailErrSpan">{{ emailError }}</span>
          <input v-model="phoneNumber" type="tel" placeholder="Phone Number" />
          <span class="error" id="phoneNumberErrSpan">{{ phoneNumberError }}</span>
        </template>


        <input v-model="password" type="password" placeholder="Password" />
        <span class="error" id="passwordErrSpan">{{ passwordError }}</span>
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

.error {
  display: block;
  color: #e74c3c;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}
</style>
