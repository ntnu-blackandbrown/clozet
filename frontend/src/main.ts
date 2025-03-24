//import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
//import router from './router'
//import LoginRegisterModal from './components/LoginRegisterModal.vue'
const app = createApp(App)

//const app = createApp(LoginRegisterModal)

app.use(createPinia())
//app.use(router)

app.mount('#app')
