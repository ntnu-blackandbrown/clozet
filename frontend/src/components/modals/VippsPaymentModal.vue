<script setup lang="ts">
import { ref, defineEmits, defineProps, computed } from 'vue'
import axios from '@/api/axios'
import { useValidatedForm, useValidatedField, vippsPaymentSchema } from '@/utils/validation'

const props = defineProps<{
  itemId: number,
  itemTitle: string,
  itemPrice: number,
  sellerId: number,
  buyerId: number,
  shippingOptionName?: string,
  shippingCost?: number,
  shippingPhoneNumber?: string
}>()

const emit = defineEmits(['close', 'back', 'paymentComplete'])

// Step tracking
const currentStep = ref(1)
const error = ref('')

// Initialize validation with the vipps schema
const {
  handleSubmit,
  isSubmitting,
  setStatus,
  isFormValid
} = useValidatedForm(vippsPaymentSchema, {
  phoneNumber: '',
  pincode: ''
})

// Get validated fields
const { value: phoneNumber } = useValidatedField('phoneNumber')
const { value: pincode } = useValidatedField('pincode')

// Go to next step
const nextStep = () => {
  if (currentStep.value === 1) {
    // Use our vipps phone number validation
    const phoneValue = phoneNumber.value as string
    const isValid = /^\+47[0-9]{8}$/.test(phoneValue)
    if (!isValid) {
      error.value = 'Please enter a valid Norwegian phone number (+47 followed by 8 digits)'
      return
    }
    error.value = ''
  }

  currentStep.value++
}

// Go to previous step
const prevStep = () => {
  if (currentStep.value === 1) {
    emit('back')
    return
  }
  currentStep.value--
  error.value = ''
}

// Process payment
const processPayment = handleSubmit(async (values) => {
  isSubmitting.value = true
  error.value = ''

  try {
    // Simulate API call delay
    await new Promise(resolve => setTimeout(resolve, 2000))

    // Create transaction
    const response = await axios.post('/api/transactions/purchase', {
      itemId: props.itemId,
      sellerId: props.sellerId,
      buyerId: props.buyerId,
      status: 'completed',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      amount: totalPrice.value,
      paymentMethod: 'vipps',
      phoneNumber: values.phoneNumber
    })

    emit('paymentComplete', response.data)
  } catch (err) {
    console.error('Payment processing error:', err)
    error.value = 'Payment failed. Please try again.'
  } finally {
    isSubmitting.value = false
  }
})

// Calculate total price including shipping
const totalPrice = computed(() => {
  const shippingFee = props.shippingCost || 0
  return props.itemPrice + shippingFee
})

// Format price for display
const formattedPrice = (price: number) => {
  return price.toFixed(2) + ' NOK'
}
</script>

