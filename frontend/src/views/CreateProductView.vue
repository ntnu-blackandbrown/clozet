<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/AuthStore'
import ProductDisplay from '@/components/product/ProductDisplay.vue'
import axios from 'axios'
import testImage from '@/assets/images/image-1.png'

const router = useRouter()
const userStore = useAuthStore()

// Form data
const formData = ref({
  title: '',
  shortDescription: '',
  longDescription: '',
  price: '',
  categoryId: '',
  locationId: '',
  shippingOptionId: '',
  latitude: '',
  longitude: '',
  condition: '',
  size: '',
  brand: '',
  color: '',
  isVippsPaymentEnabled: false,
  images: [], // Array to store image files
})

// Image upload state
const imageFiles = ref([])
const imagePreviews = ref([])
const isDragging = ref(false)
const maxImages = 5

// Form validation
const errors = ref({})
const isSubmitting = ref(false)
const testResult = ref('')

// Categories (to be fetched from backend)
const categories = ref([
  { id: 1, name: 'Tops' },
  { id: 2, name: 'Bottoms' },
  { id: 3, name: 'Dresses' },
  { id: 4, name: 'Accessories' },
  { id: 5, name: 'Shoes' },
])

// Locations (to be fetched from backend)
const locations = ref([
  { id: 1, name: 'Oslo' },
  { id: 2, name: 'Bergen' },
  { id: 3, name: 'Trondheim' },
])

// Shipping options (to be fetched from backend)
const shippingOptions = ref([
  { id: 1, name: 'Standard Shipping' },
  { id: 2, name: 'Express Shipping' },
  { id: 3, name: 'Local Pickup' },
])

// Condition options
const conditions = ref(['New', 'Like New', 'Good', 'Fair', 'Poor'])

// Size options
const sizes = ref(['XS', 'S', 'M', 'L', 'XL', 'XXL'])

// Add preview modal state
const showPreview = ref(false)

const validateForm = () => {
  errors.value = {}

  if (!formData.value.title) errors.value.title = 'Title is required'
  if (!formData.value.shortDescription)
    errors.value.shortDescription = 'Short description is required'
  if (!formData.value.longDescription) errors.value.longDescription = 'Long description is required'
  if (!formData.value.price) errors.value.price = 'Price is required'
  if (!formData.value.categoryId) errors.value.categoryId = 'Category is required'
  if (!formData.value.locationId) errors.value.locationId = 'Location is required'
  if (!formData.value.shippingOptionId)
    errors.value.shippingOptionId = 'Shipping option is required'
  if (!formData.value.condition) errors.value.condition = 'Condition is required'
  if (!formData.value.size) errors.value.size = 'Size is required'
  if (!formData.value.brand) errors.value.brand = 'Brand is required'
  if (!formData.value.color) errors.value.color = 'Color is required'
  if (imageFiles.value.length === 0) errors.value.images = 'At least one image is required'

  return Object.keys(errors.value).length === 0
}

const handleImageUpload = (event) => {
  const files = Array.from(event.target.files)
  addImages(files)
}

const handleDrop = (event) => {
  event.preventDefault()
  isDragging.value = false

  const files = Array.from(event.dataTransfer.files)
  addImages(files)
}

const addImages = (files) => {
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
        imagePreviews.value.push(e.target.result)
      }
      reader.readAsDataURL(file)
    }
  })
}

const removeImage = (index) => {
  imageFiles.value.splice(index, 1)
  imagePreviews.value.splice(index, 1)
}

const handleSubmit = async () => {
  if (!validateForm()) return

  isSubmitting.value = true
  try {
    // Create FormData object for multipart/form-data
    const submitData = new FormData()

    // Append all form fields
    Object.keys(formData.value).forEach((key) => {
      if (key === 'images') {
        // Append each image file
        imageFiles.value.forEach((file, index) => {
          submitData.append(`images[${index}]`, file)
        })
      } else {
        submitData.append(key, formData.value[key])
      }
    })

    // TODO: Implement API call to create product
    // const response = await createProduct(submitData)
    console.log('Form submitted:', Object.fromEntries(submitData))

    // Redirect to the product view or home page
    router.push('/')
  } catch (error) {
    console.error('Error creating product:', error)
  } finally {
    isSubmitting.value = false
  }
}

const handlePreview = () => {
  showPreview.value = true
}

// Function to send test data to the ItemController
const sendTestData = async () => {
  try {
    testResult.value = 'Sending test data...'

    // Create FormData object for multipart/form-data
    const formData = new FormData()

    // Create mock data based on the CreateItemDTO expected by the backend
    const mockData = {
      title: 'Test Product with Image',
      shortDescription: 'This is a test product with an image',
      longDescription:
        'This is a longer description for the test product. It contains more details about the product and includes an image from the assets folder.',
      price: 299.99,
      categoryId: 1,
      locationId: 1,
      shippingOptionId: 1,
      latitude: 59.913868,
      longitude: 10.752245,
      condition: 'New',
      size: 'M',
      brand: 'Test Brand',
      color: 'Blue',
      isVippsPaymentEnabled: true,
    }

    // Append all mock data fields to FormData
    Object.keys(mockData).forEach((key) => {
      formData.append(key, mockData[key])
    })

    // Fetch the image from assets and convert it to a File object
    try {
      const response = await fetch(testImage)
      const blob = await response.blob()
      const file = new File([blob], 'test-image.png', { type: 'image/png' })

      // Append the image file to FormData
      formData.append('images[0]', file)

      // Send the request to the backend
      const apiResponse = await axios.post('/api/items', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      })

      testResult.value = `Success! Item created with ID: ${apiResponse.data.id}`
      console.log('Test data sent successfully:', apiResponse.data)
    } catch (imageError) {
      console.error('Error processing image:', imageError)
      testResult.value = `Error processing image: ${imageError.message}`
    }
  } catch (error) {
    testResult.value = `Error: ${error.message}`
    console.error('Error sending test data:', error)
  }
}
</script>

