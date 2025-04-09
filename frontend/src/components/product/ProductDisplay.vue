<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import Badge from '@/components/utils/Badge.vue'
import WishlistButton from '@/components/utils/WishlistButton.vue'
import VippsPaymentModal from '@/components/modals/VippsPaymentModal.vue'
import ShippingDetailsModal from '@/components/modals/ShippingDetailsModal.vue'
import PurchaseSuccessModal from '@/components/modals/PurchaseSuccessModal.vue'
import BaseModal from '@/components/modals/BaseModal.vue'
import { MessagingService } from '@/api/services/MessagingService'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/AuthStore'
import { ProductService } from '@/api/services/ProductService'
interface ProductDisplayProps {
  id: number
}

const props = defineProps<ProductDisplayProps>()
const router = useRouter()
const authStore = useAuthStore()
const getItemById = async () => {
  const item = await ProductService.getItemById(props.id)
  return item.data
}
const getImagesByItemId = async () => {
  const images = await ProductService.getItemImages(props.id)
  return images.data
}

const location = ref<any>(null)
const sellerId = ref<number>(0)
const item = ref<any>(null)
const images = ref<any>(null)
const showShippingModal = ref(false)
const showVippsModal = ref(false)
const showSuccessModal = ref(false)
const isLoading = ref(false)
const shippingDetails = ref<any>(null)
const transactionDetails = ref<any>(null)
const selectedImageIndex = ref(0)

// Define transaction data interface
interface TransactionData {
  id: number
  itemId: number
  sellerId: number
  buyerId: number
  status: string
  amount: number
  createdAt: string
  updatedAt: string
  paymentMethod: string
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const handleBadgeClick = (event: { type: string; value: string }) => {
  let queryParams = {}

  switch (event.type) {
    case 'category':
      queryParams = { category: event.value }
      break
    case 'location':
      queryParams = { location: event.value }
      break
    case 'shipping':
      queryParams = { shipping: event.value }
      break
  }

  // Navigate to product list with the appropriate filter
  router.push({
    path: '/',
    query: queryParams,
  })
}

const isCurrentUserSeller = computed(() => {
  return authStore.user?.id === sellerId.value
})

const isItemAvailable = computed(() => {
  return item.value?.available !== false && item.value?.isAvailable !== false
})

const shouldDisableButtons = computed(() => {
  return isCurrentUserSeller.value || !isItemAvailable.value
})

const isLocalPickup = computed(() => {
  return item.value?.shippingOptionName === 'Local Pickup'
})

const computeCompositeId = (buyer: number, seller: number, item: number) => {
  // Ensure the user ids are in a known order (to handle both directions)
  const [firstId, secondId] = buyer < seller ? [buyer, seller] : [seller, buyer];
  return `${firstId}_${secondId}_${item}`;
};


const handleBuyClick = () => {
  if (!item.value?.vippsPaymentEnabled) {
    // Show message when Vipps is not enabled
    alert('Please contact the seller to purchase this product')
    return
  }

  if (isLocalPickup.value) {
    // Skip shipping details for local pickup
    showVippsModal.value = true
  } else {
    // Show shipping details first for other shipping methods
    showShippingModal.value = true
  }
}

const handleShippingContinue = (details: any) => {
  shippingDetails.value = details
  showShippingModal.value = false
  showVippsModal.value = true
}

const handleVippsBack = () => {
  showVippsModal.value = false
  showShippingModal.value = true
}

const handlePaymentComplete = (transactionData: TransactionData) => {
  transactionDetails.value = transactionData
  showVippsModal.value = false
  showSuccessModal.value = true
}

const handleViewPurchases = () => {
  showSuccessModal.value = false
  router.push('/profile/purchases')
}

const handleContinueShopping = () => {
  showSuccessModal.value = false
  router.push('/')
}

// Helper function to get shipping cost
const getShippingCost = computed(() => {
  if (!item.value || isLocalPickup.value) {
    return 0
  }
  return item.value.shippingPrice || 0
})

// Calculate total price including shipping
const totalPrice = computed(() => {
  const basePrice = item.value?.price || 0
  const shippingFee = getShippingCost.value || 0
  return basePrice + shippingFee
})

// Computed property for the main image URL
const mainImageUrl = computed(() => {
  return images.value?.[selectedImageIndex.value]?.imageUrl || '/default-product-image.jpg'
})

onMounted(async () => {
  item.value = await getItemById()

  location.value = item.value.latitude + ',' + item.value.longitude
  sellerId.value = item.value.sellerId
  images.value = await getImagesByItemId()
})

const handleContactSeller = async () => {
  try {
    if (!authStore.isLoggedIn || !authStore.user?.id || !sellerId.value) {
      return
    }

    // Check existing conversations
    const response = await MessagingService.getUserConversations(authStore.user.id)
    console.log(response.data)

    const compositeId = computeCompositeId(authStore.user.id, sellerId.value, props.id)
    console.log(compositeId)

    //check if conversation already exists with this seller for this item
    const existingConversation = response.data.find(
      (conv: any) => conv.conversationId === compositeId
    )

    if (existingConversation){
      router.push(`/messages/${existingConversation.conversationId}`)
      return
    } else {
      //create a new conversation
      console.log('creating new conversation')
      const messageResponse = await MessagingService.sendMessage({
        senderId: authStore.user.id.toString(),
        receiverId: sellerId.value.toString(),
        content: `Hi! I'm interested in your item: ${item.value?.title}`,
        itemId: props.id
      })

      // The message response should include the conversation ID
      const newMessageData = messageResponse.data
      const chatId = compositeId

      // Navigate to the messages view with the new conversation
      router.push(`/messages/${chatId}`)
    }
  } catch (error) {
    console.error('Error starting conversation:', error)
  }
}
</script>

<template>
  <div v-if="isLoading" class="loading-popup">
    <p>Processing your purchase...</p>
  </div>

