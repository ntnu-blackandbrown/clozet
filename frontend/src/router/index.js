import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import LoginRegisterView from '@/views/LoginRegisterView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/login',
      name: 'login',
      component: LoginRegisterView
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
