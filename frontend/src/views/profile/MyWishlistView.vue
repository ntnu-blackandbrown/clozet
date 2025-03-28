<script setup>
import { ref } from 'vue'
import ProductCard from '@/components/product/ProductCard.vue'
import ProductDisplayModal from '@/components/modals/ProductDisplayModal.vue'

const showProductModal = ref(false)
const selectedProductId = ref('')

const wishlistItems = [
  {
    id: 'wish-1',
    title: 'Leather Wallet',
    price: 450,
    category: 'Accessories',
    image: '/src/assets/images/image-3.png',
  },
  {
    id: 'wish-2',
    title: 'Smart Watch',
    price: 2500,
    category: 'Electronics',
    image: '/src/assets/images/Screenshot 2025-03-26 at 17.26.14.png',
  },
]

const openProductModal = (productId) => {
  selectedProductId.value = productId
  showProductModal.value = true
}
</script>

<template>
  <div class="my-wishlist-container">
    <h2>My Wishlist</h2>
    <div class="wishlist-grid">
      <ProductCard
        v-for="item in wishlistItems"
        :key="item.id"
        v-bind="item"
        @click="openProductModal(item.id)"
      />
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
.my-wishlist-container {
  padding: 2rem;
}

.my-wishlist-container h2 {
  margin-bottom: 2rem;
  color: #333;
  font-size: 1.5rem;
}

.wishlist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

@media (max-width: 768px) {
  .my-wishlist-container {
    padding: 1rem;
  }
}
</style>
