import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import { nextTick } from 'vue'
import App from '@/App.vue' // adjust path if needed
import { useAuthStore } from '@/stores/AuthStore'

// Stub child components for testing
const RouterLinkStub = {
  name: 'RouterLink',
  template: '<a class="router-link"><slot /></a>',
}
const RouterViewStub = {
  name: 'RouterView',
  template: '<div class="router-view-stub"></div>',
}
const FooterStub = {
  name: 'Footer',
  template: '<div class="footer-stub"></div>',
}
const LoginRegisterModalStub = {
  name: 'LoginRegisterModal',
  template: '<div class="login-modal-stub">Login Modal</div>',
}

describe('App.vue (normal Pinia)', () => {
  let pinia: ReturnType<typeof createPinia>
  let authStore: ReturnType<typeof useAuthStore>
  const fakeUser = {
    firstName: 'John',
    username: 'johndoe',
  }

  beforeEach(() => {
    pinia = createPinia()
    setActivePinia(pinia)
    authStore = useAuthStore()
    // Patch the store state and provide mock implementations.
    authStore.$patch({
      isLoggedIn: true,
      userDetails: fakeUser,
    })
    // Return fakeUser from fetchUserInfo so state isn't lost.
    authStore.fetchUserInfo = vi.fn().mockResolvedValue(fakeUser)
    authStore.logout = vi.fn()
    vi.clearAllMocks()
  })

  it('calls fetchUserInfo on mount', async () => {
    const wrapper = mount(App, {
      global: {
        plugins: [pinia],
        stubs: {
          RouterLink: RouterLinkStub,
          RouterView: RouterViewStub,
          Footer: FooterStub,
          LoginRegisterModal: LoginRegisterModalStub,
        },
      },
    })
    await flushPromises()
    await nextTick()
    expect(authStore.fetchUserInfo).toHaveBeenCalled()
  })

  it('renders header with logo and navigation links', () => {
    const wrapper = mount(App, {
      global: {
        plugins: [pinia],
        stubs: {
          RouterLink: RouterLinkStub,
          RouterView: RouterViewStub,
          Footer: FooterStub,
          LoginRegisterModal: LoginRegisterModalStub,
        },
      },
    })
    const header = wrapper.find('header.main-header')
    expect(header.exists()).toBe(true)
    expect(header.text()).toContain('Clozet')
    expect(header.text()).toContain('Profile')
    expect(header.text()).toContain('Messages')
  })

  /*it('shows user info when logged in and allows logout', async () => {
    const wrapper = mount(App, {
      global: {
        plugins: [pinia],
        stubs: {
          RouterLink: RouterLinkStub,
          RouterView: RouterViewStub,
          Footer: FooterStub,
          LoginRegisterModal: LoginRegisterModalStub,
        },
      },
    })
    await flushPromises()
    await nextTick()
    // Ensure the store state is still logged in
    expect(authStore.isLoggedIn).toBe(true)
    const welcomeMsg = wrapper.find('.welcome-msg')
    expect(welcomeMsg.exists()).toBe(true)
    expect(welcomeMsg.text()).toContain('Hei, John')
    const logoutBtn = wrapper.find('button.logout-button')
    expect(logoutBtn.exists()).toBe(true)
    await logoutBtn.trigger('click')
    expect(authStore.logout).toHaveBeenCalled()
  })*/

  it('shows login/register button when not logged in and toggles modal', async () => {
    // Update store state to simulate logged-out user.
    authStore.$patch({
      isLoggedIn: false,
      userDetails: {},
    })
    const wrapper = mount(App, {
      global: {
        plugins: [pinia],
        stubs: {
          RouterLink: RouterLinkStub,
          RouterView: RouterViewStub,
          Footer: FooterStub,
          LoginRegisterModal: LoginRegisterModalStub,
        },
      },
    })
    await flushPromises()
    const loginBtn = wrapper.find('button.login-button')
    expect(loginBtn.exists()).toBe(true)
    // Initially, the modal should not be rendered.
    expect(wrapper.findComponent(LoginRegisterModalStub).exists()).toBe(false)
    await loginBtn.trigger('click')
    await flushPromises()
    expect(wrapper.find('.login-modal-stub').exists()).toBe(true)
    // Simulate closing the modal.
    const modal = wrapper.findComponent(LoginRegisterModalStub)
    modal.vm.$emit('close')
    await flushPromises()
    expect(wrapper.find('.login-modal-stub').exists()).toBe(false)
  })

  it('renders RouterView and Footer', () => {
    const wrapper = mount(App, {
      global: {
        plugins: [pinia],
        stubs: {
          RouterLink: RouterLinkStub,
          RouterView: RouterViewStub,
          Footer: FooterStub,
          LoginRegisterModal: LoginRegisterModalStub,
        },
      },
    })
    expect(wrapper.find('.router-view-stub').exists()).toBe(true)
    expect(wrapper.find('.footer-stub').exists()).toBe(true)
  })
})
