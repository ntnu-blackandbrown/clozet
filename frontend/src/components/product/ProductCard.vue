<script setup>
import { defineProps } from 'vue'
import WishlistButton from './WishlistButton.vue'

const props = defineProps({
  id: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  price: {
    type: Number,
    required: true,
  },
  category: {
    type: String,
    required: true,
  },
  image: {
    type: String,
    required: true,
  },
})

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click', props.id)
}
</script>

<template>
  <div class="product-card" @click="handleClick">
    <div class="product-image">
      <img :src="image" :alt="title" />
      <div class="wishlist-container">
        <WishlistButton :product-id="id" @click.stop />
      </div>
    </div>
    <div class="product-info">
      <h3>{{ title }}</h3>
      <p class="price">{{ price }} NOK</p>
      <p class="category">{{ category }}</p>
      <button class="view-details">View Details</button>
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

.wishlist-container {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 1;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  padding: 4px;
}
</style>
