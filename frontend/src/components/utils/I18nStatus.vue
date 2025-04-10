<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

// isI18nEnabled is imported from the i18n module
const isI18nEnabled = import.meta.env.MODE !== 'production' || import.meta.env.VITE_ENABLE_I18N === 'true'

// Use the i18n composable to access the current locale
const { locale } = useI18n()

// Create a computed property to display the current status
const statusText = computed(() => {
  if (isI18nEnabled) {
    return `i18n Active (${locale.value})`
  } else {
    return 'i18n Disabled (Production Mode)'
  }
})
</script>

<template>
  <div class="i18n-status" :class="{ 'i18n-enabled': isI18nEnabled, 'i18n-disabled': !isI18nEnabled }">
    {{ statusText }}
  </div>
</template>

<style scoped>
.i18n-status {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  margin: 4px;
}

.i18n-enabled {
  background-color: #4caf50;
  color: white;
}

.i18n-disabled {
  background-color: #f44336;
  color: white;
}
</style>
