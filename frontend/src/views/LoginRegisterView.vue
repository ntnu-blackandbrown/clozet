<script setup lang="ts">
import { ref, computed } from 'vue'
import { useField, useForm } from 'vee-validate'
import * as yup from 'yup'
import { useAuthStore } from '@/stores/AuthStore'
import BaseModal from '@/components/modals/BaseModal.vue'

const authStore = useAuthStore()
const emit = defineEmits(['close'])
const props = defineProps({
  customTitle: {
    type: String,
    default: ''
  }
})
const isLogin = ref(true)
const isSubmitting = ref(false)
const statusMessage = ref('')
const statusType = ref('')
const debugInfo = ref('')

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
        role: 'USER',
      }

      debugInfo.value = `POST til /api/users/register med ${JSON.stringify(userData)}`

      const response = await (values.username,
      values.password,
      values.email,
      values.firstName,
      values.lastName)

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
h2 {
  color: var(--color-outer-space);
  font-size: 1.8rem;
  margin-bottom: var(--spacing-xl);
  font-weight: 600;
  text-align: center;
  letter-spacing: -0.02em;
}

form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

input {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-lg);
  font-size: 0.95rem;
  border: 1px solid var(--color-wheatfield-dark);
  border-radius: var(--border-radius);
  background-color: var(--color-white);
  transition: var(--transition-smooth);
  color: var(--color-outer-space);
}

input:focus {
  outline: none;
  border-color: var(--color-summer-green);
  box-shadow: 0 0 0 3px rgba(150, 187, 124, 0.2);
}

input::placeholder {
  color: #9CA3AF;
}

.error {
  color: #EF4444;
  font-size: 0.875rem;
  margin-top: calc(var(--spacing-xs) * -1);
  margin-bottom: var(--spacing-xs);
}

button[type='submit'] {
  width: 100%;
  padding: var(--spacing-md);
  margin-top: var(--spacing-md);
  background-color: var(--color-summer-green);
  color: var(--color-white);
  border: none;
  border-radius: var(--border-radius);
  font-weight: 500;
  font-size: 1rem;
  cursor: pointer;
  transition: var(--transition-bounce);
  display: flex;
  justify-content: center;
  align-items: center;
  gap: var(--spacing-sm);
}

button[type='submit']:hover:not(:disabled) {
  background-color: var(--color-summer-green-dark);
  transform: translateY(-2px);
  box-shadow: var(--box-shadow-medium);
}

button[type='submit']:disabled {
  background-color: #D1D5DB;
  cursor: not-allowed;
  transform: none;
}

.toggle-form {
  background: none;
  border: none;
  color: var(--color-smalt-blue);
  font-size: 0.95rem;
  margin-top: var(--spacing-lg);
  cursor: pointer;
  transition: var(--transition-smooth);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
}

.toggle-form:hover:not(:disabled) {
  color: var(--color-smalt-blue-dark);
  background-color: rgba(80, 125, 188, 0.1);
}

.toggle-form:disabled {
  color: #9CA3AF;
  cursor: not-allowed;
}

.status-message {
  text-align: center;
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius);
  margin: var(--spacing-sm) 0;
  font-size: 0.95rem;
}

.status-message.success {
  background-color: var(--color-summer-green-light);
  color: var(--color-summer-green-dark);
}

.status-message.error {
  background-color: #FEE2E2;
  color: #DC2626;
}

.status-message.info {
  background-color: var(--color-wheatfield-light);
  color: var(--color-outer-space);
}

.debug-info {
  font-family: monospace;
  font-size: 0.875rem;
  padding: var(--spacing-sm);
  background-color: #1F2937;
  color: #F3F4F6;
  border-radius: var(--border-radius-sm);
  margin-top: var(--spacing-sm);
  white-space: pre-wrap;
  word-break: break-all;
}

.spinner {
  display: inline-block;
  width: 1.25rem;
  height: 1.25rem;
  border: 2px solid var(--color-white);
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
