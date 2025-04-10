import { createI18n } from 'vue-i18n'
import { fetchLocale, LocaleMessages } from '../utils/fallbackLocale'

// Try to import locale messages directly for production build
// Fallback to empty objects if import fails, we'll load them dynamically
let en: LocaleMessages, nb: LocaleMessages, es: LocaleMessages;
try {
  en = require('../locales/en.json');
  nb = require('../locales/nb.json');
  es = require('../locales/es.json');
} catch (error) {
  console.warn('Could not import locale files directly, will try dynamic loading', error);
  en = {};
  nb = {};
  es = {};
}

// Supported locales
export const SUPPORTED_LOCALES = ['en', 'nb', 'es'] as const
export type SupportedLocale = typeof SUPPORTED_LOCALES[number]

// Default locale
const DEFAULT_LOCALE: SupportedLocale = 'en'

// Load messages with explicit imports to ensure they're included in the bundle
const messages = {
  en,
  nb,
  es
}

// Get browser locale or fallback to default
const getBrowserLocale = (): SupportedLocale => {
  const navigatorLocale = navigator.language.split('-')[0]
  console.log('Browser detected locale:', navigatorLocale)
  return SUPPORTED_LOCALES.includes(navigatorLocale as SupportedLocale)
    ? navigatorLocale as SupportedLocale
    : DEFAULT_LOCALE
}

// Try to get the locale from localStorage, then browser, then default
const getInitialLocale = (): SupportedLocale => {
  const savedLocale = localStorage.getItem('locale')
  console.log('Saved locale from localStorage:', savedLocale)

  if (savedLocale && SUPPORTED_LOCALES.includes(savedLocale as SupportedLocale)) {
    console.log('Using saved locale:', savedLocale)
    return savedLocale as SupportedLocale
  }

  const browserLocale = getBrowserLocale()
  console.log('Using browser locale:', browserLocale)
  return browserLocale
}

const initialLocale = getInitialLocale()
console.log('Initial locale set to:', initialLocale)

// Create i18n instance with explicit messages
const i18n = createI18n({
  legacy: false, // you must set `false`, to use Composition API
  locale: initialLocale,
  fallbackLocale: DEFAULT_LOCALE,
  // Use explicit messages for production reliability
  messages,
  globalInjection: true, // Adds $t, $tc, etc to all components
  missingWarn: false, // Disable warnings for missing translations in production
  fallbackWarn: false // Disable warnings for fallback translations in production
})

// Helper function to change locale
export const setLocale = async (locale: SupportedLocale): Promise<void> => {
  if (SUPPORTED_LOCALES.includes(locale)) {
    console.log('Changing locale to:', locale)

    // Try to dynamically load locale if it's not already loaded or empty
    if (!i18n.global.getLocaleMessage(locale) ||
        Object.keys(i18n.global.getLocaleMessage(locale)).length === 0) {
      try {
        const messages: LocaleMessages = await fetchLocale(locale);
        i18n.global.setLocaleMessage(locale, messages);
        console.log(`Dynamically loaded locale: ${locale}`);
      } catch (error: unknown) {
        console.error(`Failed to dynamically load locale: ${locale}`, error);
      }
    }

    i18n.global.locale.value = locale
    localStorage.setItem('locale', locale)
    document.querySelector('html')?.setAttribute('lang', locale)
  }
}

// Initialize HTML lang attribute
document.querySelector('html')?.setAttribute('lang', initialLocale)

// If the current locale messages are empty, try to load them
if (Object.keys(i18n.global.getLocaleMessage(initialLocale)).length === 0) {
  fetchLocale(initialLocale).then((messages: LocaleMessages) => {
    i18n.global.setLocaleMessage(initialLocale, messages);
    console.log(`Dynamically loaded initial locale: ${initialLocale}`);
  }).catch((error: unknown) => {
    console.error(`Failed to dynamically load initial locale: ${initialLocale}`, error);
  });
}

export default i18n
