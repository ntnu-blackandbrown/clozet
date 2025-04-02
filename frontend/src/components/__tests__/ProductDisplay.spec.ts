import { mount, flushPromises } from '@vue/test-utils'
import ProductDisplay from '@/components/product/ProductDisplay.vue'
import axios from 'axios'
import { describe, expect, it, vi, beforeEach } from 'vitest'
import type { Mocked } from 'vitest'

// Mock axios
vi.mock('axios')
const mockedAxios = axios as Mocked<typeof axios>

describe('ProductDisplay', () => {
  const mockItem = {
    id: 1,
    title: 'Test Product',
    images: ['string', 'string'],
    description_full: 'Detailed description here',
    category: 'Shoes',
    location: 'Oslo',
    price: '1200',
    seller: 'Alice',
    shipping_options: 'Posten',
    status: 'Available',
    created_at: '2024-01-01',
    updated_at: '2024-02-01',
    purchased: false,
  }

  beforeEach(() => {
    mockedAxios.get.mockResolvedValue({ data: mockItem })
  })

  it('mounts and has class "product-display"', async () => {
    const wrapper = mount(ProductDisplay, {
      props: { id: 1 },
      global: {
        stubs: {
          Badge: true,
          WishlistButton: true,
        },
      },
    })

    await flushPromises()
    expect(wrapper.classes()).toContain('product-display')
  })

  it('fetches and renders item on mount', async () => {
    const wrapper = mount(ProductDisplay, { props: { id: 1 } })
    await flushPromises()
    expect(wrapper.find('.product-display').exists()).toBe(true)

    expect(wrapper.text()).toContain(mockItem.title)
    expect(wrapper.text()).toContain(mockItem.description_full)
    expect(wrapper.findAll('.gallery-image').length).toBe(2)
  })

  it('renders Badge components with correct props', async () => {
    const wrapper = mount(ProductDisplay, {
      props: { id: 1 },
    })

    await flushPromises()
    const badges = wrapper.findAllComponents({ name: 'Badge' })
    expect(badges.length).toBeGreaterThanOrEqual(6)
  })

  it('renders WishlistButton with correct props', async () => {
    const wrapper = mount(ProductDisplay, {
      props: { id: 1 },
    })

    await flushPromises()
    const wishlist = wrapper.findComponent({ name: 'WishlistButton' })
    expect(wishlist.exists()).toBe(true)
    expect(wishlist.props('productId')).toBe(mockItem.id)
  })

  it('renders posted and updated dates', async () => {
    const wrapper = mount(ProductDisplay, {
      props: { id: 1 },
    })

    await flushPromises()
    expect(wrapper.text()).toContain('2024-01-01')
    expect(wrapper.text()).toContain('2024-02-01')
  })
})
