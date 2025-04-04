<script setup lang="ts">
import { computed, ref } from 'vue'
import type { PropType } from 'vue'
import { badgeIcons } from '@/components/utils/BadgeIcons'
import { badgeColors, type BadgeType } from '@/components/utils/BadgeColors'

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
  color: undefined,
  textColor: undefined,
  borderColor: undefined,
})

const isHovered = ref(false)
const isActive = ref(false)

const currentIcon = computed(() => {
  if (props.type === 'price') return ''
  return badgeIcons[props.type] || ''
})

const badgeStyle = computed(() => {
  const defaultColors = badgeColors[props.type]

  if (isActive.value && props.type !== 'price') {
    return {
      backgroundColor: props.color || defaultColors.activeColor,
      color: props.textColor || defaultColors.activeTextColor,
      border: props.borderColor
        ? `1px solid ${props.borderColor}`
        : `1px solid ${defaultColors.activeBorderColor}`,
    }
  } else if (isHovered.value && props.type !== 'price') {
    return {
      backgroundColor: props.color || defaultColors.hoverColor,
      color: props.textColor || defaultColors.hoverTextColor,
      border: props.borderColor
        ? `1px solid ${props.borderColor}`
        : `1px solid ${defaultColors.hoverBorderColor}`,
    }
  } else {
    return {
      backgroundColor: props.color || defaultColors.color,
      color: props.textColor || defaultColors.textColor,
      border: props.borderColor
        ? `1px solid ${props.borderColor}`
        : `1px solid ${defaultColors.borderColor}`,
    }
  }
})

const handleMouseEnter = () => {
  if (props.type !== 'price') {
    isHovered.value = true
  }
}

const handleMouseLeave = () => {
  if (props.type !== 'price') {
    isHovered.value = false
  }
}

const handleMouseDown = () => {
  if (props.type !== 'price') {
    isActive.value = true
  }
}

const handleMouseUp = () => {
  if (props.type !== 'price') {
    isActive.value = false
  }
}
</script>

<template>
  <div
    class="badge"
    :class="{ 'price-badge': type === 'price' }"
    :style="badgeStyle"
    @mouseenter="handleMouseEnter"
    @mouseleave="handleMouseLeave"
    @mousedown="handleMouseDown"
    @mouseup="handleMouseUp"
  >
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
  transition: all 0.2s ease;
  cursor: pointer;
  box-shadow: var(--box-shadow-light);
}

.badge:hover {
  transform: translateY(-1px);
  box-shadow: var(--box-shadow-medium);
}

.badge:active {
  transform: translateY(0);
}

.price-badge {
  cursor: default;
  padding: var(--spacing-xs) var(--spacing-md);
  box-shadow: none;
  background-color: white !important;
  margin-right: var(--spacing-md);
  margin-bottom: calc(var(--spacing-xs) + 5px);
  border-radius: var(--border-radius-lg);
}

.price-badge:hover {
  transform: none;
  box-shadow: none;
}

.price-badge:active {
  transform: none;
}

.badge-icon {
  width: 1rem;
  height: 1rem;
  margin-right: var(--spacing-xs);
}

.badge-text {
  display: inline-block;
}
</style>
