<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from '@/api/axios'

// State
const users = ref([])
const isLoading = ref(true)
const error = ref(null)
const searchQuery = ref('')
const selectedRole = ref('all')

// Fetch all users
const fetchUsers = async () => {
  try {
    isLoading.value = true
    error.value = null
    const response = await axios.get('/api/users')
    users.value = response.data
    isLoading.value = false
  } catch (err) {
    console.error('Error fetching users:', err)
    error.value = 'Failed to load users'
    isLoading.value = false
  }
}

// Filter users based on search and role
const filteredUsers = computed(() => {
  return users.value.filter(user => {
    const matchesSearch =
      searchQuery.value === '' ||
      user.username?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.email?.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      `${user.firstName} ${user.lastName}`.toLowerCase().includes(searchQuery.value.toLowerCase())

    const matchesRole =
      selectedRole.value === 'all' ||
      user.role?.toLowerCase() === selectedRole.value.toLowerCase()

    return matchesSearch && matchesRole
  })
})

// Toggle user active status
const toggleUserStatus = async (user) => {
  try {
    isLoading.value = true
    await axios.put(`/api/users/${user.id}`, {
      active: !user.active
    })

    // Update local state
    const index = users.value.findIndex(u => u.id === user.id)
    if (index !== -1) {
      users.value[index].active = !user.active
    }

    isLoading.value = false
  } catch (err) {
    console.error('Error updating user status:', err)
    error.value = 'Failed to update user status'
    isLoading.value = false
  }
}

// Change user role
const changeUserRole = async (user, newRole) => {
  try {
    isLoading.value = true
    await axios.put(`/api/users/${user.id}`, {
      role: newRole
    })

    // Update local state
    const index = users.value.findIndex(u => u.id === user.id)
    if (index !== -1) {
      users.value[index].role = newRole
    }

    isLoading.value = false
  } catch (err) {
    console.error('Error updating user role:', err)
    error.value = 'Failed to update user role'
    isLoading.value = false
  }
}

// Format date
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString()
}

// Get status class
const getStatusClass = (isActive) => {
  return isActive ? 'tag-green' : 'tag-red'
}

// Reset filters
const resetFilters = () => {
  searchQuery.value = ''
  selectedRole.value = 'all'
}

// Load users on component mount
onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div class="user-management">
    <div class="page-header">
      <h1>User Management</h1>
    </div>

    <div v-if="error" class="error-message">
      {{ error }}
      <button @click="fetchUsers" class="btn-secondary">Retry</button>
    </div>

    <!-- Filters -->
    <div class="filters">
      <div class="search-container">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Search users by name, username or email..."
          class="search-input"
        >
      </div>

      <div class="filter-container">
        <label for="role-filter">Filter by Role:</label>
        <select id="role-filter" v-model="selectedRole" class="role-filter">
          <option value="all">All Roles</option>
          <option value="ADMIN">Admin</option>
          <option value="ROLE_USER">Regular User</option>
        </select>

        <button @click="resetFilters" class="btn-secondary">Reset Filters</button>
      </div>
    </div>

    <!-- User Table -->
    <div class="table-container">
      <table class="admin-table" v-if="!isLoading && filteredUsers.length > 0">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th>Date Joined</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in filteredUsers" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.firstName }} {{ user.lastName }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.email }}</td>
            <td>
              <select
                v-model="user.role"
                @change="changeUserRole(user, $event.target.value)"
                class="role-select"
              >
                <option value="ADMIN">Admin</option>
                <option value="ROLE_USER">Regular User</option>
              </select>
            </td>
            <td>
              <span class="tag" :class="getStatusClass(user.active)">
                {{ user.active ? 'Active' : 'Inactive' }}
              </span>
            </td>
            <td>{{ formatDate(user.createdAt) }}</td>
            <td class="actions">
              <button
                @click="toggleUserStatus(user)"
                class="btn-secondary btn-small"
              >
                {{ user.active ? 'Deactivate' : 'Activate' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-else-if="isLoading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Loading users...</p>
      </div>

      <div v-else-if="filteredUsers.length === 0 && users.length > 0" class="empty-state">
        <p>No users found matching your filters</p>
        <button @click="resetFilters" class="btn-primary">Reset Filters</button>
      </div>

      <div v-else class="empty-state">
        <p>No users found</p>
      </div>
    </div>

    <div class="pagination-info" v-if="filteredUsers.length > 0">
      Showing {{ filteredUsers.length }} of {{ users.length }} users
    </div>
  </div>
</template>

<style scoped>
.user-management {
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

.role-filter {
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

.admin-table th, .admin-table td {
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

.role-select {
  padding: 0.5rem;
  border: 1px solid #d1d5db;
  border-radius: 0.25rem;
  font-size: 0.875rem;
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

.tag-red {
  background-color: #fde8e8;
  color: #e02424;
}

.actions {
  display: flex;
  gap: 0.5rem;
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

.btn-small {
  padding: 0.35rem 0.75rem;
  font-size: 0.875rem;
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
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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

  .admin-table th, .admin-table td {
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

  .role-filter {
    width: 100%;
  }
}
</style>
