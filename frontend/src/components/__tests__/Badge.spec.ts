import { expect, test, describe, assert } from 'vitest'
import Badge from '@/components/utils/Badge.vue'
import { mount } from '@vue/test-utils'
describe('Badge', () => {
  const wrapper = mount(Badge, {
    props: {
      name: 'Test Badge',
    },
  })

  test('correct application of default props', () => {
    expect(wrapper.props().name).toBe('Test Badge')
    expect(wrapper.props().type).toBe('category')
    expect(wrapper.props().color).toBe('#E4EAE7')
    expect(wrapper.props().textColor).toBe('#2D353F')
    expect(wrapper.props().borderColor).toBe('transparent')
    expect(wrapper.find('svg').exists()).toBe(true)
  })

  test('correct application of computed prop of price type', () => {
    const newWrapper = mount(Badge, {
      props: {
        name: 'Price',
        type: 'price',
        color: '#e2e8f0',
        textColor: '#214b89',
        borderColor: '#214b89',
      },
    })
    const currentIcon = newWrapper.find('svg')
    assert(!currentIcon.exists())
    expect(newWrapper.props().name).toBe('Price')
    expect(newWrapper.props().type).toBe('price')
    expect(newWrapper.props().color).toBe('#e2e8f0')
    expect(newWrapper.props().textColor).toBe('#214b89')
    expect(newWrapper.props().borderColor).toBe('#214b89')
  })

  test('correct reendering of currency when price type is applied', () => {
    const newWrapper = mount(Badge, {
      props: {
        name: 'Price',
        type: 'price',
        currency: 'NOK',
      },
    })
    expect(newWrapper.find('span').text()).toBe('Price NOK')
  })
})
