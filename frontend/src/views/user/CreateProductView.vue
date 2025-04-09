<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/AuthStore'
import { useCategoryStore } from '@/stores/Category'
import { useShippingOptionStore } from '@/stores/ShippingOption'
import { useValidatedForm, useValidatedField } from '@/utils/validation/useValidation'
import { productSchema } from '@/utils/validation/schemas'
import { useLocationStore } from '@/stores/Location'
import { ProductService } from '@/api/services/ProductService'

// Define props
const props = defineProps<{ id?: number }>()

// Define interfaces for TypeScript
interface Category {
  id: number
  name: string
}

interface ShippingOption {
  id: number
  name: string
}

interface Location {
  id: number
  name: string
}

interface User {
  id: number
  usernameOrEmail: string
  email: string
  firstName?: string
  lastName?: string
  active: boolean
  role: string
  phoneNumber?: string
  name?: string
}

const router = useRouter()
const route = useRoute()
const userStore = useAuthStore()
const categoryStore = useCategoryStore()
const shippingOptionStore = useShippingOptionStore()
const locationStore = useLocationStore()

// Initial form data structure (matching schema keys)
const initialFormData = {
  title: '',
  shortDescription: '',
  longDescription: '',
  price: '', // Keep as string for input binding, schema handles number conversion
  categoryId: '',
  locationId: '',
  shippingOptionId: '',
  condition: '',
  size: '',
  brand: '',
  color: '',
  isVippsPaymentEnabled: false,
  // images are handled separately
}

// Image upload state
const imageFiles = ref<File[]>([])
const imagePreviews = ref<string[]>([])
const isDragging = ref(false)
const maxImages = 5

// Form validation
const testResult = ref('')

// Determine if we are in edit mode
const isEditMode = computed(() => !!props.id)

// Categories (to be fetched from backend)
const categories = ref<Category[]>([])
// Shipping options (to be fetched from backend)
const shippingOptions = ref<ShippingOption[]>([])

// Locations (to be fetched from backend)
const locations = ref<Location[]>([])

//on mount, fetch categories, options, locations and potentially existing item data
onMounted(async () => {
  await categoryStore.fetchCategories()
  categories.value = categoryStore.categories
  await shippingOptionStore.fetchShippingOptions()
  shippingOptions.value = shippingOptionStore.shippingOptions
  await locationStore.fetchLocations()
  locations.value = locationStore.locations

  if (isEditMode.value && props.id) {
    // Fetch item data for editing
    try {
      const itemResponse = await ProductService.getItemById(props.id)
      const itemData = itemResponse.data

      // Populate form fields with existing data
      title.value = itemData.title
      shortDescription.value = itemData.shortDescription
      longDescription.value = itemData.longDescription
      price.value = itemData.price.toString()
      categoryId.value = itemData.categoryId.toString()
      locationId.value = itemData.locationId.toString()
      shippingOptionId.value = itemData.shippingOptionId.toString()
      condition.value = itemData.condition
      size.value = itemData.size
      brand.value = itemData.brand
      color.value = itemData.color
      isVippsPaymentEnabled.value = itemData.vippsPaymentEnabled

      // Fetch and populate images
      const imagesResponse = await ProductService.getItemImages(props.id)
      const fetchedImages = imagesResponse.data

      // We need to convert image URLs back to File objects or manage them differently
      // For simplicity here, we'll just store URLs for preview, but submission needs adjustment
      imagePreviews.value = fetchedImages.map((img: any) => img.imageUrl)
      // Note: We cannot easily recreate File objects from URLs. Image updating might need a separate logic.
      // For now, we'll clear the `imageFiles` ref in edit mode, requiring re-upload if changes are needed.
      imageFiles.value = [] // Clear file list, user must re-upload to change images.

    } catch (error) {
      console.error("Error fetching item data for edit:", error)
      // Optionally redirect or show an error message
      testResult.value = 'Error loading item data.'
      router.push('/') // Redirect home on error
    }
  }
})

// Condition options
const conditions = ref(['New', 'Like New', 'Good', 'Fair', 'Poor'])

// Size options
const sizes = ref(['XS', 'S', 'M', 'L', 'XL', 'XXL'])

// Add preview modal state
const showPreview = ref(false)

// Setup form validation using the new hook
const { handleSubmit, errors, resetForm, isFormValid: isVeeValid, isSubmitting, values } = useValidatedForm(
  productSchema,
  initialFormData,
)

// Setup form fields with the new hook, specifying types
const { value: title, errorMessage: titleError } = useValidatedField<string>('title')
const { value: shortDescription, errorMessage: shortDescriptionError } =
  useValidatedField<string>('shortDescription')
const { value: longDescription, errorMessage: longDescriptionError } =
  useValidatedField<string>('longDescription')
const { value: price, errorMessage: priceError } = useValidatedField<string>('price')
const { value: categoryId, errorMessage: categoryIdError } = useValidatedField<string>('categoryId')
const { value: locationId, errorMessage: locationIdError } = useValidatedField<string>('locationId')
const { value: shippingOptionId, errorMessage: shippingOptionIdError } =
  useValidatedField<string>('shippingOptionId')
