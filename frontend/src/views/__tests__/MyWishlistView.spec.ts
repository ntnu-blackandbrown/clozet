import { describe, it, expect, beforeEach, vi } from 'vitest'
import type { Mock } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import MyWishlist from '@/views/profile/MyWishlistView.vue' // <-- adjust to your actual file path
import { useAuthStore } from '@/stores/AuthStore'
import { createI18n } from 'vue-i18n'
import { createRouter, createWebHistory } from 'vue-router'
import { FavoritesService } from '@/api/services/FavoritesService'
import { ProductService } from '@/api/services/ProductService'

vi.mock('@/api/services/FavoritesService', () => ({
  FavoritesService: {
    getUserFavorites: vi.fn(),
  },
}))

vi.mock('@/api/services/ProductService', () => ({
  ProductService: {
    getAllItems: vi.fn(),
    getItemImages: vi.fn(),
  },
}))

describe('MyWishlist.vue', () => {
  let pinia: ReturnType<typeof createPinia>
  let router: ReturnType<typeof createRouter>

  // Minimal i18n config
  const i18n = createI18n({
    locale: 'en',
    messages: {
      en: {
        profile: {
          myWishlist: 'My Wishlist',
          emptyStates: {
            noWishlist: 'No items in your wishlist',
          },
        },
      },
    },
  })

  beforeEach(async () => {
    // Reset mocks
    vi.clearAllMocks()

    // Create and activate a fresh Pinia instance
    pinia = createPinia()
    setActivePinia(pinia)

    // Create a minimal router
    router = createRouter({
      history: createWebHistory(),
      routes: [
        {
          path: '/profile/wishlist/:id?',
          name: 'MyWishlist',
          component: MyWishlist,
        },
      ],
    })

    // Important to ensure router is ready before mounting, if you plan on using `router.push()`
    await router.isReady()
  })

  it('renders empty state when there are no favorites or items', async () => {
    // Mock empty favorites and items
    ;(FavoritesService.getUserFavorites as Mock).mockResolvedValue({ data: [] })
    ;(ProductService.getAllItems as Mock).mockResolvedValue({ data: [] })

    // Set up auth store
    const authStore = useAuthStore()
    authStore.user = { id: 42, name: 'Test User' } as any

    // Mount component
    const wrapper = mount(MyWishlist, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    // onMounted is async, so wait for all promises (API calls) to resolve
    await flushPromises()

    // Expect the empty state text
    expect(wrapper.text()).toContain('No items in your wishlist')
  })

  it('renders items when user has favorites', async () => {
    const favorites = [
      { id: 1, itemId: 101 },
      { id: 2, itemId: 202 },
    ]

    ;(FavoritesService.getUserFavorites as Mock).mockResolvedValue({
      data: favorites,
    })
    ;(ProductService.getAllItems as Mock).mockResolvedValue({
      data: [
        { id: 101, name: 'Item 101' },
        { id: 202, name: 'Item 202' },
        { id: 303, name: 'Item 303' },
      ],
    })
    ;(ProductService.getItemImages as Mock).mockImplementation((id: number) => {
      return Promise.resolve({
        data: [{ imageUrl: `https://example.com/product-${id}.jpg` }],
      })
    })

    const authStore = useAuthStore()
    authStore.user = { id: 42 } as any

    const wrapper = mount(MyWishlist, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()

    // Only items 101 and 202 should be in favorites
    expect((wrapper.vm as any).items.length).toBe(2)
    expect((wrapper.vm as any).items[0].image).toBe('https://example.com/product-101.jpg')
    expect((wrapper.vm as any).items[1].image).toBe('https://example.com/product-202.jpg')
    // No empty state text
    expect(wrapper.text()).not.toContain('No items in your wishlist')
  })

  it('handles error when fetching favorites', async () => {
    ;(FavoritesService.getUserFavorites as Mock).mockRejectedValue(
      new Error('Favorites error'),
    )
    // Even if ProductService is called, it may or may not be reached
    ;(ProductService.getAllItems as Mock).mockResolvedValue({
      data: [{ id: 101, name: 'Item 101' }],
    })

    const authStore = useAuthStore()
    authStore.user = { id: 42 } as any

    const wrapper = mount(MyWishlist, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()
    // Error leads to an empty array
    expect((wrapper.vm as any).items).toEqual([])
    expect(wrapper.text()).toContain('No items in your wishlist')
  })

  it('handles error when fetching images for an item', async () => {
    ;(FavoritesService.getUserFavorites as Mock).mockResolvedValue({
      data: [{ id: 1, itemId: 101 }],
    })
    ;(ProductService.getAllItems as Mock).mockResolvedValue({
      data: [{ id: 101, name: 'Item 101' }],
    })
    ;(ProductService.getItemImages as Mock).mockRejectedValue(
      new Error('Image fetch error'),
    )

    const authStore = useAuthStore()
    authStore.user = { id: 42 } as any

    const wrapper = mount(MyWishlist, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()

    // On image fetch error, it should fallback to the default product image
    expect((wrapper.vm as any).items[0].image).toBe('/default-product-image.jpg')
  })

  it('sets initialProductId if route param is present', async () => {
    // Push to a route with an ID
    router.push('/profile/wishlist/999')
    await router.isReady()

    ;(FavoritesService.getUserFavorites as Mock).mockResolvedValue({
      data: [],
    })
    ;(ProductService.getAllItems as Mock).mockResolvedValue({
      data: [],
    })

    const authStore = useAuthStore()
    authStore.user = { id: 42 } as any

    const wrapper = mount(MyWishlist, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()
    expect((wrapper.vm as any).initialProductId).toBe(999)
  })

  it('does not set initialProductId if route param is absent', async () => {
    router.push('/profile/wishlist') // no ID param
    await router.isReady()

    ;(FavoritesService.getUserFavorites as Mock).mockResolvedValue({
      data: [],
    })
    ;(ProductService.getAllItems as Mock).mockResolvedValue({
      data: [],
    })

    const authStore = useAuthStore()
    authStore.user = { id: 42 } as any

    const wrapper = mount(MyWishlist, {
      global: {
        plugins: [pinia, router, i18n],
      },
    })

    await flushPromises()
    expect((wrapper.vm as any).initialProductId).toBe(null)
  })
})
