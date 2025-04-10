<script setup>
import { ref, onMounted, computed } from 'vue'
import { TransactionService } from '@/api/services/TransactionService'
import { ProductService } from '@/api/services/ProductService'
// State
const transactions = ref([])
const isLoading = ref(true)
const error = ref(null)
const statusFilter = ref('all')
const searchQuery = ref('')
const sortKey = ref('createdAt')
const sortDirection = ref('desc')

// Fetch all transactions and item details
const fetchTransactions = async () => {
  try {
    isLoading.value = true
    error.value = null
    const response = await TransactionService.getAllTransactions()

    // Create an array of transactions with item details fetched
    const transactionsWithItems = await Promise.all(
      response.data.map(async (transaction) => {
        if (transaction.itemId) {
          try {
            const itemResponse = await ProductService.getItemById(transaction.itemId)
            return {
              ...transaction,
              item: {
                title: itemResponse.data.title,
                ...itemResponse.data
              }
            }
          } catch (itemError) {
            console.error(`Error fetching item ${transaction.itemId}:`, itemError)
            return {
              ...transaction,
              item: { title: 'Unknown Item' }
            }
          }
        }
        return {
          ...transaction,
          item: { title: 'Unknown Item' }
        }
      })
    )

    transactions.value = transactionsWithItems
    isLoading.value = false
  } catch (err) {
    console.error('Error fetching transactions:', err)
    error.value = 'Failed to load transactions'
    isLoading.value = false
  }
}

// Filter transactions based on status and search
const filteredTransactions = computed(() => {
  let filtered = transactions.value

  // Filter by status
  if (statusFilter.value !== 'all') {
    filtered = filtered.filter(
      (transaction) => transaction.status?.toUpperCase() === statusFilter.value.toUpperCase(),
    )
  }

  // Filter by search query
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(
      (transaction) =>
        transaction.buyerId?.toString().includes(query) ||
        transaction.sellerId?.toString().includes(query) ||
        transaction.paymentMethod?.toLowerCase().includes(query) ||
        transaction.item?.title?.toLowerCase().includes(query),
    )
  }

  // Sort transactions
  filtered = [...filtered].sort((a, b) => {
    const aValue = a[sortKey.value]
    const bValue = b[sortKey.value]

    if (sortKey.value === 'amount') {
      return sortDirection.value === 'asc'
        ? parseFloat(aValue) - parseFloat(bValue)
        : parseFloat(bValue) - parseFloat(aValue)
    }

    if (sortKey.value === 'createdAt') {
      return sortDirection.value === 'asc'
        ? new Date(aValue) - new Date(bValue)
        : new Date(bValue) - new Date(aValue)
    }

    return sortDirection.value === 'asc'
      ? String(aValue).localeCompare(String(bValue))
      : String(bValue).localeCompare(String(aValue))
  })

  return filtered
})

// Toggle sort direction and key
const toggleSort = (key) => {
  if (sortKey.value === key) {
    sortDirection.value = sortDirection.value === 'asc' ? 'desc' : 'asc'
  } else {
    sortKey.value = key
    sortDirection.value = 'desc'
  }
}

// Format date
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return (
    date.toLocaleDateString() +
    ' ' +
    date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  )
}

// Format currency
const formatCurrency = (amount) => {
  return `${parseFloat(amount).toFixed(2)} kr`
}

// Get status class for styling
const getStatusClass = (status) => {
  switch (status?.toUpperCase()) {
    case 'COMPLETED':
      return 'tag-green'
    case 'PENDING':
      return 'tag-yellow'
    case 'FAILED':
      return 'tag-red'
    case 'CANCELLED':
      return 'tag-gray'
    default:
      return 'tag-gray'
  }
}

// Reset filters
const resetFilters = () => {
  statusFilter.value = 'all'
  searchQuery.value = ''
}

// Load transactions on component mount
onMounted(() => {
  fetchTransactions()
})
</script>

