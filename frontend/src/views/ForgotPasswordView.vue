<template>
  <div class="forgot-password-container">
    <div class="forgot-password-card">
      <h1>Change Password</h1>
      <p class="description">Enter your current password and choose a new password.</p>

      <form @submit.prevent="handleSubmit" class="forgot-password-form">
        <div class="form-group">
          <label for="currentPassword">Current Password</label>
          <input
            type="password"
            id="currentPassword"
            v-model="currentPassword"
            required
            placeholder="Enter your current password"
            class="form-control"
          />
        </div>

        <div class="form-group">
          <label for="newPassword">New Password</label>
          <input
            type="password"
            id="newPassword"
            v-model="newPassword"
            required
            placeholder="Enter your new password"
            class="form-control"
          />
        </div>

        <div class="form-group">
          <label for="confirmPassword">Confirm New Password</label>
          <input
            type="password"
            id="confirmPassword"
            v-model="confirmPassword"
            required
            placeholder="Confirm your new password"
            class="form-control"
          />
        </div>

        <div class="message" :class="{ 'error': error, 'success': success }">
          {{ message }}
        </div>

        <button type="submit" class="submit-button" :disabled="isLoading">
          {{ isLoading ? 'Updating...' : 'Update Password' }}
        </button>

        <div class="links">
          <router-link to="/profile/settings" class="back-to-login">Back to Settings</router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const isLoading = ref(false)
const error = ref(false)
const success = ref(false)
const message = ref('')

const handleSubmit = async () => {
  if (newPassword.value !== confirmPassword.value) {
    error.value = true
    message.value = 'New passwords do not match.'
    return
  }

  isLoading.value = true
  error.value = false
  success.value = false
  message.value = ''

  try {
    // TODO: Implement the API call to your backend
    // const response = await api.post('/auth/change-password', {
    //   currentPassword: currentPassword.value,
    //   newPassword: newPassword.value
    // })

    // Simulating API call for now
    await new Promise(resolve => setTimeout(resolve, 1000))

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
    isLoading.value = false
  }
}
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

.submit-button {
  width: 100%;
  padding: 0.75rem;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-button:hover {
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
  color: #2196F3;
  text-decoration: none;
}

.back-to-login:hover {
  text-decoration: underline;
}
</style>
