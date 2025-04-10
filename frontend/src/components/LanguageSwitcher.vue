<template>
  <div class="language-switcher">
    <button @click="toggleDropdown" class="language-button" aria-haspopup="true" :aria-expanded="isOpen">
      <span class="globe-icon">🌐</span>
      <span class="current-language">
        {{ currentLocale === 'en' ? 'English' :
           currentLocale === 'nb' ? 'Norsk' :
           currentLocale === 'es' ? 'Español' : 'English' }}
      </span>
    </button>
    <div v-if="isOpen" class="language-dropdown">
      <button
        @click="changeLocale('en')"
        class="language-option"
        :class="{ active: currentLocale === 'en' }"
      >
        English
      </button>
      <button
        @click="changeLocale('nb')"
        class="language-option"
        :class="{ active: currentLocale === 'nb' }"
      >
        Norsk
      </button>
      <button
        @click="changeLocale('es')"
        class="language-option"
        :class="{ active: currentLocale === 'es' }"
      >
        Español
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useI18n } from 'vue-i18n'
import { setLocale, SUPPORTED_LOCALES } from '@/i18n'

const { locale } = useI18n()
const currentLocale = ref(locale.value)
const isOpen = ref(false)

onMounted(() => {
  currentLocale.value = locale.value
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})

const toggleDropdown = (event: Event) => {
  event.stopPropagation()
  isOpen.value = !isOpen.value
}

const handleClickOutside = (event: Event) => {
  const target = event.target as HTMLElement
  if (!target.closest('.language-switcher')) {
    isOpen.value = false
  }
}

const changeLocale = (localeValue: string) => {
  if (SUPPORTED_LOCALES.includes(localeValue)) {
    currentLocale.value = localeValue
    setLocale(localeValue)
    isOpen.value = false
  }
}
</script>

<style scoped>
.language-switcher {
  position: relative;
  display: inline-block;
  z-index: 100;
  margin: 0 10px;
}

.language-button {
  display: flex;
  align-items: center;
  background-color: #f1e7ca;
  color: #3a4951;
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.language-button:hover {
  background-color: #e8ddb8;
  transform: translateY(-2px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.globe-icon {
  margin-right: 6px;
  font-size: 16px;
}

.language-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 5px;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
  overflow: hidden;
  min-width: 120px;
}

.language-option {
  display: block;
  width: 100%;
  padding: 8px 12px;
  text-align: left;
  background: none;
  border: none;
  cursor: pointer;
  color: #3a4951;
  transition: background-color 0.2s;
}

.language-option:hover {
  background-color: #f5f5f5;
}

.language-option.active {
  background-color: #f1e7ca;
  font-weight: 500;
}

/* Mobile styles */
@media (max-width: 768px) {
  .language-switcher {
    display: block;
    margin: 10px 0;
    width: 100%;
  }

  .language-button {
    width: 100%;
    justify-content: center;
    padding: 10px;
    font-size: 16px;
  }

  .language-dropdown {
    width: 100%;
    position: relative;
    margin-top: 5px;
  }

  .language-option {
    padding: 12px;
    font-size: 16px;
    text-align: center;
  }
}
</style>
