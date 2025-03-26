<script setup>
import { defineProps, computed } from 'vue'

const props = defineProps({
  name: {
    type: String,
    required: true
  },
  type: {
    type: String,
    default: 'category', // category, location, seller, price
    validator: (value) => ['category', 'location', 'seller', 'price'].includes(value)
  },
  amount: {
    type: [String, Number],
    default: ''
  },
  currency: {
    type: String,
    default: 'NOK'
  },
  color: {
    type: String,
    default: '' // Will be set based on type if not provided
  },
  textColor: {
    type: String,
    default: '' // Will be set based on type if not provided
  }
})

// Default colors based on badge type
const defaultColors = {
  category: { bg: '#e2e8f0', text: '#1a202c' },   // Light gray
  location: { bg: '#f3f4f6', text: '#374151' },   // Light gray-blue
  seller: { bg: '#edf2f7', text: '#2d3748' },     // Light blue-gray
  price: { bg: '#f0fff4', text: '#276749' }       // Light green
}

// Compute actual colors to use
const bgColor = computed(() => props.color || defaultColors[props.type].bg)
const txtColor = computed(() => props.textColor || defaultColors[props.type].text)

// Compute display text based on badge type
const displayText = computed(() => {
  if (props.type === 'price') {
    return props.amount
  }
  return props.name
})

// Get the appropriate icon based on type
const getIcon = () => {
  switch (props.type) {
    case 'category':
      return `
        <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"></path>
        <line x1="7" y1="7" x2="7.01" y2="7"></line>
      `
    case 'location':
      return `
        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
        <circle cx="12" cy="10" r="3"></circle>
      `
    case 'seller':
      return `
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
        <circle cx="12" cy="7" r="4"></circle>
      `
    default:
      return ''
  }
}
</script>

<template>
  <div class="badge" :style="{ backgroundColor: bgColor, color: txtColor }">
    <template v-if="type === 'price'">
      <span class="currency-icon">{{ currency }}</span>
    </template>
    <template v-else>
      <svg v-if="getIcon()" class="badge-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
           fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
           v-html="getIcon()">
      </svg>
    </template>
    <span class="badge-text">{{ displayText }}</span>
  </div>
</template>

<style>
.badge {
  display: inline-flex;
  align-items: center;
  padding: 0.5rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.875rem;
  font-weight: 500;
  margin-right: 0.5rem;
  margin-bottom: 0.5rem;
}

.badge-icon, .currency-icon {
  width: 1rem;
  height: 1rem;
  margin-right: 0.375rem;
}

.currency-icon {
  font-weight: 600;
  font-size: 0.75rem;
}

.badge-text {
  display: inline-block;
}
</style>
