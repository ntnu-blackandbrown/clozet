<script setup>
import { ref } from 'vue'
import ProductCard from '@/components/product/ProductCard.vue'
import ProductDisplayModal from '@/components/modals/ProductDisplayModal.vue'

const showProductModal = ref(false)
const selectedProductId = ref('')

const myPosts = [
  {
    id: 'post-1',
    title: 'Nike Running Shoes',
    price: 1200,
    category: 'Shoes',
    image: '/src/assets/images/main-image.png',
  },
  {
    id: 'post-2',
    title: 'Designer Backpack',
    price: 800,
    category: 'Bags',
    image: '/src/assets/images/image-1.png',
  },
  {
    id: 'post-3',
    title: 'Casual Denim Jacket',
    price: 950,
    category: 'Clothing',
    image: '/src/assets/images/image-2.png',
  },
]

const openProductModal = (productId) => {
  selectedProductId.value = productId
  showProductModal.value = true
}
</script>

<template>
  <div class="my-posts-container">
    <h2>My Posts</h2>
    <div class="posts-grid">
      <ProductCard
        v-for="post in myPosts"
        :key="post.id"
        v-bind="post"
        @click="openProductModal(post.id)"
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
.my-posts-container {
  padding: 2rem;
}

.my-posts-container h2 {
  margin-bottom: 2rem;
  color: #333;
  font-size: 1.5rem;
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

@media (max-width: 768px) {
  .my-posts-container {
    padding: 1rem;
  }
}
</style>
