<script setup>
import { ref, onMounted } from 'vue'

// Statistics data
const statistics = ref({
  totalUsers: 0,
  totalItems: 0,
  totalCategories: 0,
  totalTransactions: 0,
  recentUsers: [],
  recentItems: []
})

const isLoading = ref(true)
const error = ref(null)

// Fetch dashboard statistics
const fetchStatistics = async () => {
  try {
    isLoading.value = true
    error.value = null

    // TODO: Once backend endpoint is created, replace with actual API call
    // const response = await axios.get('/api/admin/statistics')
    // statistics.value = response.data

    // Mock data for now
    // Simulate API call
    setTimeout(() => {
      statistics.value = {
        totalUsers: 35,
        totalItems: 128,
        totalCategories: 18,
        totalTransactions: 52,
        recentUsers: [
          { id: 1, username: 'emmasmith1', firstName: 'Emma', lastName: 'Smith', email: 'emmasmith@example.com', createdAt: '2023-05-15' },
          { id: 2, username: 'noahjohnson1', firstName: 'Noah', lastName: 'Johnson', email: 'noahjohnson@example.com', createdAt: '2023-05-14' },
          { id: 3, username: 'oliviawilliams1', firstName: 'Olivia', lastName: 'Williams', email: 'oliviawilliams@example.com', createdAt: '2023-05-13' }
        ],
        recentItems: [
          { id: 1, title: "Men's Classic T-Shirt - Black", price: 199.50, seller: 'demoSeller', createdAt: '2023-05-16' },
          { id: 2, title: "Women's Summer Dress - Floral", price: 450.00, seller: 'emmasmith1', createdAt: '2023-05-15' },
          { id: 3, title: "Leather Jacket - Brown", price: 1200.00, seller: 'oliviawilliams1', createdAt: '2023-05-14' }
        ]
      }
      isLoading.value = false
    }, 800)
  } catch (err) {
    console.error('Error fetching admin statistics:', err)
    error.value = 'Failed to load dashboard data'
    isLoading.value = false
  }
}

onMounted(() => {
  fetchStatistics()
})
</script>

<template>
  <div class="admin-overview">
    <div class="page-header">
      <h1>Dashboard Overview</h1>
      <p class="subtitle">Welcome to the admin dashboard</p>
    </div>

    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>Loading dashboard data...</p>
    </div>

    <div v-else-if="error" class="error-container">
      <p>{{ error }}</p>
      <button @click="fetchStatistics" class="retry-button">Retry</button>
    </div>

    <template v-else>
      <!-- Statistics Cards -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">👥</div>
          <div class="stat-content">
            <h3 class="stat-title">Total Users</h3>
            <p class="stat-value">{{ statistics.totalUsers }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">👕</div>
          <div class="stat-content">
            <h3 class="stat-title">Total Items</h3>
            <p class="stat-value">{{ statistics.totalItems }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">🏷️</div>
          <div class="stat-content">
            <h3 class="stat-title">Categories</h3>
            <p class="stat-value">{{ statistics.totalCategories }}</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon">💰</div>
          <div class="stat-content">
            <h3 class="stat-title">Transactions</h3>
            <p class="stat-value">{{ statistics.totalTransactions }}</p>
          </div>
        </div>
      </div>

      <!-- Recent Activity Section -->
      <div class="activity-section">
        <div class="activity-card">
          <h3 class="activity-title">Recent Users</h3>
          <div class="activity-content">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Username</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Date Joined</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in statistics.recentUsers" :key="user.id">
                  <td>{{ user.username }}</td>
                  <td>{{ user.firstName }} {{ user.lastName }}</td>
                  <td>{{ user.email }}</td>
                  <td>{{ user.createdAt }}</td>
                </tr>
              </tbody>
            </table>
            <div class="view-all-link">
              <RouterLink to="/admin/users">View All Users</RouterLink>
            </div>
          </div>
        </div>

        <div class="activity-card">
          <h3 class="activity-title">Recent Items</h3>
          <div class="activity-content">
            <table class="data-table">
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Price</th>
                  <th>Seller</th>
                  <th>Date Listed</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in statistics.recentItems" :key="item.id">
                  <td>{{ item.title }}</td>
                  <td>{{ item.price.toFixed(2) }} kr</td>
                  <td>{{ item.seller }}</td>
                  <td>{{ item.createdAt }}</td>
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

.loading-container, .error-container {
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
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
}

.activity-card {
  background-color: white;
  border-radius: 0.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.activity-title {
  font-size: 1.1rem;
  font-weight: 600;
  padding: 1rem 1.5rem;
  margin: 0;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  color: var(--color-limed-spruce);
}

.activity-content {
  padding: 1rem 0;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th, .data-table td {
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

  .data-table th, .data-table td {
    padding: 0.75rem 1rem;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>
