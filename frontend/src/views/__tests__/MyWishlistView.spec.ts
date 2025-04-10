import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import MyWishlistView from '@/views/profile/MyWishlistView.vue'
import { ProductService } from '@/api/services/ProductService'
import { FavoritesService } from '@/api/services/FavoritesService'
import { createMockI18n } from '@/test/i18nMock'
import type { I18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/AuthStore'
import ProductList from '@/components/product/ProductList.vue'

// Mock the services
vi.mock('@/api/services/ProductService', () => ({
  ProductService: {
    getAllItems: vi.fn(),
    getItemImages: vi.fn()
  }
}))

vi.mock('@/api/services/FavoritesService', () => ({
  FavoritesService: {
    getUserFavorites: vi.fn()
  }
}))

// Mock the AuthStore
vi.mock('@/stores/AuthStore', () => ({
  useAuthStore: vi.fn()
}))

// Mock the ProductList component
vi.mock('@/components/product/ProductList.vue', () => ({
  default: {
    name: 'ProductList',
    props: ['items', 'routeBasePath', 'initialProductId'],
    template: '<div data-testid="product-list">Product List Mock</div>'
  }
}))

// Mock vue-router
vi.mock('vue-router', () => ({
  useRoute: vi.fn(() => ({
    params: {}
  }))
}))

// Helper function to create a mock Axios response
const createAxiosResponse = (data: any) => ({
  data,
  status: 200,
  statusText: 'OK',
  headers: {},
  config: {} as any
})

describe('MyWishlistView', () => {
  let i18n: I18n;

  beforeEach(() => {
    vi.clearAllMocks();

    // Setup i18n mock with profile translations
    i18n = createMockI18n({
      en: {
        profile: {
          myWishlist: 'My Wishlist',
          emptyStates: {
            noWishlist: 'Your wishlist is empty'
          }
        }
      }
    });

    // Mock auth store user
    vi.mocked(useAuthStore).mockReturnValue({
      user: { id: 1, name: 'Test User', email: 'test@example.com' },
      isLoggedIn: true
    } as any);
  });

  it('fetches wishlist items and displays them', async () => {
    // Mock favorites data
    const mockFavorites = [
      { id: 1, userId: 1, itemId: 101 },
      { id: 2, userId: 1, itemId: 102 }
    ];

    // Mock all marketplace items
    const mockMarketplaceItems = [
      { id: 101, name: 'Item 101', price: 100, description: 'Desc 101' },
      { id: 102, name: 'Item 102', price: 200, description: 'Desc 102' },
      { id: 103, name: 'Item 103', price: 300, description: 'Desc 103' } // Not in favorites
    ];

    vi.mocked(FavoritesService.getUserFavorites).mockResolvedValue(createAxiosResponse(mockFavorites));
    vi.mocked(ProductService.getAllItems).mockResolvedValue(createAxiosResponse(mockMarketplaceItems));

    // Mock image fetching for the two favorited items
    vi.mocked(ProductService.getItemImages)
      .mockResolvedValueOnce(createAxiosResponse([{ imageUrl: '/image101.jpg' }]))
      .mockResolvedValueOnce(createAxiosResponse([{ imageUrl: '/image102.jpg' }]));

    const wrapper = mount(MyWishlistView, {
      global: {
        plugins: [i18n],
        stubs: {
          ProductList: true
        }
      }
    });

    await flushPromises();

    // Verify API calls
    expect(FavoritesService.getUserFavorites).toHaveBeenCalledWith(1);
    expect(ProductService.getAllItems).toHaveBeenCalled();
    expect(ProductService.getItemImages).toHaveBeenCalledTimes(2);

    // Should have filtered to just the favorited items with images
    expect(wrapper.vm.items).toHaveLength(2);
    expect(wrapper.vm.items[0].id).toBe(101);
    expect(wrapper.vm.items[0].image).toBe('/image101.jpg');
    expect(wrapper.vm.items[1].id).toBe(102);
    expect(wrapper.vm.items[1].image).toBe('/image102.jpg');

    // Check that ProductList is displayed with correct props
    const productList = wrapper.findComponent('[data-testid="product-list"]');
    expect(productList.exists()).toBe(true);
  });

  it('shows empty state when wishlist is empty', async () => {
    // Mock empty favorites
    vi.mocked(FavoritesService.getUserFavorites).mockResolvedValue(createAxiosResponse([]));
    vi.mocked(ProductService.getAllItems).mockResolvedValue(createAxiosResponse([{ id: 101, name: 'Item 101' }]));

    const wrapper = mount(MyWishlistView, {
      global: {
        plugins: [i18n],
        stubs: {
          ProductList: true
        }
      }
    });

    await flushPromises();

    // Check for empty state
    const emptyState = wrapper.find('.empty-state');
    expect(emptyState.exists()).toBe(true);
    expect(emptyState.text()).toContain('Your wishlist is empty');

    // ProductList should not be displayed
    expect(wrapper.findComponent(ProductList).exists()).toBe(false);
  });

  it('handles favorites API error gracefully', async () => {
    // Mock favorites API error
    vi.mocked(FavoritesService.getUserFavorites).mockRejectedValue(new Error('Failed to fetch favorites'));

    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    const wrapper = mount(MyWishlistView, {
      global: {
        plugins: [i18n],
        stubs: {
          ProductList: true
        }
      }
    });

    await flushPromises();

    // Should log the error
    expect(consoleSpy).toHaveBeenCalled();

    // Should display empty state as fallback
    expect(wrapper.find('.empty-state').exists()).toBe(true);
    expect(wrapper.vm.items).toEqual([]);

    consoleSpy.mockRestore();
  });

  it('handles marketplace items API error gracefully', async () => {
    // Mock favorites success but marketplace error
    vi.mocked(FavoritesService.getUserFavorites).mockResolvedValue(
      createAxiosResponse([{ id: 1, userId: 1, itemId: 101 }])
    );
    vi.mocked(ProductService.getAllItems).mockRejectedValue(new Error('Failed to fetch items'));

    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    const wrapper = mount(MyWishlistView, {
      global: {
        plugins: [i18n],
        stubs: {
          ProductList: true
        }
      }
    });

    await flushPromises();

    // Should log the error
    expect(consoleSpy).toHaveBeenCalled();

    // Should display empty state as fallback
    expect(wrapper.find('.empty-state').exists()).toBe(true);
    expect(wrapper.vm.items).toEqual([]);

    consoleSpy.mockRestore();
  });

  it('handles image fetch error gracefully', async () => {
    // Mock data with one favorited item
    vi.mocked(FavoritesService.getUserFavorites).mockResolvedValue(
      createAxiosResponse([{ id: 1, userId: 1, itemId: 101 }])
    );
    vi.mocked(ProductService.getAllItems).mockResolvedValue(
      createAxiosResponse([{ id: 101, name: 'Item 101' }])
    );
    vi.mocked(ProductService.getItemImages).mockRejectedValue(new Error('Image fetch failed'));

    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    const wrapper = mount(MyWishlistView, {
      global: {
        plugins: [i18n],
        stubs: {
          ProductList: true
        }
      }
    });

    await flushPromises();

    // Should log the error
    expect(consoleSpy).toHaveBeenCalled();

    // Should use default image
    expect(wrapper.vm.items[0].image).toBe('/default-product-image.jpg');

    consoleSpy.mockRestore();
  });

  it('uses product ID from route params if available', async () => {
    // Mock route with product ID
    vi.mock('vue-router', () => ({
      useRoute: vi.fn(() => ({
        params: { id: '101' }
      }))
    }));

    // Mock empty data to simplify test
    vi.mocked(FavoritesService.getUserFavorites).mockResolvedValue(createAxiosResponse([]));
    vi.mocked(ProductService.getAllItems).mockResolvedValue(createAxiosResponse([]));

    const wrapper = mount(MyWishlistView, {
      global: {
        plugins: [i18n],
        stubs: {
          ProductList: true
        }
      }
    });

    await flushPromises();

    expect(wrapper.vm.initialProductId).toBe(101);
  });
});
