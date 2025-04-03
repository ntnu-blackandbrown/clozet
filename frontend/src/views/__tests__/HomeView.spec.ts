import { mount } from '@vue/test-utils'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import HomePage from '@/views/HomeView.vue' // Adjust path if needed

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

describe('HomePage.vue', () => {
  beforeEach(() => {
    pushSpy.mockClear()
  })

  it('renders welcome headings', () => {
    const wrapper = mount(HomePage, {
      global: {
        stubs: {
          // Stubbing child components with minimal output
          Badge: true,
          ProductList: true,
        },
      },
    })
    expect(wrapper.text()).toContain('Welcome to Clozet!')
    expect(wrapper.text()).toContain('The new way to shop for clothes')
  })

  it('renders four Badge components with correct names', () => {
    // Provide a custom stub to capture props for Badge
    const BadgeStub = {
      name: 'Badge',
      props: ['type', 'name'],
      template: `<div class="badge-stub">{{ name }}</div>`,
    }
    const wrapper = mount(HomePage, {
      global: {
        stubs: {
          Badge: BadgeStub,
          ProductList: true,
        },
      },
    })
    const badges = wrapper.findAllComponents({ name: 'Badge' })
    expect(badges.length).toBe(4)
    const badgeNames = badges.map(badge => badge.text())
    expect(badgeNames).toEqual(['Tops', 'Bottoms', 'Dresses', 'Accessories'])
  })

  it('renders search input with correct placeholder and icon', () => {
    const wrapper = mount(HomePage, {
      global: {
        stubs: {
          Badge: true,
          ProductList: true,
        },
      },
    })
    const input = wrapper.find('input.search-bar')
    expect(input.exists()).toBe(true)
    expect(input.attributes('placeholder')).toBe('Search for a product...')
    const svg = wrapper.find('svg.search-icon')
    expect(svg.exists()).toBe(true)
  })

  it('navigates to create-product page when "Create a post!" button is clicked', async () => {
    const wrapper = mount(HomePage, {
      global: {
        stubs: {
          Badge: true,
          ProductList: true,
        },
      },
    })
    const button = wrapper.find('button')
    await button.trigger('click')
    expect(pushSpy).toHaveBeenCalledWith('/create-product')
  })

  it('renders ProductList component', () => {
    const ProductListStub = {
      name: 'ProductList',
      template: '<div class="product-list-stub"></div>',
    }
    const wrapper = mount(HomePage, {
      global: {
        stubs: {
          Badge: true,
          ProductList: ProductListStub,
        },
      },
    })
    const productList = wrapper.findComponent({ name: 'ProductList' })
    expect(productList.exists()).toBe(true)
  })
})
