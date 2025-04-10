import { fileURLToPath, URL } from 'node:url'
import { resolve } from 'node:path'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'
import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
    // Add Vue I18n plugin with improved configuration
    VueI18nPlugin({
      include: resolve(__dirname, './src/locales/**'),
      runtimeOnly: false,
      compositionOnly: false,
      fullInstall: true,
      forceStringify: true, // Ensures locale messages are converted to strings
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  define: {
    global: 'globalThis', // ✅ Fix for SockJS "global is not defined" error
    __VUE_I18N_FULL_INSTALL__: true,
    __VUE_I18N_LEGACY_API__: false,
    __INTLIFY_PROD_DEVTOOLS__: false,
  },
  // Ensure JSON files are properly processed and included in build
  assetsInclude: ['**/*.json'],
  build: {
    // Ensure Netlify preserves the file structure
    assetsInlineLimit: 0,
    rollupOptions: {
      // Make sure locale files are processed properly
      input: {
        main: resolve(__dirname, 'index.html'),
        // Include all locale files explicitly
        en: resolve(__dirname, 'src/locales/en.json'),
        nb: resolve(__dirname, 'src/locales/nb.json'),
        es: resolve(__dirname, 'src/locales/es.json'),
      }
    }
  }
})
