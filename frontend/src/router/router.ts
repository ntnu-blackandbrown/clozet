import { createRouter, createWebHistory } from 'vue-router'
import LoginRegisterView from '@/views/LoginRegisterView.vue'
import VerifyInfo from '@/views/VerifyInfo.vue'
import EmailVerification from '@/views/EmailVerification.vue'
import Dashboard from '@/views/Dashboard.vue'

const routes = [
  { path: '/', name: 'Home', component: LoginRegisterView },
  { path: '/login', name: 'Login', component: LoginRegisterView },
  { path: '/verify-info', name: 'VerifyInfo', component: VerifyInfo },
  { path: '/verify', name: 'EmailVerification', component: EmailVerification },
  { path: '/dashboard', name: 'Dashboard', component: Dashboard },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
