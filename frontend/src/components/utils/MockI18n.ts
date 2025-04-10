/**
 * MockI18n - A simple mock implementation for vue-i18n in production
 *
 * This mock simply returns the translation key as-is instead of looking up translations.
 * It implements the same interface as vue-i18n's $t function to make the switch seamless.
 */

// Simple mock for $t function that just returns the key in production
export const $t = (key: string, ...args: any[]): string => {
  // Just extract and return the key
  return key
}

// Export a plugin to register the mock globally
export const mockI18nPlugin = {
  install: (app: any) => {
    // Add the mock $t function to the global properties
    app.config.globalProperties.$t = $t

    // Also make it available in the composition API
    app.provide('i18n', {
      t: $t,
      locale: { value: 'en' },
      availableLocales: ['en'],
      global: {
        t: $t,
        locale: { value: 'en' }
      }
    })
  }
}

export default mockI18nPlugin