<template>
  <div class="vipps-payment-modal">
    <div class="vipps-header">
      <img src="@/assets/images/vipps.png" alt="Vipps Logo" class="vipps-logo" />
      <h2>Vipps Payment</h2>
    </div>

    <!-- Step 1: Phone Number -->
    <div v-if="currentStep === 1" class="vipps-step">
      <h3>Enter your phone number</h3>
      <p class="step-description">Please enter your Norwegian phone number connected to Vipps</p>

      <div v-if="props.shippingPhoneNumber" class="shipping-phone-notice">
        <p>Your shipping phone number is: <strong>{{ props.shippingPhoneNumber }}</strong></p>
        <p class="hint">You can use the same number or enter a different one for Vipps payment</p>
      </div>

      <div class="input-group">
        <input
          type="tel"
          v-model="phoneNumber"
          placeholder="+47 12345678"
          class="vipps-input"
        />
      </div>

      <p v-if="error" class="error-message">{{ error }}</p>

      <div class="button-group">
        <button @click="prevStep" class="vipps-button back">Back to Shipping</button>
        <button
          @click="nextStep"
          class="vipps-button next"
          :disabled="!phoneNumber || !/^\+47[0-9]{8}$/.test(phoneNumber as string)"
        >Next</button>
      </div>
    </div>

    <!-- Step 2: Confirm Purchase -->
    <div v-if="currentStep === 2" class="vipps-step">
      <h3>Confirm Your Purchase</h3>

      <div class="purchase-details">
        <div class="detail-row">
          <span class="detail-label">Item:</span>
          <span class="detail-value">{{ itemTitle }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">Item Price:</span>
          <span class="detail-value">{{ formattedPrice(itemPrice) }}</span>
        </div>
        <div v-if="shippingCost && shippingCost > 0" class="detail-row">
          <span class="detail-label">Shipping Cost:</span>
          <span class="detail-value">{{ formattedPrice(shippingCost) }}</span>
        </div>
        <div v-if="shippingOptionName" class="detail-row">
          <span class="detail-label">Shipping Method:</span>
          <span class="detail-value">{{ shippingOptionName }}</span>
        </div>
        <div class="detail-row total-row">
          <span class="detail-label">Total Amount:</span>
          <span class="detail-value price">{{ formattedPrice(totalPrice) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">Vipps Phone:</span>
          <span class="detail-value">{{ phoneNumber }}</span>
        </div>
      </div>

      <div class="button-group">
        <button @click="prevStep" class="vipps-button back">Back</button>
        <button @click="nextStep" class="vipps-button next">Confirm</button>
      </div>
    </div>

    <!-- Step 3: PIN Code -->
    <div v-if="currentStep === 3" class="vipps-step">
      <h3>Enter your Vipps PIN</h3>
      <p class="step-description">Please enter your 4-digit Vipps PIN code to complete the payment</p>

      <div class="input-group">
        <input
          type="password"
          v-model="pincode"
          placeholder="****"
          maxlength="4"
          class="vipps-input pin-input"
        />
      </div>

      <p v-if="error" class="error-message">{{ error }}</p>

      <div class="button-group">
        <button @click="prevStep" class="vipps-button back" :disabled="isSubmitting">Back</button>
        <button
          @click="processPayment"
          class="vipps-button pay"
          :disabled="!isFormValid || isSubmitting || !pincode || (pincode as string).length !== 4"
        >
          <span v-if="isSubmitting">Processing...</span>
          <span v-else>Pay Now</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.vipps-payment-modal {
  width: 100%;
  max-width: 500px;
  margin: 0 auto;
}

.vipps-header {
  display: flex;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #f2f2f2;
}

.vipps-logo {
  width: 60px;
  height: auto;
  margin-right: 1rem;
}

.vipps-step {
  display: flex;
  flex-direction: column;
}

.step-description {
  color: #666;
  margin-bottom: 1.5rem;
}

.input-group {
  margin-bottom: 1.5rem;
}

.vipps-input {
  width: 100%;
  padding: 12px 15px;
  border: 2px solid #FF5B24;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s;
}

.vipps-input:focus {
  border-color: #e94e1b;
}

.pin-input {
  font-size: 20px;
  letter-spacing: 8px;
  text-align: center;
  max-width: 200px;
  margin: 0 auto;
}

.button-group {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
}

.vipps-button {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.vipps-button.cancel {
  background-color: #f2f2f2;
  color: #333;
}

.vipps-button.cancel:hover {
  background-color: #e5e5e5;
}

.vipps-button.next, .vipps-button.pay {
  background-color: #FF5B24;
  color: white;
}

.vipps-button.next:hover, .vipps-button.pay:hover {
  background-color: #e94e1b;
}

.vipps-button.back {
  background-color: #f2f2f2;
  color: #333;
}

.vipps-button.back:hover {
  background-color: #e5e5e5;
}

.vipps-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  color: #d32f2f;
  margin-bottom: 1rem;
  font-size: 14px;
}

.purchase-details {
  background-color: #f9f9f9;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.75rem;
}

.detail-row:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-weight: 600;
  color: #555;
}

.detail-value {
  color: #333;
}

.detail-value.price {
  font-weight: 700;
  color: #FF5B24;
}

.total-row {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #e0e0e0;
  font-size: 1.1em;
}

.total-row .detail-label,
.total-row .detail-value {
  font-weight: 700;
}

.shipping-phone-notice {
  background-color: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1rem;
  border: 1px solid #e9ecef;
}

.shipping-phone-notice p {
  margin: 0;
}

.shipping-phone-notice .hint {
  color: #666;
  font-size: 0.9em;
  margin-top: 0.5rem;
}
</style>
