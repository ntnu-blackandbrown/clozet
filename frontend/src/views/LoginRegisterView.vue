<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useField, useForm } from 'vee-validate'
import * as yup from 'yup'
import { useAuthStore } from '@/stores/AuthStore'
import BaseModal from '@/components/modals/BaseModal.vue'
import { useRouter } from 'vue-router'
import axios from '@/api/axios'

const router = useRouter()
const authStore = useAuthStore()
const emit = defineEmits(['close'])
const props = defineProps({
  customTitle: {
    type: String,
    default: '',
  },
  initialMode: {
    type: String,
    default: 'login',
  },
})

const isLogin = ref(props.initialMode === 'login')
const isSubmitting = ref(false)
const statusMessage = ref('')
const statusType = ref('')
const debugInfo = ref('')

// Set initial mode based on prop
onMounted(() => {
  isLogin.value = props.initialMode === 'login'
})

const formTitle = computed(() => {
  if (props.customTitle) {
    return isLogin.value ? props.customTitle : props.customTitle.replace('login', 'register')
  }
  return isLogin.value ? 'Login' : 'Register'
})

const toggleText = computed(() =>
  isLogin.value ? 'Need an account? Register' : 'Already have an account? Login',
)

// Login schema
const loginSchema = yup.object({
  username: yup.string().required('Username is required'),
  password: yup.string().required('Password is required'),
})

// Register schema
const registerSchema = yup.object({
  username: yup.string().required('Username is required'),
  firstName: yup.string().required('First name is required'),
  lastName: yup.string().required('Last name is required'),
  email: yup.string().email('Invalid email').required('Email is required'),
  password: yup
    .string()
    .required('Password is required')
    .min(8, 'Password must be at least 8 characters')
    .matches(/[A-Z]/, 'Password must contain at least one uppercase letter')
    .matches(/[a-z]/, 'Password must contain at least one lowercase letter')
    .matches(/[0-9]/, 'Password must contain at least one number'),
  confirmPassword: yup
    .string()
    .required('Please confirm your password')
    .oneOf([yup.ref('password')], 'Passwords must match'),
})

const currentSchema = computed(() => (isLogin.value ? loginSchema : registerSchema))

const { handleSubmit, errors, resetForm } = useForm({
  validationSchema: currentSchema,
})

// Define form fields with validation
const { value: username, errorMessage: usernameError } = useField('username')
const { value: password, errorMessage: passwordError } = useField('password')
const { value: confirmPassword, errorMessage: confirmPasswordError } = useField('confirmPassword')
const { value: firstName, errorMessage: firstNameError } = useField('firstName')
const { value: lastName, errorMessage: lastNameError } = useField('lastName')
const { value: email, errorMessage: emailError } = useField('email')

const isFormValid = computed(() => {
  if (isLogin.value) {
    return !errors.value.username && !errors.value.password && username.value && password.value
  } else {
    return (
      !errors.value.username &&
      !errors.value.firstName &&
      !errors.value.lastName &&
      !errors.value.email &&
      !errors.value.password &&
      !errors.value.confirmPassword &&
      username.value &&
      firstName.value &&
      lastName.value &&
      email.value &&
      password.value &&
      confirmPassword.value
    )
  }
})

const toggleForm = () => {
  isLogin.value = !isLogin.value
  statusMessage.value = ''
  statusType.value = ''
  debugInfo.value = ''
  resetForm()

  // Update the URL based on the current mode
  if (isLogin.value) {
    router.replace('/login')
  } else {
    router.replace('/register')
  }
}

const submit = handleSubmit(async (values) => {
  isSubmitting.value = true
  statusMessage.value = isLogin.value ? 'Logger inn...' : 'Registrerer bruker...'
  statusType.value = 'info'
  debugInfo.value = ''

  try {
    if (isLogin.value) {
      // Login using AuthStore (JWT cookie approach)
      debugInfo.value = `POST til /api/auth/login med ${JSON.stringify({ username: values.username, password: values.password })}`
      const result = await authStore.login(values.username, values.password)

      if (result.success) {
        statusMessage.value = `Innlogging vellykket!`
        statusType.value = 'success'
        setTimeout(() => {
          emit('close')
        }, 1500)
      } else {
        statusMessage.value = 'Innlogging feilet. Kontroller brukernavn og passord.'
        statusType.value = 'error'
      }
    } else {
      // Direct registration with correct endpoint
      console.log("Registering now")
      const userData = {
        username: values.username,
        email: values.email,
        password: values.password,
        firstName: values.firstName,
        lastName: values.lastName,
        role: 'USER',
      }
      console.log("userData", userData)
      console.log("Sending request now")

      debugInfo.value = `POST til /api/auth/register med ${JSON.stringify(userData)}`

      const response = await authStore.register(userData.username, userData.password, userData.email, userData.firstName, userData.lastName)

      if (response.success) {
        statusMessage.value = `Registeration sucessful! Please check your email for verification`
      }
    }
  } catch (error: any) {
    console.error('Error:', error)
    statusMessage.value = isLogin.value
      ? 'Innlogging feilet på grunn av teknisk feil.'
      : 'Registrering feilet på grunn av teknisk feil.'
    statusType.value = 'error'
    debugInfo.value = `Feil: ${error.message}, Status: ${error.response?.status}, Data: ${JSON.stringify(error.response?.data)}`
  } finally {
    isSubmitting.value = false
  }
})
</script>

