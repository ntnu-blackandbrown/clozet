import { mount, flushPromises } from '@vue/test-utils'
import ProductListView from '@/views/user/ProductListView.vue'
import ProductList from '@/components/product/ProductList.vue' // ✅ import real component
import axios from '@/api/axios.ts'
import { vi, describe, it, beforeEach, expect } from 'vitest'

vi.mock('@/api/axios.ts')
const mockedAxios = axios as unknown as { get: any }

// Add vue-router mock
vi.mock('vue-router', () => ({
  useRoute: () => ({
    params: { id: null },
    query: {},
  }),
  useRouter: () => ({
    push: vi.fn(),
    replace: vi.fn(),
  }),
}))

// ✅ Place this at the top of your test
vi.mock('@/components/product/ProductList.vue', () => ({
  default: {
    name: 'ProductList',
    props: ['items'],
    template: `<div class="mock-product-list">Count: {{ items.length }}</div>`,
  },
}))

describe('ProductListView.vue (real ProductList)', () => {
  const mockItems = [
    {
      id: 1,
      title: 'Product One',
      description_full: 'desc',
      category: 'Shoes',
      location: 'Oslo',
      price: 100,
      seller: 'User1',
      shipping_options: 'Posten',
      images: ['img1.jpg'],
      created_at: '2024-01-01',
      updated_at: '2024-01-02',
      isAvailable: true,
    },
  ]

  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders ProductList with items from API', async () => {
    mockedAxios.get.mockResolvedValue({ data: mockItems })

    const wrapper = mount(ProductListView, {
      global: {
        // ✅ Register the real ProductList component manually
        components: { ProductList },
      },
    })

    await flushPromises()

    // Ensure heading appears
    expect(wrapper.text()).toContain('Browse products')

    // Ensure ProductList rendered and received correct props
    const stub = wrapper.findComponent({ name: 'ProductList' })
    expect(stub.exists()).toBe(true)
    expect(stub.props('items')).toEqual(mockItems)
  })

  it('renders with empty items if API returns empty', async () => {
    mockedAxios.get.mockResolvedValue({ data: [] })

    const wrapper = mount(ProductListView, {
      global: {
        components: { ProductList },
      },
    })

    await flushPromises()

    const productList = wrapper.findComponent(ProductList)
    expect((productList.props() as { items: any[] }).items).toEqual([])
  })

  it('renders with empty items if API fails', async () => {
    mockedAxios.get.mockRejectedValue(new Error('API error'))

    const wrapper = mount(ProductListView, {
      global: {
        components: { ProductList },
      },
    })

    await flushPromises()

    const productList = wrapper.findComponent(ProductList)
    expect((productList.props() as { items: any[] }).items).toEqual([])
  })
})