const { value: condition, errorMessage: conditionError } = useValidatedField<string>('condition')
const { value: size, errorMessage: sizeError } = useValidatedField<string>('size')
const { value: brand, errorMessage: brandError } = useValidatedField<string>('brand')
const { value: color, errorMessage: colorError } = useValidatedField<string>('color')
const { value: isVippsPaymentEnabled } = useValidatedField<boolean>('isVippsPaymentEnabled')

// Updated computed property to check if form is valid
const isFormValid = computed(() => {
  // In edit mode, image upload might not be strictly required if keeping old ones
  // However, our current setup requires re-upload for changes.
  // Let's keep the check simple: form must be valid according to VeeValidate.
  // We might need more complex logic if we allow keeping existing images without re-uploading.
  const imagesRequired = !isEditMode.value // Only require images for new products
  return isVeeValid.value && (imagesRequired ? imageFiles.value.length > 0 : true)
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

  const files = Array.from(event.dataTransfer?.files || [])
  addImages(files)
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
        imagePreviews.value.push(e.target?.result as string)
      }
      reader.readAsDataURL(file)
    }
  })
}

const removeImage = (index: number) => {
  imageFiles.value.splice(index, 1)
  imagePreviews.value.splice(index, 1)
}

const onSubmit = handleSubmit(async (formValues) => {
  // Check image requirement again, especially for create mode
  if (!isEditMode.value && imageFiles.value.length === 0) {
    alert('Please upload at least one image for a new product.')
    return
  }

  testResult.value = isEditMode.value ? 'Updating product...' : 'Submitting product...'

  try {
    const payload = {
      title: formValues.title,
      shortDescription: formValues.shortDescription,
      longDescription: formValues.longDescription,
      price: parseFloat(formValues.price), // Ensure price is parsed correctly
      categoryId: parseInt(formValues.categoryId),
      locationId: parseInt(formValues.locationId),
      shippingOptionId: parseInt(formValues.shippingOptionId),
      condition: formValues.condition,
      size: formValues.size,
      brand: formValues.brand,
      color: formValues.color,
      isVippsPaymentEnabled: formValues.isVippsPaymentEnabled,
      // sellerId is handled by the backend using the authenticated user
    }

    let itemId: number;

    if (isEditMode.value && props.id) {
      // 1. Update the item details
      const response = await ProductService.updateItem(props.id, payload)
      itemId = response.data.id
      testResult.value = `Success! Item updated with ID: ${itemId}`
      console.log('Product updated:', response.data)

      // 2. Handle image updates (if new images were added)
      // This part is tricky because we cleared imageFiles. If the user adds new images,
      // we need to upload them. If they didn't, we assume they keep the old ones.
      // The backend might need logic to handle removal of old images if new ones are uploaded.
      if (imageFiles.value.length > 0) {
        console.log('New images detected for upload during update...')
        // Potential: Delete existing images before uploading new ones? Requires backend support.
        // await ProductService.deleteItemImages(itemId); // Example: if backend supports this
        for (const file of imageFiles.value) {
          const formData = new FormData()
          formData.append('file', file)
          formData.append('itemId', itemId.toString())
          await ProductService.uploadImages(formData)
        }
        testResult.value += ' and new images uploaded successfully.'
        console.log('New images uploaded.')
      } else {
        testResult.value += '. Kept existing images.'
      }

    } else {
      // Create Mode
      // 1. Create the item first
      const response = await ProductService.createItem(payload)
      itemId = response.data.id
      testResult.value = `Success! Item created with ID: ${itemId}`
      console.log('Product created:', response.data)

      // 2. Upload each image
      for (const file of imageFiles.value) {
        const formData = new FormData()
        formData.append('file', file)
        formData.append('itemId', itemId.toString())
        await ProductService.uploadImages(formData)
      }
      testResult.value += ' and images uploaded successfully.'
      console.log('All images uploaded.')
    }

    // Optional: redirect after success
    setTimeout(() => {
      // Redirect to the product detail page after create/update
      router.push(`/products/${itemId}`)
    }, 1500)

  } catch (error: any) {
    testResult.value = `Error: ${error.response?.data?.message || error.message}`
    console.error('Error submitting form:', error)
  }
})

</script>

