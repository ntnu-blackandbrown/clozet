<script setup>
import { ref } from 'vue'
import ProductCard from '@/components/product/ProductCard.vue'
import ProductDisplayModal from '@/components/modals/ProductDisplayModal.vue'

const activeSection = ref('profile')
const showProductModal = ref(false)
const selectedProductId = ref('')

const sections = [
  { id: 'profile', label: 'Profile Settings' },
  { id: 'posts', label: 'My Posts' },
  { id: 'wishlist', label: 'My Wishlist' },
  { id: 'purchases', label: 'My Purchases' }
]

const setActiveSection = (section) => {
  activeSection.value = section
}

const openProductModal = (productId) => {
  selectedProductId.value = productId
  showProductModal.value = true
}

// Sample data for products
const myPosts = [
  {
    id: 'post-1',
    title: 'Nike Running Shoes',
    price: 1200,
    category: 'Shoes',
    image: '/src/assets/images/main-image.png'
  },
  {
    id: 'post-2',
    title: 'Designer Backpack',
    price: 800,
    category: 'Bags',
    image: '/src/assets/images/image-1.png'
  },
  {
    id: 'post-3',
    title: 'Casual Denim Jacket',
    price: 950,
    category: 'Clothing',
    image: '/src/assets/images/image-2.png'
  }
]

const wishlistItems = [
  {
    id: 'wish-1',
    title: 'Leather Wallet',
    price: 450,
    category: 'Accessories',
    image: '/src/assets/images/image-3.png'
  },
  {
    id: 'wish-2',
    title: 'Smart Watch',
    price: 2500,
    category: 'Electronics',
    image: '/src/assets/images/Screenshot 2025-03-26 at 17.26.14.png'
  }
]

const purchaseHistory = [
  {
    id: 'purchase-1',
    title: 'Vintage Sunglasses',
    price: 600,
    category: 'Accessories',
    image: '/src/assets/images/main-image.png',
    purchaseDate: '2024-03-15'
  },
  {
    id: 'purchase-2',
    title: 'Summer T-Shirt',
    price: 250,
    category: 'Clothing',
    image: '/src/assets/images/image-1.png',
    purchaseDate: '2024-03-10'
  }
]
</script>

<template>
  <div class="profile-container">
    <!-- Vertical Navigation Menu -->
    <nav class="profile-nav">
      <button
        v-for="section in sections"
        :key="section.id"
        :class="['nav-button', { active: activeSection === section.id }]"
        @click="setActiveSection(section.id)"
      >
        {{ section.label }}
      </button>
    </nav>

    <!-- Content Area -->
    <div class="profile-content">
      <!-- Profile Settings Section -->
      <div v-if="activeSection === 'profile'" class="profile-section">
        <h2>Profile Settings</h2>
        <div class="profile-form">
          <div class="name-fields">
            <div class="form-group">
              <label>First Name</label>
              <input type="text" placeholder="Your first name" />
            </div>
            <div class="form-group">
              <label>Last Name</label>
              <input type="text" placeholder="Your last name" />
            </div>
          </div>
          <div class="credentials-fields">
            <div class="form-group">
              <label>Username</label>
              <input type="text" placeholder="Your username" />
            </div>
            <div class="form-group">
              <label>Password</label>
              <input type="password" placeholder="Your password" />
            </div>
          </div>
          <div class="contact-fields">
            <div class="form-group">
              <label>Email</label>
              <input type="email" placeholder="Your email" />
            </div>
            <div class="form-group">
              <label>Phone Number</label>
              <input type="tel" placeholder="Your phone number" />
            </div>
          </div>
          <div class="form-actions">
            <button class="save-button">Save Changes</button>
            <button class="delete-button">Delete Account</button>
          </div>
        </div>
      </div>

      <!-- My Posts Section -->
      <div v-if="activeSection === 'posts'" class="profile-section">
        <h2>My Posts</h2>
        <div class="posts-grid">
          <ProductCard
            v-for="post in myPosts"
            :key="post.id"
            v-bind="post"
            @click="openProductModal(post.id)"
          />
        </div>
      </div>

      <!-- My Wishlist Section -->
      <div v-if="activeSection === 'wishlist'" class="profile-section">
        <h2>My Wishlist</h2>
        <div class="wishlist-grid">
          <ProductCard
            v-for="item in wishlistItems"
            :key="item.id"
            v-bind="item"
            @click="openProductModal(item.id)"
          />
        </div>
      </div>

      <!-- My Purchases Section -->
      <div v-if="activeSection === 'purchases'" class="profile-section">
        <h2>My Purchases</h2>
        <div class="purchases-list">
          <div v-for="purchase in purchaseHistory" :key="purchase.id" class="purchase-item">
            <ProductCard
              v-bind="purchase"
              @click="openProductModal(purchase.id)"
            />
            <div class="purchase-date">
              Purchased on: {{ purchase.purchaseDate }}
            </div>
          </div>
        </div>
      </div>
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
.profile-container {
  display: flex;
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.profile-nav {
  width: 250px;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.nav-button {
  padding: 1rem;
  text-align: left;
  background: none;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  color: #333;
  transition: all 0.2s ease;
}

.nav-button:hover {
  background-color: #f3f4f6;
}

.nav-button.active {
  background-color: #e5e7eb;
  font-weight: 500;
}

.profile-content {
  flex: 1;
  background-color: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.profile-section h2 {
  margin-bottom: 2rem;
  color: #333;
  font-size: 1.5rem;
}

.profile-form {
  max-width: 600px;
}

.name-fields,
.credentials-fields,
.contact-fields {
  display: flex;
  gap: 4rem;
  margin-bottom: 1.5rem;
}

.name-fields .form-group,
.credentials-fields .form-group,
.contact-fields .form-group {
  flex: 1;
  margin-bottom: 0;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #374151;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e1e1e1;
  border-radius: 8px;
  background-color: #f8f9fa;
  transition: all 0.2s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
  background-color: white;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

.form-group textarea {
  min-height: 100px;
  resize: vertical;
}

.form-actions {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.save-button,
.delete-button {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.save-button {
  background-color: #3b82f6;
  color: white;
  border: none;
}

.save-button:hover {
  background-color: #2563eb;
}

.delete-button {
  background-color: #ef4444;
  color: white;
  border: none;
}

.delete-button:hover {
  background-color: #dc2626;
}

.placeholder {
  text-align: center;
  padding: 2rem;
  background-color: #f3f4f6;
  border-radius: 8px;
  color: #6b7280;
}

.posts-grid,
.wishlist-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

.purchases-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1.5rem;
}

.purchase-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.purchase-item :deep(.product-card) {
  width: 100%;
  margin: 0;
  cursor: pointer;
}

.purchase-item :deep(.product-card:hover) {
  transform: translateY(-2px);
  transition: transform 0.2s ease;
}

.purchase-item :deep(.product-image) {
  height: 150px;
}

.purchase-item :deep(.product-info h3) {
  font-size: 1rem;
}

.purchase-item :deep(.price) {
  font-size: 0.9rem;
}

.purchase-item :deep(.category) {
  font-size: 0.8rem;
}

.purchase-date {
  color: #6b7280;
  font-size: 0.875rem;
  text-align: center;
  margin-top: 0.5rem;
}

@media (max-width: 768px) {
  .profile-container {
    flex-direction: column;
    padding: 1rem;
  }

  .profile-nav {
    width: 100%;
    flex-direction: row;
    overflow-x: auto;
    padding-bottom: 1rem;
  }

  .nav-button {
    white-space: nowrap;
  }
}
</style>
