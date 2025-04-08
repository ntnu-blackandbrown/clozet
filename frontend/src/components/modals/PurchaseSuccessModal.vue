<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  itemTitle: string
  totalAmount: number
  shippingAddress?: {
    firstName: string
    lastName: string
    streetAddress: string
    postalCode: string
    city: string
    country: string
  }
}>()

const emit = defineEmits(['close', 'viewPurchases'])

const formattedPrice = computed(() => {
  // Ensure the number is properly rounded to 2 decimal places
  const roundedAmount = Math.round(props.totalAmount * 100) / 100
  return new Intl.NumberFormat('no-NO', {
    style: 'currency',
    currency: 'NOK',
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(roundedAmount)
})
</script>

<template>
  <div class="success-modal">
    <div class="success-icon">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
        <path
          d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"
        />
      </svg>
    </div>

    <h2>Purchase Successful!</h2>
    <p class="success-message">
      Thank you for your purchase. Your transaction has been completed successfully.
    </p>

    <div class="purchase-summary">
      <h3>Purchase Summary</h3>
      <div class="summary-item">
        <span class="label">Item:</span>
        <span class="value">{{ itemTitle }}</span>
      </div>
      <div class="summary-item">
        <span class="label">Total Amount:</span>
        <span class="value amount">{{ formattedPrice }}</span>
      </div>
    </div>

    <div v-if="shippingAddress" class="shipping-info">
      <h3>Shipping Details</h3>
      <div class="address">
        <p>{{ shippingAddress.firstName }} {{ shippingAddress.lastName }}</p>
        <p>{{ shippingAddress.streetAddress }}</p>
        <p>{{ shippingAddress.postalCode }} {{ shippingAddress.city }}</p>
        <p>{{ shippingAddress.country }}</p>
      </div>
    </div>

    <div class="actions">
      <button @click="emit('viewPurchases')" class="view-purchases-btn">View My Purchases</button>
      <button @click="emit('close')" class="continue-shopping-btn">Continue Shopping</button>
    </div>
  </div>
</template>

<style scoped>
.success-modal {
  text-align: center;
  padding: 2rem;
  max-width: 500px;
  margin: 0 auto;
}

.success-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  color: #4caf50;
}

.success-icon svg {
  width: 100%;
  height: 100%;
}

h2 {
  color: #4caf50;
  margin-bottom: 1rem;
  font-size: 1.8rem;
}

.success-message {
  color: #666;
  margin-bottom: 2rem;
  font-size: 1.1rem;
  line-height: 1.5;
}

.purchase-summary,
.shipping-info {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  text-align: left;
}

h3 {
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.2rem;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.summary-item .label {
  color: #666;
  font-weight: 500;
}

.summary-item .value {
  color: #333;
}

.summary-item .amount {
  color: #4caf50;
  font-weight: 600;
}

.shipping-info .address {
  color: #333;
  line-height: 1.5;
}

.shipping-info .address p {
  margin: 0.25rem 0;
}

.actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-top: 2rem;
}

.view-purchases-btn,
.continue-shopping-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.view-purchases-btn {
  background-color: #4caf50;
  color: white;
}

.view-purchases-btn:hover {
  background-color: #45a049;
}

.continue-shopping-btn {
  background-color: #f2f2f2;
  color: #333;
}

.continue-shopping-btn:hover {
  background-color: #e5e5e5;
}

@media (max-width: 480px) {
  .actions {
    flex-direction: column;
  }

  .view-purchases-btn,
  .continue-shopping-btn {
    width: 100%;
  }
}
</style>
