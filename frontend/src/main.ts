//import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
//import router from './router'
//import LoginRegisterModal from './components/LoginRegisterModal.vue'
//import ProductDisplay from './components/ProductDisplay.vue'
const app = createApp(App)
//const app = createApp(LoginRegisterModal)
//const app = createApp(ProductDisplay)
app.use(createPinia())
//app.use(router)

app.mount('#app')
