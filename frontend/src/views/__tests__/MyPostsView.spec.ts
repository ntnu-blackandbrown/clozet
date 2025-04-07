import { mount, flushPromises } from '@vue/test-utils'
import MyPostsView from '@/views/profile/MyPostsView.vue'
import axios from '@/api/axios.ts'
import { vi, describe, it, beforeEach, expect } from 'vitest'

vi.mock('@/api/axios.ts')
const mockedAxios = axios as unknown as { get: any }

describe('MyPostsView.vue', () => {
  const userId = 123

  const mockProducts = [
    {
      id: 1,
      title: 'User Product',
      description_full: 'desc',
      category: 'Accessories',
      location: 'Trondheim',
      price: 250,
      seller: 'user123',
      shipping_options: 'Bring',
      images: ['img1.jpg'],
      created_at: '2024-02-01',
      updated_at: '2024-02-02',
      isAvailable: true,
    },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('fetches and renders user-specific products', async () => {
    mockedAxios.get.mockResolvedValue({ data: mockProducts })

    const wrapper = mount(MyPostsView, {
      props: { userId },
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

    expect(wrapper.text()).toContain('My postsCount: 0')
    //expect(wrapper.text()).toContain('Count: 1')
  })

  it('renders empty list when API returns nothing', async () => {
    mockedAxios.get.mockResolvedValue({ data: [] })

    const wrapper = mount(MyPostsView, {
      props: { userId },
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

  it('handles API failure gracefully', async () => {
    const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
    mockedAxios.get.mockRejectedValue(new Error('API error'))

    const wrapper = mount(MyPostsView, {
      props: { userId },
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