<script setup>
import { ref } from 'vue'
import ProductCard from '@/components/product/ProductCard.vue'
import ProductDisplayModal from '@/components/modals/ProductDisplayModal.vue'

const showProductModal = ref(false)
const selectedProductId = ref('')

const purchaseHistory = [
  {
    id: 'purchase-1',
    title: 'Vintage Sunglasses',
    price: 600,
    category: 'Accessories',
    image: '/src/assets/images/main-image.png',
    purchaseDate: '2024-03-15',
    purchased: true,
  },
  {
    id: 'purchase-2',
    title: 'Summer T-Shirt',
    price: 250,
    category: 'Clothing',
    image: '/src/assets/images/image-1.png',
    purchaseDate: '2024-03-10',
    purchased: true,
  },
]

const openProductModal = (productId) => {
  selectedProductId.value = productId
  showProductModal.value = true
}
</script>

<template>
  <div class="my-purchases-container">
    <h2>My Purchases</h2>
    <div class="purchases-list">
      <div v-for="purchase in purchaseHistory" :key="purchase.id" class="purchase-item">
        <ProductCard v-bind="purchase" @click="openProductModal(purchase.id)" />
        <div class="purchase-date">Purchased on: {{ purchase.purchaseDate }}</div>
      </div>
    </div>

    <!-- Product Display Modal -->
    <ProductDisplayModal
      v-if="showProductModal"
      :productId="selectedProductId"
      @close="showProductModal = false"
    />
  </div>
</template>

<style scoped>
.my-purchases-container {
  padding: 2rem;
}

.my-purchases-container h2 {
  margin-bottom: 2rem;
  color: #333;
  font-size: 1.5rem;
}

.purchases-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.5rem;
}

.purchase-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.purchase-item :deep(.product-card) {
  width: 100%;
  margin: 0;
  cursor: pointer;
}

.purchase-item :deep(.product-card:hover) {
  transform: translateY(-2px);
  transition: transform 0.2s ease;
}

.purchase-item :deep(.product-image) {
  height: 150px;
}

.purchase-item :deep(.product-info h3) {
  font-size: 1rem;
}

.purchase-item :deep(.price) {
  font-size: 0.9rem;
}

.purchase-item :deep(.category) {
  font-size: 0.8rem;
}

.purchase-date {
  color: #6b7280;
  font-size: 0.875rem;
  text-align: center;
  margin-top: 0.5rem;
}

@media (max-width: 768px) {
  .my-purchases-container {
    padding: 1rem;
  }
}
</style>
