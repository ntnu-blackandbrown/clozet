import { createI18n } from 'vue-i18n'
import en from '@/locales/en.json'
import nb from '@/locales/nb.json'
import es from '@/locales/es.json'

// Supported locales
export const SUPPORTED_LOCALES = ['en', 'nb', 'es'] as const
export type SupportedLocale = typeof SUPPORTED_LOCALES[number]

// Default locale
const DEFAULT_LOCALE: SupportedLocale = 'en'

// Get browser locale or fallback to default
const getBrowserLocale = (): SupportedLocale => {
  try {
    const navigatorLocale = navigator.language.split('-')[0]
    console.log('Browser detected locale:', navigatorLocale)
    return SUPPORTED_LOCALES.includes(navigatorLocale as SupportedLocale)
      ? navigatorLocale as SupportedLocale
      : DEFAULT_LOCALE
  } catch (e) {
    console.error('Error detecting browser locale:', e)
    return DEFAULT_LOCALE
  }
}

// Try to get the locale from localStorage, then browser, then default
const getInitialLocale = (): SupportedLocale => {
  try {
    const savedLocale = localStorage.getItem('locale')
    console.log('Saved locale from localStorage:', savedLocale)

    if (savedLocale && SUPPORTED_LOCALES.includes(savedLocale as SupportedLocale)) {
      console.log('Using saved locale:', savedLocale)
      return savedLocale as SupportedLocale
    }

    const browserLocale = getBrowserLocale()
    console.log('Using browser locale:', browserLocale)
    return browserLocale
  } catch (e) {
    console.error('Error getting initial locale:', e)
    return DEFAULT_LOCALE
  }
}

const initialLocale = getInitialLocale()
console.log('Initial locale set to:', initialLocale)

// Hard-code the translations to avoid build issues
const messages = {
  en,
  nb,
  es
}

// Create i18n instance
const i18n = createI18n({
  legacy: false,
  locale: initialLocale,
  fallbackLocale: DEFAULT_LOCALE,
  messages,
  globalInjection: true,
  missingWarn: process.env.NODE_ENV === 'development',
  fallbackWarn: process.env.NODE_ENV === 'development',
  silentTranslationWarn: process.env.NODE_ENV !== 'development',
  silentFallbackWarn: process.env.NODE_ENV !== 'development',
})

// Helper function to change locale
export const setLocale = (locale: SupportedLocale): void => {
  if (SUPPORTED_LOCALES.includes(locale)) {
    console.log('Changing locale to:', locale)
    i18n.global.locale.value = locale
    localStorage.setItem('locale', locale)
    document.querySelector('html')?.setAttribute('lang', locale)
  }
}

// Initialize HTML lang attribute
document.querySelector('html')?.setAttribute('lang', initialLocale)

export default i18n
