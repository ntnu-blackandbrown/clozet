<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/AuthStore'
import BaseModal from '@/components/modals/BaseModal.vue'
import { useRouter } from 'vue-router'
import {
  useValidatedForm,
  useValidatedField,
  loginSchema,
  registerSchema,
} from '@/utils/validation'
import { AuthService } from '@/api/services/AuthService'
import * as yup from 'yup'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
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
const statusMessage = ref('')
const statusType = ref('')
const debugInfo = ref('')
const showPassword = ref(false)

// Set initial mode based on prop
onMounted(() => {
  isLogin.value = props.initialMode === 'login'
})

const formTitle = computed(() => {
  if (props.customTitle) {
    return isLogin.value ? props.customTitle : props.customTitle.replace('login', 'register')
  }
  return isLogin.value ? t('auth.loginTitle') : t('auth.registerTitle')
})

const toggleText = computed(() =>
  isLogin.value ? t('auth.dontHaveAccount') + ' ' + t('common.register') : t('auth.alreadyHaveAccount') + ' ' + t('common.login'),
)

// Interface for form values
interface FormValues {
  username: string
  password: string
  confirmPassword: string
  firstName: string
  lastName: string
  email: string
}

// Create a custom login schema without password validation
const loginSchemaNoPasswordValidation = yup.object({
  username: yup.string().required('Username is required'),
  password: yup.string().required('Password is required'), // Minimal validation, just requiring it's not empty
})

// Use the current schema based on login/register mode
const currentSchema = computed(() =>
  isLogin.value ? loginSchemaNoPasswordValidation : registerSchema,
)

// Set up initial form values
const initialValues: FormValues = {
  username: '',
  password: '',
  confirmPassword: '',
  firstName: '',
  lastName: '',
  email: '',
}

const {
  handleSubmit,
  errors,
  resetForm,
  isFormValid: originalIsFormValid,
  isSubmitting,
  values,
} = useValidatedForm<FormValues>(currentSchema.value, initialValues)

// Custom form validation for login mode
const isFormValid = computed(() => {
  if (isLogin.value) {
    // For login, only check username and password
    return values.username && values.password && !errors.value.username && !errors.value.password
  }
  // For registration, use the original validation
  return originalIsFormValid.value
})

// Define form fields with validation
const { value: username, errorMessage: usernameError } = useValidatedField('username')
const { value: password, errorMessage: passwordError } = useValidatedField('password')
const { value: confirmPassword, errorMessage: confirmPasswordError } =
  useValidatedField('confirmPassword')
const { value: firstName, errorMessage: firstNameError } = useValidatedField('firstName')
const { value: lastName, errorMessage: lastNameError } = useValidatedField('lastName')
const { value: email, errorMessage: emailError } = useValidatedField('email')

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
  statusMessage.value = isLogin.value ? t('common.loading') : t('common.loading')
  statusType.value = 'info'
  debugInfo.value = ''

  try {
    if (isLogin.value) {
      // Login using AuthStore (JWT cookie approach)
      const result = await authStore.login(values.username, values.password)

      if (result.success) {
        statusMessage.value = t('common.login') + ' ' + t('common.success')
        statusType.value = 'success'
        setTimeout(() => {
          emit('close')
        }, 1500)
      } else {
        statusMessage.value = result.message || t('auth.loginFailed')
        statusType.value = 'error'
        isSubmitting.value = false // Reset loading state immediately on login failure
      }
    } else {
      // Direct registration with correct endpoint
      console.log('Registering now')
      const userData = {
        username: values.username,
        email: values.email,
        password: values.password,
        firstName: values.firstName,
        lastName: values.lastName,
        role: 'USER',
      }
      console.log('userData', userData)
      console.log('Sending request now')


      const response = await AuthService.register(
        userData.username,
        userData.password,
        userData.email,
        userData.firstName,
        userData.lastName,
      )

      if (response.status === 200) {
        statusMessage.value = t('auth.registrationSuccess')
      }
    }
  } catch (error: any) {
    console.error('Error:', error)
    statusMessage.value = isLogin.value
      ? t('auth.loginFailed2')
      : t('auth.registrationFailed')
    statusType.value = 'error'
  } finally {
    isSubmitting.value = false
  }
})
</script>

