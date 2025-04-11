import { shallowMount } from '@vue/test-utils'
import { nextTick } from 'vue'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import ProductDisplayModal from '@/components/modals/ProductDisplayModal.vue'

// Prepare mocks for vue-router before importing the component
let mockReplace = vi.fn()
let mockRoute = { path: '/default' }
const mockRouter = { replace: mockReplace }

vi.mock('vue-router', () => ({
  useRouter: () => mockRouter,
  useRoute: () => mockRoute,
}))

describe('ProductDisplayModal.vue', () => {
  // Use a stub for the BaseModal so we can trigger its "close" event.
  const globalStubs = {
    // Provide a stub for BaseModal with a set name so that we can locate it
    BaseModal: {
      name: 'BaseModal',
      template: '<div><slot /></div>',
    },
    // We stub ProductDisplay since it's rendered inside the modal.
    ProductDisplay: {
      name: 'ProductDisplay',
      template: '<div></div>',
    },
  }

  beforeEach(() => {
    // Reset the router mock before each test.
    mockReplace.mockReset()
  })

  it('emits "close" and does not redirect if on a messages page', async () => {
    // Arrange: set route path to simulate a messages page.
    mockRoute.path = '/messages/inbox'
    const wrapper = shallowMount(ProductDisplayModal, {
      props: { productId: 123 },
      global: { stubs: globalStubs },
    })
    // Act: simulate the BaseModal close event.
    await wrapper.findComponent({ name: 'BaseModal' }).vm.$emit('close')
    await nextTick()

    // Assert: router.replace is not called and the component emits a close event.
    expect(mockReplace).not.toHaveBeenCalled()
    expect(wrapper.emitted('close')).toBeTruthy()
    expect(wrapper.emitted('close')?.length).toBe(1)
  })

  it('redirects to the profile page if the current route includes "/profile/"', async () => {
    // Arrange: simulate a profile route.
    // e.g. for '/profile/johndoe', the base path is calculated as '/profile/' then trimmed to '/profile'
    mockRoute.path = '/profile/johndoe'
    const wrapper = shallowMount(ProductDisplayModal, {
      props: { productId: 456 },
      global: { stubs: globalStubs },
    })

    // Act: trigger the close event.
    await wrapper.findComponent({ name: 'BaseModal' }).vm.$emit('close')
    await nextTick()

    // Assert: expect router.replace to have been called with the trimmed base path.
    expect(mockReplace).toHaveBeenCalledWith('/profile')
    expect(wrapper.emitted('close')).toBeTruthy()
  })

  it('redirects to home if on any other route', async () => {
    // Arrange: set a route that does not match messages or profile.
    mockRoute.path = '/other/page'
    const wrapper = shallowMount(ProductDisplayModal, {
      props: { productId: 789 },
      global: { stubs: globalStubs },
    })

    // Act: simulate the BaseModal close event.
    await wrapper.findComponent({ name: 'BaseModal' }).vm.$emit('close')
    await nextTick()

    // Assert: default behavior should redirect to home.
    expect(mockReplace).toHaveBeenCalledWith('/')
    expect(wrapper.emitted('close')).toBeTruthy()
  })
})
