import { shallowMount, VueWrapper } from '@vue/test-utils'
import { nextTick } from 'vue'
import { describe, it, expect, beforeEach, vi } from 'vitest'
import ProductDisplay from '@/components/product/ProductDisplay.vue'

// ----- MOCK SETUP -----

// Mock ProductService methods
import { ProductService } from '@/api/services/ProductService'
vi.mock('@/api/services/ProductService', () => ({
  ProductService: {
    getItemById: vi.fn(),
    getItemImages: vi.fn(),
  },
}))

// Mock MessagingService methods
import { MessagingService } from '@/api/services/MessagingService'
vi.mock('@/api/services/MessagingService', () => ({
  MessagingService: {
    getUserConversations: vi.fn(),
    sendMessage: vi.fn(),
  },
}))

// Mock vue-router: useRouter (push and replace)
const mockPush = vi.fn()
const mockReplace = vi.fn()
vi.mock('vue-router', () => ({
  useRouter: () => ({
    push: mockPush,
    replace: mockReplace,
  }),
}))

// Mock AuthStore
const mockAuthStore = {
  user: { id: 1 },
  isLoggedIn: true,
}
vi.mock('@/stores/AuthStore', () => ({
  useAuthStore: () => mockAuthStore,
}))

// ----- SAMPLE DATA -----
const sampleItem = {
  id: 10,
  title: "Test Product",
  longDescription: "This is a detailed description of the test product.",
  available: true,
  isAvailable: true,
  sellerId: 99,
  vippsPaymentEnabled: true,
  shippingOptionName: "Standard Shipping", // try non local pickup by default
  shippingPrice: 10,
  categoryName: "Electronics",
  locationName: "Oslo",
  price: 100,
  currency: "NOK",
  sellerName: "Test Seller",
  brand: "BrandX",
  color: "Red",
  condition: "New",
  size: "M",
  createdAt: "2020-01-01T12:00:00Z",
  updatedAt: "2020-02-01T12:00:00Z",
  latitude: 59.91,
  longitude: 10.75,
}
const sampleImages = [
  { imageUrl: "http://example.com/img1.jpg" },
  { imageUrl: "http://example.com/img2.jpg" },
]

// Default mocks for ProductService
beforeEach(() => {
  vi.clearAllMocks()
  ;(ProductService.getItemById as any).mockResolvedValue({ data: sampleItem })
  ;(ProductService.getItemImages as any).mockResolvedValue({ data: sampleImages })
  // Reset router mocks
  mockPush.mockReset()
  mockReplace.mockReset()
})

