<script setup>
import { ref, onMounted } from 'vue'
import { CategoryService } from '@/api/services/CategoryService'
import CreateCategoryModal from '@/components/admin/categories/CreateCategoryModal.vue' // Import the new component

// State
const categories = ref([])
const isLoading = ref(true)
const error = ref(null)
const successMessage = ref(null) // Add success message state
const showCreateModal = ref(false) // State for the new create modal

// Form for *editing* categories only
const editCategoryForm = ref({
  id: null,
  name: '',
  description: '',
  parentId: null,
})

const showEditForm = ref(false) // Renamed from showForm
const editFormErrors = ref({}) // Renamed from formErrors

// Fetch all categories
const fetchCategories = async () => {
  try {
    isLoading.value = true
    error.value = null
    const response = await CategoryService.getAllCategories()
    categories.value = response.data
    isLoading.value = false
  } catch (err) {
    console.error('Error fetching categories:', err)
    error.value = 'Failed to load categories'
    isLoading.value = false
  }
}

// Create a new category (using data from the new modal)
const createCategory = async (newCategoryData) => {
  // Validation is now handled within CreateCategoryModal
  try {
    isLoading.value = true
    await CategoryService.createCategory(newCategoryData)
    await fetchCategories()
    isLoading.value = false
    showCreateModal.value = false // Close the create modal
  } catch (err) {
    console.error('Error creating category:', err)
    // TODO: Consider showing the error message within the modal or as a notification
    error.value = 'Failed to create category'
    isLoading.value = false
  }
}

// Update an existing category
const updateCategory = async () => {
  if (!validateEditForm()) return // Use renamed validation function

  try {
    isLoading.value = true
    error.value = null // Clear previous errors
    await CategoryService.updateCategory(editCategoryForm.value.id, editCategoryForm.value)
    await fetchCategories()
    isLoading.value = false
    successMessage.value = `Category "${editCategoryForm.value.name}" updated successfully` // Set success message
    resetEditForm() // Use renamed reset function
    showEditForm.value = false // Use renamed state variable

    // Clear success message after 5 seconds
    setTimeout(() => {
      successMessage.value = null
    }, 5000)
  } catch (err) {
    console.error('Error updating category:', err)
    error.value = `Failed to update category: ${err.response?.data?.message || 'Unknown error'}` // More specific error
    isLoading.value = false
  }
}

// Delete a category
const deleteCategory = async (id) => {
  if (!confirm('Are you sure you want to delete this category?')) return

  try {
    isLoading.value = true
    await CategoryService.deleteCategory(id)
    await fetchCategories()
    isLoading.value = false
  } catch (err) {
    console.error('Error deleting category:', err)
    error.value = 'Failed to delete category'
    isLoading.value = false
  }
}

// Form submission handler (Only for Edit Form now)
const handleEditSubmit = () => {
  updateCategory()
}

// Open form to add a new category (Now opens the new modal)
const openAddCategoryModal = () => {
  showCreateModal.value = true
}

// Open form to edit an existing category
const editCategory = (category) => {
  resetEditForm() // Reset edit form before populating
  editCategoryForm.value = {
    id: category.id,
    name: category.name,
    description: category.description,
    parentId: category.parent ? category.parent.id : null,
  }
  showEditForm.value = true
}

// Reset *edit* form fields
const resetEditForm = () => {
  editCategoryForm.value = {
    id: null,
    name: '',
    description: '',
    parentId: null,
  }
  editFormErrors.value = {}
}

// Validate *edit* form inputs
const validateEditForm = () => {
  editFormErrors.value = {}

  if (!editCategoryForm.value.name.trim()) {
    editFormErrors.value.name = 'Category name is required'
  } else if (editCategoryForm.value.name.length < 3 || editCategoryForm.value.name.length > 100) {
     editFormErrors.value.name = 'Name must be between 3 and 100 characters'
  }

  if (!editCategoryForm.value.description.trim()) {
    editFormErrors.value.description = 'Description is required'
  } else if (editCategoryForm.value.description.length > 255) {
    editFormErrors.value.description = 'Description cannot exceed 255 characters'
  }

  return Object.keys(editFormErrors.value).length === 0
}

// Get parent category name
const getParentName = (category) => {
  if (!category.parent) return '-'
  return category.parent.name
}

// Load categories on component mount
onMounted(() => {
  fetchCategories()
})
</script>

