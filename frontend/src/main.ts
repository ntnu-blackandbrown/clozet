import { createApp } from 'vue'
import { createPinia } from 'pinia'
// Import removed to disable localStorage persistence
// import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

import App from './App.vue'
import router from './router/router'
import i18n from './i18n'

const app = createApp(App)
const pinia = createPinia()

// Disabled localStorage persistence for security
// pinia.use(piniaPluginPersistedstate)

app.use(pinia)
app.use(router)
app.use(i18n)

app.mount('#app')
