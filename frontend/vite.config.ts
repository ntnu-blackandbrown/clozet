import { fileURLToPath, URL } from 'node:url'

import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  // Load env file based on `mode` in the current directory.
  // Set the third parameter to '' to load all env regardless of the `VITE_` prefix.
  const env = loadEnv(mode, process.cwd(), '')
  const isProd = mode === 'production'

  return {
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
      // Make sure environment is accessible in the code
      'process.env.NODE_ENV': JSON.stringify(isProd ? 'production' : 'development'),
      'process.env.DISABLE_I18N': JSON.stringify(isProd ? 'true' : 'false'),
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
  }
})
