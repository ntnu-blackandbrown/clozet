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
  <div class="create-post-btn">
    <button>Create a post!</button>
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
  position: relative;
  display: inline-block;
  margin-right: 10px;
}

.search-bar {
  width: 300px;
  padding: 8px 12px;
  font-size: 16px;
  border: 2px solid ;
  border-radius: 24px;
  padding-right: 40px;
}

.search-icon {
  position: absolute;
  right: 10px;
  top: 50%;
  width: 20px;
  height: 20px;
  transform: translateY(-50%);
  pointer-events: none;
  stroke: black;
}

.create-post-btn {
  display: inline-block;
  vertical-align: middle;
}

.create-post-btn button {
  background-color: gray;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 24px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.create-post-btn button:hover {
  background-color: black;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.create-post-btn button:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>
