import { fileURLToPath } from 'node:url'
import { mergeConfig, defineConfig, configDefaults } from 'vitest/config'
import viteConfig from './vite.config'

export default defineConfig(async () => {
  const viteConfigResult = typeof viteConfig === 'function' ? await viteConfig({ mode: 'test', command: 'build' }) : viteConfig

  return mergeConfig(
    viteConfigResult,
    {
      test: {
        environment: 'jsdom',
        exclude: [...configDefaults.exclude, 'e2e/**'],
        root: fileURLToPath(new URL('./', import.meta.url)),
        setupFiles: ['./src/test/setup.ts'],
        hookTimeout: 10000,
      },
    }
  )
})
