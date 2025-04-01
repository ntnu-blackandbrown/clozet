<script setup lang="ts">
import { computed, defineProps } from 'vue'
import type { PropType } from 'vue'


const props = defineProps({
  name: {
    type: String,
    required: true,
  },
  type: {
    type: String as PropType<
      'category' | 'location' | 'seller' | 'price' | 'shipping' | 'availability'
    >,
    default: 'category',
  },
  currency: {
    type: String,
    default: 'NOK',
  },
  color: {
    type: String,
    default: '#e2e8f0',
  },
  textColor: {
    type: String,
    default: '#214b89',
  },
  borderColor: {
    type: String,
    default: '#214b89',
  }
})

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
  `,
}

const currentIcon = computed(() => {
  if (props.type === 'price') return ''
  return iconPaths[props.type] || ''
})

</script>

<template>
  <div class="badge" :style="{ backgroundColor: props.color, color: props.textColor, border: `2px solid ${props.borderColor}`}">
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
    <span v-if="type === 'price'" class="badge-text">{{ props.name }} {{ props.currency }}</span>
    <span v-else class="badge-text">{{ props.name }}</span>
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
