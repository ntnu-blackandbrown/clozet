import { describe, it, expect, beforeEach, vi } from 'vitest'
import type { Mock } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import { createRouter, createWebHistory } from 'vue-router'
import { createI18n } from 'vue-i18n'
import MyPostsView from '@/views/profile/MyPostsView.vue' // <-- Update to your actual file path
import { useAuthStore } from '@/stores/AuthStore'
import { ProductService } from '@/api/services/ProductService'

vi.mock('@/api/services/ProductService', () => ({
  ProductService: {
    getItemsBySeller: vi.fn(),
    getItemImages: vi.fn(),
  },
}))

describe('MyPostsView.vue', () => {
  let pinia: ReturnType<typeof createPinia>
  let router: ReturnType<typeof createRouter>

  // Minimal i18n setup
  const i18n = createI18n({
    locale: 'en',
    messages: {
      en: {
        profile: {
          myPosts: 'My Posts',
          emptyStates: {
            noPosts: 'No posts found',
          },
        },
      },
    },
  })

  beforeEach(async () => {
    vi.clearAllMocks()

    // Create and activate a fresh Pinia instance
    pinia = createPinia()
    setActivePinia(pinia)

    // Create a minimal router
    router = createRouter({
      history: createWebHistory(),
      routes: [
        {
          path: '/profile/posts/:id?',
          name: 'MyPosts',
          component: MyPostsView,
        },
      ],
    })

    // We must wait for the router to be ready before mounting, if we push routes.
    await router.isReady()
  })

  it('renders empty state if there are no posts', async () => {
    // Mock the ProductService to return an empty list
    ;(ProductService.getItemsBySeller as Mock).mockResolvedValue({
      data: [],
    })

    const authStore = useAuthStore()
    authStore.user = { id: 42, name: 'Test User' } as any

    const wrapper = mount(MyPostsView, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    // Wait for onMounted calls to finish
    await flushPromises()

    // Should display empty state message
    expect(wrapper.text()).toContain('No posts found')
    // Items should be empty
    expect((wrapper.vm as any).items).toEqual([])
  })

  it('fetches and displays user posts successfully', async () => {
    // Mock data returned by the getItemsBySeller call
    ;(ProductService.getItemsBySeller as Mock).mockResolvedValue({
      data: [
        { id: 101, name: 'Post 101', available: true },
        { id: 202, name: 'Post 202', available: false },
      ],
    })

    // Mock the getItemImages call
    ;(ProductService.getItemImages as Mock).mockImplementation((itemId: number) => {
      return Promise.resolve({
        data: [{ imageUrl: `https://example.com/image-${itemId}.jpg` }],
      })
    })

    const authStore = useAuthStore()
    authStore.user = { id: 42 } as any

    const wrapper = mount(MyPostsView, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()

    // Should have 2 items
    expect((wrapper.vm as any).items).toHaveLength(2)
    // Confirm the mapped images and isAvailable property
    const [item1, item2] = (wrapper.vm as any).items
    expect(item1.image).toBe('https://example.com/image-101.jpg')
    expect(item1.isAvailable).toBe(true)
    expect(item2.image).toBe('https://example.com/image-202.jpg')
    expect(item2.isAvailable).toBe(false)

    // No empty state message
    expect(wrapper.text()).not.toContain('No posts found')
  })

  it('handles error when fetching posts', async () => {
    // Mock an error
    ;(ProductService.getItemsBySeller as Mock).mockRejectedValue(
      new Error('Server error'),
    )

    const authStore = useAuthStore()
    authStore.user = { id: 42 } as any

    const wrapper = mount(MyPostsView, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()

    // Should handle error gracefully and show empty state
    expect((wrapper.vm as any).items).toEqual([])
    expect(wrapper.text()).toContain('No posts found')
  })

  it('falls back to default image when fetching images fails', async () => {
    ;(ProductService.getItemsBySeller as Mock).mockResolvedValue({
      data: [
        { id: 101, name: 'Post 101', available: true },
      ],
    })
    // Mock failure of getItemImages
    ;(ProductService.getItemImages as Mock).mockRejectedValue(
      new Error('Image fetch error'),
    )

    const authStore = useAuthStore()
    authStore.user = { id: 42 } as any

    const wrapper = mount(MyPostsView, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()

    expect((wrapper.vm as any).items[0].image).toBe('/default-product-image.jpg')
    expect((wrapper.vm as any).items[0].isAvailable).toBe(true)
  })

  it('sets initialProductId if route param is present', async () => {
    // Push to a route with an ID
    router.push('/profile/posts/999')
    await router.isReady()

    // Mock success but no data
    ;(ProductService.getItemsBySeller as Mock).mockResolvedValue({ data: [] })

    const authStore = useAuthStore()
    authStore.user = { id: 42 } as any

    const wrapper = mount(MyPostsView, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()
    expect((wrapper.vm as any).initialProductId).toBe(999)
  })

  it('does not set initialProductId if route param is absent', async () => {
    // No ID in route
    router.push('/profile/posts')
    await router.isReady()

    // Mock success but no data
    ;(ProductService.getItemsBySeller as Mock).mockResolvedValue({ data: [] })

    const authStore = useAuthStore()
    authStore.user = { id: 42 } as any

    const wrapper = mount(MyPostsView, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()
    expect((wrapper.vm as any).initialProductId).toBe(null)
  })
})
