<script setup>
import { ref } from 'vue'
import Badge from '@/components/Badge.vue'

// Define props
const props = defineProps({
  imageUrl: {
    type: String,
    default: '',
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
})

// Import all images from assets/images
import mainImage from '@/assets/images/main-image.png'
import image1 from '@/assets/images/image-1.png'
import image2 from '@/assets/images/image-2.png'
import image3 from '@/assets/images/image-3.png'
import screenshot from '@/assets/images/Screenshot 2025-03-26 at 17.26.14.png'

// Store all images in an array
const images = ref([
  { src: mainImage, alt: 'Main Image' },
  { src: image1, alt: 'Image 1' },
  { src: image2, alt: 'Image 2' },
  { src: image3, alt: 'Image 3' },
  { src: screenshot, alt: 'Screenshot' },
])
</script>

<template>
  <div class="product-display">
    <div class="product-image-container">
      <div class="gallery-container">
        <div v-for="(image, index) in images" :key="index" class="gallery-item">
          <img :src="image.src" :alt="image.alt" class="gallery-image" />
        </div>
      </div>
      <div class="main-image-container">
        <img :src="images[0].src" :alt="images[0].alt" class="main-image" />
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
        <button class="wishlist-button">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="24"
            height="24"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="heart-icon"
          >
            <path
              d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"
            ></path>
          </svg>
        </button>
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

.wishlist-button {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem;
  border-radius: 50%;
  border: 1px solid #e2e8f0;
  background-color: white;
  cursor: pointer;
  transition: all 0.2s ease;
}

.wishlist-button:hover {
  background-color: #fef2f2;
  border-color: #fecaca;
}

.wishlist-button .heart-icon {
  color: #64748b;
  transition: color 0.2s ease;
}

.wishlist-button:hover .heart-icon {
  color: #e11d48;
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
