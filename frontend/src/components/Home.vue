<script setup>
import ProductList from './ProductList.vue'
import Badge from './Badge.vue'
</script>

<template>
  <h1>Welcome to Clozet!</h1>
  <h3>The new way to shop for clothes</h3>

  <!--get the five categories with the most products from the database -->
  <div class="badge-container">
    <Badge type="category" name="Tops" />
    <Badge type="category" name="Bottoms" />
    <Badge type="category" name="Dresses" />
    <Badge type="category" name="Accessories" />
    <Badge type="category" name="Shoes" />
  </div>
  <div class="search-wrapper">
    <input
      class="search-bar"
      type="text"
      placeholder="Search for a product..."
    />
    <!-- Inline SVG icon -->
    <svg
      class="search-icon"
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      stroke-width="2"
      stroke-linecap="round"
      stroke-linejoin="round"
    >
      <circle cx="11" cy="11" r="8" />
      <line x1="21" y1="21" x2="16.65" y2="16.65" />
    </svg>
  </div>

  <div class="featured-products">
    <ProductList />
  </div>
</template>

<style scoped>
.featured-products {
  width: 100%;
}

.search-wrapper {
  position: relative; /* Let us position the icon absolutely within this container */
  display: inline-block;
}

.search-bar {
  width: 300px;
  padding: 8px 12px;
  font-size: 16px;
  border: 2px solid ;
  border-radius: 24px;
  padding-right: 40px; /* space for the icon on the right */
}

/* Position the SVG in the top-right corner of the input */
.search-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  width: 20px;
  height: 20px;
  transform: translateY(-50%);
  pointer-events: none; /* so clicks go through to the input */
  stroke: black; /* pick a color that suits your design */
}
</style>
