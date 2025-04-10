import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import App from '@/App.vue' // Adjust path to your App component
import { useAuthStore } from '@/stores/AuthStore'
import LoginRegisterModal from '@/views/LoginRegisterView.vue'
import Footer from '@/components/layout/Footer.vue'
import { createMockI18n } from '@/test/i18nMock'

// Mock the child components if desired:
vi.mock('@/views/LoginRegisterView.vue', () => ({
  default: {
    name: 'LoginRegisterModal',
    props: ['initialMode'],
    template: '<div>Mocked LoginRegisterModal. Mode: {{ initialMode }}</div>',
  },
}))

vi.mock('@/components/layout/Footer.vue', () => ({
  default: {
    name: 'Footer',
    template: '<footer>Mocked Footer</footer>',
  },
}))

describe('App.vue', () => {
  let pinia: ReturnType<typeof createPinia>
  let router: ReturnType<typeof createRouter>
  let i18n;

  beforeEach(async () => {
    // Clear mocks and session storage
    vi.clearAllMocks()
    // If you want to ensure a fresh sessionStorage, you can do:
    // vi.stubGlobal('sessionStorage', { getItem: vi.fn(), setItem: vi.fn(), removeItem: vi.fn() })
    // But we can just rely on real sessionStorage for testing if we like.

    // Create fresh Pinia & set active
    pinia = createPinia()
    setActivePinia(pinia)

    // Setup i18n mock
    i18n = createMockI18n()

    // Create a router with minimal routes
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: '/', name: 'Home', component: { template: '<div>Home</div>' } },
        { path: '/login', name: 'Login', component: { template: '<div>Login Page</div>' } },
        {
          path: '/register',
          name: 'Register',
          component: { template: '<div>Register Page</div>' },
        },
        {
          path: '/profile',
          name: 'Profile',
          component: { template: '<div>Profile Page</div>' },
        },
        {
          path: '/messages',
          name: 'Messages',
          component: { template: '<div>Messages Page</div>' },
        },
        {
          path: '/admin',
          name: 'Admin',
          component: { template: '<div>Admin Page</div>' },
        },
        {
          path: '/create-product',
          name: 'CreateProduct',
          component: { template: '<div>Create Product Page</div>' },
        },
      ],
    })
    // Make sure the router is ready before we mount
    await router.isReady()
  })

  it('calls fetchUserInfo if sessionStorage has "user"', async () => {
    // Put a mock user in sessionStorage
    sessionStorage.setItem('user', JSON.stringify({ id: 1, name: 'TestUser' }))

    // Spy on store method
    const authStore = useAuthStore()
    vi.spyOn(authStore, 'fetchUserInfo').mockResolvedValue(undefined)

    // Mount App
    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()

    // fetchUserInfo should have been called
    expect(authStore.fetchUserInfo).toHaveBeenCalled()
    // Clean up
    sessionStorage.removeItem('user')
  })

  it('shows login modal if the route is /login on mount', async () => {
    router.push('/login')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()
    // The component sets showLoginModal => true if path === /login
    expect(wrapper.vm.showLoginModal).toBe(true)
    expect(wrapper.vm.initialAuthMode).toBe('login')
    // The login modal should appear in the DOM
    expect(wrapper.findComponent(LoginRegisterModal).exists()).toBe(true)
  })

  it('shows register modal if the route is /register on mount', async () => {
    router.push('/register')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()
    expect(wrapper.vm.showLoginModal).toBe(true)
    expect(wrapper.vm.initialAuthMode).toBe('register')
    expect(wrapper.findComponent(LoginRegisterModal).exists()).toBe(true)
  })

  it('hides modal on route change away from /login or /register', async () => {
    // Start from /login so the modal is visible
    router.push('/login')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()
    expect(wrapper.vm.showLoginModal).toBe(true)

    // Now navigate to home
    router.push('/')
    await flushPromises()

    expect(wrapper.vm.showLoginModal).toBe(false)
    // The login modal should be removed
    expect(wrapper.findComponent(LoginRegisterModal).exists()).toBe(false)
  })

  it('handles handleLoginClick correctly: sets modal to open, route -> /login', async () => {
    router.push('/')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })
    await flushPromises()

    // The login button is in the template if not logged in
    // We'll simulate a user not logged in
    const authStore = useAuthStore()
    ;(authStore as any).isLoggedIn = false

    // Find the login button (desktop or mobile)
    const loginBtn = wrapper.find('.login-btn')
    expect(loginBtn.exists()).toBe(true)

    // Click it
    await loginBtn.trigger('click')
    await flushPromises()

    // Should open modal, route changed to /login
    expect(wrapper.vm.showLoginModal).toBe(true)
    expect(wrapper.vm.initialAuthMode).toBe('login')
    expect(router.currentRoute.value.path).toBe('/login')
  })

  it('handles logout: calls store.logout and navigates to home', async () => {
    // Force user logged in
    const authStore = useAuthStore()
    ;(authStore as any).isLoggedIn = true
    vi.spyOn(authStore, 'logout').mockResolvedValue({ success: true, message: 'Logged out successfully' })

    router.push('/profile')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })
    await flushPromises()

    const logoutBtn = wrapper.find('.logout-btn')
    expect(logoutBtn.exists()).toBe(true)

    await logoutBtn.trigger('click')
    await flushPromises()

    // Check that store.logout is called
    expect(authStore.logout).toHaveBeenCalled()
    // Router should have navigated to '/'
    expect(router.currentRoute.value.path).toBe('/')
  })

  it('shows admin dashboard link if user is ADMIN', async () => {
    const authStore = useAuthStore()
    ;(authStore as any).isLoggedIn = true
    ;(authStore as any).userDetails = { role: 'ADMIN' } as any

    router.push('/')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })
    await flushPromises()

    // Check for "Admin Dashboard" link
    const adminLink = wrapper.find('.admin-link')
    expect(adminLink.exists()).toBe(true)
    expect(adminLink.text()).toBe('Admin Dashboard')
  })

  it('shows normal user links if user is not ADMIN', async () => {
    const authStore = useAuthStore()
    ;(authStore as any).isLoggedIn = true
    ;(authStore as any).userDetails = { role: 'USER' } as any

    router.push('/')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })
    await flushPromises()

    // Should not find admin link
    expect(wrapper.find('.admin-link').exists()).toBe(false)
    // Should see Profile, Messages, Sell Items
    expect(wrapper.text()).toContain('Profile')
    expect(wrapper.text()).toContain('Messages')
    expect(wrapper.text()).toContain('Sell Items')
  })

  it('toggles mobile menu open/close on hamburger click', async () => {
    router.push('/')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })
    await flushPromises()

    // Initially mobileMenuOpen should be false
    expect(wrapper.vm.mobileMenuOpen).toBe(false)

    // The hamburger button
    const hamburgerBtn = wrapper.find('.hamburger-menu-btn')
    expect(hamburgerBtn.exists()).toBe(true)

    // Click to open
    await hamburgerBtn.trigger('click')
    expect(wrapper.vm.mobileMenuOpen).toBe(true)

    // Click to close
    await hamburgerBtn.trigger('click')
    expect(wrapper.vm.mobileMenuOpen).toBe(false)
  })

  it('closes mobile menu upon route change', async () => {
    router.push('/')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })
    await flushPromises()

    // Open the mobile menu
    wrapper.vm.mobileMenuOpen = true
    await flushPromises()
    expect(wrapper.vm.mobileMenuOpen).toBe(true)

    // Navigate somewhere
    router.push('/profile')
    await flushPromises()

    // Expect it to close
    expect(wrapper.vm.mobileMenuOpen).toBe(false)
  })

  it('closes login modal when handleCloseAuthModal is called', async () => {
    // Start with login modal open
    router.push('/login')
    await router.isReady()

    const wrapper = mount(App, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })
    await flushPromises()

    // Confirm it's open
    expect(wrapper.vm.showLoginModal).toBe(true)
    // The modal is visible
    const modal = wrapper.findComponent(LoginRegisterModal)
    expect(modal.exists()).toBe(true)

    // Trigger @close
    await modal.vm.$emit('close')
    await flushPromises()

    // The modal should be closed, and route replaced with '/'
    expect(wrapper.vm.showLoginModal).toBe(false)
    expect(router.currentRoute.value.path).toBe('/')
  })
})