<template>
  <div class="create-product-container">
    <h1>Create New Product</h1>

    <!-- Test Button Section -->
    <div class="test-section">
      <h2>Test API Connection</h2>
      <p>Click the button below to send test data with an image to the backend:</p>
      <button @click="sendTestData" class="test-button">Send Test Data with Image</button>
      <div
        v-if="testResult"
        class="test-result"
        :class="{ success: testResult.includes('Success') }"
      >
        {{ testResult }}
      </div>
    </div>

    <form @submit.prevent="handleSubmit" class="product-form">
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
        <span class="error-message" v-if="errors.images">{{ errors.images }}</span>
      </section>

      <!-- Basic Information -->
      <section class="form-section">
        <h2>Basic Information</h2>

        <div class="form-group">
          <label for="title">Title</label>
          <input id="title" v-model="formData.title" type="text" :class="{ error: errors.title }" />
          <span class="error-message" v-if="errors.title">{{ errors.title }}</span>
        </div>

        <div class="form-group">
          <label for="shortDescription">Short Description</label>
          <input
            id="shortDescription"
            v-model="formData.shortDescription"
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
            v-model="formData.longDescription"
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
            v-model="formData.price"
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
          <select id="category" v-model="formData.categoryId" :class="{ error: errors.categoryId }">
            <option value="">Select a category</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
          <span class="error-message" v-if="errors.categoryId">{{ errors.categoryId }}</span>
        </div>

        <div class="form-group">
          <label for="condition">Condition</label>
          <select id="condition" v-model="formData.condition" :class="{ error: errors.condition }">
            <option value="">Select condition</option>
            <option v-for="condition in conditions" :key="condition" :value="condition">
              {{ condition }}
            </option>
          </select>
          <span class="error-message" v-if="errors.condition">{{ errors.condition }}</span>
        </div>

        <div class="form-group">
          <label for="size">Size</label>
          <select id="size" v-model="formData.size" :class="{ error: errors.size }">
            <option value="">Select size</option>
            <option v-for="size in sizes" :key="size" :value="size">
              {{ size }}
            </option>
          </select>
          <span class="error-message" v-if="errors.size">{{ errors.size }}</span>
        </div>

        <div class="form-group">
          <label for="brand">Brand</label>
          <input id="brand" v-model="formData.brand" type="text" :class="{ error: errors.brand }" />
          <span class="error-message" v-if="errors.brand">{{ errors.brand }}</span>
        </div>

        <div class="form-group">
          <label for="color">Color</label>
          <input id="color" v-model="formData.color" type="text" :class="{ error: errors.color }" />
          <span class="error-message" v-if="errors.color">{{ errors.color }}</span>
        </div>
      </section>

      <!-- Location & Shipping -->
      <section class="form-section">
        <h2>Location & Shipping</h2>

        <div class="form-group">
          <label for="location">Location</label>
          <select id="location" v-model="formData.locationId" :class="{ error: errors.locationId }">
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
            v-model="formData.shippingOptionId"
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
            <input type="checkbox" v-model="formData.isVippsPaymentEnabled" />
            Enable Vipps Payment
          </label>
        </div>
      </section>

      <div class="form-actions">
        <button type="button" @click="router.back()" class="cancel-button">Cancel</button>
        <button type="button" class="preview-button" @click="handlePreview">Preview Product</button>
        <button type="submit" class="submit-button" :disabled="isSubmitting">
          {{ isSubmitting ? 'Creating...' : 'Create Product' }}
        </button>
      </div>
    </form>

    <!-- Preview Modal -->
    <div v-if="showPreview" class="preview-modal">
      <div class="preview-modal-content">
        <button class="close-button" @click="showPreview = false">×</button>
        <ProductDisplay
          :images="imagePreviews"
          :title="formData.title"
          :description_full="formData.longDescription"
          :category="categories.find((c) => c.id === formData.categoryId)?.name || ''"
          :location="locations.find((l) => l.id === formData.locationId)?.name || ''"
          :price="Number(formData.price)"
          :seller="userStore.user?.name || 'Current User'"
          :shipping_options="
            shippingOptions.find((s) => s.id === formData.shippingOptionId)?.name || ''
          "
          :status="'Available'"
          :created_at="new Date().toLocaleDateString()"
          :updated_at="new Date().toLocaleDateString()"
          :purchased="false"
        />
      </div>
    </div>
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

/* Test Section Styles */
.test-section {
  background: #f0f9ff;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
  border: 1px solid #bae6fd;
}

.test-section h2 {
  margin-bottom: 1rem;
  color: #0369a1;
  font-size: 1.25rem;
}

.test-button {
  background-color: #0ea5e9;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 0.75rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  margin-top: 0.5rem;
}

.test-button:hover {
  background-color: #0284c7;
}

.test-result {
  margin-top: 1rem;
  padding: 0.75rem;
  border-radius: 6px;
  background-color: #fee2e2;
  color: #b91c1c;
  font-size: 0.875rem;
}

.test-result.success {
  background-color: #dcfce7;
  color: #166534;
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

.preview-button {
  background-color: #4a90e2;
  color: white;
  border: none;
  border-radius: 0.375rem;
  padding: 0.75rem 1.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.preview-button:hover {
  background-color: #357abd;
}

.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.preview-modal-content {
  background-color: white;
  padding: 2rem;
  border-radius: 0.5rem;
  max-width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
}

.close-button {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #666;
}

.close-button:hover {
  color: #333;
}
</style>
