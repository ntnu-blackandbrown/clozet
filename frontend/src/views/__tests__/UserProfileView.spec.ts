import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createRouter, createWebHistory } from 'vue-router'
import { createI18n } from 'vue-i18n'
import ProfileLayout from '@/views/profile/ProfileLayout.vue' // Update to the correct path
import ProductDisplayModal from '@/components/modals/ProductDisplayModal.vue'

// Optional: mock the ProductDisplayModal if you don't want to fully render it
vi.mock('@/components/modals/ProductDisplayModal.vue', () => ({
  default: {
    name: 'ProductDisplayModal',
    props: ['productId'],
    template: '<div>Mocked ProductModal. ProductID: {{ productId }} <button @click="$emit(\'close\')">Close</button></div>',
  },
}))

describe('ProfileLayout.vue', () => {
  let router: ReturnType<typeof createRouter>

  // Minimal i18n messages
  const i18n = createI18n({
    legacy: false, // Add this to match the i18nMock.ts setting
    locale: 'en',
    messages: {
      en: {
        profile: {
          navigation: {
            profileSettings: 'Profile Settings',
            myPosts: 'My Posts',
            myWishlist: 'My Wishlist',
            myPurchases: 'My Purchases',
            changePassword: 'Change Password',
          },
        },
      },
    },
  })

  beforeEach(async () => {
    vi.clearAllMocks()

    // Create a router with minimal test routes
    router = createRouter({
      history: createWebHistory(),
      routes: [
        {
          path: '/profile/settings',
          name: 'ProfileSettings',
          component: {
            template: '<div>Profile Settings Content</div>',
          },
        },
        {
          path: '/profile/posts',
          name: 'MyPosts',
          component: {
            template: '<div>My Posts Content</div>',
          },
        },
        {
          path: '/profile/wishlist',
          name: 'MyWishlist',
          component: {
            template: '<div>My Wishlist Content</div>',
          },
        },
        {
          path: '/profile/purchases',
          name: 'MyPurchases',
          component: {
            template: '<div>My Purchases Content</div>',
          },
        },
        {
          path: '/profile/change-password',
          name: 'ChangePassword',
          component: {
            template: '<div>Change Password Content</div>',
          },
        },
      ],
    })

    // Make sure router is ready before mounting
    await router.isReady()
  })

  it('renders navigation links and sets the active route', async () => {
    // Start from /profile/posts
    router.push('/profile/posts')
    await router.isReady()

    const wrapper = mount(ProfileLayout, {
      global: {
        plugins: [router, i18n],
      },
    })

    await flushPromises()

    // Find all nav links
    const navLinks = wrapper.findAll('.nav-link')
    expect(navLinks.length).toBe(5)

    // Check their text from i18n
    const linkTexts = navLinks.map((link) => link.text())
    expect(linkTexts).toContain('Profile Settings')
    expect(linkTexts).toContain('My Posts')
    expect(linkTexts).toContain('My Wishlist')
    expect(linkTexts).toContain('My Purchases')
    expect(linkTexts).toContain('Change Password')

    // Confirm the "My Posts" link is active for /profile/posts
    const activeLink = wrapper.find('.nav-link.active')
    expect(activeLink.exists()).toBe(true)
    expect(activeLink.text()).toBe('My Posts')
  })

  it('renders router-view content', async () => {
    // Navigate to /profile/settings
    router.push('/profile/settings')
    await router.isReady()

    const wrapper = mount(ProfileLayout, {
      global: {
        plugins: [router, i18n],
      },
    })

    await flushPromises()

    // The <main> should contain "Profile Settings Content" from the route component
    expect(wrapper.text()).toContain('Profile Settings Content')
  })

  it('product modal is hidden by default', async () => {
    router.push('/profile/posts')
    await router.isReady()

    const wrapper = mount(ProfileLayout, {
      global: {
        plugins: [router, i18n],
      },
    })

    await flushPromises()

    // The modal should not be rendered initially
    expect(wrapper.findComponent(ProductDisplayModal).exists()).toBe(false)
  })

  it('opens the product modal with the correct productId', async () => {
    router.push('/profile/wishlist')
    await router.isReady()

    // Mount with initial data to show the modal
    const wrapper = mount(ProfileLayout, {
      global: {
        plugins: [router, i18n],
      },
      data() {
        return {
          showProductModal: true,
          selectedProductId: 123
        }
      }
    })

    await flushPromises()

    // Now the modal should appear with productId = 123
    const modal = wrapper.findComponent(ProductDisplayModal)
    expect(modal.exists()).toBe(true)
    expect(modal.props('productId')).toBe(123)
  })

  it('closes product modal when @close is emitted', async () => {
    router.push('/profile/posts')
    await router.isReady()

    const wrapper = mount(ProfileLayout, {
      global: {
        plugins: [router, i18n],
      },
      // Start with the modal open:
      data() {
        return {
          showProductModal: true,
          selectedProductId: 555,
        }
      },
    })

    await flushPromises()

    // The modal should be present
    let modal = wrapper.findComponent(ProductDisplayModal)
    expect(modal.exists()).toBe(true)

    // Emit close event
    await modal.vm.$emit('close')
    await flushPromises()

    // Modal should be hidden now
    modal = wrapper.findComponent(ProductDisplayModal)
    expect(modal.exists()).toBe(false)
  })
})