  <!-- ShippingDetailsModal component - only show if Vipps is enabled -->
  <BaseModal
    v-if="showShippingModal && item.vippsPaymentEnabled"
    @close="showShippingModal = false"
  >
    <ShippingDetailsModal
      :shipping-option-name="item.shippingOptionName"
      :initial-values="shippingDetails"
      @close="showShippingModal = false"
      @continue="handleShippingContinue"
    />
  </BaseModal>

  <!-- VippsPaymentModal component - only show if Vipps is enabled -->
  <BaseModal v-if="showVippsModal && item.vippsPaymentEnabled" @close="showVippsModal = false">
    <VippsPaymentModal
      :item-id="item.id"
      :item-title="item.title"
      :item-price="item.price"
      :seller-id="sellerId"
      :buyer-id="authStore.user?.id || 0"
      :shipping-option-name="!isLocalPickup ? item.shippingOptionName : undefined"
      :shipping-cost="getShippingCost.value"
      :shipping-phone-number="shippingDetails?.phone"
      @close="showVippsModal = false"
      @back="handleVippsBack"
      @payment-complete="handlePaymentComplete"
    />
  </BaseModal>

  <!-- Success Modal -->
  <BaseModal v-if="showSuccessModal" @close="handleContinueShopping">
    <PurchaseSuccessModal
      :item-title="item.title"
      :total-amount="totalPrice"
      :shipping-address="!isLocalPickup ? shippingDetails : undefined"
      @view-purchases="handleViewPurchases"
      @close="handleContinueShopping"
    />
  </BaseModal>

  <div v-if="item" class="product-display">
    <div class="product-image-container">
      <div class="gallery-container" v-if="images && images.length > 1">
        <div
          v-for="(image, index) in images"
          :key="index"
          class="gallery-item"
          :class="{ active: index === selectedImageIndex }"
          @click="selectedImageIndex = index"
        >
          <img :src="image.imageUrl" :alt="'Product image ' + (index + 1)" class="gallery-image" />
        </div>
      </div>
      <div class="main-image-container">
        <img
          :src="mainImageUrl"
          :alt="'Main product image'"
          class="main-image"
        />
      </div>
    </div>

