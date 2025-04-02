import { createRouter, createWebHistory } from 'vue-router'
import LoginRegisterView from '@/views/LoginRegisterView.vue'


const routes = [
  { path: '/', name: 'Home', component: LoginRegisterView },
  { path: '/login', name: 'Login', component: LoginRegisterView },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
