import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import App from './App.vue'
import router from './router/router'
import i18n from './i18n'
import mockI18nPlugin from './components/utils/MockI18n'

const app = createApp(App)
const pinia = createPinia()

pinia.use(piniaPluginPersistedstate)

app.use(pinia)
app.use(router)

// Use real i18n in development, mock in production
if (process.env.NODE_ENV !== 'production' || process.env.DISABLE_I18N !== 'true') {
  console.log('Using full i18n implementation')
  app.use(i18n)
} else {
  console.log('Using mock i18n implementation (keys only)')
  app.use(mockI18nPlugin)
}

app.mount('#app')
