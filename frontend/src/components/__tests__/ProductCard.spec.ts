import { shallowMount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import ProductCard from '@/components/product/ProductCard.vue'
import type { Product } from '@/types/product'
describe('ProductCard.vue', () => {
  const factory = (propsData: any) => {
    return shallowMount(ProductCard, {
      props: propsData,
      global: {
        stubs: {
          WishlistButton: true,
          Badge: true,
        },
      },
    })
  }

  it('renders product details correctly when available', () => {
    const wrapper = factory({
      id: 1,
      title: 'Test Product',
      price: 100,
      category: 'Electronics',
      image: 'http://example.com/image.jpg',
      location: 'Oslo',
      isVippsPaymentEnabled: true,
      isWishlisted: false,
      isAvailable: true,
    })

    // Verify that the product image is rendered with the correct attributes
    const img = wrapper.find('img')
    expect(img.attributes('src')).toBe('http://example.com/image.jpg')
    expect(img.attributes('alt')).toBe('Test Product')

    // Verify that the product title is present (the template displays title)
    expect(wrapper.text()).toContain('Test Product')

    // Verify that the button displays "View Details" when available
    const button = wrapper.find('button.view-details')
    expect(button.text()).toBe('View Details')

    // The sold overlay should not be present
    expect(wrapper.find('.sold-overlay').exists()).toBe(false)

    // The root element should not have the "sold" class
    expect(wrapper.classes()).not.toContain('sold')
  })

  it('emits "click" when product is available', async () => {
    const wrapper = factory({
      id: 1,
      title: 'Test Product',
      price: 100,
      category: 'Electronics',
      image: 'http://example.com/image.jpg',
      location: 'Oslo',
      isVippsPaymentEnabled: true,
      isWishlisted: false,
      isAvailable: true,
    })

    // Trigger a click on the product card
    await wrapper.trigger('click')
    expect(wrapper.emitted('click')).toBeTruthy()
    // The emitted event should carry the product id
    expect(wrapper.emitted('click')?.[0]).toEqual([1])
  })

  it('does not emit "click" when product is not available', async () => {
    const wrapper = factory({
      id: 2,
      title: 'Unavailable Product',
      price: 150,
      category: 'Books',
      image: 'http://example.com/image2.jpg',
      location: 'Bergen',
      isVippsPaymentEnabled: false,
      isWishlisted: true,
      isAvailable: false,
    })

    // Trigger a click on the product card
    await wrapper.trigger('click')
    // No "click" event should be emitted because the product is sold
    expect(wrapper.emitted('click')).toBeFalsy()

    // The sold overlay should be visible
    expect(wrapper.find('.sold-overlay').exists()).toBe(true)

    // The button text should change to "Sold"
    const button = wrapper.find('button.view-details')
    expect(button.text()).toBe('Sold')

    // The product card's root element should have the "sold" class
    expect(wrapper.classes()).toContain('sold')
  })
})
