import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  define: {
    global: 'globalThis' // ✅ Fix for SockJS "global is not defined" error
  },
  // Ensure JSON files are properly processed and included in build
  assetsInclude: ['**/*.json'],
  build: {
    // Ensure Netlify preserves the file structure
    assetsInlineLimit: 0
  }
})
