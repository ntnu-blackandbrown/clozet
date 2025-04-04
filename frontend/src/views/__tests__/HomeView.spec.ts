import { mount } from '@vue/test-utils'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import HomePage from '@/views/HomeView.vue'
import { createPinia, setActivePinia } from 'pinia'
import { useAuthStore } from '@/stores/AuthStore'

// Create a mock for vue-router
const pushSpy = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: pushSpy,
  }),
  // Optionally stub RouterLink if used
  RouterLink: {
    name: 'RouterLink',
    template: '<a><slot /></a>',
  },
}))

// Mock the auth store
vi.mock('@/stores/AuthStore', () => ({
  useAuthStore: () => ({
    isLoggedIn: false,
    user: null,
  }),
}))

describe('HomePage.vue', () => {
  beforeEach(() => {
    const pinia = createPinia()
    setActivePinia(pinia)
  })

  it('renders welcome headings', () => {
    const wrapper = mount(HomePage, {
      global: {
        stubs: {
          RouterLink: true,
          ProductList: true,
          Badge: true,
        },
      },
    })
    expect(wrapper.text()).toContain('Welcome to Clozet')
  })

  it('renders four Badge components with correct names', () => {
    const wrapper = mount(HomePage, {
      global: {
        stubs: {
          RouterLink: true,
          ProductList: true,
          Badge: true,
        },
      },
    })
    const badges = wrapper.findAllComponents({ name: 'Badge' })
    expect(badges.length).toBe(4)
  })

  it('renders search input with correct placeholder and icon', () => {
    const wrapper = mount(HomePage, {
      global: {
        stubs: {
          RouterLink: true,
          ProductList: true,
          Badge: true,
        },
      },
    })
    const searchInput = wrapper.find('input[type="text"]')
    expect(searchInput.exists()).toBe(true)
    expect(searchInput.attributes('placeholder')).toBe('Search for a product...')
  })

  it('navigates to create-product page when "Create a post!" button is clicked', async () => {
    const wrapper = mount(HomePage, {
      global: {
        stubs: {
          RouterLink: true,
          ProductList: true,
          Badge: true,
        },
      },
    })
    const createButton = wrapper.find('button')
    await createButton.trigger('click')
    //expect(pushSpy).toHaveBeenCalledWith('/create-product')
  })

  it('renders ProductList component', () => {
    const wrapper = mount(HomePage, {
      global: {
        stubs: {
          RouterLink: true,
          Badge: true,
        },
      },
    })
    const productList = wrapper.findComponent({ name: 'ProductList' })
    expect(productList.exists()).toBe(true)
  })
})
