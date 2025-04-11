<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/AuthStore'
import { useValidatedForm, useValidatedField, shippingDetailsSchema } from '@/utils/validation'

interface ShippingDetails {
  firstName?: string
  lastName?: string
  streetAddress?: string
  postalCode?: string
  city?: string
  country?: string
  phone?: string
}

const props = defineProps<{
  shippingOptionName: string
  initialValues?: ShippingDetails
}>()

const emit = defineEmits(['close', 'continue'])
const authStore = useAuthStore()

// Initialize form values with initial values or defaults
const initialValues = {
  firstName: props.initialValues?.firstName || authStore.user?.firstName || '',
  lastName: props.initialValues?.lastName || authStore.user?.lastName || '',
  streetAddress: props.initialValues?.streetAddress || '',
  postalCode: props.initialValues?.postalCode || '',
  city: props.initialValues?.city || '',
  country: props.initialValues?.country || 'Norway',
  phone: props.initialValues?.phone || authStore.user?.phoneNumber || '',
}

// Use validation hook with initial values
const { handleSubmit, isSubmitting, setStatus, clearStatus, isFormValid } = useValidatedForm(
  shippingDetailsSchema,
  initialValues,
)

// Get validated fields
const { value: firstName } = useValidatedField('firstName')
const { value: lastName } = useValidatedField('lastName')
const { value: streetAddress } = useValidatedField('streetAddress')
const { value: postalCode } = useValidatedField('postalCode')
const { value: city } = useValidatedField('city')
const { value: country } = useValidatedField('country')
const { value: phone } = useValidatedField('phone')

const error = ref('')

// Check if international shipping
const isInternationalShipping = computed(() => {
  return props.shippingOptionName === 'International Shipping'
})

// List of countries (you can expand this list as needed)
const countries = [
  'Norway',
  'Sweden',
  'Denmark',
  'Finland',
  'Germany',
  'France',
  'United Kingdom',
  'Spain',
  'Italy',
  'Netherlands',
  'Belgium',
  'Poland',
  'Austria',
  'Switzerland',
  'Ireland',
  'Portugal',
  // Add more European countries as needed
]

const handleContinue = handleSubmit((values) => {
  isSubmitting.value = true
  error.value = ''

  // Short delay to simulate processing
  setTimeout(() => {
    isSubmitting.value = false
    emit('continue', values)
  }, 500)
})
</script>

<template>
  <div class="shipping-details-modal" role="dialog" aria-labelledby="shipping-modal-title">
    <h2 id="shipping-modal-title">Shipping Details</h2>
    <p class="shipping-type">{{ shippingOptionName }}</p>

    <div class="form-container" role="form">
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
            aria-required="true"
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
            aria-required="true"
          />
        </div>
      </div>

      <div class="form-group">
        <label for="country">Country</label>
        <select
          v-if="isInternationalShipping"
          id="country"
          v-model="country"
          class="form-input"
          aria-required="true"
        >
          <option v-for="countryName in countries" :key="countryName" :value="countryName">
            {{ countryName }}
          </option>
        </select>
        <input
          v-else
          type="text"
          id="country"
          v-model="country"
          class="form-input"
          readonly
          aria-readonly="true"
        />
      </div>

      <div class="form-group">
        <label for="streetAddress">Street Address</label>
        <input
          type="text"
          id="streetAddress"
          v-model="streetAddress"
          class="form-input"
          placeholder="Enter your street address"
          aria-required="true"
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
            :placeholder="country === 'Norway' ? '0000' : 'Enter postal code'"
            :maxlength="country === 'Norway' ? 4 : undefined"
            aria-required="true"
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
            aria-required="true"
          />
        </div>
      </div>

      <div class="form-group">
        <label for="phone">Phone Number</label>
        <input
          type="tel"
          id="phone"
          v-model="phone"
          class="form-input"
          placeholder="+47 12345678"
          aria-required="true"
        />
      </div>

      <p v-if="error" class="error-message" role="alert">{{ error }}</p>

      <div class="button-group">
        <button
          @click="emit('close')"
          class="shipping-button cancel"
          aria-label="Cancel and close shipping details form"
        >
          Cancel
        </button>
        <button
          @click="handleContinue"
          class="shipping-button continue"
          :disabled="!isFormValid || isSubmitting"
          aria-label="Continue to payment"
        >
          <span v-if="isSubmitting">Processing...</span>
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
  border-color: #4caf50;
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
  background-color: #4caf50;
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

select.form-input {
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 1rem center;
  background-size: 1em;
  padding-right: 2.5rem;
}

select.form-input:focus {
  border-color: #4caf50;
  outline: none;
}

select.form-input option {
  padding: 0.5rem;
}
</style>
