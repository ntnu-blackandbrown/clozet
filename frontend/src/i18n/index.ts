import { createI18n } from 'vue-i18n'
import en from '@/locales/en.json'
import nb from '@/locales/nb.json'

// Supported locales
export const SUPPORTED_LOCALES = ['en', 'nb']

// Default locale
const DEFAULT_LOCALE = 'en'

// Get browser locale or fallback to default
const getBrowserLocale = (): string => {
  const navigatorLocale = navigator.language.split('-')[0]
  return SUPPORTED_LOCALES.includes(navigatorLocale) ? navigatorLocale : DEFAULT_LOCALE
}

// Try to get the locale from localStorage, then browser, then default
const getInitialLocale = (): string => {
  const savedLocale = localStorage.getItem('locale')
  if (savedLocale && SUPPORTED_LOCALES.includes(savedLocale)) {
    return savedLocale
  }
  return getBrowserLocale()
}

// Create i18n instance
const i18n = createI18n({
  legacy: false, // you must set `false`, to use Composition API
  locale: getInitialLocale(),
  fallbackLocale: DEFAULT_LOCALE,
  messages: {
    en,
    nb
  },
  globalInjection: true, // Adds $t, $tc, etc to all components
  missingWarn: false, // Disable warnings for missing translations in production
  fallbackWarn: false // Disable warnings for fallback translations in production
})

// Helper function to change locale
export const setLocale = (locale: string): void => {
  if (SUPPORTED_LOCALES.includes(locale)) {
    i18n.global.locale.value = locale
    localStorage.setItem('locale', locale)
    document.querySelector('html')?.setAttribute('lang', locale)
  }
}

// Initialize HTML lang attribute
document.querySelector('html')?.setAttribute('lang', getInitialLocale())

export default i18n
