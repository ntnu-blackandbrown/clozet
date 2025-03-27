<script setup lang="ts">
import { computed, defineProps } from 'vue'
import type { PropType } from 'vue'

// 1) Define props directly in defineProps with default values
const props = defineProps({
  name: {
    type: String,
    required: true
  },
  type: {
    type: String as PropType<'category' | 'location' | 'seller' | 'price' | 'shipping' | 'availability'>,
    default: 'category'
  },
  amount: {
    type: [String, Number] as PropType<string | number>,
    default: ''
  },
  currency: {
    type: String,
    default: 'NOK'
  },
  color: {
    type: String,
    default: ''
  },
  textColor: {
    type: String,
    default: ''
  }
})

// 2) Store default colors in a plain object (no ref needed)
const defaultColors = {
  category: { bg: '#e2e8f0', text: '#1a202c' },
  location: { bg: '#f3f4f6', text: '#374151' },
  seller: { bg: '#edf2f7', text: '#2d3748' },
  price: { bg: '#f0fff4', text: '#276749' },
  shipping: { bg: '#ebf8ff', text: '#2b6cb0' },
  availability: { bg: '#fef6e4', text: '#c05621' }
}

// 3) Store icon paths in a plain object
const iconPaths = {
  category: `
    <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"></path>
    <line x1="7" y1="7" x2="7.01" y2="7"></line>
  `,
  location: `
    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
    <circle cx="12" cy="10" r="3"></circle>
  `,
  seller: `
    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
    <circle cx="12" cy="7" r="4"></circle>
  `,
  shipping: `
    <rect x="1" y="3" width="15" height="13"></rect>
    <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
    <circle cx="5.5" cy="18.5" r="2.5"></circle>
    <circle cx="18.5" cy="18.5" r="2.5"></circle>
  `,
  availability: `
    <circle cx="12" cy="12" r="10"></circle>
    <polyline points="12 6 12 12 16 14"></polyline>
  `
}

// 4) Use computed properties for colors, display text, and icon
const bgColor = computed(() => {
  // Fallback to the default color for the given type
  return props.color || defaultColors[props.type].bg
})

const txtColor = computed(() => {
  // Fallback to the default text color for the given type
  return props.textColor || defaultColors[props.type].text
})

const currentIcon = computed(() => {
  if (props.type === 'price') return ''
  return iconPaths[props.type] || ''
})

const displayText = computed(() => {
  // If it's a price, we show amount; otherwise, we show name
  return props.type === 'price' ? props.amount : props.name
})

// Only relevant if you need a separate "formattedPrice"
const formattedPrice = computed(() => {
  return props.type === 'price' ? `${props.amount} ${props.currency}` : ''
})
</script>

<template>
  <div class="badge" :style="{ backgroundColor: bgColor, color: txtColor }">
    <svg
      v-if="currentIcon"
      class="badge-icon"
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
      v-html="currentIcon"
    />
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
