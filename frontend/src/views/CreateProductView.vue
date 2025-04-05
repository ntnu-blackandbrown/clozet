<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/AuthStore'
import { useCategoryStore } from '@/stores/Category'
import { useShippingOptionStore } from '@/stores/ShippingOption'
import axios from '@/api/axios'
import { useField, useForm } from 'vee-validate'
import * as yup from 'yup'

interface Category {
  id: number
  name: string
  description?: string
  parentId?: number
  createdAt?: string
  updatedAt?: string
  subcategoryIds?: number[]
  parentName?: string
}

interface ShippingOption {
  id: number
  name: string
  description: string
  estimatedDays: number
  price: number
  isTracked: boolean
}

interface Location {
  id: number
  name: string
}

interface FormValues {
  title: string
  shortDescription: string
  longDescription: string
  price: string
  categoryId: string
  locationId: string
  shippingOptionId: string
  condition: string
  size: string
  brand: string
  color: string
  isVippsPaymentEnabled: boolean
}

const router = useRouter()
const userStore = useAuthStore()
const categoryStore = useCategoryStore()
const shippingOptionStore = useShippingOptionStore()

// Image upload state
const imageFiles = ref<File[]>([])
const imagePreviews = ref<string[]>([])
const isDragging = ref(false)
const maxImages = 5

// Form validation
const isSubmitting = ref(false)

// Categories (to be fetched from backend)
const categories = ref<Category[]>([])
// Shipping options (to be fetched from backend)
const shippingOptions = ref<ShippingOption[]>([])

// Locations (to be fetched from backend)
const locations = ref<Location[]>([
  { id: 1, name: 'Oslo' },
  { id: 2, name: 'Bergen' },
  { id: 3, name: 'Trondheim' },
])

//on mount, fetch categories
onMounted(async () => {
  await categoryStore.fetchCategories()
  categories.value = categoryStore.categories
  await shippingOptionStore.fetchShippingOptions()
  shippingOptions.value = shippingOptionStore.shippingOptions
})

// Condition options
const conditions = ref(['New', 'Like New', 'Good', 'Fair', 'Poor'])

// Size options
const sizes = ref(['XS', 'S', 'M', 'L', 'XL', 'XXL'])

// Validation schema
const schema = yup.object({
  title: yup.string().required('Title is required'),
  shortDescription: yup.string().required('Short description is required'),
  longDescription: yup.string().required('Long description is required'),
  price: yup
    .number()
    .required('Price is required')
    .positive('Price must be positive')
    .typeError('Price must be a number'),
  categoryId: yup.number().required('Category is required'),
  locationId: yup.number().required('Location is required'),
  shippingOptionId: yup.number().required('Shipping option is required'),
  condition: yup.string().required('Condition is required'),
  size: yup.string().required('Size is required'),
  brand: yup.string().required('Brand is required'),
  color: yup.string().required('Color is required'),
  isVippsPaymentEnabled: yup.boolean(),
})

// Initialize form with vee-validate
const { handleSubmit, errors, resetForm } = useForm<FormValues>({
  validationSchema: schema,
  initialValues: {
    title: '',
    shortDescription: '',
    longDescription: '',
    price: '',
    categoryId: '',
    locationId: '',
    shippingOptionId: '',
    condition: '',
    size: '',
    brand: '',
    color: '',
    isVippsPaymentEnabled: false,
  },
})

// Form fields
const { value: title } = useField<string>('title')
const { value: shortDescription } = useField<string>('shortDescription')
const { value: longDescription } = useField<string>('longDescription')
const { value: price } = useField<string>('price')
const { value: categoryId } = useField<string>('categoryId')
const { value: locationId } = useField<string>('locationId')
const { value: shippingOptionId } = useField<string>('shippingOptionId')
const { value: condition } = useField<string>('condition')
const { value: size } = useField<string>('size')
const { value: brand } = useField<string>('brand')
const { value: color } = useField<string>('color')
const { value: isVippsPaymentEnabled } = useField<boolean>('isVippsPaymentEnabled')

// Computed property to check if form is valid
const isFormValid = computed(() => {
  return (
    !errors.value.title &&
    !errors.value.shortDescription &&
    !errors.value.longDescription &&
    !errors.value.price &&
    !errors.value.categoryId &&
    !errors.value.locationId &&
    !errors.value.shippingOptionId &&
    !errors.value.condition &&
    !errors.value.size &&
    !errors.value.brand &&
    !errors.value.color &&
    title.value &&
    shortDescription.value &&
    longDescription.value &&
    price.value &&
    categoryId.value &&
    locationId.value &&
    shippingOptionId.value &&
    condition.value &&
    size.value &&
    brand.value &&
    color.value &&
    imageFiles.value.length > 0
  )
})

const handleImageUpload = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files) {
    const files = Array.from(input.files)
    addImages(files)
  }
}

