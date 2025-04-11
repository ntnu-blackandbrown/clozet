<script setup>
import { ref, onMounted } from 'vue'
import { ShippingService } from '@/api/services/ShippingService'

// State
const shippingOptions = ref([])
const isLoading = ref(true)
const error = ref(null)
const successMessage = ref(null)

// Form for adding/editing shipping options
const shippingForm = ref({
  id: null,
  name: '',
  description: '',
  estimatedDays: 1,
  price: 0,
  isTracked: false,
})

const formMode = ref('add') // 'add' or 'edit'
const showForm = ref(false)
const formErrors = ref({})

// Fetch all shipping options
const fetchShippingOptions = async () => {
  try {
    isLoading.value = true
    error.value = null
    const response = await ShippingService.getAllShippingOptions()
    shippingOptions.value = response.data
    isLoading.value = false
  } catch (err) {
    console.error('Error fetching shipping options:', err)
    error.value = 'Failed to load shipping options'
    isLoading.value = false
  }
}

// Create a new shipping option
const createShippingOption = async () => {
  if (!validateForm()) return

  try {
    isLoading.value = true
    error.value = null
    await ShippingService.createShippingOption(shippingForm.value)
    await fetchShippingOptions()
    isLoading.value = false
    successMessage.value = `Shipping option "${shippingForm.value.name}" created successfully`
    resetForm()
    showForm.value = false

    // Clear success message after 5 seconds
    setTimeout(() => {
      successMessage.value = null
    }, 5000)
  } catch (err) {
    console.error('Error creating shipping option:', err)
    error.value = `Failed to create shipping option: ${err.response?.data?.message || 'Unknown error'}`
    isLoading.value = false
  }
}

// Update an existing shipping option
const updateShippingOption = async () => {
  if (!validateForm()) return

  try {
    isLoading.value = true
    error.value = null
    await ShippingService.updateShippingOption(shippingForm.value.id, shippingForm.value)
    await fetchShippingOptions()
    isLoading.value = false
    successMessage.value = `Shipping option "${shippingForm.value.name}" updated successfully`
    resetForm()
    showForm.value = false

    // Clear success message after 5 seconds
    setTimeout(() => {
      successMessage.value = null
    }, 5000)
  } catch (err) {
    console.error('Error updating shipping option:', err)
    error.value = `Failed to update shipping option: ${err.response?.data?.message || 'Unknown error'}`
    isLoading.value = false
  }
}

// Form submission handler
const handleSubmit = () => {
  if (formMode.value === 'add') {
    createShippingOption()
  } else {
    updateShippingOption()
  }
}

// Open form to add a new shipping option
const addShippingOption = () => {
  formMode.value = 'add'
  resetForm()
  showForm.value = true
}

// Open form to edit an existing shipping option
const editShippingOption = (option) => {
  formMode.value = 'edit'
  shippingForm.value = {
    id: option.id,
    name: option.name,
    description: option.description,
    estimatedDays: option.estimatedDays,
    price: option.price,
    isTracked: option.isTracked,
  }
  showForm.value = true
}

// Reset form fields
const resetForm = () => {
  shippingForm.value = {
    id: null,
    name: '',
    description: '',
    estimatedDays: 1,
    price: 0,
    isTracked: false,
  }
  formErrors.value = {}
}

// Validate form inputs
const validateForm = () => {
  formErrors.value = {}

  if (!shippingForm.value.name.trim()) {
    formErrors.value.name = 'Name is required'
  }

  if (!shippingForm.value.description.trim()) {
    formErrors.value.description = 'Description is required'
  }

  if (shippingForm.value.estimatedDays < 1) {
    formErrors.value.estimatedDays = 'Estimated days must be at least 1'
  }

  if (shippingForm.value.price < 0) {
    formErrors.value.price = 'Price cannot be negative'
  }

  return Object.keys(formErrors.value).length === 0
}

// Format price to NOK
const formatPrice = (price) => {
  return `${price.toFixed(2)} kr`
}

// Load shipping options on component mount
onMounted(() => {
  fetchShippingOptions()
})
</script>

