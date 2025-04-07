<script setup lang="ts">
import { ref, defineEmits, defineProps } from 'vue'
import axios from '@/api/axios'

const props = defineProps<{
  itemId: number,
  itemTitle: string,
  itemPrice: number,
  sellerId: number,
  buyerId: number
}>()

const emit = defineEmits(['close', 'paymentComplete'])

// Step tracking
const currentStep = ref(1)
const phoneNumber = ref('')
const pincode = ref('')
const isProcessing = ref(false)
const error = ref('')

// Phone number validation
const isValidPhoneNumber = (phone: string) => {
  // Norwegian phone number: +47 followed by 8 digits
  const regex = /^\+47[0-9]{8}$/
  return regex.test(phone)
}

// Go to next step
const nextStep = () => {
  if (currentStep.value === 1) {
    if (!isValidPhoneNumber(phoneNumber.value)) {
      error.value = 'Please enter a valid Norwegian phone number (+47 followed by 8 digits)'
      return
    }
    error.value = ''
  }

  currentStep.value++
}

// Go to previous step
const prevStep = () => {
  currentStep.value--
  error.value = ''
}

// Process payment
const processPayment = async () => {
  if (pincode.value.length !== 4) {
    error.value = 'PIN must be 4 digits'
    return
  }

  isProcessing.value = true
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
      amount: props.itemPrice,
      paymentMethod: 'vipps',
      phoneNumber: phoneNumber.value
    })

    emit('paymentComplete', response.data)
  } catch (err) {
    console.error('Payment processing error:', err)
    error.value = 'Payment failed. Please try again.'
  } finally {
    isProcessing.value = false
  }
}

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
        <button @click="emit('close')" class="vipps-button cancel">Cancel</button>
        <button @click="nextStep" class="vipps-button next">Next</button>
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
          <span class="detail-label">Amount:</span>
          <span class="detail-value price">{{ formattedPrice(itemPrice) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">Phone:</span>
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
        <button @click="prevStep" class="vipps-button back" :disabled="isProcessing">Back</button>
        <button @click="processPayment" class="vipps-button pay" :disabled="isProcessing">
          <span v-if="isProcessing">Processing...</span>
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
</style>
