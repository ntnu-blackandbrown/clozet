import { createRouter, createWebHistory, type Router } from 'vue-router'
import { useAuthStore } from '@/stores/AuthStore'
import MessagesView from '../views/MessagesView.vue'

const router: Router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/create-product',
      name: 'create-product',
      component: () => import('@/views/CreateProductView.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/messages',
      name: 'messages',
      component: MessagesView,
      children: [
        {
          path: ':chatId',
          name: 'chat',
          component: MessagesView,
        },
      ],
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/UserProfileView.vue'),
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
    {
      path: '/',
      redirect: '/messages',
    },
  ],
})

router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // If not logged in and store has not been initialized yet, check user status
  if (!authStore.isLoggedIn && !from.name) {
    await authStore.fetchUserInfo()
  }

  // Protected routes logic
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router