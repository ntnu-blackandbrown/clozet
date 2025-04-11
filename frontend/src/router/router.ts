import { createRouter, createWebHistory } from 'vue-router'
import MessagesView from '../views/user/MessagesView.vue'
import { useAuthStore } from '@/stores/AuthStore'
import NotFoundView from '@/views/NotFoundView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/user/HomeView.vue'),
    },
    {
      path: '/reset-password',
      name: 'reset-password',
      component: () => import('@/views/verification/ResetPasswordVerifier.vue'),
    },
    {
      path: '/verify',
      name: 'registration-verifier',
      component: () => import('@/views/verification/RegisterationVerifier.vue'),
    },
    {
      path: '/products/:id',
      name: 'product-detail',
      component: () => import('@/views/user/HomeView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/user/HomeView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/user/HomeView.vue'),
    },
    {
      path: '/create-product',
      name: 'create-product',
      component: () => import('@/views/user/CreateProductView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/product/edit/:id',
      name: 'edit-product',
      component: () => import('@/views/user/CreateProductView.vue'),
      props: (route) => ({ id: parseInt(route.params.id as string) }),
      meta: { requiresAuth: true },
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
      component: () => import('@/views/user/UserProfileView.vue'),
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
          path: 'posts/:id',
          name: 'my-posts-detail',
          component: () => import('@/views/profile/MyPostsView.vue'),
        },
        {
          path: 'wishlist',
          name: 'my-wishlist',
          component: () => import('@/views/profile/MyWishlistView.vue'),
        },
        {
          path: 'wishlist/:id',
          name: 'my-wishlist-detail',
          component: () => import('@/views/profile/MyWishlistView.vue'),
        },
        {
          path: 'purchases',
          name: 'my-purchases',
          component: () => import('@/views/profile/MyPurchasesView.vue'),
        },
        {
          path: 'change-password',
          name: 'change-password',
          component: () => import('@/views/profile/ChangePasswordView.vue'),
        },
      ],
    },
    // Admin dashboard routes
    {
      path: '/admin',
      name: 'admin',
      component: () => import('@/views/admin/AdminDashboard.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        {
          path: '',
          redirect: '/admin/overview',
        },
        {
          path: 'overview',
          name: 'admin-overview',
          component: () => import('@/views/admin/AdminOverview.vue'),
        },
        {
          path: 'categories',
          name: 'admin-categories',
          component: () => import('@/views/admin/categories/CategoryManagement.vue'),
        },
        {
          path: 'locations',
          name: 'admin-locations',
          component: () => import('@/views/admin/locations/LocationManagement.vue'),
        },
        {
          path: 'shipping',
          name: 'admin-shipping',
          component: () => import('@/views/admin/shipping/ShippingManagement.vue'),
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('@/views/admin/users/UserManagement.vue'),
        },
        {
          path: 'transactions',
          name: 'admin-transactions',
          component: () => import('@/views/admin/transactions/TransactionManagement.vue'),
        },
      ],
    },
    // Catch-all 404 route - MUST BE LAST
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: NotFoundView,
    },
  ],
})

// Navigation guard
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()

  // Check if route requires authentication
  if (to.meta.requiresAuth) {
    // Verify authentication status
    const isAuthenticated = await authStore.checkAuth()

    if (!isAuthenticated) {
      // Store the intended destination for redirect after login
      return next({
        path: '/login',
        query: { redirect: to.fullPath },
      })
    }

    // Check if admin access is required
    if (to.meta.requiresAdmin && authStore.userDetails?.role !== 'ADMIN') {
      return next('/')
    }
  }

  // Handle redirecting from login page when already authenticated
  if ((to.name === 'login' || to.name === 'register') && authStore.isLoggedIn) {
    return next('/')
  }

  next()
})

export default router
