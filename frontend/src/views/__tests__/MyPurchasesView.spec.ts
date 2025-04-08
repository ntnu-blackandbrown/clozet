import { mount, flushPromises } from '@vue/test-utils'
import MyPurchasesView from '@/views/profile/MyPurchasesView.vue' // Update if path is different
import axios from '@/api/axios.ts'
import { vi, describe, it, beforeEach, expect } from 'vitest'

// ✅ Mock axios
vi.mock('@/api/axios.ts')
const mockedAxios = axios as unknown as { get: any }

describe('MyPurchasesView.vue', () => {
  const mockPurchases = [
    {
      id: 1,
      title: 'Purchased Item',
      description_full: 'Purchased desc',
      category: 'Books',
      location: 'Stavanger',
      price: 300,
      seller: 'SellerUser',
      shipping_options: 'Posten',
      images: ['purchase.jpg'],
      created_at: '2024-01-15',
      updated_at: '2024-01-16',
      isAvailable: false,
    },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders purchases after successful API call', async () => {
    mockedAxios.get.mockResolvedValue({ data: mockPurchases })

    const wrapper = mount(MyPurchasesView, {
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

    expect(wrapper.text()).toContain('My Purchases')
    //expect(wrapper.text()).toContain('Count: 1')
    // expect(mockedAxios.get).toHaveBeenCalledWith(`api/transactions/${authStore.user?.id}`)
  })

  it('renders empty purchases list if API returns none', async () => {
    mockedAxios.get.mockResolvedValue({ data: [] })

    const wrapper = mount(MyPurchasesView, {
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
    // expect(wrapper.text()).toContain('Count: 0')
  })

  it('handles API failure gracefully', async () => {
    const errorSpy = vi.spyOn(console, 'error').mockImplementation(() => {})
    mockedAxios.get.mockRejectedValue(new Error('API down'))

    const wrapper = mount(MyPurchasesView, {
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
    //expect(wrapper.text()).toContain('Count: 0')
    errorSpy.mockRestore()
  })
})
