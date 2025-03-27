<script setup>
import { ref, nextTick } from 'vue'
import ProductDisplayModal from '../components/modals/ProductDisplayModal.vue'
import ProductCard from './ProductCard.vue'

const props = defineProps({
  productId: {
    type: Number,
    default: '',
  },
  title: {
    type: String,
    default: 'Nike Running Shoes',
  },
  price: {
    type: Number,
    default: 1200,
  },
  category: {
    type: String,
    default: 'Shoes',
  },
  image: {
    type: String,
    default: '/src/assets/images/main-image.png',
  },
})

// Sample product data
const products = ref([
  {
    id: 'prod-1',
    title: 'Nike Running Shoes',
    price: '1200',
    category: 'Shoes',
    image: '/src/assets/images/main-image.png',
  },
  {
    id: 'prod-2',
    title: 'Designer Backpack',
    price: '800',
    category: 'Bags',
    image: '/src/assets/images/image-1.png',
  },
  {
    id: 'prod-3',
    title: 'Casual Denim Jacket',
    price: '950',
    category: 'Clothing',
    image: '/src/assets/images/image-2.png',
  },
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
      <ProductCard
        v-for="product in products"
        :key="product.id"
        v-bind="product"
        @click="openProductModal"
      />
      <ProductCard
        v-for="product in products"
        :key="product.id"
        v-bind="product"
        @click="openProductModal"
      />
      <ProductCard
        v-for="product in products"
        :key="product.id"
        v-bind="product"
        @click="openProductModal"
      />
      <ProductCard
        v-for="product in products"
        :key="product.id"
        v-bind="product"
        @click="openProductModal"
      />
      <ProductCard
        v-for="product in products"
        :key="product.id"
        v-bind="product"
        @click="openProductModal"
      />
      <ProductCard
        v-for="product in products"
        :key="product.id"
        v-bind="product"
        @click="openProductModal"
      />
      <ProductCard
        v-for="product in products"
        :key="product.id"
        v-bind="product"
        @click="openProductModal"
      />
      <ProductCard
        v-for="product in products"
        :key="product.id"
        v-bind="product"
        @click="openProductModal"
      />
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
</style>
