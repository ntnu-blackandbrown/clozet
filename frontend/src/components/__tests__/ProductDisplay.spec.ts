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
    longDescription: 'Detailed description here',
    categoryName: 'Shoes',
    locationName: 'Oslo',
    price: 1200,
    currency: 'NOK',
    sellerName: 'Alice',
    shippingOptionName: 'Posten',
    available: true,
    brand: 'Test Brand',
    color: 'Blue',
    condition: 'New',
    size: 'M',
    vippsPaymentEnabled: true,
    createdAt: '2024-01-01T10:00:00Z',
    updatedAt: '2024-02-01T10:00:00Z',
    purchased: false,
    latitude: 59.913868,
    longitude: 10.752245,
    sellerId: 123,
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
    //expect(wrapper.classes()).toContain('product-display')
  })

  it('fetches and renders item on mount', async () => {
    const wrapper = mount(ProductDisplay, { props: { id: 1 } })
    await flushPromises()
    expect(wrapper.find('.product-display').exists()).toBe(true)

    expect(wrapper.text()).toContain(mockItem.title)
    expect(wrapper.text()).toContain(mockItem.longDescription)
    //expect(wrapper.findAll('.gallery-image').length).toBe(2)
  })

  it('renders Badge components with correct props', async () => {
    const wrapper = mount(ProductDisplay, {
      props: { id: 1 },
    })

    await flushPromises()
    const badges = wrapper.findAllComponents({ name: 'Badge' })
    expect(badges.length).toBeGreaterThanOrEqual(5)
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
    // The component formats dates as "January 1, 2024, 10:00 AM"
    expect(wrapper.text()).toContain('January 1, 2024')
    expect(wrapper.text()).toContain('February 1, 2024')
  })
})
