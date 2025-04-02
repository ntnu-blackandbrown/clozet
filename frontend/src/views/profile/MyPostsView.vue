<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import ProductCard from '@/components/product/ProductCard.vue'
import ProductDisplayModal from '@/components/modals/ProductDisplayModal.vue'
import type { Product } from '@/types/product'

const myPosts = ref<Product[]>([])
const props = defineProps<{
  userId: number
}>()

onMounted(async () => {
  const response = await axios.get(`api/items/seller/${props.userId}`)
  myPosts.value = response.data
})

const showProductModal = ref(false)
const selectedProductId = ref<number | null>(null)

const openProductModal = (productId: number) => {
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
