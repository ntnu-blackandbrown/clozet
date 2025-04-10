<template>
  <div class="language-switcher">
    <select v-model="currentLocale" @change="changeLocale" class="locale-select">
      <option value="en">English</option>
      <option value="nb">Norsk</option>
    </select>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { setLocale, SUPPORTED_LOCALES } from '@/i18n'

const { locale } = useI18n()
const currentLocale = ref(locale.value)

onMounted(() => {
  currentLocale.value = locale.value
})

const changeLocale = () => {
  setLocale(currentLocale.value)
}
</script>

<style scoped>
.language-switcher {
  display: inline-block;
  margin-left: 10px;
}

.locale-select {
  padding: 5px;
  border-radius: 4px;
  border: 1px solid #ccc;
  background-color: white;
  font-size: 14px;
  cursor: pointer;
}
</style>
