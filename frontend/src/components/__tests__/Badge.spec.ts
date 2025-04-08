import { mount } from '@vue/test-utils'
import { describe, it, expect, vi } from 'vitest'
import Badge from '@/components/utils/Badge.vue'

// --- Mock external dependencies ---
// Mock badgeIcons so that we can verify that the icon is rendered correctly.
vi.mock('@/components/utils/BadgeIcons', () => ({
  badgeIcons: {
    category: '<svg>category-icon</svg>',
    location: '<svg>location-icon</svg>',
    shipping: '<svg>shipping-icon</svg>',
  },
}))

// Mock badgeColors with simple color values
vi.mock('@/components/utils/BadgeColors', () => ({
  badgeColors: {
    category: {
      color: '#cccccc',
      textColor: '#000000',
      borderColor: '#aaaaaa',
      hoverColor: '#dddddd',
      hoverTextColor: '#111111',
      hoverBorderColor: '#bbbbbb',
      activeColor: '#eeeeee',
      activeTextColor: '#222222',
      activeBorderColor: '#999999',
    },
    location: {
      color: '#cccccc',
      textColor: '#000000',
      borderColor: '#aaaaaa',
      hoverColor: '#dddddd',
      hoverTextColor: '#111111',
      hoverBorderColor: '#bbbbbb',
      activeColor: '#eeeeee',
      activeTextColor: '#222222',
      activeBorderColor: '#999999',
    },
    shipping: {
      color: '#cccccc',
      textColor: '#000000',
      borderColor: '#aaaaaa',
      hoverColor: '#dddddd',
      hoverTextColor: '#111111',
      hoverBorderColor: '#bbbbbb',
      activeColor: '#eeeeee',
      activeTextColor: '#222222',
      activeBorderColor: '#999999',
    },
    price: {
      color: '#ffffff',
      textColor: '#000000',
      borderColor: '#000000',
      hoverColor: '#ffffff',
      hoverTextColor: '#000000',
      hoverBorderColor: '#000000',
      activeColor: '#ffffff',
      activeTextColor: '#000000',
      activeBorderColor: '#000000',
    },
  },
  // Also export the BadgeType type (no need for implementation in tests)
  BadgeType: undefined,
}))

describe('Badge.vue', () => {
  it('renders badge text with name and currency when type is price', () => {
    const wrapper = mount(Badge, {
      props: {
        name: '100',
        type: 'price',
        currency: 'NOK',
      },
    })
    // For price badges, the span shows the name and currency
    expect(wrapper.text()).toContain('100 NOK')
  })

  it('renders badge text with name when type is not price', () => {
    const wrapper = mount(Badge, {
      props: {
        name: 'Electronics',
        type: 'category',
      },
    })
    expect(wrapper.text()).toContain('Electronics')
  })

  it('renders an icon for non-price badges', () => {
    const wrapper = mount(Badge, {
      props: {
        name: 'LocationName',
        type: 'location',
      },
    })
    // The svg should be rendered with the mocked icon HTML
    const svg = wrapper.find('svg.badge-icon')
    expect(svg.exists()).toBe(true)
    // The inner HTML of the SVG should match our mock value
    expect(svg.html()).toContain('location-icon')
  })

  it('does not render an icon for price badges', () => {
    const wrapper = mount(Badge, {
      props: {
        name: '50',
        type: 'price',
        currency: 'USD',
      },
    })
    const svg = wrapper.find('svg.badge-icon')
    expect(svg.exists()).toBe(false)
  })

  it('emits a click event with correct payload when clickable', async () => {
    const wrapper = mount(Badge, {
      props: {
        name: 'Books',
        type: 'category', // category is clickable per computed isClickable
      },
    })
    await wrapper.trigger('click')
    expect(wrapper.emitted('click')).toBeTruthy()
    // Expect the payload to include the badge type and the value (name)
    expect(wrapper.emitted('click')?.[0]).toEqual([{ type: 'category', value: 'Books' }])
  })

  it('does not emit a click event when badge type is "price"', async () => {
    const wrapper = mount(Badge, {
      props: {
        name: '200',
        type: 'price',
        currency: 'USD',
      },
    })
    await wrapper.trigger('click')
    expect(wrapper.emitted('click')).toBeFalsy()
  })

  it('changes style on hover and active events for non-price badges', async () => {
    const wrapper = mount(Badge, {
      props: {
        name: 'Test',
        type: 'shipping',
      },
    })

    // Capture the initial inline style
    const initialStyle = wrapper.attributes('style')

    // Trigger mouseenter (hover)
    await wrapper.trigger('mouseenter')
    const hoverStyle = wrapper.attributes('style')
    expect(hoverStyle).not.toEqual(initialStyle)
    // The computed style should use the hover colors from our mocked badgeColors

    // Trigger mousedown (active)
    await wrapper.trigger('mousedown')
    const activeStyle = wrapper.attributes('style')
    expect(activeStyle).not.toEqual(hoverStyle)

    // Trigger mouseup (active state released)
    await wrapper.trigger('mouseup')
    const afterActiveStyle = wrapper.attributes('style')
    // after mouseup, style should revert to hover style
    expect(afterActiveStyle).toEqual(hoverStyle)
  })

  it('does not change hover or active states when type is "price"', async () => {
    const wrapper = mount(Badge, {
      props: {
        name: '50',
        type: 'price',
        currency: 'USD',
      },
    })
    const initialStyle = wrapper.attributes('style')
    await wrapper.trigger('mouseenter')
    await wrapper.trigger('mousedown')
    await wrapper.trigger('mouseup')
    await wrapper.trigger('mouseleave')
    const finalStyle = wrapper.attributes('style')
    // For price badges, styles should remain unchanged (and cursor default)
    expect(finalStyle).toEqual(initialStyle)
  })
})
