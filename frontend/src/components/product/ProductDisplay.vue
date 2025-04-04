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

onMounted(async () => {
  item.value = await getItemById()
  location.value = item.value.latitude + ',' + item.value.longitude
  console.log('=== FULL ITEM DATA ===')
  console.log('ID:', item.value.id)
  console.log('Title:', item.value.title)
  console.log('Available:', item.value.available)
  console.log('Brand:', item.value.brand)
  console.log('Category ID:', item.value.categoryId)
  console.log('Category Name:', item.value.categoryName)
  console.log('Color:', item.value.color)
  console.log('Condition:', item.value.condition)
  console.log('Created At:', item.value.createdAt)
  console.log('Images:', item.value.images)
  console.log('Latitude:', item.value.latitude)
  console.log('Location ID:', item.value.locationId)
  console.log('Location Name:', item.value.locationName)
  console.log('Longitude:', item.value.longitude)
  console.log('Long Description:', item.value.longDescription)
  console.log('Price:', item.value.price)
  console.log('Seller ID:', item.value.sellerId)
  console.log('Seller Name:', item.value.sellerName)
  console.log('Shipping Option ID:', item.value.shippingOptionId)
  console.log('Shipping Option Name:', item.value.shippingOptionName)
  console.log('Short Description:', item.value.shortDescription)
  console.log('Size:', item.value.size)
  console.log('Updated At:', item.value.updatedAt)
  console.log('=====================')
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
        <Badge :name="item.price.toString()|| 'N/A'" :currency="item.currency || 'NOK'" type="price" />
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
      <div>
        <p>
          {{ item.brand }}
        </p>
        <p>{{ item.color }}</p>
        <p>{{ item.condition }}</p>
        <p>{{ item.size }}</p>
        <img v-if="item.vippsPaymentEnabled" src="@/assets/images/vipps.png" alt="Vipps Payment Available" class="vipps-image" />
      </div>
      <div id="product-info">
        <div class="info-item">
          <span class="info-label">Posted:</span>
          <span class="info-value">{{ item.createdAt }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">Updated:</span>
          <span class="info-value">{{ item.updatedAt }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
.product-display {
  display: flex;
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
</style>