<template>
  <div class="create-product-container">
    <h1>{{ isEditMode ? 'Edit Product' : 'Create New Product' }}</h1>

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
        <span class="error-message" v-if="!isFormValid && !isEditMode && imageFiles.length === 0"
          >At least one image is required for a new product</span
        >
        <span class="info-message" v-if="isEditMode && imageFiles.length === 0"
         >Current images will be kept. Upload new images to replace them.</span
        >
      </section>

      <!-- Basic Information -->
      <section class="form-section">
        <h2>Basic Information</h2>

        <div class="form-group">
          <label for="title">Title</label>
          <input id="title" v-model="title" type="text" :class="{ error: titleError }" />
          <span class="error-message" v-if="titleError">{{ titleError }}</span>
        </div>

        <div class="form-group">
          <label for="shortDescription">Short Description</label>
          <input
            id="shortDescription"
            v-model="shortDescription"
            type="text"
            :class="{ error: shortDescriptionError }"
          />
          <span class="error-message" v-if="shortDescriptionError">{{
            shortDescriptionError
          }}</span>
        </div>

        <div class="form-group">
          <label for="longDescription">Long Description</label>
          <textarea
            id="longDescription"
            v-model="longDescription"
            rows="4"
            :class="{ error: longDescriptionError }"
          ></textarea>
          <span class="error-message" v-if="longDescriptionError">{{ longDescriptionError }}</span>
        </div>

        <div class="form-group">
          <label for="price">Price (NOK)</label>
          <input
            id="price"
            v-model="price"
            type="number"
            min="0"
            step="0.01"
            :class="{ error: priceError }"
          />
          <span class="error-message" v-if="priceError">{{ priceError }}</span>
        </div>
      </section>

      <!-- Product Details -->
      <section class="form-section">
        <h2>Product Details</h2>

        <div class="form-group">
          <label for="category">Category</label>
          <select id="category" v-model="categoryId" :class="{ error: categoryIdError }">
            <option value="">Select a category</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
          <span class="error-message" v-if="categoryIdError">{{ categoryIdError }}</span>
        </div>

        <div class="form-group">
          <label for="condition">Condition</label>
          <select id="condition" v-model="condition" :class="{ error: conditionError }">
            <option value="">Select condition</option>
            <option v-for="c in conditions" :key="c" :value="c">
              {{ c }}
            </option>
          </select>
          <span class="error-message" v-if="conditionError">{{ conditionError }}</span>
        </div>

        <div class="form-group">
          <label for="size">Size</label>
          <select id="size" v-model="size" :class="{ error: sizeError }">
            <option value="">Select size</option>
            <option v-for="s in sizes" :key="s" :value="s">
              {{ s }}
            </option>
          </select>
          <span class="error-message" v-if="sizeError">{{ sizeError }}</span>
        </div>

        <div class="form-group">
          <label for="brand">Brand</label>
          <input id="brand" v-model="brand" type="text" :class="{ error: brandError }" />
          <span class="error-message" v-if="brandError">{{ brandError }}</span>
        </div>

        <div class="form-group">
          <label for="color">Color</label>
          <input id="color" v-model="color" type="text" :class="{ error: colorError }" />
          <span class="error-message" v-if="colorError">{{ colorError }}</span>
        </div>
      </section>

      <!-- Location & Shipping -->
      <section class="form-section">
        <h2>Location & Shipping</h2>

        <div class="form-group">
          <label for="location">Location</label>
          <select id="location" v-model="locationId" :class="{ error: locationIdError }">
            <option value="">Select location</option>
            <option v-for="location in locations" :key="location.id" :value="location.id">
              {{ location.name }}
            </option>
          </select>
          <span class="error-message" v-if="locationIdError">{{ locationIdError }}</span>
        </div>

        <div class="form-group">
          <label for="shipping">Shipping Option</label>
          <select
            id="shipping"
            v-model="shippingOptionId"
            :class="{ error: shippingOptionIdError }"
          >
            <option value="">Select shipping option</option>
            <option v-for="option in shippingOptions" :key="option.id" :value="option.id">
              {{ option.name }}
            </option>
          </select>
          <span class="error-message" v-if="shippingOptionIdError">{{
            shippingOptionIdError
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
        <button type="button" @click="showPreview = true" class="preview-button" :disabled="!isVeeValid">Preview</button>
        <button type="submit" class="submit-button" :disabled="!isFormValid || isSubmitting">
          {{ isSubmitting ? (isEditMode ? 'Updating...' : 'Creating...') : (isEditMode ? 'Update Product' : 'Create Product') }}
        </button>
      </div>
    </form>

    <!-- Preview Modal -->
    <div v-if="showPreview" class="preview-modal">
      <div class="preview-modal-content">
        <button class="close-button" @click="showPreview = false">×</button>
        <ProductDisplay
          :images="imagePreviews"
          :title="title || ''"
          :description_full="longDescription || ''"
          :category="categories.find((c: Category) => c.id === parseInt(categoryId))?.name || ''"
          :location="locations.find((l: Location) => l.id === parseInt(locationId))?.name || ''"
          :price="Number(price) || 0"
          :seller="userStore.user?.firstName || userStore.user?.usernameOrEmail || 'Current User'"
          :shipping_options="
            shippingOptions.find((s: ShippingOption) => s.id === parseInt(shippingOptionId))
              ?.name || ''
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

/* Add style for info message */
.info-message {
  display: block;
  margin-top: 0.5rem;
  color: #3b82f6; /* Blue color for info */
  font-size: 0.875rem;
}
</style>
