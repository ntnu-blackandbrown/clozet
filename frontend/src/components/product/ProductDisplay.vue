<script setup>
import { ref } from 'vue'
import Badge from '@/components/utils/Badge.vue'
import WishlistButton from '@/components/utils/WishlistButton.vue'

// Define props
const props = defineProps({
  images: {
    type: Array,
    default: () => [],
  },
  itemId: {
    type: String,
    default: '',
  },
  title: {
    type: String,
    default: 'Nike Shoes',
  },
  description_full: {
    type: String,
    default:
      "This is a long description of the product. Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
  },
  category: {
    type: String,
    default: 'Shoes',
  },
  location: {
    type: String,
    default: 'Oslo',
  },
  price: {
    type: Number,
    default: 1000,
  },
  seller: {
    type: String,
    default: 'John Doe',
  },
  shipping_options: {
    type: String,
    default: 'Standard Shipping',
  },
  status: {
    type: String,
    default: 'Available',
  },
  created_at: {
    type: String,
    default: '2021-01-01',
  },
  updated_at: {
    type: String,
    default: '2021-01-01',
  },
  purchased: {
    type: Boolean,
    default: false,
  },
})

// Remove the static image imports and ref
</script>

<template>
  <div class="product-display">
    <div class="product-image-container">
      <div class="gallery-container">
        <div v-for="(image, index) in images" :key="index" class="gallery-item">
          <img :src="image" :alt="'Product image ' + (index + 1)" class="gallery-image" />
        </div>
      </div>
      <div class="main-image-container">
        <img :src="images[0]" :alt="'Main product image'" class="main-image" />
      </div>
    </div>

    <div class="product-details">
      <div id="product-title">
        <h3>{{ title }}</h3>
      </div>
      <div id="product-description">
        <p>{{ description_full }}</p>
        <Badge :name="category" type="category" />
        <Badge :name="location" type="location" />
        <Badge :amount="price" currency="NOK" type="price" />
      </div>
      <div id="seller-info">
        <Badge :name="seller" type="seller" />
        <Badge :name="shipping_options" type="shipping" />
        <Badge :name="status" type="availability" />
      </div>
      <div class="action-buttons">
        <button class="contact-button">Contact Seller</button>
        <WishlistButton :product-id="itemId" :purchased="purchased" />
      </div>
      <div id="product-info">
        <div class="info-item">
          <span class="info-label">Posted:</span>
          <span class="info-value">{{ created_at }}</span>
        </div>
        <div class="info-item">
          <span class="info-label">Updated:</span>
          <span class="info-value">{{ updated_at }}</span>
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
</style>
