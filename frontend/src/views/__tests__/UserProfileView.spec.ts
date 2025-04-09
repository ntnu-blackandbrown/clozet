import { shallowMount, flushPromises } from '@vue/test-utils'
import { nextTick } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import UserProfileView from '@/views/user/UserProfileView.vue' // Adjust the path according to your project

// Stub RouterLink component for testing.
const RouterLinkStub = {
  name: 'RouterLink',
  props: ['to'],
  template: '<a :href="to"><slot /></a>',
}

// Stub RouterView component.
const RouterViewStub = {
  name: 'RouterView',
  template: '<div class="router-view-stub"></div>',
}

// Stub ProductDisplayModal to verify it receives the correct prop.
const ProductDisplayModalStub = {
  name: 'ProductDisplayModal',
  props: ['productId'],
  template: '<div class="product-display-modal-stub"></div>',
}

// Mock vue-router composables.
vi.mock('vue-router', () => ({
  useRoute: () => ({ path: '/profile/settings' }),
  useRouter: () => ({
    push: vi.fn(),
    back: vi.fn(),
  }),
}))

describe('UserProfileView.vue', () => {
  let wrapper: any

  beforeEach(() => {
    wrapper = shallowMount(UserProfileView, {
      global: {
        stubs: {
          RouterLink: RouterLinkStub,
          RouterView: RouterViewStub,
          ProductDisplayModal: ProductDisplayModalStub,
        },
      },
    })
  })

  it('renders navigation links with the correct active class', () => {
    // Find all RouterLink stubs.
    const links = wrapper.findAllComponents(RouterLinkStub)
    // Find the link with the "to" prop equal to "/profile/settings"
    const settingsLink = links.find((link: any) => link.props('to') === '/profile/settings')
    expect(settingsLink.exists()).toBe(true)
    // Since useRoute returns path "/profile/settings", it should have the "active" class.
    expect(settingsLink.classes()).toContain('active')
  })

  it('opens the product modal when openProductModal is called', async () => {
    // Call the exposed method openProductModal with a test product ID.
    wrapper.vm.openProductModal('42')
    await nextTick()
    // Verify that selectedProductId and showProductModal updated.
    expect(wrapper.vm.selectedProductId).toBe('42')
    expect(wrapper.vm.showProductModal).toBe(true)
    // The ProductDisplayModal stub should be rendered with productId prop.
    const modal = wrapper.findComponent(ProductDisplayModalStub)
    expect(modal.exists()).toBe(true)
    expect(modal.props('productId')).toBe('42')
  })

  it('closes the product modal when the modal emits a "close" event', async () => {
    // Open modal first.
    wrapper.vm.openProductModal('42')
    await nextTick()
    let modal = wrapper.findComponent(ProductDisplayModalStub)
    expect(modal.exists()).toBe(true)
    // Simulate the modal emitting a "close" event.
    modal.vm.$emit('close')
    await nextTick()
    // Verify that showProductModal is set to false.
    expect(wrapper.vm.showProductModal).toBe(false)
  })

  it('renders the RouterView in the content area', () => {
    const routerView = wrapper.findComponent(RouterViewStub)
    expect(routerView.exists()).toBe(true)
  })
})
