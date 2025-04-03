import { mount, flushPromises } from '@vue/test-utils'
import MyWishlistView from '@/views/profile/MyWishlistView.vue' // Adjust path if needed
import axios from '@/api/axios.ts'
import { expect, vi, describe, it, beforeEach } from 'vitest'

// ✅ Mock axios used in the component
vi.mock('@/api/axios.ts')
const mockedAxios = axios as unknown as { get: any }

describe('MyWishlistView.vue', () => {
  const mockWishlistItems = [
    {
      id: 1,
      title: 'Wishlist Product',
      description_full: 'Nice item',
      category: 'Gadgets',
      location: 'Bergen',
      price: 999,
      seller: 'CoolStore',
      shipping_options: 'Bring',
      images: ['wishlist.jpg'],
      created_at: '2024-03-10',
      updated_at: '2024-03-11',
      isAvailable: true,
    },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders wishlist products after successful API call', async () => {
    mockedAxios.get.mockResolvedValue({ data: mockWishlistItems })

    const wrapper = mount(MyWishlistView, {
      global: {
        stubs: {
          ProductList: {
            name: 'ProductList',
            props: ['items'],
            template: `<div class="mock-product-list">Count: {{ items.length }}</div>`,
          },
        },
      },
    })

    await flushPromises()

    expect(wrapper.text()).toContain('My Wishlist')
    expect(wrapper.text()).toContain('Count: 1')
    expect(mockedAxios.get).toHaveBeenCalledWith('api/items/wishlist')
  })

  it('renders empty wishlist if API returns none', async () => {
    mockedAxios.get.mockResolvedValue({ data: [] })

    const wrapper = mount(MyWishlistView, {
      global: {
        stubs: {
          ProductList: {
            name: 'ProductList',
            props: ['items'],
            template: `<div class="mock-product-list">Count: {{ items.length }}</div>`,
          },
        },
      },
    })

    await flushPromises()
    expect(wrapper.text()).toContain('Count: 0')
  })

  it('handles API error gracefully', async () => {
    const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
    mockedAxios.get.mockRejectedValue(new Error('API crash'))

    const wrapper = mount(MyWishlistView, {
      global: {
        stubs: {
          ProductList: {
            name: 'ProductList',
            props: ['items'],
            template: `<div class="mock-product-list">Count: {{ items.length }}</div>`,
          },
        },
      },
    })

    await flushPromises()
    expect(wrapper.text()).toContain('Count: 0')
    errorSpy.mockRestore()
  })
})