const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  isDragging.value = false

  if (event.dataTransfer?.files) {
    const files = Array.from(event.dataTransfer.files)
    addImages(files)
  }
}

const addImages = (files: File[]) => {
  const validFiles = files.filter((file) => file.type.startsWith('image/'))

  if (validFiles.length + imageFiles.value.length > maxImages) {
    alert(`You can only upload up to ${maxImages} images`)
    return
  }

  validFiles.forEach((file) => {
    if (imageFiles.value.length < maxImages) {
      imageFiles.value.push(file)
      const reader = new FileReader()
      reader.onload = (e) => {
        if (e.target?.result) {
          imagePreviews.value.push(e.target.result as string)
        }
      }
      reader.readAsDataURL(file)
    }
  })
}

const removeImage = (index: number) => {
  imageFiles.value.splice(index, 1)
  imagePreviews.value.splice(index, 1)
}

const onSubmit = handleSubmit(async (values) => {
  if (imageFiles.value.length === 0) {
    alert('Please upload at least one image')
    return
  }

  isSubmitting.value = true
  try {
    // Create FormData object for multipart/form-data
    const submitData = new FormData()

    // Append all form fields
    Object.keys(values).forEach((key) => {
      const typedKey = key as keyof FormValues
      submitData.append(key, String(values[typedKey]))
    })

    // Append each image file
    imageFiles.value.forEach((file, index) => {
      submitData.append(`images[${index}]`, file)
    })

    const response = await axios.post('/api/items', submitData)
    console.log('Form submitted:', Object.fromEntries(submitData))

    // Redirect to the product view or home page
    router.push('/')
  } catch (error) {
    console.error('Error creating product:', error)
  } finally {
    isSubmitting.value = false
  }
})
</script>

<template>
  <div class="create-product-container">
    <h1>Create New Product</h1>

    <form @submit.prevent="onSubmit" class="product-form">
      <!-- Image Upload Section -->
      <section class="form-section">
        <h2>Product Images</h2>
        <div class="image-upload-container">
          <div
            class="image-upload-area"
            :class="{ dragging: isDragging }"
            @dragenter.prevent="isDragging = true"
            @dragleave.prevent="isDragging = false"
            @dragover.prevent
            @drop="handleDrop"
          >
            <input
              type="file"
              accept="image/*"
              multiple
              @change="handleImageUpload"
              class="file-input"
              id="image-upload"
            />
            <label for="image-upload" class="upload-label">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="upload-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              >
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                <polyline points="17 8 12 3 7 8" />
                <line x1="12" y1="3" x2="12" y2="15" />
              </svg>
              <p>Drag and drop images here or click to select</p>
              <p class="upload-hint">Maximum {{ maxImages }} images allowed</p>
            </label>
          </div>

          <!-- Image Previews -->
          <div v-if="imagePreviews.length > 0" class="image-previews">
            <div v-for="(preview, index) in imagePreviews" :key="index" class="image-preview">
              <img :src="preview" :alt="'Product image ' + (index + 1)" />
              <button
                type="button"
                class="remove-image"
                @click="removeImage(index)"
                aria-label="Remove image"
              >
                ×
              </button>
            </div>
          </div>
        </div>
        <span class="error-message" v-if="imageFiles.length === 0"
          >Please upload at least one image</span
        >
      </section>

      <!-- Basic Information -->
      <section class="form-section">
        <h2>Basic Information</h2>

        <div class="form-group">
          <label for="title">Title</label>
          <input id="title" v-model="title" type="text" :class="{ error: errors.title }" />
          <span class="error-message" v-if="errors.title">{{ errors.title }}</span>
        </div>

        <div class="form-group">
          <label for="shortDescription">Short Description</label>
          <input
            id="shortDescription"
            v-model="shortDescription"
            type="text"
            :class="{ error: errors.shortDescription }"
          />
          <span class="error-message" v-if="errors.shortDescription">{{
            errors.shortDescription
          }}</span>
        </div>

        <div class="form-group">
          <label for="longDescription">Long Description</label>
          <textarea
            id="longDescription"
            v-model="longDescription"
            rows="4"
            :class="{ error: errors.longDescription }"
          ></textarea>
          <span class="error-message" v-if="errors.longDescription">{{
            errors.longDescription
          }}</span>
        </div>

        <div class="form-group">
          <label for="price">Price (NOK)</label>
          <input
            id="price"
            v-model="price"
            type="number"
            min="0"
            step="0.01"
            :class="{ error: errors.price }"
          />
          <span class="error-message" v-if="errors.price">{{ errors.price }}</span>
        </div>
      </section>

      <!-- Product Details -->
      <section class="form-section">
        <h2>Product Details</h2>

        <div class="form-group">
          <label for="category">Category</label>
          <select id="category" v-model="categoryId" :class="{ error: errors.categoryId }">
            <option value="">Select a category</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
          <span class="error-message" v-if="errors.categoryId">{{ errors.categoryId }}</span>
        </div>

        <div class="form-group">
          <label for="condition">Condition</label>
          <select id="condition" v-model="condition" :class="{ error: errors.condition }">
            <option value="">Select condition</option>
            <option v-for="condition in conditions" :key="condition" :value="condition">
              {{ condition }}
            </option>
          </select>
          <span class="error-message" v-if="errors.condition">{{ errors.condition }}</span>
        </div>

        <div class="form-group">
          <label for="size">Size</label>
          <select id="size" v-model="size" :class="{ error: errors.size }">
            <option value="">Select size</option>
            <option v-for="size in sizes" :key="size" :value="size">
              {{ size }}
            </option>
          </select>
          <span class="error-message" v-if="errors.size">{{ errors.size }}</span>
        </div>

        <div class="form-group">
          <label for="brand">Brand</label>
          <input id="brand" v-model="brand" type="text" :class="{ error: errors.brand }" />
          <span class="error-message" v-if="errors.brand">{{ errors.brand }}</span>
        </div>

        <div class="form-group">
          <label for="color">Color</label>
          <input id="color" v-model="color" type="text" :class="{ error: errors.color }" />
          <span class="error-message" v-if="errors.color">{{ errors.color }}</span>
        </div>
      </section>

      <!-- Location & Shipping -->
      <section class="form-section">
        <h2>Location & Shipping</h2>

        <div class="form-group">
          <label for="location">Location</label>
          <select id="location" v-model="locationId" :class="{ error: errors.locationId }">
            <option value="">Select location</option>
            <option v-for="location in locations" :key="location.id" :value="location.id">
              {{ location.name }}
            </option>
          </select>
          <span class="error-message" v-if="errors.locationId">{{ errors.locationId }}</span>
        </div>

        <div class="form-group">
          <label for="shipping">Shipping Option</label>
          <select
            id="shipping"
            v-model="shippingOptionId"
            :class="{ error: errors.shippingOptionId }"
          >
            <option value="">Select shipping option</option>
            <option v-for="option in shippingOptions" :key="option.id" :value="option.id">
              {{ option.name }}
            </option>
          </select>
          <span class="error-message" v-if="errors.shippingOptionId">{{
            errors.shippingOptionId
          }}</span>
        </div>

        <div class="form-group">
          <label class="checkbox-label">
            <input type="checkbox" v-model="isVippsPaymentEnabled" />
            Enable Vipps Payment
          </label>
        </div>
      </section>

      <div class="form-actions">
        <button type="button" @click="router.back()" class="cancel-button">Cancel</button>
        <button type="submit" class="submit-button" :disabled="!isFormValid">
          {{ isSubmitting ? 'Creating...' : 'Create Product' }}
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.create-product-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
}