<template>
  <div class="transaction-management">
    <div class="page-header">
      <h1 id="transaction-management-title">Transaction Management</h1>
    </div>

    <div v-if="error" class="error-message" role="alert">
      {{ error }}
      <button
        @click="fetchTransactions"
        class="btn-secondary"
        aria-label="Retry loading transactions"
      >
        Retry
      </button>
    </div>

    <!-- Filters -->
    <div class="filters" role="search" aria-labelledby="transaction-management-title">
      <div class="search-container">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Search by buyer, seller, item..."
          class="search-input"
          aria-label="Search transactions"
        />
      </div>

      <div class="filter-container">
        <label for="status-filter">Status:</label>
        <select
          id="status-filter"
          v-model="statusFilter"
          class="status-filter"
          aria-label="Filter by status"
        >
          <option value="all">All Statuses</option>
          <option value="COMPLETED">Completed</option>
          <option value="PENDING">Pending</option>
          <option value="FAILED">Failed</option>
          <option value="CANCELLED">Cancelled</option>
        </select>

        <button @click="resetFilters" class="btn-secondary" aria-label="Reset all filters">
          Reset Filters
        </button>
      </div>
    </div>

    <!-- Transaction Table -->
    <div class="table-container">
      <div style="overflow-x: auto" v-if="!isLoading && filteredTransactions.length > 0">
        <table class="admin-table" aria-labelledby="transaction-management-title">
          <thead>
            <tr>
              <th
                @click="toggleSort('id')"
                class="sortable"
                scope="col"
                aria-sort="sortKey === 'id' ? sortDirection : 'none'"
                tabindex="0"
              >
                ID
                <span v-if="sortKey === 'id'" class="sort-indicator" aria-hidden="true">
                  {{ sortDirection === 'asc' ? '↑' : '↓' }}
                </span>
              </th>
              <th scope="col">Item</th>
              <th scope="col">Buyer</th>
              <th scope="col">Seller</th>
              <th
                @click="toggleSort('amount')"
                class="sortable"
                scope="col"
                aria-sort="sortKey === 'amount' ? sortDirection : 'none'"
                tabindex="0"
              >
                Amount
                <span v-if="sortKey === 'amount'" class="sort-indicator" aria-hidden="true">
                  {{ sortDirection === 'asc' ? '↑' : '↓' }}
                </span>
              </th>
              <th scope="col">Status</th>
              <th
                @click="toggleSort('createdAt')"
                class="sortable"
                scope="col"
                aria-sort="sortKey === 'createdAt' ? sortDirection : 'none'"
                tabindex="0"
              >
                Date
                <span v-if="sortKey === 'createdAt'" class="sort-indicator" aria-hidden="true">
                  {{ sortDirection === 'asc' ? '↑' : '↓' }}
                </span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="transaction in filteredTransactions" :key="transaction.id">
              <td>{{ transaction.id }}</td>
              <td>{{ transaction.item?.title || 'Unknown Item' }}</td>
              <td>{{ transaction.buyerId }}</td>
              <td>{{ transaction.sellerId }}</td>
              <td>{{ formatCurrency(transaction.amount) }}</td>
              <td>
                <span class="tag" :class="getStatusClass(transaction.status)">
                  {{ transaction.status }}
                </span>
              </td>
              <td>{{ formatDate(transaction.createdAt) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-else-if="isLoading" class="loading-container" role="status" aria-live="polite">
        <div class="loading-spinner" aria-hidden="true"></div>
        <p>Loading transactions...</p>
      </div>

      <div
        v-else-if="filteredTransactions.length === 0 && transactions.length > 0"
        class="empty-state"
        aria-live="polite"
      >
        <p>No transactions found matching your filters</p>
        <button @click="resetFilters" class="btn-primary" aria-label="Reset all filters">
          Reset Filters
        </button>
      </div>

      <div v-else class="empty-state" aria-live="polite">
        <p>No transactions found</p>
      </div>
    </div>

    <div class="pagination-info" v-if="filteredTransactions.length > 0" aria-live="polite">
      Showing {{ filteredTransactions.length }} of {{ transactions.length }} transactions
    </div>
  </div>
</template>

<style scoped>
.transaction-management {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  font-size: 2rem;
  font-weight: 600;
  color: var(--color-limed-spruce);
  margin: 0;
}

.error-message {
  background-color: #fde8e8;
  color: #e02424;
  padding: 1rem;
  border-radius: 0.5rem;
  margin-bottom: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filters {
  background-color: white;
  padding: 1rem 1.5rem;
  border-radius: 0.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: center;
}

.search-container {
  flex: 1;
  min-width: 250px;
}

.search-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 0.25rem;
  font-size: 1rem;
}

.filter-container {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.filter-container label {
  font-weight: 500;
  color: var(--color-limed-spruce);
}

.status-filter {
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 0.25rem;
  font-size: 1rem;
  color: var(--color-limed-spruce);
}

.table-container {
  background-color: white;
  border-radius: 0.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
}

.admin-table th,
.admin-table td {
  text-align: left;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.admin-table th {
  font-weight: 500;
  background-color: #f9fafb;
  color: var(--color-slate-gray);
}

.admin-table tr:last-child td {
  border-bottom: none;
}

.sortable {
  cursor: pointer;
  position: relative;
  user-select: none;
}

.sortable:hover {
  background-color: #f3f4f6;
}

.sort-indicator {
  margin-left: 0.25rem;
  font-weight: bold;
}

.tag {
  display: inline-block;
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  font-size: 0.875rem;
  font-weight: 500;
}

.tag-green {
  background-color: #def7ec;
  color: #0e9f6e;
}

.tag-yellow {
  background-color: #fef3c7;
  color: #d97706;
}

.tag-red {
  background-color: #fde8e8;
  color: #e02424;
}

.tag-gray {
  background-color: #f3f4f6;
  color: #6b7280;
}

.btn-primary {
  background-color: var(--color-limed-spruce);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
  cursor: pointer;
  font-weight: 500;
}

.btn-secondary {
  background-color: #f3f4f6;
  color: var(--color-limed-spruce);
  border: 1px solid #d1d5db;
  padding: 0.5rem 1rem;
  border-radius: 0.25rem;
  cursor: pointer;
  font-weight: 500;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem;
}

.loading-spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid var(--color-limed-spruce);
  width: 30px;
  height: 30px;
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

.empty-state {
  text-align: center;
  padding: 3rem;
  color: var(--color-slate-gray);
}

.empty-state p {
  margin-bottom: 1rem;
}

.pagination-info {
  margin-top: 1rem;
  text-align: right;
  color: var(--color-slate-gray);
  font-size: 0.875rem;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .admin-table th,
  .admin-table td {
    padding: 0.75rem 0.5rem;
    font-size: 0.875rem;
  }

  .filters {
    flex-direction: column;
    align-items: stretch;
  }

  .filter-container {
    flex-direction: column;
    align-items: flex-start;
  }

  .status-filter {
    width: 100%;
  }
}
</style>
