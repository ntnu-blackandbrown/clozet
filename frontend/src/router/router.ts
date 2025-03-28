import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/components/Home.vue'),
    },
    {
      path: '/product-display',
      name: 'product-display',
      component: () => import('@/components/modals/ProductDisplayModal.vue'),
    },
    {
      path:'/edit-profile',
      name:'edit-profile',
      component: () => import('@/components/user/UserProfile.vue'),
    }
  ],
})

export default router