    <div class="product-details">
      <div id="product-title">
        <h3>{{ item.title }}</h3>
      </div>
      <div id="product-description">
        <p>{{ item.longDescription }}</p>
        <Badge :name="item.categoryName || 'N/A'" type="category" @click="handleBadgeClick" />
        <Badge :name="item.locationName || 'N/A'" type="location" @click="handleBadgeClick" />
        <Badge
          :name="item.price.toString() || 'N/A'"
          :currency="item.currency || 'NOK'"
          type="price"
          :borderColor="item.price ? '#3A4951' : undefined"
        />
      </div>
      <div id="seller-info">
        <Badge :name="item.sellerName || 'N/A'" type="seller" />
        <Badge :name="item.shippingOptionName || 'N/A'" type="shipping" @click="handleBadgeClick" />
      </div>
      <div class="action-buttons">
        <button
          class="contact-button"
          @click="handleContactSeller"
          :disabled="shouldDisableButtons"
        >
          Contact Seller
        </button>
        <button class="buy-button" @click="handleBuyClick" :disabled="shouldDisableButtons">
          Buy Item
        </button>
        <WishlistButton
          :product-id="item.id"
          :is-available="isItemAvailable"
        />
      </div>
      <div class="product-details-list">
        <p class="detail-item">
          <span class="detail-label">Brand:</span>
          <span class="detail-value">{{ item.brand }}</span>
        </p>
        <p class="detail-item">
          <span class="detail-label">Color:</span>
          <span class="detail-value">{{ item.color }}</span>
        </p>
        <p class="detail-item">
          <span class="detail-label">Condition:</span>
          <span class="detail-value">{{ item.condition }}</span>
        </p>
        <p class="detail-item">
          <span class="detail-label">Size:</span>
          <span class="detail-value">{{ item.size }}</span>
        </p>
        <img
          v-if="item.vippsPaymentEnabled"
          src="@/assets/images/vipps.png"
          alt="Vipps Payment Available"
          class="vipps-image"
        />
      </div>
      <div id="product-info">
        <div class="info-item">
          <span class="info-label">Posted:</span>
          <span class="info-value">{{ formatDate(item.createdAt) }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">Updated:</span>
          <span class="info-value">{{ formatDate(item.updatedAt) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
.product-display {
  display: flex;
  background-color: #eff7f3;
  padding: 2rem;
  border-radius: 8px;
}

.product-image-container {
  display: flex;
  margin-right: 20px;
  align-items: flex-start;
}

.gallery-container {
  display: flex;
  flex-direction: column;
  margin-right: 15px;
  height: 300px;
  width: 90px;
  overflow-y: auto;
}

.main-image-container {
  flex-grow: 1;
}

.main-image {
  max-width: 300px;
  height: auto;
  display: block;
}

.gallery-item {
  width: 90px;
  height: 90px;
  margin-bottom: 10px;
  flex-shrink: 0;
  overflow: hidden;
}

.gallery-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.2s;
  opacity: 0.7;
}

.gallery-image:hover {
  border-color: #4a90e2;
}

.gallery-item.active .gallery-image {
  border-color: #4a90e2;
  opacity: 1;
}

.gallery-image:hover {
  opacity: 1;
}

.product-details {
  flex: 1;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1.5rem;
  margin-bottom: 1.5rem;
}

.contact-button {
  background-color: #6b5b95;
  color: white;
  border: none;
  border-radius: 0.375rem;
  padding: 0.75rem 1.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.contact-button:hover:not(:disabled) {
  background-color: #5a4b7d;
}

.contact-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  opacity: 0.7;
}

#product-info {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
  font-size: 0.875rem;
  color: #64748b;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.contact-button:disabled::after {
  content: 'Please log in to contact seller';
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  padding: 0.5rem;
  background-color: #333;
  color: white;
  border-radius: 0.25rem;
  font-size: 0.75rem;
  white-space: nowrap;
  opacity: 0;
  pointer-events: none;
  transition: opacity 0.2s ease;
}

.contact-button:disabled:hover::after {
  opacity: 1;
}

.info-label {
  font-weight: 500;
  margin-right: 0.5rem;
  color: #475569;
}

.info-value {
  color: #64748b;
}

.vipps-image {
  max-width: 100px;
  height: auto;
  margin-top: 10px;
}

.product-details-list {
  margin: 1rem 0;
  padding: 1rem;
  /*background-color: #f8f9fa;*/
  background-color: white;
  border-radius: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
  color: #3a4951;
}

.detail-label {
  font-weight: 600;
  min-width: 100px;
  color: #64748b;
}

.detail-value {
  color: #3a4951;
}

.loading-popup {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  font-size: 1.5rem;
  z-index: 1000;
}

.buy-button {
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 0.375rem;
  padding: 0.75rem 1.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.buy-button:hover:not(:disabled) {
  background-color: #45a049;
}

.buy-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  opacity: 0.7;
}
</style>