<template>
  <BaseModal @close="emit('close')" maxWidth="400px" padding="3rem" hideCloseButton>
    <h2 id="modal-title">{{ formTitle }}</h2>

    <!-- FORM CONTENT-->
    <form @submit.prevent="submit" aria-labelledby="modal-title">
      <div class="form-group">
        <label for="username" class="sr-only">{{ $t('common.username') }}</label>
        <input
          v-if="isLogin"
          type="text"
          id="username"
          v-model="username"
          :placeholder="$t('common.username')"
          aria-required="true"
          :aria-invalid="!!usernameError"
        />
        <span class="error" id="usernameErrSpan" v-if="isLogin && usernameError" role="alert">{{
          usernameError
        }}</span>
      </div>

      <template v-if="!isLogin">
        <div class="form-group">
          <label for="register-username" class="sr-only">{{ $t('common.username') }}</label>
          <input
            id="register-username"
            v-model="username"
            type="text"
            :placeholder="$t('common.username')"
            aria-required="true"
            :aria-invalid="!!usernameError"
          />
          <span class="error" id="usernameErrSpan" v-if="usernameError" role="alert">{{
            usernameError
          }}</span>
        </div>

        <div class="form-group">
          <label for="firstName" class="sr-only">{{ $t('common.firstName') }}</label>
          <input
            id="firstName"
            v-model="firstName"
            type="text"
            :placeholder="$t('common.firstName')"
            aria-required="true"
            :aria-invalid="!!firstNameError"
          />
          <span class="error" id="firstNameErrSpan" v-if="firstNameError" role="alert">{{
            firstNameError
          }}</span>
        </div>

        <div class="form-group">
          <label for="lastName" class="sr-only">{{ $t('common.lastName') }}</label>
          <input
            id="lastName"
            v-model="lastName"
            type="text"
            :placeholder="$t('common.lastName')"
            aria-required="true"
            :aria-invalid="!!lastNameError"
          />
          <span class="error" id="lastNameErrSpan" v-if="lastNameError" role="alert">{{
            lastNameError
          }}</span>
        </div>

        <div class="form-group">
          <label for="email" class="sr-only">{{ $t('common.email') }}</label>
          <input
            id="email"
            v-model="email"
            type="email"
            :placeholder="$t('common.email')"
            aria-required="true"
            :aria-invalid="!!emailError"
          />
          <span class="error" id="emailErrSpan" v-if="emailError" role="alert">{{
            emailError
          }}</span>
        </div>
      </template>

      <div class="password-input-container form-group">
        <label for="password" class="sr-only">{{ $t('common.password') }}</label>
        <input
          id="password"
          v-model="password"
          :type="showPassword ? 'text' : 'password'"
          :placeholder="$t('common.password')"
          aria-required="true"
          :aria-invalid="!!passwordError"
        />
        <button
          type="button"
          @click="showPassword = !showPassword"
          class="toggle-password"
          :aria-label="showPassword ? $t('common.hide') + ' ' + $t('common.password') : $t('common.show') + ' ' + $t('common.password')"
          aria-controls="password"
        >
          {{ showPassword ? $t('common.hide') : $t('common.show') }}
        </button>
      </div>
      <span class="error" id="passwordErrSpan" v-if="passwordError" role="alert">{{
        passwordError
      }}</span>

      <div v-if="!isLogin" class="password-input-container form-group">
        <label for="confirmPassword" class="sr-only">{{ $t('common.confirmPassword') }}</label>
        <input
          id="confirmPassword"
          v-model="confirmPassword"
          :type="showPassword ? 'text' : 'password'"
          :placeholder="$t('common.confirmPassword')"
          aria-required="true"
          :aria-invalid="!!confirmPasswordError"
          aria-describedby="confirmPasswordErrSpan"
        />
      </div>
      <span
        v-if="!isLogin && confirmPasswordError"
        class="error"
        id="confirmPasswordErrSpan"
        role="alert"
        >{{ confirmPasswordError }}</span
      >

      <!-- Status Message -->
      <div
        v-if="statusMessage"
        class="status-message"
        :class="statusType"
        role="status"
        aria-live="polite"
      >
        {{ statusMessage }}
      </div>


      <button type="submit" :disabled="!isFormValid || isSubmitting" :aria-busy="isSubmitting">
        <span v-if="isSubmitting">
          <span class="spinner" aria-hidden="true"></span>
          <span class="sr-only">{{ $t('common.loading') }}</span>
        </span>
        <span v-else>{{ isLogin ? $t('common.login') : $t('common.register') }}</span>
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
  border-color: #4caf50;
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
  background-color: #4caf50;
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
  color: #2196f3;
  font-size: 0.95rem;
  margin-top: 1.5rem;
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
}

.toggle-form:hover:not(:disabled) {
  color: #1976d2;
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

.password-input-container {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.password-input-container input {
  flex-grow: 1;
  margin-bottom: 0;
  padding-right: 2.5rem;
}

.password-input-container .toggle-password {
  position: absolute;
  right: 0.5rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  padding: 0.2rem 0.4rem;
  cursor: pointer;
  font-size: 0.8rem;
  color: #666;
  line-height: 1;
}

.password-input-container .toggle-password:focus {
  outline: none;
}

.form-group {
  margin-bottom: 0.5rem;
}

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}
</style>
