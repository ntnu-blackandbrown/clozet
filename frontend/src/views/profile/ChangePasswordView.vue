<template>
  <div class="forgot-password-container">
    <div class="forgot-password-card">
      <h1>Change Password</h1>
      <p class="description">Enter your current password and choose a new password.</p>

      <form @submit.prevent="submit" class="forgot-password-form">
        <div class="form-group">
          <label for="currentPassword">Current Password</label>
          <div class="password-input-container">
            <input
              :type="showCurrentPassword ? 'text' : 'password'"
              id="currentPassword"
              v-model="currentPassword"
              placeholder="Enter your current password"
              class="form-control"
              :class="{ 'error-input': currentPasswordError }"
            />
            <button type="button" @click="showCurrentPassword = !showCurrentPassword" class="toggle-password">
              {{ showCurrentPassword ? 'Hide' : 'Show' }}
            </button>
          </div>
          <span class="error" v-if="currentPasswordError">{{ currentPasswordError }}</span>
        </div>

        <div class="form-group">
          <label for="newPassword">New Password</label>
          <div class="password-input-container">
            <input
              :type="showNewPassword ? 'text' : 'password'"
              id="newPassword"
              v-model="newPassword"
              placeholder="Enter your new password"
              class="form-control"
              :class="{ 'error-input': newPasswordError }"
            />
             <button type="button" @click="showNewPassword = !showNewPassword" class="toggle-password">
              {{ showNewPassword ? 'Hide' : 'Show' }}
            </button>
          </div>
          <span class="error" v-if="newPasswordError">{{ newPasswordError }}</span>
        </div>

        <div class="form-group">
          <label for="confirmPassword">Confirm New Password</label>
           <div class="password-input-container">
            <input
              :type="showConfirmPassword ? 'text' : 'password'"
              id="confirmPassword"
              v-model="confirmPassword"
              placeholder="Confirm your new password"
              class="form-control"
              :class="{ 'error-input': confirmPasswordError }"
            />
            <button type="button" @click="showConfirmPassword = !showConfirmPassword" class="toggle-password">
              {{ showConfirmPassword ? 'Hide' : 'Show' }}
            </button>
          </div>
          <span class="error" v-if="confirmPasswordError">{{ confirmPasswordError }}</span>
        </div>

        <div class="message" :class="{ error: error, success: success }">
          {{ message }}
        </div>

        <button type="submit" class="submit-button" :disabled="!isFormValid || isSubmitting">
          <span v-if="isSubmitting">
            <span class="spinner"></span>
          </span>
          <span v-else>Update Password</span>
        </button>

        <div class="links">
          <router-link to="/profile/settings" class="back-to-login">Back to Settings</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { AuthService } from '@/api/services/AuthService'
import { useValidatedForm, useValidatedField, changePasswordSchema } from '@/utils/validation'

const router = useRouter()

// Define form values interface
interface ChangePasswordFormValues {
  currentPassword: string
  newPassword: string
  confirmPassword: string
}

// Use our validation hook
const { handleSubmit, errors, resetForm, isFormValid, isSubmitting } =
  useValidatedForm<ChangePasswordFormValues>(changePasswordSchema, {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  })

// Setup fields with validation
const { value: currentPassword, errorMessage: currentPasswordError } =
  useValidatedField('currentPassword')
const { value: newPassword, errorMessage: newPasswordError } = useValidatedField('newPassword')
const { value: confirmPassword, errorMessage: confirmPasswordError } =
  useValidatedField('confirmPassword')

const error = ref(false)
const success = ref(false)
const message = ref('')

// Add state for each password field's visibility
const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

// Handle form submission
const submit = handleSubmit(async (values) => {
  isSubmitting.value = true
  error.value = false
  success.value = false
  message.value = ''

  try {
    // Call API to update password
    await AuthService.changePassword(
      values.currentPassword,
      values.newPassword,
      values.confirmPassword,
    )

    success.value = true
    message.value = 'Password has been updated successfully.'

    // Redirect to profile settings after 2 seconds
    setTimeout(() => {
      router.push('/profile/settings')
    }, 2000)
  } catch (err) {
    error.value = true
    message.value = 'An error occurred. Please check your current password and try again.'
  } finally {
    isSubmitting.value = false
  }
})
</script>

<style scoped>
.forgot-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background-color: #f5f5f5;
}

.forgot-password-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

h1 {
  text-align: center;
  color: #333;
  margin-bottom: 1rem;
}

.description {
  text-align: center;
  color: #666;
  margin-bottom: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #333;
}

.form-control {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.error-input {
  border-color: #ef4444;
}

.error {
  color: #ef4444;
  font-size: 0.875rem;
  margin-top: 0.25rem;
  display: block;
}

.submit-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
}

.submit-button:hover:not(:disabled) {
  background-color: #45a049;
}

.submit-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.message {
  margin: 1rem 0;
  padding: 0.75rem;
  border-radius: 4px;
  text-align: center;
}

.error {
  background-color: #ffebee;
  color: #c62828;
}

.success {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.links {
  margin-top: 1.5rem;
  text-align: center;
}

.back-to-login {
  color: #2196f3;
  text-decoration: none;
}

.back-to-login:hover {
  text-decoration: underline;
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

/* Added styles for password input container and toggle button */
.password-input-container {
  /* Add relative positioning to contain the absolute button */
  position: relative;
  display: flex;
  align-items: center;
  /* Removed gap as button is now inside */
}

.password-input-container input {
  flex-grow: 1; /* Make input take available space */
  margin-bottom: 0; /* Remove default margin if any */
  /* Add padding to the right to make space for the button */
  padding-right: 2.5rem; /* Adjust as needed */
}

.password-input-container .toggle-password {
   /* Position the button absolutely inside the container */
  position: absolute;
  right: 0.5rem; /* Adjust position */
  top: 50%;
  transform: translateY(-50%);
  /* Style as an icon button */
  background: none;
  border: none;
  padding: 0.2rem 0.4rem; /* Adjusted padding for text */
  cursor: pointer;
  font-size: 0.8rem; /* Adjusted font size for text */
  color: #666; /* Adjust icon color */
  line-height: 1; /* Ensure proper vertical alignment */
}

/* Ensure toggle button doesn't receive focus outline if not desired */
.password-input-container .toggle-password:focus {
  outline: none;
}
</style>
