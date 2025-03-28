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
      path: '/profile',
      name: 'profile',
      component: () => import('@/views/UserProfileView.vue'),
      children: [
        {
          path: '',
          redirect: '/profile/settings'
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
      ]
    },
  ],
})

export default router
