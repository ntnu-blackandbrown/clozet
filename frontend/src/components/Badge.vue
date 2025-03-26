<script setup lang="ts">
import { computed, ref } from 'vue'

// Define props with TypeScript types
interface Props {
  name: string
  type?: 'category' | 'location' | 'seller' | 'price' | 'shipping' | 'availability'
  amount?: string | number
  currency?: string
  color?: string
  textColor?: string
}

const props = withDefaults(defineProps<Props>(), {
  type: 'category',
  amount: '',
  currency: 'NOK',
  color: '',
  textColor: ''
})

// Default colors based on badge type using refs
const defaultColors = ref({
  category: { bg: '#e2e8f0', text: '#1a202c' },    // Light gray
  location: { bg: '#f3f4f6', text: '#374151' },    // Light gray-blue
  seller: { bg: '#edf2f7', text: '#2d3748' },      // Light blue-gray
  price: { bg: '#f0fff4', text: '#276749' },       // Light green
  shipping: { bg: '#ebf8ff', text: '#2b6cb0' },    // Light blue
  availability: { bg: '#fef6e4', text: '#c05621' } // Light orange
})

// Compute actual colors to use
const bgColor = computed(() => props.color || defaultColors.value[props.type].bg)
const txtColor = computed(() => props.textColor || defaultColors.value[props.type].text)

// Compute display text based on badge type
const displayText = computed(() => {
  if (props.type === 'price') {
    return props.amount
  }
  return props.name
})

// Store the current icon in a ref
const currentIcon = ref('')

// Update the icon when needed
const getIcon = () => {
  switch (props.type) {
    case 'category':
      currentIcon.value = `
        <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"></path>
        <line x1="7" y1="7" x2="7.01" y2="7"></line>
      `
      break
    case 'location':
      currentIcon.value = `
        <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
        <circle cx="12" cy="10" r="3"></circle>
      `
      break
    case 'seller':
      currentIcon.value = `
        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
        <circle cx="12" cy="7" r="4"></circle>
      `
      break
    case 'price':
      currentIcon.value = `
        <path d="M12 1v22M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
      `
      break
    case 'shipping':
      currentIcon.value = `
        <rect x="1" y="3" width="15" height="13"></rect>
        <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
        <circle cx="5.5" cy="18.5" r="2.5"></circle>
        <circle cx="18.5" cy="18.5" r="2.5"></circle>
      `
      break
    case 'availability':
      currentIcon.value = `
        <circle cx="12" cy="12" r="10"></circle>
        <polyline points="12 6 12 12 16 14"></polyline>
      `
      break
    default:
      currentIcon.value = ''
  }
  return currentIcon.value
}

// Format price display with currency
const formattedPrice = computed(() => {
  if (props.type === 'price') {
    return `${props.amount} ${props.currency}`
  }
  return ''
})

// Get icon on component creation
getIcon()
</script>

<template>
  <div class="badge" :style="{ backgroundColor: bgColor, color: txtColor }">
    <svg v-if="currentIcon" class="badge-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"
         fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"
         v-html="currentIcon">
    </svg>
    <span v-if="type === 'price'" class="badge-text">{{ displayText }} {{ currency }}</span>
    <span v-else class="badge-text">{{ displayText }}</span>
  </div>
</template>

<style scoped>
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

.badge-icon {
  width: 1rem;
  height: 1rem;
  margin-right: 0.375rem;
}

.badge-text {
  display: inline-block;
}
</style>