<template>
  <BaseModal @close="emit('close')" maxWidth="400px" padding="3rem" hideCloseButton>
    <h2>{{ formTitle }}</h2>

    <!-- FORM CONTENT-->
    <form @submit.prevent="submit">
      <input v-if="isLogin" type="text" v-model="username" placeholder="Username" />
      <span class="error" id="usernameErrSpan" v-if="isLogin">{{ usernameError }}</span>

      <template v-if="!isLogin">
        <input v-model="username" type="text" placeholder="Username" />
        <span class="error" id="usernameErrSpan">{{ usernameError }}</span>
        <input v-model="firstName" type="text" placeholder="First Name" />
        <span class="error" id="firstNameErrSpan">{{ firstNameError }}</span>
        <input v-model="lastName" type="text" placeholder="Last Name" />
        <span class="error" id="lastNameErrSpan">{{ lastNameError }}</span>
        <input v-model="email" type="email" placeholder="Email" />
        <span class="error" id="emailErrSpan">{{ emailError }}</span>
      </template>

      <input v-model="password" type="password" placeholder="Password" />
      <span class="error" id="passwordErrSpan">{{ passwordError }}</span>
      <input
        v-if="!isLogin"
        v-model="confirmPassword"
        type="password"
        placeholder="Confirm Password"
      />
      <span v-if="!isLogin" class="error" id="confirmPasswordErrSpan">{{
        confirmPasswordError
      }}</span>

      <!-- Status Message -->
      <div v-if="statusMessage" class="status-message" :class="statusType">
        {{ statusMessage }}
      </div>

      <!-- Debug Info -->
      <div v-if="debugInfo" class="debug-info">
        {{ debugInfo }}
      </div>

      <button type="submit" :disabled="!isFormValid || isSubmitting">
        <span v-if="isSubmitting">
          <span class="spinner"></span>
        </span>
        <span v-else>{{ isLogin ? 'Login' : 'Register' }}</span>
      </button>
    </form>

    <!-- FORM SWITCH -->
    <p>
      <button
        id="toggle-form-btn"
        data-testid="toggle-form-btn"
        class="toggle-form"
        @click="toggleForm"
        :disabled="isSubmitting"
      >
        {{ toggleText }}
      </button>
    </p>
  </BaseModal>
</template>

<style scoped>
h2 {
  color: #333;
  font-size: 1.8rem;
  margin-bottom: 1.5rem;
  font-weight: 600;
  text-align: center;
  letter-spacing: -0.02em;
}

form {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

input {
  width: 100%;
  padding: 0.75rem 1rem;
  font-size: 0.95rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  transition: all 0.2s ease;
  color: #333;
}

input:focus {
  outline: none;
  border-color: #4CAF50;
  box-shadow: 0 0 0 3px rgba(150, 187, 124, 0.2);
}

input::placeholder {
  color: #9ca3af;
}

.error {
  color: #ef4444;
  font-size: 0.875rem;
  margin-top: -0.25rem;
  margin-bottom: 0.25rem;
}

button[type='submit'] {
  width: 100%;
  padding: 0.75rem;
  margin-top: 1rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: 500;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
}

button[type='submit']:hover:not(:disabled) {
  background-color: #45a049;
  transform: translateY(-2px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

button[type='submit']:disabled {
  background-color: #d1d5db;
  cursor: not-allowed;
  transform: none;
}

.toggle-form {
  background: none;
  border: none;
  color: #2196F3;
  font-size: 0.95rem;
  margin-top: 1.5rem;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
}

.toggle-form:hover:not(:disabled) {
  color: #1976D2;
  background-color: rgba(80, 125, 188, 0.1);
}

.toggle-form:disabled {
  color: #9ca3af;
  cursor: not-allowed;
}

.status-message {
  text-align: center;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  margin: 0.5rem 0;
  font-size: 0.95rem;
}

.status-message.success {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-message.error {
  background-color: #fee2e2;
  color: #dc2626;
}

.status-message.info {
  background-color: #f3f4f6;
  color: #333;
}

.debug-info {
  font-family: monospace;
  font-size: 0.875rem;
  padding: 0.5rem;
  background-color: #1f2937;
  color: #f3f4f6;
  border-radius: 4px;
  margin-top: 0.5rem;
  white-space: pre-wrap;
  word-break: break-all;
}

.spinner {
  display: inline-block;
  width: 1.25rem;
  height: 1.25rem;
  border: 2px solid #ffffff;
  border-radius: 50%;
  border-top-color: transparent;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

p {
  text-align: center;
  margin: 0;
}
</style>