.create-product-container h1 {
  margin-bottom: 2rem;
  color: #333;
}

.form-section {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.form-section h2 {
  margin-bottom: 1.5rem;
  color: #4a5568;
  font-size: 1.25rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  color: #4a5568;
  font-weight: 500;
}

input[type='text'],
input[type='number'],
select,
textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

input[type='text']:focus,
input[type='number']:focus,
select:focus,
textarea:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.error {
  border-color: #ef4444;
}

.error-message {
  display: block;
  margin-top: 0.5rem;
  color: #ef4444;
  font-size: 0.875rem;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.checkbox-label input[type='checkbox'] {
  width: 1.25rem;
  height: 1.25rem;
  margin: 0;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 2rem;
}

.cancel-button,
.submit-button {
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-button {
  background-color: #e2e8f0;
  color: #4a5568;
  border: none;
}

.cancel-button:hover {
  background-color: #cbd5e0;
}

.submit-button {
  background-color: #3b82f6;
  color: white;
  border: none;
}

.submit-button:hover:not(:disabled) {
  background-color: #2563eb;
}

.submit-button:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

@media (max-width: 640px) {
  .create-product-container {
    padding: 1rem;
  }

  .form-actions {
    flex-direction: column;
  }

  .cancel-button,
  .submit-button {
    width: 100%;
  }
}

.image-upload-container {
  margin-bottom: 1.5rem;
}

.image-upload-area {
  border: 2px dashed #e2e8f0;
  border-radius: 8px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  background-color: #f8fafc;
}

.image-upload-area.dragging {
  border-color: #3b82f6;
  background-color: #eff6ff;
}

.file-input {
  display: none;
}

.upload-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.upload-icon {
  width: 48px;
  height: 48px;
  color: #64748b;
}

.upload-hint {
  font-size: 0.875rem;
  color: #64748b;
}

.image-previews {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.image-preview {
  position: relative;
  aspect-ratio: 1;
  border-radius: 8px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-image {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.25rem;
  line-height: 1;
  transition: background-color 0.2s;
}

.remove-image:hover {
  background-color: rgba(0, 0, 0, 0.7);
}

@media (max-width: 640px) {
  .image-previews {
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }
}
</style>
