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
  color: '#e2e8f0',
  textColor: '#214b89',
  borderColor: '#214b89',
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
