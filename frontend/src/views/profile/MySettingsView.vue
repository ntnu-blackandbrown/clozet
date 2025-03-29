<script setup>
import { ref } from 'vue'

const formData = ref({
  firstName: '',
  lastName: '',
  username: '',
  password: '',
  email: '',
  phoneNumber: '',
})

const handleSaveChanges = () => {
  // TODO: Implement save changes functionality
  console.log('Saving changes:', formData.value)
}

const handleDeleteAccount = () => {
  // TODO: Implement delete account functionality
  console.log('Deleting account')
}
</script>

<template>
  <div class="profile-settings-container">
    <h2>Profile Settings</h2>
    <div class="profile-form">
      <div class="name-fields">
        <div class="form-group">
          <label>First Name</label>
          <input type="text" placeholder="Your first name" v-model="formData.firstName" />
        </div>
        <div class="form-group">
          <label>Last Name</label>
          <input type="text" placeholder="Your last name" v-model="formData.lastName" />
        </div>
      </div>
      <div class="credentials-fields">
        <div class="form-group">
          <label>Username</label>
          <input type="text" placeholder="Your username" v-model="formData.username" />
        </div>
        <div class="form-group">
          <label>Password</label>
          <input type="password" placeholder="Your password" v-model="formData.password" />
        </div>
      </div>
      <div class="contact-fields">
        <div class="form-group">
          <label>Email</label>
          <input type="email" placeholder="Your email" v-model="formData.email" />
        </div>
        <div class="form-group">
          <label>Phone Number</label>
          <input type="tel" placeholder="Your phone number" v-model="formData.phoneNumber" />
        </div>
      </div>
      <div class="form-actions">
        <button class="save-button" @click="handleSaveChanges">Save Changes</button>
        <button class="delete-button" @click="handleDeleteAccount">Delete Account</button>
      </div>
    </div>
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
.credentials-fields,
.contact-fields {
  display: flex;
  gap: 4rem;
  margin-bottom: 1.5rem;
}

.name-fields .form-group,
.credentials-fields .form-group,
.contact-fields .form-group {
  flex: 1;
  margin-bottom: 0;
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
  .credentials-fields,
  .contact-fields {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>
