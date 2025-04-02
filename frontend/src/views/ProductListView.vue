<script setup lang="ts">
import { ref, nextTick, onMounted } from 'vue'
import ProductDisplayModal from '@/components/modals/ProductDisplayModal.vue'
import ProductCard from '@/components/product/ProductCard.vue'
import axios from 'axios'
import type { Product } from '@/types/product'

const items = ref<Product[]>([])
const selectedProductId = ref<number | null>(null)

onMounted(async () => {
  const response = await axios.get('api/items')
  items.value = response.data
})

const showProductModal = ref(false)
const productModalRef = ref(null)

const openProductModal = (productId: number) => {
  selectedProductId.value = productId
  showProductModal.value = true
}
</script>

<template>
  <div class="product-list">
    <h2>Featured Products</h2>
    <div class="products-grid">
      <ProductCard
        v-for="item in items"
        :key="item.id"
        :id="item.id"
        :title="item.title"
        :price="Number(item.price)"
        :category="item.category"
        :image="item.images[0]"
        :location="item.location"
        :isVippsPaymentEnabled="true"
        :isWishlisted="false"
        @click="openProductModal(item.id)"
      />
    </div>
    <ProductDisplayModal
      v-if="showProductModal && selectedProductId"
      :product-id="selectedProductId"
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
</style>