<template>
  <div class="category-management">
    <div class="page-header">
      <h1 id="category-management-title">Category Management</h1>
      <button @click="openAddCategoryModal" class="btn-primary" aria-label="Add new category">
        Add New Category
      </button>
    </div>

    <!-- Success Message -->
    <div v-if="successMessage" class="success-message" role="status">
      {{ successMessage }}
    </div>

    <div v-if="error" class="error-message" role="alert">
      {{ error }}
      <button @click="fetchCategories" class="btn-secondary" aria-label="Retry loading categories">
        Retry
      </button>
    </div>

    <!-- Category Table -->
    <div class="table-container">
      <div style="overflow-x: auto" v-if="!isLoading && categories.length > 0">
        <table class="admin-table" aria-labelledby="category-management-title">
          <thead>
            <tr>
              <th scope="col">ID</th>
              <th scope="col">Name</th>
              <th scope="col">Description</th>
              <th scope="col">Parent Category</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="category in categories" :key="category.id">
              <td>{{ category.id }}</td>
              <td>{{ category.name }}</td>
              <td>{{ category.description }}</td>
              <td>{{ getParentName(category) }}</td>
              <td class="actions">
                <button
                  @click="editCategory(category)"
                  class="btn-icon edit"
                  aria-label="Edit category: {{ category.name }}"
                >
                  ✎
                </button>

              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-else-if="isLoading" class="loading-container" role="status" aria-live="polite">
        <div class="loading-spinner" aria-hidden="true"></div>
        <p>Loading categories...</p>
      </div>

      <div v-else class="empty-state">
        <p>No categories found</p>
        <button @click="openAddCategoryModal" class="btn-primary" aria-label="Add first category">
          Add Your First Category
        </button>
      </div>
    </div>

    <!-- New Create Category Modal -->
    <CreateCategoryModal
      :is-visible="showCreateModal"
      :existing-categories="categories"
      @close="showCreateModal = false"
      @create="createCategory"
    />

    <!-- Edit Category Form Modal (Existing modal repurposed for editing only) -->
    <div
      v-if="showEditForm"
      class="modal-backdrop"
      @click="showEditForm = false"
      aria-modal="true"
      role="dialog"
      aria-labelledby="category-form-title"
    >
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 id="category-form-title">
            Edit Category
          </h3>
          <button @click="showEditForm = false" class="btn-close" aria-label="Close form">×</button>
        </div>

        <form @submit.prevent="handleEditSubmit" class="category-form">
          <div class="form-group">
            <label for="edit-name">Category Name</label>
            <input
              type="text"
              id="edit-name"
              v-model="editCategoryForm.name"
              :class="{ 'input-error': editFormErrors.name }"
              aria-required="true"
              :aria-invalid="editFormErrors.name ? 'true' : 'false'"
              :aria-describedby="editFormErrors.name ? 'edit-name-error' : undefined"
            />
            <span v-if="editFormErrors.name" class="error-text" id="edit-name-error" role="alert">{{
              editFormErrors.name
            }}</span>
          </div>

          <div class="form-group">
            <label for="edit-description">Description</label>
            <textarea
              id="edit-description"
              v-model="editCategoryForm.description"
              rows="3"
              :class="{ 'input-error': editFormErrors.description }"
              aria-required="true"
              :aria-invalid="editFormErrors.description ? 'true' : 'false'"
              :aria-describedby="editFormErrors.description ? 'edit-description-error' : undefined"
            ></textarea>
            <span
              v-if="editFormErrors.description"
              class="error-text"
              id="edit-description-error"
              role="alert"
              >{{ editFormErrors.description }}</span
            >
          </div>

          <div class="form-group">
            <label for="edit-parentId">Parent Category (Optional)</label>
            <select id="edit-parentId" v-model="editCategoryForm.parentId" aria-required="false">
              <option :value="null">None (Top-Level Category)</option>
              <option
                v-for="cat in categories"
                :key="cat.id"
                :value="cat.id"
                :disabled="cat.id === editCategoryForm.id"
              >
                {{ cat.name }}
              </option>
            </select>
          </div>

          <div class="form-actions">
            <button
              type="button"
              @click="showEditForm = false"
              class="btn-secondary"
              aria-label="Cancel"
            >
              Cancel
            </button>
            <button type="submit" class="btn-primary" aria-label="Submit category form">
              Update Category
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.category-management {
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

.success-message {
  background-color: #f0fdf4;
  color: #16a34a;
  padding: 1rem;
  border-radius: 0.5rem;
  margin-bottom: 1rem;
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

.btn-icon {
  background: none;
  border: none;
  font-size: 1.25rem;
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 4px;
}

.btn-icon.edit {
  color: var(--color-limed-spruce);
}

.btn-icon.delete {
  color: #e02424;
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

/* Modal styles */
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1500;
}

.modal-content {
  background-color: white;
  border-radius: 0.5rem;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.modal-header h3 {
  margin: 0;
  font-weight: 600;
  color: var(--color-limed-spruce);
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--color-slate-gray);
}

.category-form {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--color-limed-spruce);
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 0.25rem;
  font-size: 1rem;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--color-limed-spruce);
  box-shadow: 0 0 0 3px rgba(51, 65, 85, 0.1);
}

.input-error {
  border-color: #e02424 !important;
}

.error-text {
  color: #e02424;
  font-size: 0.875rem;
  margin-top: 0.5rem;
  display: block;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .admin-table th,
  .admin-table td {
    padding: 0.75rem 1rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions button {
    width: 100%;
  }
}
</style>
