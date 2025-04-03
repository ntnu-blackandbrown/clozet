<script setup>
import { ref, computed } from 'vue'
import { useField, useForm } from 'vee-validate'
import * as yup from 'yup'

// Define validation schema
const schema = yup.object({
  firstName: yup.string().required('First name is required'),
  lastName: yup.string().required('Last name is required'),
  username: yup.string().required('Username is required'),
  email: yup.string().email('Invalid email').required('Email is required'),
  phoneNumber: yup.string().matches(/^\+?[\d\s-]+$/, 'Invalid phone number format'),
})

const { handleSubmit, errors, resetForm } = useForm({
  validationSchema: schema,
})

// Define form fields using useField
const { value: firstName, errorMessage: firstNameError } = useField('firstName')
const { value: lastName, errorMessage: lastNameError } = useField('lastName')
const { value: username, errorMessage: usernameError } = useField('username')
const { value: email, errorMessage: emailError } = useField('email')
const { value: phoneNumber, errorMessage: phoneNumberError } = useField('phoneNumber')

const isSubmitting = ref(false)
const statusMessage = ref('')
const statusType = ref('')

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
  statusMessage.value = 'Saving changes...'
  statusType.value = 'info'

  try {
    // TODO: Implement API call to save changes
    console.log('Saving changes:', values)

    statusMessage.value = 'Changes saved successfully!'
    statusType.value = 'success'
  } catch (error) {
    console.error('Error saving changes:', error)
    statusMessage.value = 'Failed to save changes'
    statusType.value = 'error'
  } finally {
    isSubmitting.value = false
  }
})

const handleDeleteAccount = async () => {
  if (!confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
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
    <h2>Profile Settings</h2>
    <form @submit.prevent="handleSaveChanges" class="profile-form">
      <div class="name-fields">
        <div class="form-group">
          <label>First Name</label>
          <input type="text" placeholder="Your first name" v-model="firstName" />
          <span class="error" v-if="firstNameError">{{ firstNameError }}</span>
        </div>
        <div class="form-group">
          <label>Last Name</label>
          <input type="text" placeholder="Your last name" v-model="lastName" />
          <span class="error" v-if="lastNameError">{{ lastNameError }}</span>
        </div>
      </div>
      <div class="credentials-fields">
        <div class="form-group">
          <label>Username</label>
          <input type="text" placeholder="Your username" v-model="username" />
          <span class="error" v-if="usernameError">{{ usernameError }}</span>
        </div>
        <div class="form-group">
          <label>Email</label>
          <input type="email" placeholder="Your email" v-model="email" />
          <span class="error" v-if="emailError">{{ emailError }}</span>
        </div>
      </div>
      <div class="phone-field">
        <div class="form-group">
          <label>Phone Number</label>
          <input type="tel" placeholder="Your phone number" v-model="phoneNumber" />
          <span class="error" v-if="phoneNumberError">{{ phoneNumberError }}</span>
        </div>
      </div>

      <!-- Status Message -->
      <div v-if="statusMessage" class="status-message" :class="statusType">
        {{ statusMessage }}
      </div>

      <div class="form-actions">
        <button type="submit" class="save-button" :disabled="!isFormValid || isSubmitting">
          <span v-if="isSubmitting">
            <span class="spinner"></span>
          </span>
          <span v-else>Save Changes</span>
        </button>
        <button type="button" class="delete-button" @click="handleDeleteAccount" :disabled="isSubmitting">
          Delete Account
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
</style>

