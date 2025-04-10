<script setup>
import { ref, computed, onMounted } from 'vue'
import { useField, useForm } from 'vee-validate'
import * as yup from 'yup'
import { useAuthStore } from '@/stores/AuthStore'
import { useRouter } from 'vue-router'
import { AuthService } from '@/api/services/AuthService'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const authStore = useAuthStore()
const router = useRouter()

// Define validation schema
const schema = yup.object({
  firstName: yup.string().required('First name is required'),
  lastName: yup.string().required('Last name is required'),
  username: yup.string().required('Username is required'),
  email: yup.string().email('Invalid email').required('Email is required'),
})

const { handleSubmit, errors, resetForm, setValues } = useForm({
  validationSchema: schema,
})

// Define form fields using useField
const { value: firstName, errorMessage: firstNameError } = useField('firstName')
const { value: lastName, errorMessage: lastNameError } = useField('lastName')
const { value: username, errorMessage: usernameError } = useField('username')
const { value: email, errorMessage: emailError } = useField('email')

const isSubmitting = ref(false)
const statusMessage = ref('')
const statusType = ref('')

// Load user data when component mounts
onMounted(async () => {
  if (authStore.user) {
    setUserValues()
  } else {
    // If user data is not in store, fetch it
    await authStore.fetchUserInfo()
    if (authStore.user) {
      setUserValues()
    }
  }
})

const setUserValues = () => {
  setValues({
    firstName: authStore.user.firstName || '',
    lastName: authStore.user.lastName || '',
    username: authStore.user.username || '',
    email: authStore.user.email || '',
  })
}

const isFormValid = computed(() => {
  return (
    !errors.value.firstName &&
    !errors.value.lastName &&
    !errors.value.username &&
    !errors.value.email &&
    firstName.value &&
    lastName.value &&
    username.value &&
    email.value
  )
})

const handleSaveChanges = handleSubmit(async (values) => {
  isSubmitting.value = true
  statusMessage.value = t('profile.settings.savingChanges')
  statusType.value = 'info'

  try {
    await AuthService.updateUser(authStore.user.id, values)
    setUserValues()
    statusMessage.value = t('profile.settings.changesSuccess')
    statusType.value = 'success'
    setTimeout(() => {
      authStore.logout()
      router.push('/')
    }, 1500)
    // Try to fetch updated user info
  } catch (error) {
    console.error('Error saving changes:', error)
    statusMessage.value = t('profile.settings.changesFailed')
    statusType.value = 'error'
  } finally {
    isSubmitting.value = false
  }
})

const handleDeleteAccount = async () => {
  if (!confirm(t('profile.settings.confirmDelete'))) {
    return
  }

  try {
    // TODO: Implement API call to delete account
    console.log('Deleting account')
  } catch (error) {
    console.error('Error deleting account:', error)
  }
}
</script>

<template>
  <div class="profile-settings-container">
    <h2 id="profile-settings-title">{{ $t('profile.settings.title') }}</h2>
    <form
      @submit.prevent="handleSaveChanges"
      class="profile-form"
      aria-labelledby="profile-settings-title"
    >
      <div class="name-fields">
        <div class="form-group">
          <label for="firstName">{{ $t('common.firstName') }}</label>
          <input
            type="text"
            id="firstName"
            :placeholder="$t('common.firstName')"
            v-model="firstName"
            aria-required="true"
            aria-invalid="firstNameError ? true : false"
          />
          <span class="error" v-if="firstNameError" role="alert">{{ firstNameError }}</span>
        </div>
        <div class="form-group">
          <label for="lastName">{{ $t('common.lastName') }}</label>
          <input
            type="text"
            id="lastName"
            :placeholder="$t('common.lastName')"
            v-model="lastName"
            aria-required="true"
            aria-invalid="lastNameError ? true : false"
          />
          <span class="error" v-if="lastNameError" role="alert">{{ lastNameError }}</span>
        </div>
      </div>
      <div class="credentials-fields">
        <div class="form-group">
          <label for="username">{{ $t('common.username') }}</label>
          <input
            type="text"
            id="username"
            :placeholder="$t('common.username')"
            v-model="username"
            aria-required="true"
            aria-invalid="usernameError ? true : false"
          />
          <span class="error" v-if="usernameError" role="alert">{{ usernameError }}</span>
        </div>
        <div class="form-group">
          <label for="email">{{ $t('common.email') }}</label>
          <input
            type="email"
            id="email"
            :placeholder="$t('common.email')"
            v-model="email"
            aria-required="true"
            aria-invalid="emailError ? true : false"
          />
          <span class="error" v-if="emailError" role="alert">{{ emailError }}</span>
        </div>
      </div>

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

      <div class="form-actions">
        <button
          type="submit"
          class="save-button"
          :disabled="!isFormValid || isSubmitting"
          aria-busy="isSubmitting"
        >
          <span v-if="isSubmitting">
            <span class="spinner" aria-hidden="true"></span>
            <span class="sr-only">{{ $t('profile.settings.savingChanges') }}</span>
          </span>
          <span v-else>{{ $t('profile.settings.saveChanges') }}</span>
        </button>
        <button
          type="button"
          class="delete-button"
          @click="handleDeleteAccount"
          :disabled="isSubmitting"
          :aria-label="$t('profile.settings.deleteAccount')"
        >
          {{ $t('profile.settings.deleteAccount') }}
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.profile-settings-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.profile-settings-container h2 {
  margin-bottom: 2rem;
  color: #333;
  font-size: 1.5rem;
}

.profile-form {
  max-width: 600px;
}

.name-fields,
.credentials-fields {
  display: flex;
  gap: 4rem;
  margin-bottom: 1.5rem;
}

.name-fields .form-group,
.credentials-fields .form-group {
  flex: 1;
  margin-bottom: 0;
}

.phone-field {
  margin-bottom: 1.5rem;
}

.phone-field .form-group {
  max-width: calc(50% - 2rem);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #374151;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e1e1e1;
  border-radius: 8px;
  background-color: #f8f9fa;
  transition: all 0.2s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #3b82f6;
  background-color: white;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.save-button,
.delete-button {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.save-button {
  background-color: #3b82f6;
  color: white;
  border: none;
}

.save-button:hover {
  background-color: #2563eb;
}

.delete-button {
  background-color: #ef4444;
  color: white;
  border: none;
}

.delete-button:hover {
  background-color: #dc2626;
}

@media (max-width: 768px) {
  .profile-settings-container {
    padding: 1rem;
  }

  .name-fields,
  .credentials-fields {
    flex-direction: column;
    gap: 1rem;
  }

  .phone-field .form-group {
    max-width: 100%;
  }
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
