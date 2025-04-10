import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import MyPostsView from '@/views/profile/MyPostsView.vue'
import { ProductService } from '@/api/services/ProductService'
import { createMockI18n } from '@/test/i18nMock'
import type { I18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/AuthStore'
import ProductList from '@/components/product/ProductList.vue'

// Mock the ProductService
vi.mock('@/api/services/ProductService', () => ({
  ProductService: {
    getItemsBySeller: vi.fn(),
    getItemImages: vi.fn()
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

describe('MyPostsView', () => {
  let i18n: I18n;

  beforeEach(() => {
    vi.clearAllMocks();

    // Setup i18n mock with profile translations
    i18n = createMockI18n({
      en: {
        profile: {
          myPosts: 'My Posts',
          emptyStates: {
            noPosts: 'You have no posts yet'
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

  it('fetches and displays user posts on mount', async () => {
    // Mock the API responses
    const mockItems = [
      { id: 1, name: 'Item 1', price: 100, description: 'Desc 1', available: true },
      { id: 2, name: 'Item 2', price: 200, description: 'Desc 2', available: false }
    ];

    vi.mocked(ProductService.getItemsBySeller).mockResolvedValue({ data: mockItems });
    vi.mocked(ProductService.getItemImages).mockResolvedValueOnce({
      data: [{ imageUrl: '/image1.jpg' }]
    }).mockResolvedValueOnce({
      data: [{ imageUrl: '/image2.jpg' }]
    });

    const wrapper = mount(MyPostsView, {
      global: {
        plugins: [i18n]
      }
    });

    await flushPromises();

    // Verify API calls
    expect(ProductService.getItemsBySeller).toHaveBeenCalledWith(1);
    expect(ProductService.getItemImages).toHaveBeenCalledTimes(2);

    // Check that ProductList is displayed with correct props
    const productList = wrapper.findComponent('[data-testid="product-list"]');
    expect(productList.exists()).toBe(true);

    // The items should have images and mapped isAvailable property
    expect(wrapper.vm.items).toEqual([
      { ...mockItems[0], image: '/image1.jpg', isAvailable: true },
      { ...mockItems[1], image: '/image2.jpg', isAvailable: false }
    ]);
  });

  it('shows empty state when user has no posts', async () => {
    // Mock empty items response
    vi.mocked(ProductService.getItemsBySeller).mockResolvedValue({ data: [] });

    const wrapper = mount(MyPostsView, {
      global: {
        plugins: [i18n]
      }
    });

    await flushPromises();

    // Check for empty state
    const emptyState = wrapper.find('.empty-state');
    expect(emptyState.exists()).toBe(true);
    expect(emptyState.text()).toContain('You have no posts yet');

    // ProductList should not be displayed
    expect(wrapper.findComponent(ProductList).exists()).toBe(false);
  });

  it('handles API errors gracefully', async () => {
    // Mock API failure
    vi.mocked(ProductService.getItemsBySeller).mockRejectedValue(new Error('Failed to fetch'));

    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    const wrapper = mount(MyPostsView, {
      global: {
        plugins: [i18n]
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
    const mockItems = [{ id: 1, name: 'Item 1', price: 100, description: 'Desc 1', available: true }];

    vi.mocked(ProductService.getItemsBySeller).mockResolvedValue({ data: mockItems });
    vi.mocked(ProductService.getItemImages).mockRejectedValue(new Error('Image fetch failed'));

    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

    const wrapper = mount(MyPostsView, {
      global: {
        plugins: [i18n]
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
        params: { id: '42' }
      }))
    }));

    vi.mocked(ProductService.getItemsBySeller).mockResolvedValue({ data: [] });

    const wrapper = mount(MyPostsView, {
      global: {
        plugins: [i18n]
      }
    });

    await flushPromises();

    expect(wrapper.vm.initialProductId).toBe(42);
  });
});
