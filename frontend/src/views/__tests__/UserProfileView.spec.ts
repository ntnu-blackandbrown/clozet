import { mount, flushPromises } from '@vue/test-utils'
import { vi, describe, it, expect } from 'vitest'
import UserProfileView from '@/views/UserProfileView.vue' // adjust path if needed

// Mock vue-router to provide a consistent route
vi.mock('vue-router', () => ({
  useRoute: () => ({ path: '/profile/settings' }),
  RouterLink: {
    name: 'RouterLink',
    template: '<a class="nav-link"><slot /></a>',
  },
  RouterView: {
    name: 'RouterView',
    template: '<div class="router-view"><slot /></div>',
  },
}))

describe('UserProfileView.vue', () => {
  it('renders navigation links with correct active class', async () => {
    const wrapper = mount(UserProfileView, {
      global: {
        stubs: {
          // Stub the ProductDisplayModal so it doesn’t interfere with navigation tests
          ProductDisplayModal: {
            name: 'ProductDisplayModal',
            props: ['productId'],
            template: '<div class="product-display-modal">Product ID: {{ productId }}</div>',
          },
        },
      },
    })

    // Find all navigation links (they have class "nav-link")
    const navLinks = wrapper.findAll('.nav-link')
    expect(navLinks.length).toBe(4)

    // Check that the link text for "Profile Settings" has the active class
    const settingsLink = navLinks.find((link) => link.text().includes('Profile Settings'))
    expect(settingsLink.classes()).toContain('active')
  })

  it('conditionally renders ProductDisplayModal when openProductModal is called', async () => {
    const wrapper = mount(UserProfileView, {
      global: {
        stubs: {
          ProductDisplayModal: {
            name: 'ProductDisplayModal',
            props: ['productId'],
            template: '<div class="product-display-modal">Product ID: {{ productId }}</div>',
          },
          // Ensure RouterLink and RouterView are stubbed (if not already by the vue-router mock)
          RouterLink: true,
          RouterView: true,
        },
      },
    })

    // Initially, the modal should not be rendered
    expect(wrapper.findComponent({ name: 'ProductDisplayModal' }).exists()).toBe(false)

    // To test the modal functionality, we need to call openProductModal.
    // For this, please ensure your component includes:
    // defineExpose({ openProductModal })
    if (typeof wrapper.vm.openProductModal !== 'function') {
      throw new Error(
        'openProductModal is not exposed. Please add defineExpose({ openProductModal }) in your component.',
      )
    }

    // Call openProductModal with a sample product id
    wrapper.vm.openProductModal('123')
    await flushPromises()

    // The modal should now be rendered with productId "123"
    const modal = wrapper.findComponent({ name: 'ProductDisplayModal' })
    expect(modal.exists()).toBe(true)
    expect(modal.props('productId')).toBe('123')

    // Now simulate closing the modal by emitting the "close" event
    modal.vm.$emit('close')
    await flushPromises()

    // The modal should no longer be rendered
    expect(wrapper.findComponent({ name: 'ProductDisplayModal' }).exists()).toBe(false)
  })
})
