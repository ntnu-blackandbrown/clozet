import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/product-display',
      name: 'product-display',
      component: () => import('@/components/modals/ProductDisplayModal.vue'),
    },
    {
      path: '/edit-profile',
      name: 'edit-profile',
      component: () => import('@/views/UserProfileView.vue'),
    },
  ],
})

export default router
