<script setup lang="ts">
import { computed } from 'vue'
import type { PropType } from 'vue'
import { badgeIcons, type BadgeType } from '@/components/utils/BadgeIcons'

interface BadgeProps {
  name: string
  type?: BadgeType
  currency?: string
  color?: string
  textColor?: string
  borderColor?: string
}

const props = withDefaults(defineProps<BadgeProps>(), {
  type: 'category',
  color: 'var(--color-conch)', // Using Conch as background
  textColor: '#2D353F', // Outer Sea for text
  borderColor: '#2D353F', // Outer Sea for border
})

const currentIcon = computed(() => {
  if (props.type === 'price') return ''
  return badgeIcons[props.type] || ''
})

const badgeStyle = computed(() => ({
  backgroundColor: props.color,
  color: props.textColor,
  border: `2px solid ${props.borderColor}`,
}))
</script>

<template>
  <div class="badge" :style="badgeStyle">
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
    <span class="badge-text">
      {{ props.name }}{{ type === 'price' ? ` ${props.currency}` : '' }}
    </span>
  </div>
</template>

<style scoped>
.badge {
  display: inline-flex;
  align-items: center;
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-lg);
  font-size: 0.875rem;
  font-weight: 500;
  margin-right: var(--spacing-xs);
  margin-bottom: var(--spacing-xs);
  transition: var(--transition-smooth);
  cursor: pointer;
  box-shadow: var(--box-shadow-light);
}

.badge:hover {
  transform: translateY(-1px);
  box-shadow: var(--box-shadow-medium);
  background-color: var(--color-conch-light) !important;
  border-color: #2D353F !important;
  color: #2D353F !important;
}

.badge:active {
  transform: translateY(0);
  background-color: var(--color-conch-dark) !important;
}

.badge-icon {
  width: 1rem;
  height: 1rem;
  margin-right: var(--spacing-xs);
  stroke: #2D353F;
}

.badge-text {
  display: inline-block;
}
</style>
