<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Badge from '@/components/utils/Badge.vue'
import WishlistButton from '@/components/utils/WishlistButton.vue'
import axios from 'axios'
interface ProductDisplayProps {
  id: number
}

const props = defineProps<ProductDisplayProps>()

const getItemById = async () => {
  const item = await axios.get(`/api/items/${props.id}`)
  return item.data
}

const location = ref<any>(null)

const item = ref<any>(null)

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(async () => {
  item.value = await getItemById()
  location.value = item.value.latitude + ',' + item.value.longitude
})
</script>

<template>
  <div v-if="item" class="product-display">
    <div class="product-image-container">
      <div class="gallery-container">
        <div v-for="(image, index) in item.images || []" :key="index" class="gallery-item">
          <img :src="image" :alt="'Product image ' + (index + 1)" class="gallery-image" />
        </div>
      </div>
      <div class="main-image-container">
        <img :src="item.images?.[0] || '/default-product-image.jpg'" :alt="'Main product image'" class="main-image" />
      </div>
    </div>

    <div class="product-details">
      <div id="product-title">
        <h3>{{ item.title }}</h3>
      </div>
      <div id="product-description">
        <p>{{ item.longDescription }}</p>
        <Badge :name="item.categoryName || 'N/A'" type="category" />
        <Badge :name="item.locationName || 'N/A'" type="location" />
        <Badge
          :name="item.price.toString()|| 'N/A'"
          :currency="item.currency || 'NOK'"
          type="price"
          :borderColor="item.price ? '#3A4951' : undefined"
        />
      </div>
      <div id="seller-info">
        <Badge :name="item.sellerName || 'N/A'" type="seller" />
        <Badge :name="item.shippingOptionName || 'N/A'" type="shipping" />
        <Badge :name="item.available ? 'Available' : 'Not Available'" type="availability" />
      </div>
      <div class="action-buttons">
        <button class="contact-button">Contact Seller</button>
        <WishlistButton :product-id="item.id" :purchased="item.purchased" />
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
        <img v-if="item.vippsPaymentEnabled" src="@/assets/images/vipps.png" alt="Vipps Payment Available" class="vipps-image" />
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
  background-color: #EFF7F3;
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
}

.gallery-image:hover {
  border-color: #4a90e2;
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
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 0.375rem;
  padding: 0.75rem 1.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.contact-button:hover {
  background-color: #2563eb;
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
  color: #3A4951;
}

.detail-label {
  font-weight: 600;
  min-width: 100px;
  color: #64748b;
}

.detail-value {
  color: #3A4951;
}
</style>
