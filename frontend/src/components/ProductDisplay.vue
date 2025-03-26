<script setup>
import { ref, onMounted } from 'vue'
import Badge from '../components/Badge.vue'
const imageUrl = ref('')
const itemId = ref('')
const title = ref('Nike Shoes')
const description_full = ref(
  "This is a long description of the product. Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.",
)
const category = ref('Shoes')
const location = ref('Oslo')
const price = ref('1000')
const seller = ref('John Doe')
const shipping_options = ref('Standard Shipping')
const status = ref('Available')
const created_at = ref('2021-01-01')
const updated_at = ref('2021-01-01')

// Import all images from assets/images
import mainImage from '../assets/images/main-image.png'
import image1 from '../assets/images/image-1.png'
import image2 from '../assets/images/image-2.png'
import image3 from '../assets/images/image-3.png'
import screenshot from '../assets/images/Screenshot 2025-03-26 at 17.26.14.png'

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
        <Badge name="Your Category Name" type="category" />
        <Badge name="Your Location Name" type="location" />
        <Badge amount="1000" currency="NOK" type="price" />
      </div>
      <div id="seller-info">
        <Badge name="Your Seller Name" type="seller" />
        <p>{{ shipping_options }}</p>
        <p>{{ status }}</p>
      </div>
      <div>
        <button>Contact Seller</button>
        <button>Wishlist</button>
      </div>
      <div id="product-info">
        <p>{{ created_at }}</p>
        <p>{{ updated_at }}</p>
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
</style>
