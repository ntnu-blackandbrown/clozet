<script setup lang="ts">
import { ref, defineEmits, defineProps, onMounted } from 'vue'
import { useAuthStore } from '@/stores/AuthStore'

const props = defineProps<{
  shippingOptionName: string
}>()

const emit = defineEmits(['close', 'continue'])
const authStore = useAuthStore()

const firstName = ref(authStore.user?.firstName || '')
const lastName = ref(authStore.user?.lastName || '')
const streetAddress = ref('')
const postalCode = ref('')
const city = ref('')
const country = ref('Norway')
const phone = ref(authStore.user?.phoneNumber || '')
const error = ref('')
const isProcessing = ref(false)

// Form validation
const validateForm = () => {
  if (!firstName.value || !lastName.value || !streetAddress.value ||
      !postalCode.value || !city.value || !country.value || !phone.value) {
    error.value = 'All fields are required'
    return false
  }

  // Norwegian postal code validation (4 digits)
  if (!/^\d{4}$/.test(postalCode.value)) {
    error.value = 'Please enter a valid Norwegian postal code (4 digits)'
    return false
  }

  // Basic phone validation
  if (!/^\+?\d{8,15}$/.test(phone.value)) {
    error.value = 'Please enter a valid phone number'
    return false
  }

  return true
}

const handleContinue = () => {
  if (!validateForm()) {
    return
  }

  isProcessing.value = true
  error.value = ''

  // Gather shipping details
  const shippingDetails = {
    firstName: firstName.value,
    lastName: lastName.value,
    streetAddress: streetAddress.value,
    postalCode: postalCode.value,
    city: city.value,
    country: country.value,
    phone: phone.value
  }

  // Short delay to simulate processing
  setTimeout(() => {
    isProcessing.value = false
    emit('continue', shippingDetails)
  }, 500)
}
</script>

<template>
  <div class="shipping-details-modal">
    <h2>Shipping Details</h2>
    <p class="shipping-type">{{ shippingOptionName }}</p>

    <div class="form-container">
      <div class="form-row">
        <div class="form-group">
          <label for="firstName">First Name</label>
          <input
            type="text"
            id="firstName"
            v-model="firstName"
            class="form-input"
            placeholder="Enter your first name"
            :disabled="!!authStore.user?.firstName"
          />
        </div>
        <div class="form-group">
          <label for="lastName">Last Name</label>
          <input
            type="text"
            id="lastName"
            v-model="lastName"
            class="form-input"
            placeholder="Enter your last name"
            :disabled="!!authStore.user?.lastName"
          />
        </div>
      </div>

      <div class="form-group">
        <label for="streetAddress">Street Address</label>
        <input
          type="text"
          id="streetAddress"
          v-model="streetAddress"
          class="form-input"
          placeholder="Enter your street address"
        />
      </div>

      <div class="form-row">
        <div class="form-group">
          <label for="postalCode">Postal Code</label>
          <input
            type="text"
            id="postalCode"
            v-model="postalCode"
            class="form-input"
            placeholder="0000"
            maxlength="4"
          />
        </div>
        <div class="form-group">
          <label for="city">City</label>
          <input
            type="text"
            id="city"
            v-model="city"
            class="form-input"
            placeholder="Enter your city"
          />
        </div>
      </div>

      <div class="form-group">
        <label for="country">Country</label>
        <input
          type="text"
          id="country"
          v-model="country"
          class="form-input"
          readonly
        />
      </div>

      <div class="form-group">
        <label for="phone">Phone Number</label>
        <input
          type="tel"
          id="phone"
          v-model="phone"
          class="form-input"
          placeholder="+47 12345678"
        />
      </div>

      <p v-if="error" class="error-message">{{ error }}</p>

      <div class="button-group">
        <button @click="emit('close')" class="shipping-button cancel">Cancel</button>
        <button
          @click="handleContinue"
          class="shipping-button continue"
          :disabled="isProcessing"
        >
          <span v-if="isProcessing">Processing...</span>
          <span v-else>Continue to Payment</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.shipping-details-modal {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

h2 {
  margin-bottom: 0.5rem;
  color: #333;
}

.shipping-type {
  color: #666;
  margin-bottom: 2rem;
  font-style: italic;
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-group {
  flex: 1;
  display: flex;
  flex-direction: column;
}

label {
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #555;
}

.form-input {
  padding: 12px 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.2s;
}

.form-input:focus {
  border-color: #4CAF50;
  outline: none;
}

.error-message {
  color: #d32f2f;
  margin-top: 0.5rem;
  font-size: 14px;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 2rem;
}

.shipping-button {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.shipping-button.cancel {
  background-color: #f2f2f2;
  color: #333;
}

.shipping-button.cancel:hover {
  background-color: #e5e5e5;
}

.shipping-button.continue {
  background-color: #4CAF50;
  color: white;
  min-width: 180px;
}

.shipping-button.continue:hover {
  background-color: #45a049;
}

.shipping-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 600px) {
  .form-row {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>
