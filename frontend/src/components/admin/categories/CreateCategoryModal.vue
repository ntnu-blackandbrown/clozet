<script setup>
import { ref, reactive, watch } from 'vue'

const props = defineProps({
  isVisible: {
    type: Boolean,
    required: true,
  },
  existingCategories: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['close', 'create'])

const categoryData = reactive({
  name: '',
  description: '',
  parentId: null,
})

const formErrors = reactive({
  name: null,
  description: null,
})

// Reset form when modal becomes visible
watch(
  () => props.isVisible,
  (newValue) => {
    if (newValue) {
      resetForm()
    }
  }
)

const validateForm = () => {
  formErrors.name = null
  formErrors.description = null
  let isValid = true

  if (!categoryData.name.trim()) {
    formErrors.name = 'Category name is required'
    isValid = false
  } else if (categoryData.name.length < 3 || categoryData.name.length > 100) {
    formErrors.name = 'Name must be between 3 and 100 characters'
    isValid = false
  }

  if (!categoryData.description.trim()) {
    formErrors.description = 'Description is required'
    isValid = false
  } else if (categoryData.description.length > 255) {
    formErrors.description = 'Description cannot exceed 255 characters'
    isValid = false
  }

  return isValid
}

const handleSubmit = () => {
  if (validateForm()) {
    emit('create', { ...categoryData })
  }
}

const closeModal = () => {
  emit('close')
}

const resetForm = () => {
  categoryData.name = ''
  categoryData.description = ''
  categoryData.parentId = null
  formErrors.name = null
  formErrors.description = null
}
</script>

<template>
  <div
    v-if="isVisible"
    class="modal-backdrop"
    @click="closeModal"
    aria-modal="true"
    role="dialog"
    aria-labelledby="create-category-modal-title"
  >
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h3 id="create-category-modal-title">Add New Category</h3>
        <button @click="closeModal" class="btn-close" aria-label="Close form">×</button>
      </div>

      <form @submit.prevent="handleSubmit" class="category-form">
        <div class="form-group">
          <label for="create-name">Category Name</label>
          <input
            type="text"
            id="create-name"
            v-model="categoryData.name"
            :class="{ 'input-error': formErrors.name }"
            aria-required="true"
            :aria-invalid="formErrors.name ? 'true' : 'false'"
            :aria-describedby="formErrors.name ? 'create-name-error' : undefined"
          />
          <span v-if="formErrors.name" class="error-text" id="create-name-error" role="alert">{{ formErrors.name }}</span>
        </div>

        <div class="form-group">
          <label for="create-description">Description</label>
          <textarea
            id="create-description"
            v-model="categoryData.description"
            rows="3"
            :class="{ 'input-error': formErrors.description }"
            aria-required="true"
            :aria-invalid="formErrors.description ? 'true' : 'false'"
            :aria-describedby="formErrors.description ? 'create-description-error' : undefined"
          ></textarea>
          <span v-if="formErrors.description" class="error-text" id="create-description-error" role="alert">{{ formErrors.description }}</span>
        </div>

        <div class="form-group">
          <label for="create-parentId">Parent Category (Optional)</label>
          <select id="create-parentId" v-model="categoryData.parentId" aria-required="false">
            <option :value="null">None (Top-Level Category)</option>
            <option
              v-for="cat in existingCategories"
              :key="cat.id"
              :value="cat.id"
            >
              {{ cat.name }}
            </option>
          </select>
        </div>

        <div class="form-actions">
          <button type="button" @click="closeModal" class="btn-secondary" aria-label="Cancel">Cancel</button>
          <button type="submit" class="btn-primary" aria-label="Add Category">Add Category</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
/* Reusing styles from CategoryManagement.vue for consistency */
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
  z-index: 100;
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

/* Minimal adjustments for potential small screen improvements */
@media (max-width: 768px) {
  .form-actions {
    flex-direction: column;
  }
  .form-actions button {
    width: 100%;
  }
}
</style>
