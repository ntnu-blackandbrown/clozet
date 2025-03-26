<script setup>
import { ref, nextTick } from 'vue'
import ProductDisplayModal from './ProductDisplayModal.vue'

// Sample product data
const products = ref([
  {
    id: 'prod-1',
    title: 'Nike Running Shoes',
    price: '1200',
    category: 'Shoes',
    image: '/src/assets/images/main-image.png'
  },
  {
    id: 'prod-2',
    title: 'Designer Backpack',
    price: '800',
    category: 'Bags',
    image: '/src/assets/images/image-1.png'
  },
  {
    id: 'prod-3',
    title: 'Casual Denim Jacket',
    price: '950',
    category: 'Clothing',
    image: '/src/assets/images/image-2.png'
  }
])

const showProductModal = ref(false)
const productModalRef = ref(null)

const openProductModal = (productId) => {
  showProductModal.value = true
  // Use nextTick to ensure the modal is rendered before setting the productId
  nextTick(() => {
    productModalRef.value?.setProductId(productId)
  })
}
</script>

<template>
  <div class="product-list">
    <h2>Featured Products</h2>

    <div class="products-grid">
      <div
        v-for="product in products"
        :key="product.id"
        class="product-card"
        @click="openProductModal(product.id)"
      >
        <div class="product-image">
          <img :src="product.image" :alt="product.title">
        </div>
        <div class="product-info">
          <h3>{{ product.title }}</h3>
          <p class="price">{{ product.price }} NOK</p>
          <p class="category">{{ product.category }}</p>
          <button class="view-details">View Details</button>
        </div>
      </div>
    </div>

    <ProductDisplayModal
      v-if="showProductModal"
      ref="productModalRef"
      @close="showProductModal = false"
    />
  </div>
</template>

<style scoped>
.product-list {
  padding: 1rem;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}

.product-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 200px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  padding: 1rem;
}

.product-info h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
}

.price {
  font-weight: bold;
  color: #3b82f6;
  margin: 0.25rem 0;
}

.category {
  color: #6b7280;
  font-size: 0.9rem;
  margin: 0.25rem 0 1rem 0;
}

.view-details {
  width: 100%;
  padding: 0.5rem;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.view-details:hover {
  background-color: #2563eb;
}
</style>
