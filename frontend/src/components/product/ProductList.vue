<script setup lang="ts">
import { ref, nextTick, onMounted, watch } from 'vue'
import ProductDisplayModal from '@/components/modals/ProductDisplayModal.vue'
import ProductCard from '@/components/product/ProductCard.vue'
import type { Product } from '@/types/product'
import { useRouter } from 'vue-router'

const router = useRouter()
const props = defineProps<{
  items: Product[]
  initialProductId?: number | null
  routeBasePath?: string
}>()

const selectedProductId = ref<number | null>(null)

const showProductModal = ref(false)
const productModalRef = ref(null)

const openProductModal = (productId: number) => {
  selectedProductId.value = productId
  showProductModal.value = true

  const basePath = props.routeBasePath || '/products/'
  router.replace(`${basePath}${productId}`)
}

// Watch for changes in the initialProductId prop
watch(
  () => props.initialProductId,
  (newId) => {
    if (newId) {
      selectedProductId.value = newId
      showProductModal.value = true
    } else {
      showProductModal.value = false
    }
  },
  { immediate: true },
)
</script>

<template>
  <div class="product-list">
    <div v-if="items.length === 0" class="no-items" role="status" aria-live="polite">No items available</div>
    <div v-else class="products-grid">
      <ProductCard
        v-for="item in items"
        :key="item.id"
        :id="item.id"
        :title="item.title"
        :price="Number(item.price)"
        :category="item.category"
        :image="item.image || '/default-product-image.jpg'"
        :location="item.location"
        :isVippsPaymentEnabled="item.vippsPaymentEnabled"
        :isWishlisted="item.wishlisted"
        :isAvailable="item.isAvailable"
        @click="openProductModal(item.id)"
        :aria-label="`View details for ${item.title}, priced at ${item.price}`"
      />
    </div>
    <ProductDisplayModal
      v-if="showProductModal && selectedProductId"
      :product-id="selectedProductId"
      @close="showProductModal = false"
      aria-label="Product details"
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

.no-items {
  text-align: center;
  padding: 2rem;
  color: #666;
}
</style>
