<script setup>
import { ref, onMounted } from 'vue'
// Import API services
import { UserService } from '@/api/services/UserService'
import { ProductService } from '@/api/services/ProductService'
import { CategoryService } from '@/api/services/CategoryService'
import { TransactionService } from '@/api/services/TransactionService'

// Statistics data
const statistics = ref({
  totalUsers: 0,
  totalItems: 0,
  totalCategories: 0,
  totalTransactions: 0, // Initialize transaction count
  recentUsers: [],
  recentItems: [],
})

const isLoading = ref(true)
const error = ref(null)

// Helper function to sort by date and get top N items
const getRecentItems = (items, count = 3) => {
  if (!items || !Array.isArray(items)) return []
  // Assuming items have a 'createdAt' field that can be parsed into a Date
  return items.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)).slice(0, count)
}

// Fetch dashboard statistics from APIs
const fetchStatistics = async () => {
  try {
    isLoading.value = true
    error.value = null

    // Make concurrent API calls
    const [usersResponse, productsResponse, categoriesResponse, transactionsResponse] =
      await Promise.all([
        UserService.getAllUsers().catch((e) => {
          console.error('Failed to fetch users:', e)
          return { data: [] }
        }),
        ProductService.getAllItems().catch((e) => {
          console.error('Failed to fetch products:', e)
          return { data: [] }
        }),
        CategoryService.getAllCategories().catch((e) => {
          console.error('Failed to fetch categories:', e)
          return { data: [] }
        }),
        TransactionService.getAllTransactions().catch((e) => {
          console.error('Failed to fetch transactions:', e)
          return { data: [] }
        }),
      ])

    const users = usersResponse.data || []
    const products = productsResponse.data || []
    const categories = categoriesResponse.data || []
    const transactions = transactionsResponse.data || []

    // Calculate statistics
    statistics.value = {
      totalUsers: users.length,
      totalItems: products.length,
      totalCategories: categories.length,
      totalTransactions: transactions.length,
      recentUsers: getRecentItems(users),
      recentItems: getRecentItems(products),
    }

    // Remove mock data timeout
    // setTimeout(() => { ... }, 800) // This block is removed

    isLoading.value = false
  } catch (err) {
    // This top-level catch might be less necessary if individual calls are handled
    // But keep it as a fallback
    console.error('Error fetching admin statistics:', err)
    error.value = 'Failed to load some dashboard data. Check console for details.'
    isLoading.value = false // Ensure loading stops even if Promise.all fails globally
  }
}

onMounted(() => {
  fetchStatistics()
})
</script>

<template>
  <div class="admin-overview">
    <div class="page-header">
      <h1 id="dashboard-title">Dashboard Overview</h1>
      <p class="subtitle">Welcome to the admin dashboard</p>
    </div>

    <div v-if="isLoading" class="loading-container" role="status" aria-live="polite">
      <div class="loading-spinner" aria-hidden="true"></div>
      <p>Loading dashboard data...</p>
    </div>

    <div v-else-if="error" class="error-container" role="alert">
      <p>{{ error }}</p>
      <button
        @click="fetchStatistics"
        class="retry-button"
        aria-label="Retry loading dashboard data"
      >
        Retry
      </button>
    </div>

    <template v-else>
      <!-- Statistics Cards -->
      <div class="stats-grid" role="region" aria-labelledby="dashboard-title">
        <div class="stat-card">
          <div class="stat-icon" aria-hidden="true">👥</div>
          <div class="stat-content">
            <h3 class="stat-title">Total Users</h3>
            <p class="stat-value">{{ statistics.totalUsers }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" aria-hidden="true">👕</div>
          <div class="stat-content">
            <h3 class="stat-title">Total Items</h3>
            <p class="stat-value">{{ statistics.totalItems }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" aria-hidden="true">🏷️</div>
          <div class="stat-content">
            <h3 class="stat-title">Categories</h3>
            <p class="stat-value">{{ statistics.totalCategories }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" aria-hidden="true">💰</div>
          <div class="stat-content">
            <h3 class="stat-title">Transactions</h3>
            <p class="stat-value">{{ statistics.totalTransactions }}</p>
          </div>
        </div>
      </div>

      <!-- Recent Activity Section -->
      <div class="activity-section">
        <div class="activity-card">
          <h3 id="recent-users-title" class="activity-title">Recent Users</h3>
          <div class="activity-content">
            <table class="data-table" aria-labelledby="recent-users-title">
              <thead>
                <tr>
                  <th scope="col">Username</th>
                  <th scope="col">Name</th>
                  <th scope="col">Email</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in statistics.recentUsers" :key="user.id">
                  <td>{{ user.username }}</td>
                  <td>{{ user.firstName }} {{ user.lastName }}</td>
                  <td>{{ user.email }}</td>
                </tr>
              </tbody>
            </table>
            <div class="view-all-link">
              <RouterLink to="/admin/users" aria-label="View all users">View All Users</RouterLink>
            </div>
          </div>
        </div>

        <div class="activity-card">
          <h3 id="recent-items-title" class="activity-title">Recent Items</h3>
          <div class="activity-content">
            <table class="data-table" aria-labelledby="recent-items-title">
              <thead>
                <tr>
                  <th scope="col">Title</th>
                  <th scope="col">Price</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in statistics.recentItems" :key="item.id">
                  <td>{{ item.title }}</td>
                  <td>{{ item.price.toFixed(2) }} kr</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.admin-overview {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 2rem;
  font-weight: 600;
  color: var(--color-limed-spruce);
  margin-bottom: 0.5rem;
}

.subtitle {
  color: var(--color-slate-gray);
  margin-top: 0;
}

.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.loading-spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid var(--color-limed-spruce);
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.retry-button {
  background-color: var(--color-limed-spruce);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
  cursor: pointer;
  margin-top: 1rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background-color: white;
  border-radius: 0.5rem;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stat-icon {
  font-size: 2rem;
  margin-right: 1rem;
}

.stat-title {
  color: var(--color-slate-gray);
  font-size: 0.9rem;
  margin: 0 0 0.5rem 0;
  font-weight: 500;
}

.stat-value {
  color: var(--color-limed-spruce);
  font-size: 1.75rem;
  font-weight: 600;
  margin: 0;
}

.activity-section {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

.activity-card {
  background-color: #ffffff;
  border: 1px solid var(--color-gallery);
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.activity-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--color-limed-spruce);
  margin-bottom: 1rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid var(--color-gallery);
}

.activity-content {
  flex-grow: 1;
  position: relative;
  max-height: 300px; /* Set a max height */
  overflow-y: auto; /* Enable vertical scrollbar when content overflows */
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  text-align: left;
  padding: 0.75rem 1.5rem;
}

.data-table th {
  font-weight: 500;
  color: var(--color-slate-gray);
  font-size: 0.9rem;
}

.data-table tr:nth-child(even) {
  background-color: rgba(0, 0, 0, 0.02);
}

.view-all-link {
  text-align: center;
  padding: 1rem;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
}

.view-all-link a {
  color: var(--color-limed-spruce);
  text-decoration: none;
  font-weight: 500;
}

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .activity-section {
    grid-template-columns: 1fr;
  }

  .data-table th,
  .data-table td {
    padding: 0.75rem 1rem;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
