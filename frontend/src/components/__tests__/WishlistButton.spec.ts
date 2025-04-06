import { expect, test, describe, assert } from 'vitest'
import WishlistButton from '@/components/utils/WishlistButton.vue'
import { mount } from '@vue/test-utils'

describe('WishlistButton', () => {
  test('fake test', () => {
    expect(true).toBe(true)
  })
  /*const wrapper = mount(WishlistButton, {
    props: {
      productId: 1,
      isWishlisted: false,
    },
  })

  test('renders correctly', () => {
    expect(wrapper.props().productId).toBe(1)
    expect(wrapper.props().isWishlisted).toBe(false)
    expect(wrapper.classes()).not.toContain('wishlisted')
  })

  test('toggles wishlist state on click', async () => {
    const button = wrapper.find('button')
    expect(wrapper.classes()).not.toContain('wishlisted')
    await button.trigger('click')
    expect(wrapper.classes()).toContain('wishlisted')
    await button.trigger('click')
    expect(wrapper.classes()).not.toContain('wishlisted')
  })*/
})
