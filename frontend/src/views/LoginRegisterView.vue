<script setup lang="ts">
import { ref, computed } from 'vue'
import { useField, useForm } from 'vee-validate'
import * as yup from 'yup'
import { useAuthStore } from '@/stores/AuthStore'
import axios from '@/api/axios'
import BaseModal from '@/components/modals/BaseModal.vue'

const authStore = useAuthStore()
const emit = defineEmits(['close'])

const isLogin = ref(true)
const isSubmitting = ref(false)
const statusMessage = ref('')
const statusType = ref('')
const debugInfo = ref('')

const formTitle = computed(() => (isLogin.value ? 'Login' : 'Register'))
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

const { value: username, errorMessage: usernameError } = useField('username')
const { value: password, errorMessage: passwordError } = useField('password')
const { value: confirmPassword, errorMessage: confirmPasswordError } = useField('confirmPassword')
const { value: firstName, errorMessage: firstNameError } = useField('firstName')
const { value: lastName, errorMessage: lastNameError } = useField('lastName')
const { value: email, errorMessage: emailError } = useField('email')

const toggleForm = () => {
  isLogin.value = !isLogin.value
  statusMessage.value = ''
  statusType.value = ''
  debugInfo.value = ''
  resetForm()
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
      const userData = {
        username: values.username,
        email: values.email,
        password: values.password,
        firstName: values.firstName,
        lastName: values.lastName,
        role: 'USER'
      }

      debugInfo.value = `POST til /api/users/register med ${JSON.stringify(userData)}`

      const response = await axios.post('/api/users/register', userData)

      if (response.data) {
        statusMessage.value = `Registrering vellykket! Velkommen, ${response.data.username}`
        statusType.value = 'success'

        // Auto-login with new credentials
        const loginResult = await authStore.login(values.username, values.password)
        if (loginResult.success) {
          setTimeout(() => {
            emit('close')
          }, 1500)
        }
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

button[type='submit'] {
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
  position: relative;
}

button[type='submit']:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

button[type='submit']:hover:not(:disabled) {
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

.toggle-form:hover:not(:disabled) {
  color: #333;
}

.toggle-form:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.error {
  display: block;
  color: #e74c3c;
  font-size: 0.8rem;
  margin: 0.25rem 0 0.5rem;
  padding-left: 0.5rem;
}

.status-message {
  padding: 10px;
  border-radius: 4px;
  margin: 10px 0;
  font-size: 0.9rem;
  text-align: center;
}

.debug-info {
  padding: 10px;
  border-radius: 4px;
  margin: 10px 0;
  font-size: 0.75rem;
  background-color: #f8f9fa;
  color: #666;
  word-break: break-all;
  text-align: left;
  font-family: monospace;
}

.status-message.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.status-message.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.status-message.info {
  background-color: #d1ecf1;
  color: #0c5460;
  border: 1px solid #bee5eb;
}

.spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s linear infinite;
  margin-right: 5px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 480px) {
  :deep(.container) {
    padding: 2rem;
  }
}
</style>
