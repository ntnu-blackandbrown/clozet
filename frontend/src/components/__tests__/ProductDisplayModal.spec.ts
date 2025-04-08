import { mount } from '@vue/test-utils'
import ProductDisplayModal from '@/components/modals/ProductDisplayModal.vue'
import { describe, it, expect, vi } from 'vitest'

// Add vue-router mock
vi.mock('vue-router', () => ({
  useRouter: () => ({
    replace: vi.fn(),
  }),
  useRoute: () => ({
    path: '/products/123',
    params: { id: '123' },
  }),
}))

describe('ProductDisplayModal', () => {
  const factory = () =>
    mount(ProductDisplayModal, {
      props: { productId: 123 },
      global: {
        stubs: {
          BaseModal: {
            template: `<div><slot></slot><button @click="$emit('close')">Close</button></div>`,
          },
          ProductDisplay: {
            template: '<div data-test="product-display" />',
            props: ['id'],
          },
        },
      },
    })

  it('renders ProductDisplay stub element', () => {
    const wrapper = factory()
    const productDisplay = wrapper.find('[data-test="product-display"]')
    expect(productDisplay.exists()).toBe(true)
  })

  it('emits close event when BaseModal emits close', async () => {
    const wrapper = factory()
    await wrapper.find('button').trigger('click')
    expect(wrapper.emitted('close')).toBeTruthy()
  })
})
