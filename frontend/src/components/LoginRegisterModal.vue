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
    .matches(/[0-9]/, 'Password must contain at least one number'),
  confirmPassword: yup.string()
    .required('Please confirm your password')
    .oneOf([yup.ref('password')], 'Passwords must match')
})

// Use computed to switch between schemas
const currentSchema = computed(() => isLogin.value ? loginSchema : registerSchema)

const { handleSubmit, errors, meta } = useForm({
  validationSchema: currentSchema
})

// Add computed property for form validity
const isFormValid = computed(() => {
  if (isLogin.value) {
    return !errors.value.identificator && !errors.value.password &&
           identificator.value && password.value
  } else {
    return !errors.value.userName && !errors.value.firstName && !errors.value.lastName &&
           !errors.value.email && !errors.value.password && !errors.value.confirmPassword &&
           userName.value && firstName.value && lastName.value && email.value &&
           password.value && confirmPassword.value
  }
})

const { value: identificator, errorMessage: identificatorError } = useField('identificator')
const { value: password, errorMessage: passwordError } = useField('password')
const { value: confirmPassword, errorMessage: confirmPasswordError } = useField('confirmPassword')

const { value: userName, errorMessage: userNameError } = useField('userName')
const { value: firstName, errorMessage: firstNameError } = useField('firstName')
const { value: lastName, errorMessage: lastNameError } = useField('lastName')
const { value: email, errorMessage: emailError } = useField('email')

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
        <span class="error" id="identificatorErrSpan">{{ identificatorError }}</span>
        <template v-if="!isLogin">
          <input v-model="userName" type="text" placeholder="Username" />
          <span class="error" id="userNameErrSpan">{{ userNameError }}</span>
          <input v-model="firstName" type="text" placeholder="First Name" />
          <span class="error" id="firstNameErrSpan">{{ firstNameError }}</span>
          <input v-model="lastName" type="text" placeholder="Last Name" />
          <span class="error" id="lastNameErrSpan">{{ lastNameError }}</span>
          <input v-model="email" type="email" placeholder="Email" />
          <span class="error" id="emailErrSpan">{{ emailError }}</span>
        </template>


        <input v-model="password" type="password" placeholder="Password" />
        <span class="error" id="passwordErrSpan">{{ passwordError }}</span>
        <input v-if="!isLogin" v-model="confirmPassword" type="password" placeholder="Confirm Password" />
        <span v-if="!isLogin" class="error" id="confirmPasswordErrSpan">{{ confirmPasswordError }}</span>
        <button type="submit" :disabled="!isFormValid">{{ isLogin ? 'Login' : 'Register' }}</button>
       </form>

       <!-- FORM SWITCH -->
        <p>
          <button id="toggle-form-btn" data-testid="toggle-form-btn" class="toggle-form" @click="toggleForm">{{ toggleText }}</button>
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
  background-color: rgba(0, 0, 0, 0.2);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
  padding: 2rem;
  overflow-y: auto;
}

.container {
  background: white;
  padding: 3rem;
  border-radius: 16px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  margin: auto;
  max-height: 90vh;
  overflow-y: auto;
}

.container::-webkit-scrollbar {
  width: 8px;
}

.container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.container h2 {
  color: #333;
  font-size: 1.8rem;
  margin-bottom: 2rem;
  font-weight: 500;
  text-align: center;
}

input {
  width: 100%;
  margin: 0.75rem 0;
  padding: 1rem 1.25rem;
  font-size: 0.95rem;
  border: 1px solid #e1e1e1;
  border-radius: 8px;
  background-color: #f8f9fa;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: #c4c4c4;
  background-color: white;
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.05);
}

button[type="submit"] {
  width: 100%;
  padding: 1rem;
  font-size: 1rem;
  margin-top: 1.5rem;
  background-color: #333;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

button[type="submit"]:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

button[type="submit"]:hover:not(:disabled) {
  background-color: #444;
}

.toggle-form {
  background: none;
  border: none;
  color: #666;
  text-decoration: none;
  cursor: pointer;
  font-size: 0.9rem;
  display: block;
  margin: 1.5rem auto 0;
  transition: color 0.3s ease;
}

.toggle-form:hover {
  color: #333;
}

.error {
  display: block;
  color: #e74c3c;
  font-size: 0.8rem;
  margin: 0.25rem 0 0.5rem;
  padding-left: 0.5rem;
}

@media (max-width: 480px) {
  .container {
    padding: 2rem;
  }
}
</style>