describe('ProductDisplay.vue', () => {
  let wrapper: VueWrapper<any>

  // Use stubs for the many child components to isolate testing this component’s logic.
  const globalStubs = {
    Badge: { template: '<div></div>' },
    WishlistButton: { template: '<div></div>' },
    VippsPaymentModal: { template: '<div></div>' },
    ShippingDetailsModal: { template: '<div></div>' },
    PurchaseSuccessModal: { template: '<div></div>' },
    BaseModal: { template: '<div><slot /></div>' },
  }

  beforeEach(async () => {
    // Mount the component with the required prop "id"
    wrapper = shallowMount(ProductDisplay, {
      props: { id: sampleItem.id },
      global: { stubs: globalStubs },
    })
    // Wait for the onMounted hook to complete its async calls
    await nextTick()
    await nextTick()
  })

  // --- onMounted and Data Fetching ---
  it('fetches item and images on mount and sets location and sellerId', () => {
    expect(wrapper.vm.item).toEqual(sampleItem)
    expect(wrapper.vm.images).toEqual(sampleImages)
    expect(wrapper.vm.sellerId).toBe(sampleItem.sellerId)
    expect(wrapper.vm.location).toBe(`${sampleItem.latitude},${sampleItem.longitude}`)
  })

  // --- Computed Properties ---
  it('computes mainImageUrl based on selectedImageIndex', async () => {
    // By default, index is 0
    expect(wrapper.vm.mainImageUrl).toBe(sampleImages[0].imageUrl)
    // Change selectedImageIndex and check update
    wrapper.vm.selectedImageIndex = 1
    await nextTick()
    expect(wrapper.vm.mainImageUrl).toBe(sampleImages[1].imageUrl)
  })

  it('calculates totalPrice correctly (price + shippingFee)', () => {
    // Expected: 100 + 10 = 110
    expect(wrapper.vm.totalPrice).toBe(110)
  })

  it('computes getShippingCost correctly (non Local Pickup)', () => {
    // shippingCost should be shippingPrice (10) because shippingOptionName is not "Local Pickup"
    expect(wrapper.vm.getShippingCost).toBe(10)
  })

  // --- Method: handleBadgeClick ---
  it('navigates to the product list with correct query when badge is clicked', () => {
    // For category badge:
    wrapper.vm.handleBadgeClick({ type: 'category', value: 'Electronics' })
    expect(mockPush).toHaveBeenCalledWith({ path: '/', query: { category: 'Electronics' } })
    // For location badge:
    wrapper.vm.handleBadgeClick({ type: 'location', value: 'Oslo' })
    expect(mockPush).toHaveBeenCalledWith({ path: '/', query: { location: 'Oslo' } })
    // For shipping badge:
    wrapper.vm.handleBadgeClick({ type: 'shipping', value: 'Standard Shipping' })
    expect(mockPush).toHaveBeenCalledWith({ path: '/', query: { shipping: 'Standard Shipping' } })
  })

  // --- Method: handleBuyClick ---
  it('alerts and does not proceed if vippsPaymentEnabled is false', () => {
    // Set vippsPaymentEnabled to false and spy on window.alert
    wrapper.vm.item.vippsPaymentEnabled = false
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {})
    wrapper.vm.handleBuyClick()
    expect(alertSpy).toHaveBeenCalledWith('Please contact the seller to purchase this product')
    alertSpy.mockRestore()
  })

  it('shows Vipps modal immediately if item is local pickup', () => {
    // Set shippingOptionName to "Local Pickup"
    wrapper.vm.item.vippsPaymentEnabled = true
    wrapper.vm.item.shippingOptionName = 'Local Pickup'
    // Reset modals
    wrapper.vm.showShippingModal = false
    wrapper.vm.showVippsModal = false
    wrapper.vm.handleBuyClick()
    expect(wrapper.vm.showVippsModal).toBe(true)
    expect(wrapper.vm.showShippingModal).toBe(false)
  })

  it('shows Shipping modal if not local pickup', () => {
    // Reset modals for standard shipping (sampleItem is Standard Shipping)
    wrapper.vm.item.vippsPaymentEnabled = true
    wrapper.vm.item.shippingOptionName = 'Standard Shipping'
    wrapper.vm.showShippingModal = false
    wrapper.vm.showVippsModal = false
    wrapper.vm.handleBuyClick()
    expect(wrapper.vm.showShippingModal).toBe(true)
  })

  // --- Modal Navigation Methods ---
  it('handleShippingContinue saves details and toggles modals', () => {
    const shippingInfo = { phone: '12345678', address: 'Test Street 1' }
    wrapper.vm.showShippingModal = true
    wrapper.vm.showVippsModal = false
    wrapper.vm.handleShippingContinue(shippingInfo)
    expect(wrapper.vm.shippingDetails).toEqual(shippingInfo)
    expect(wrapper.vm.showShippingModal).toBe(false)
    expect(wrapper.vm.showVippsModal).toBe(true)
  })

  it('handleVippsBack toggles modals appropriately', () => {
    wrapper.vm.showVippsModal = true
    wrapper.vm.showShippingModal = false
    wrapper.vm.handleVippsBack()
    expect(wrapper.vm.showVippsModal).toBe(false)
    expect(wrapper.vm.showShippingModal).toBe(true)
  })

  it('handlePaymentComplete stores transaction data and shows success modal', () => {
    const transactionData = {
      id: 1,
      itemId: sampleItem.id,
      sellerId: sampleItem.sellerId,
      buyerId: 1,
      status: 'completed',
      amount: 110,
      createdAt: '2020-03-01T12:00:00Z',
      updatedAt: '2020-03-01T12:05:00Z',
      paymentMethod: 'vipps',
    }
    wrapper.vm.showVippsModal = true
    wrapper.vm.showSuccessModal = false
    wrapper.vm.handlePaymentComplete(transactionData)
    expect(wrapper.vm.transactionDetails).toEqual(transactionData)
    expect(wrapper.vm.showVippsModal).toBe(false)
    expect(wrapper.vm.showSuccessModal).toBe(true)
  })

  it('handleViewPurchases navigates to profile purchases and hides success modal', () => {
    wrapper.vm.showSuccessModal = true
    wrapper.vm.handleViewPurchases()
    expect(wrapper.vm.showSuccessModal).toBe(false)
    expect(mockPush).toHaveBeenCalledWith('/profile/purchases')
  })

  it('handleContinueShopping navigates to home and hides success modal', () => {
    wrapper.vm.showSuccessModal = true
    wrapper.vm.handleContinueShopping()
    expect(wrapper.vm.showSuccessModal).toBe(false)
    expect(mockPush).toHaveBeenCalledWith('/')
  })

  // --- Computed: isCurrentUserSeller ---
  it('computes isCurrentUserSeller correctly', async () => {
    // With authStore.user.id (1) and sellerId (99), result should be false
    expect(wrapper.vm.isCurrentUserSeller).toBe(false)
    // Change sellerId to match current user
    wrapper.vm.sellerId = 1
    await nextTick()
    expect(wrapper.vm.isCurrentUserSeller).toBe(true)
  })

  // --- Method: handleContactSeller ---
  describe('handleContactSeller', () => {
    it('returns early if not logged in', async () => {
      // Simulate logged-out state
      mockAuthStore.isLoggedIn = false
      await wrapper.vm.handleContactSeller()
      // No navigation should occur
      expect(mockPush).not.toHaveBeenCalled()
      // Restore login state for later tests
      mockAuthStore.isLoggedIn = true
    })

    it('navigates to an existing conversation if found', async () => {
      // Ensure logged in and set sample conversation
      mockAuthStore.user = { id: 1 }
      const existingConversation = {
        senderId: "1",
        receiverId: "99",
        itemId: sampleItem.id,
        conversationId: "conv123",
      }
      ;(MessagingService.getUserConversations as any).mockResolvedValue({ data: [existingConversation] })
      await wrapper.vm.handleContactSeller()
      expect(MessagingService.getUserConversations).toHaveBeenCalledWith(1)
      expect(mockPush).toHaveBeenCalledWith(`/messages/${existingConversation.conversationId}`)
    })

    it('creates a new conversation and navigates if none exists', async () => {
      mockAuthStore.user = { id: 1 }
      // Return empty conversation list
      ;(MessagingService.getUserConversations as any).mockResolvedValue({ data: [] })
      const newConversation = { conversationId: "newConv456", id: "newConv456" }
      ;(MessagingService.sendMessage as any).mockResolvedValue({ data: newConversation })
      await wrapper.vm.handleContactSeller()
      expect(MessagingService.getUserConversations).toHaveBeenCalledWith(1)
      expect(MessagingService.sendMessage).toHaveBeenCalled()
      expect(mockPush).toHaveBeenCalledWith(`/messages/${newConversation.conversationId}`)
    })
  })
})
