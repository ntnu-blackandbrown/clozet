import { createRouter, createWebHistory } from 'vue-router'
import MessagesView from '../views/MessagesView.vue'
import { useAuthStore } from '@/stores/AuthStore'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/products/:id',
      name: 'product-detail',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/create-product',
      name: 'create-product',
      component: () => import('@/views/CreateProductView.vue'),
    },
    {
      path: '/messages/:chatId?',
      name: 'messages',
      component: MessagesView,
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/UserProfileView.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/profile/settings',
        },
        {
          path: 'settings',
          name: 'profile-settings',
          component: () => import('@/views/profile/MySettingsView.vue'),
        },
        {
          path: 'posts',
          name: 'my-posts',
          component: () => import('@/views/profile/MyPostsView.vue'),
        },
        {
          path: 'wishlist',
          name: 'my-wishlist',
          component: () => import('@/views/profile/MyWishlistView.vue'),
        },
        {
          path: 'purchases',
          name: 'my-purchases',
          component: () => import('@/views/profile/MyPurchasesView.vue'),
        },
      ],
    },
  ],
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    // Redirect to home page if trying to access protected route while not logged in
    next('/')
  } else {
    next()
  }
})

export default router
