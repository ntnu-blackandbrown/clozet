import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue({
      script: {
        // Prevent importing .json from being converted to URL
        refTransform: false
      }
    }),
    vueJsx(),
    vueDevTools(),
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
  json: {
    stringify: false, // Don't stringify JSON files during build
    namedExports: true,
  },
  build: {
    // Make sure source maps are generated
    sourcemap: true,
    // Optimize dependency bundling
    commonjsOptions: {
      transformMixedEsModules: true,
    }
  }
})