<template>
  <div class="shipping-management">
    <div class="page-header">
      <h1 id="shipping-management-title">Shipping Options Management</h1>
      <button @click="addShippingOption" class="btn-primary" aria-label="Add new shipping option">
        Add New Shipping Option
      </button>
    </div>

    <!-- Success Message -->
    <div v-if="successMessage" class="success-message" role="status">
      {{ successMessage }}
    </div>

    <div v-if="error" class="error-message" role="alert">
      {{ error }}
      <button
        @click="fetchShippingOptions"
        class="btn-secondary"
        aria-label="Retry loading shipping options"
      >
        Retry
      </button>
    </div>

    <!-- Shipping Options Table -->
    <div class="table-container">
      <div style="overflow-x: auto" v-if="!isLoading && shippingOptions.length > 0">
        <table class="admin-table" aria-labelledby="shipping-management-title">
          <thead>
            <tr>
              <th scope="col">ID</th>
              <th scope="col">Name</th>
              <th scope="col">Description</th>
              <th scope="col">Estimated Days</th>
              <th scope="col">Price</th>
              <th scope="col">Tracked</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="option in shippingOptions" :key="option.id">
              <td>{{ option.id }}</td>
              <td>{{ option.name }}</td>
              <td>{{ option.description }}</td>
              <td>{{ option.estimatedDays }} days</td>
              <td>{{ formatPrice(option.price) }}</td>
              <td>
                <span class="tag" :class="option.isTracked ? 'tag-green' : 'tag-gray'">
                  {{ option.isTracked ? 'Yes' : 'No' }}
                </span>
              </td>
              <td class="actions">
                <button
                  @click="editShippingOption(option)"
                  class="btn-icon edit"
                  aria-label="Edit shipping option: {{ option.name }}"
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
        <p>Loading shipping options...</p>
      </div>

      <div v-else class="empty-state">
        <p>No shipping options found</p>
        <button
          @click="addShippingOption"
          class="btn-primary"
          aria-label="Add first shipping option"
        >
          Add Your First Shipping Option
        </button>
      </div>
    </div>

    <!-- Shipping Option Form Modal -->
    <div
      v-if="showForm"
      class="modal-backdrop"
      @click="showForm = false"
      aria-modal="true"
      role="dialog"
      aria-labelledby="shipping-form-title"
    >
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 id="shipping-form-title">
            {{ formMode === 'add' ? 'Add New Shipping Option' : 'Edit Shipping Option' }}
          </h3>
          <button @click="showForm = false" class="btn-close" aria-label="Close form">×</button>
        </div>

        <form @submit.prevent="handleSubmit" class="shipping-form">
          <div class="form-group">
            <label for="name">Name</label>
            <input
              type="text"
              id="name"
              v-model="shippingForm.name"
              :class="{ 'input-error': formErrors.name }"
              aria-required="true"
              :aria-invalid="formErrors.name ? 'true' : 'false'"
              :aria-describedby="formErrors.name ? 'name-error' : undefined"
            />
            <span v-if="formErrors.name" class="error-text" id="name-error" role="alert">{{
              formErrors.name
            }}</span>
          </div>

          <div class="form-group">
            <label for="description">Description</label>
            <textarea
              id="description"
              v-model="shippingForm.description"
              rows="3"
              :class="{ 'input-error': formErrors.description }"
              aria-required="true"
              :aria-invalid="formErrors.description ? 'true' : 'false'"
              :aria-describedby="formErrors.description ? 'description-error' : undefined"
            ></textarea>
            <span
              v-if="formErrors.description"
              class="error-text"
              id="description-error"
              role="alert"
              >{{ formErrors.description }}</span
            >
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label for="estimatedDays">Estimated Days</label>
              <input
                type="number"
                id="estimatedDays"
                v-model.number="shippingForm.estimatedDays"
                min="1"
                :class="{ 'input-error': formErrors.estimatedDays }"
                aria-required="true"
                :aria-invalid="formErrors.estimatedDays ? 'true' : 'false'"
                :aria-describedby="formErrors.estimatedDays ? 'days-error' : undefined"
              />
              <span
                v-if="formErrors.estimatedDays"
                class="error-text"
                id="days-error"
                role="alert"
                >{{ formErrors.estimatedDays }}</span
              >
            </div>

            <div class="form-group half">
              <label for="price">Price (NOK)</label>
              <input
                type="number"
                id="price"
                v-model.number="shippingForm.price"
                step="0.01"
                min="0"
                :class="{ 'input-error': formErrors.price }"
                aria-required="true"
                :aria-invalid="formErrors.price ? 'true' : 'false'"
                :aria-describedby="formErrors.price ? 'price-error' : undefined"
              />
              <span v-if="formErrors.price" class="error-text" id="price-error" role="alert">{{
                formErrors.price
              }}</span>
            </div>
          </div>

          <div class="form-group checkbox-group">
            <label class="checkbox-label">
              <input
                type="checkbox"
                v-model="shippingForm.isTracked"
                id="isTracked"
                aria-label="Tracked shipping"
              />
              <span class="checkbox-text">Tracked Shipping</span>
            </label>
          </div>

          <div class="form-actions">
            <button
              type="button"
              @click="showForm = false"
              class="btn-secondary"
              aria-label="Cancel"
            >
              Cancel
            </button>
            <button type="submit" class="btn-primary" aria-label="Submit shipping option form">
              {{ formMode === 'add' ? 'Add Shipping Option' : 'Update Shipping Option' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.shipping-management {
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

.tag-gray {
  background-color: #f3f4f6;
  color: #6b7280;
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

.shipping-form {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.form-group.half {
  flex: 1;
  margin-bottom: 0;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: var(--color-limed-spruce);
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 0.25rem;
  font-size: 1rem;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: var(--color-limed-spruce);
  box-shadow: 0 0 0 3px rgba(51, 65, 85, 0.1);
}

.checkbox-group {
  margin-bottom: 1.5rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox-label input {
  margin-right: 0.5rem;
  width: auto;
}

.checkbox-text {
  font-weight: 500;
  color: var(--color-limed-spruce);
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

  .form-row {
    flex-direction: column;
    gap: 1.5rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .form-actions button {
    width: 100%;
  }
}
</style>
