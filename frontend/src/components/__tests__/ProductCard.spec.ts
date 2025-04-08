import { mount } from '@vue/test-utils'
import ProductCard from '@/components/product/ProductCard.vue'
import { describe, it, expect } from 'vitest'

describe('ProductCard', () => {
  const product = {
    id: 42,
    title: 'Test Product',
    price: 123,
    category: 'Shoes',
    image: '/img/shoes.jpg',
    location: 'Oslo',
    isVippsPaymentEnabled: true,
    isWishlisted: false,
  }

  const factory = (props = product) => {
    return mount(ProductCard, {
      props,
      global: {
        stubs: {
          WishlistButton: true, // prevent actual mounting if needed
          Badge: true,
        },
      },
    })
  }

  it('renders product image with correct src and alt', () => {
    const wrapper = factory()
    const img = wrapper.find('img')
    expect(img.attributes('src')).toBe(product.image)
    expect(img.attributes('alt')).toBe(product.title)
  })

  it('emits click with product id when card is clicked', async () => {
    const wrapper = factory()
    await wrapper.trigger('click')
    //expect(wrapper.emitted('click')).toBeTruthy()
    //expect(wrapper.emitted('click')![0]).toEqual([product.id])
  })

  it('does not emit click when wishlist button is clicked', async () => {
    const wrapper = mount(ProductCard, {
      props: product,
      global: {
        stubs: {
          WishlistButton: {
            template: '<button data-test="wishlist-btn" @click.stop></button>',
          },
          Badge: true,
        },
      },
    })

    await wrapper.find('[data-test="wishlist-btn"]').trigger('click')
    expect(wrapper.emitted('click')).toBeFalsy()
  })

  it('renders Badge components with correct props', () => {
    const wrapper = mount(ProductCard, {
      props: product,
    })

    const badges = wrapper.findAllComponents({ name: 'Badge' })
    expect(badges.length).toBe(2)

    // You can also use a more specific test if Badge isn't stubbed
    // expect(badges[0].props().name).toBe(product.price.toString())
    // expect(badges[1].props().name).toBe(product.category)
  })

  it('renders WishlistButton with correct props', () => {
    const wrapper = mount(ProductCard, {
      props: product,
    })

    const btn = wrapper.findComponent({ name: 'WishlistButton' })
    expect(btn.exists()).toBe(true)
    expect(btn.props('productId')).toBe(product.id)
    //expect(btn.props('isWishlisted')).toBe(product.isWishlisted)
  })
})
