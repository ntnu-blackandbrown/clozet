import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import EmailVerification from '@/views/EmailVerification.vue'
import Dashboard from '@/views/Dashboard.vue'
import ForgotPasswordView from '@/views/ForgotPasswordView.vue'
import ResetPasswordView from '@/views/ResetPasswordView.vue'
import UserProfileView from '@/views/UserProfileView.vue'
import LoginRegisterView from '@/views/LoginRegisterView.vue'
import VerifyInfo from '@/views/VerifyInfo.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/verify',
      name: 'verify',
      component: EmailVerification
    },
    {
      path: '/verify-info',
      name: 'verify-info',
      component: VerifyInfo
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: Dashboard,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginRegisterView
    },
    {
      path: '/forgot-password',
      name: 'forgot-password',
      component: ForgotPasswordView
    },
    {
      path: '/reset-password',
      name: 'reset-password',
      component: ResetPasswordView
    },
    {
      path: '/profile',
      name: 'profile',
      component: UserProfileView,
      meta: { requiresAuth: true }
    }
  ]
})

// Navigation guard to check auth for protected routes
router.beforeEach((to, from, next) => {
  const authStore = import.meta.env.VITE_API_BASE_URL + '/api/auth'
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // Check if user is authenticated
    fetch(`${authStore}/me`, { credentials: 'include' })
      .then(response => {
        if (response.ok) {
          next()
        } else {
          next({ name: 'login', query: { redirect: to.fullPath } })
        }
      })
      .catch(error => {
        console.error('Auth check error:', error)
        next({ name: 'login', query: { redirect: to.fullPath } })
      })
  } else {
    next()
  }
})

export default router
