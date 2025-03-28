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
  <div>
    <input type="text" placeholder="Search for a product" />
  </div>

  <div class="featured-products">
    <ProductList />
  </div>
</template>

<style scoped>
.featured-products {
  width: 100%;
}
</style>
