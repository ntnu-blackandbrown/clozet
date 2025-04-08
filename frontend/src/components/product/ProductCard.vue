<script setup lang="ts">
import WishlistButton from '@/components/utils/WishlistButton.vue'
import Badge from '@/components/utils/Badge.vue'

interface ProductCardProps {
  id: number
  title: string
  price: number
  category: string
  image: string
  location: string
  isVippsPaymentEnabled: boolean
  isWishlisted: boolean
  isAvailable?: boolean
}

const props = defineProps<ProductCardProps>()

const emit = defineEmits(['click'])

const handleClick = () => {
  if (props.isAvailable !== false) {
    emit('click', props.id)
  }
}
</script>

<template>
  <div class="product-card" :class="{ sold: isAvailable === false }" @click="handleClick">
    <div class="product-image">
      <img :src="props.image" :alt="props.title" />
      <div class="wishlist-container">
        <WishlistButton
          :product-id="props.id"
          :is-wishlisted="props.isWishlisted"
          :is-available="props.isAvailable"
          @click.stop
        />
      </div>
      <div v-if="isAvailable === false" class="sold-overlay">
        <span>SOLD</span>
      </div>
    </div>
    <div class="product-info">
      <h3>{{ title }}</h3>
      <Badge :name="props.price.toString()" type="price" :currency="'NOK'" />
      <Badge :name="props.category" type="category" />
      <button class="view-details" :disabled="isAvailable === false">
        {{ isAvailable === false ? 'Sold' : 'View Details' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.product-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
  background-color: #3a4951;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.1);
}

.product-image {
  height: 200px;
  overflow: hidden;
  position: relative;
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
  color: #c3d7cc;
}

.product-info h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  color: white;
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
  background-color: #f1e7ca;
  color: #3a4951;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.view-details:hover {
  background-color: #e5d9b8;
}

.wishlist-container {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 1;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  padding: 4px;
}

.product-card.sold {
  opacity: 0.8;
  cursor: default;
}

.product-card.sold:hover {
  transform: none;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.sold-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.sold-overlay span {
  background-color: #ff4b4b;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-weight: bold;
  font-size: 1.2rem;
}

.view-details:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
</style>
