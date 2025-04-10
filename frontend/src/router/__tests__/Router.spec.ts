import { nextTick } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import router from '@/router/router.js'

// --- Mock AuthStore ---
// We replace the useAuthStore implementation so we can control its return value.
let fakeAuthStore: any

vi.mock('@/stores/AuthStore', () => ({
  useAuthStore: () => fakeAuthStore,
}))

describe('Router Navigation Guard', () => {
  beforeEach(() => {
    // For each test, reset the fake auth store to default: logged out.
    fakeAuthStore = {
      user: null,
      isLoggedIn: false,
      userDetails: {},
      checkAuth: async () => !!fakeAuthStore.user
    }
    // Reset router to initial state by pushing to home (assuming home is public)
    router.push({ name: 'home' })
  })

  it('redirects to "/" when accessing a protected route (requiresAuth) while not logged in', async () => {
    // Attempt to navigate to the messages route which requires authentication.
    await router.push({ name: 'messages' })
    // Wait for the router to process the navigation guard.
    await nextTick()
    // Expect redirection to the home route.
    expect(router.currentRoute.value.name).toBe('home')
  })

  it('redirects to "/" when accessing an admin route while not an admin', async () => {
    // Simulate a logged in user, but with non-admin privileges.
    fakeAuthStore.user = { id: 2 }
    fakeAuthStore.isLoggedIn = true
    fakeAuthStore.userDetails = { role: 'USER' } // Not admin

    // Attempt to navigate to an admin route.
    await router.push({ name: 'admin' })
    await nextTick()
    // Expect redirection to home.
    expect(router.currentRoute.value.name).toBe('home')
  })

  it('allows navigation to admin routes when user is an admin', async () => {
    // Simulate a logged in admin user.
    fakeAuthStore.user = { id: 2 }
    fakeAuthStore.isLoggedIn = true
    fakeAuthStore.userDetails = { role: 'ADMIN' }

    // Navigate to an admin route.
    await router.push({ name: 'admin' })
    await nextTick()
    // Expect navigation to succeed.
    expect(router.currentRoute.value.name).toBe('admin')
  })

  it('allows navigation to public routes without authentication', async () => {
    // User is not logged in.
    fakeAuthStore.user = null
    fakeAuthStore.isLoggedIn = false

    // Navigate to the home route.
    await router.push({ name: 'home' })
    await nextTick()
    expect(router.currentRoute.value.name).toBe('home')
  })
})
