<script setup>
import ProductList from '@/views/ProductListView.vue'
import Badge from '@/components/utils/Badge.vue'
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="home-container">
    <div class="content-section">
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
        <input class="search-bar" type="text" placeholder="Search for a product..." />
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
        <button @click="router.push('/create-product')">Create a post!</button>
      </div>

      <div class="featured-products">
        <ProductList />
      </div>
    </div>
    <div class="image-section">
      <img src="@/assets/images/homepage.png" alt="Clozet Homepage" class="homepage-image" />
    </div>
  </div>
</template>

<style scoped>
.home-container {
  display: flex;
  gap: var(--spacing-xl);
  align-items: flex-start;
  max-width: 1400px;
  margin: 0 auto;
}

.content-section {
  flex: 1;
  max-width: 800px;
}

.image-section {
  flex: 1;
  position: sticky;
  top: var(--spacing-xl);
  padding-top: var(--spacing-xl);
}

.homepage-image {
  width: 100%;
  height: auto;
  max-height: 80vh;
  object-fit: contain;
  border-radius: var(--border-radius-lg);
}

h1 {
  color: var(--color-limed-spruce);
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: var(--spacing-sm);
  letter-spacing: -0.02em;
}

h3 {
  color: var(--color-limed-spruce);
  font-size: 1.25rem;
  font-weight: 400;
  margin-bottom: var(--spacing-xl);
  opacity: 0.8;
}

.badge-container {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
  margin-bottom: var(--spacing-xl);
}

.featured-products {
  width: 100%;
  margin-top: var(--spacing-xl);
}

.search-wrapper {
  position: relative;
  display: inline-block;
  margin-right: var(--spacing-md);
}

.search-bar {
  width: 300px;
  padding: var(--spacing-sm) var(--spacing-lg);
  padding-right: 40px;
  font-size: 0.95rem;
  border: 2px solid #2D353F;
  border-radius: var(--border-radius-lg);
  background-color: var(--color-white);
  transition: var(--transition-smooth);
  color: var(--color-limed-spruce);
}

.search-bar:focus {
  outline: none;
  border-color: #2D353F;
  box-shadow: 0 0 0 3px rgba(45, 53, 63, 0.2);
}

.search-bar::placeholder {
  color: #9CA3AF;
}

.search-icon {
  position: absolute;
  right: var(--spacing-md);
  top: 50%;
  width: 20px;
  height: 20px;
  transform: translateY(-50%);
  pointer-events: none;
  stroke: #2D353F;
  transition: var(--transition-smooth);
}

.create-post-btn {
  display: inline-block;
  vertical-align: middle;
}

.create-post-btn button {
  background-color: #2D353F;
  color: var(--color-white);
  border: none;
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition-bounce);
  box-shadow: var(--box-shadow-light);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.create-post-btn button:hover {
  background-color: #343D48;
  transform: translateY(-2px);
  box-shadow: var(--box-shadow-medium);
}

.create-post-btn button:active {
  transform: translateY(0);
  background-color: #262D36;
  box-shadow: var(--box-shadow-light);
}

@media (max-width: 1024px) {
  .home-container {
    flex-direction: column;
  }

  .image-section {
    position: static;
    order: -1;
    padding-top: 0;
    margin-bottom: var(--spacing-xl);
  }

  .homepage-image {
    max-height: 50vh;
  }
}

@media (max-width: 768px) {
  .search-wrapper {
    display: block;
    margin-right: 0;
    margin-bottom: var(--spacing-md);
  }

  .search-bar {
    width: 100%;
  }

  .create-post-btn {
    display: block;
    width: 100%;
  }

  .create-post-btn button {
    width: 100%;
    justify-content: center;
  }
}
</style>
