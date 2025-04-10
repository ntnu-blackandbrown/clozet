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
import { useI18n } from 'vue-i18n'

// Get i18n instance
const { t } = useI18n()

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
      console.error('Error fetching item data for edit:', error)
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

// Setup form validation using the new hook
const {
  handleSubmit,
  errors,
  resetForm,
  isFormValid: isVeeValid,
  isSubmitting,
  values,
} = useValidatedForm(productSchema, initialFormData)

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

    let itemId: number

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
    <h1 id="create-product-title">{{ isEditMode ? $t('createProduct.editTitle') : $t('createProduct.title') }}</h1>

    <form @submit.prevent="onSubmit" class="product-form" aria-labelledby="create-product-title">
      <!-- Image Upload Section -->
      <section class="form-section">
        <h2 id="images-section">{{ $t('createProduct.sections.images') }}</h2>
        <div class="image-upload-container">
          <div
            class="image-upload-area"
            :class="{ dragging: isDragging }"
            @dragenter.prevent="isDragging = true"
            @dragleave.prevent="isDragging = false"
            @dragover.prevent
            @drop="handleDrop"
            role="region"
            aria-labelledby="images-section"
          >
            <input
              type="file"
              accept="image/*"
              multiple
              @change="handleImageUpload"
              class="file-input"
              id="image-upload"
              :aria-label="$t('createProduct.sections.images')"
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
                aria-hidden="true"
              >
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
                <polyline points="17 8 12 3 7 8" />
                <line x1="12" y1="3" x2="12" y2="15" />
              </svg>
              <p>{{ $t('createProduct.imageUpload.dragDrop') }}</p>
              <p class="upload-hint">{{ $t('createProduct.imageUpload.maxImages', { max: maxImages }) }}</p>
            </label>
          </div>

          <!-- Image Previews -->
          <div
            v-if="imagePreviews.length > 0"
            class="image-previews"
            role="list"
            aria-label="Uploaded product images"
          >
            <div
              v-for="(preview, index) in imagePreviews"
              :key="index"
              class="image-preview"
              role="listitem"
            >
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
        <span
          class="error-message"
          v-if="!isFormValid && !isEditMode && imageFiles.length === 0"
          role="alert"
          >{{ $t('createProduct.imageUpload.required') }}</span
        >
        <span class="info-message" v-if="isEditMode && imageFiles.length === 0"
          >{{ $t('createProduct.imageUpload.keepCurrent') }}</span
        >
      </section>

      <!-- Basic Information -->
      <section class="form-section">
        <h2 id="basic-info-section">{{ $t('createProduct.sections.basicInfo') }}</h2>

        <div class="form-group">
          <label for="title">{{ $t('createProduct.fields.title') }}</label>
          <input
            id="title"
            v-model="title"
            type="text"
            :class="{ error: titleError }"
            aria-required="true"
            :aria-invalid="!!titleError"
          />
          <span class="error-message" v-if="titleError" role="alert">{{ titleError }}</span>
        </div>

        <div class="form-group">
          <label for="shortDescription">{{ $t('createProduct.fields.shortDescription') }}</label>
          <input
            id="shortDescription"
            v-model="shortDescription"
            type="text"
            :class="{ error: shortDescriptionError }"
            aria-required="true"
            :aria-invalid="!!shortDescriptionError"
          />
          <span class="error-message" v-if="shortDescriptionError" role="alert">{{
            shortDescriptionError
          }}</span>
        </div>

        <div class="form-group">
          <label for="longDescription">{{ $t('createProduct.fields.longDescription') }}</label>
          <textarea
            id="longDescription"
            v-model="longDescription"
            rows="4"
            :class="{ error: longDescriptionError }"
            aria-required="true"
            :aria-invalid="!!longDescriptionError"
          ></textarea>
          <span class="error-message" v-if="longDescriptionError" role="alert">{{
            longDescriptionError
          }}</span>
        </div>

        <div class="form-group">
          <label for="price">{{ $t('createProduct.fields.price') }}</label>
          <input
            id="price"
            v-model="price"
            type="number"
            min="0"
            step="0.01"
            :class="{ error: priceError }"
            aria-required="true"
            :aria-invalid="!!priceError"
          />
          <span class="error-message" v-if="priceError" role="alert">{{ priceError }}</span>
        </div>
      </section>

      <!-- Product Details -->
      <section class="form-section">
        <h2 id="product-details-section">{{ $t('createProduct.sections.details') }}</h2>

        <div class="form-group">
          <label for="category">{{ $t('createProduct.fields.category') }}</label>
          <select
            id="category"
            v-model="categoryId"
            :class="{ error: categoryIdError }"
            aria-required="true"
            :aria-invalid="!!categoryIdError"
          >
            <option value="">{{ $t('createProduct.placeholders.selectCategory') }}</option>
            <option v-for="category in categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </option>
          </select>
          <span class="error-message" v-if="categoryIdError" role="alert">{{
            categoryIdError
          }}</span>
        </div>

        <div class="form-group">
          <label for="condition">{{ $t('createProduct.fields.condition') }}</label>
          <select
            id="condition"
            v-model="condition"
            :class="{ error: conditionError }"
            aria-required="true"
            :aria-invalid="!!conditionError"
          >
            <option value="">{{ $t('createProduct.placeholders.selectCondition') }}</option>
            <option v-for="c in conditions" :key="c" :value="c">
              {{ c }}
            </option>
          </select>
          <span class="error-message" v-if="conditionError" role="alert">{{ conditionError }}</span>
        </div>

        <div class="form-group">
          <label for="size">{{ $t('createProduct.fields.size') }}</label>
          <select
            id="size"
            v-model="size"
            :class="{ error: sizeError }"
            aria-required="true"
            :aria-invalid="!!sizeError"
          >
            <option value="">{{ $t('createProduct.placeholders.selectSize') }}</option>
            <option v-for="s in sizes" :key="s" :value="s">
              {{ s }}
            </option>
          </select>
          <span class="error-message" v-if="sizeError" role="alert">{{ sizeError }}</span>
        </div>

        <div class="form-group">
          <label for="brand">{{ $t('createProduct.fields.brand') }}</label>
          <input
            id="brand"
            v-model="brand"
            type="text"
            :class="{ error: brandError }"
            aria-required="true"
            :aria-invalid="!!brandError"
          />
          <span class="error-message" v-if="brandError" role="alert">{{ brandError }}</span>
        </div>

        <div class="form-group">
          <label for="color">{{ $t('createProduct.fields.color') }}</label>
          <input
            id="color"
            v-model="color"
            type="text"
            :class="{ error: colorError }"
            aria-required="true"
            :aria-invalid="!!colorError"
          />
          <span class="error-message" v-if="colorError" role="alert">{{ colorError }}</span>
        </div>
      </section>

      <!-- Location & Shipping -->
      <section class="form-section">
        <h2 id="location-shipping-section">{{ $t('createProduct.sections.locationShipping') }}</h2>

        <div class="form-group">
          <label for="location">{{ $t('createProduct.fields.location') }}</label>
          <select
            id="location"
            v-model="locationId"
            :class="{ error: locationIdError }"
            aria-required="true"
            :aria-invalid="!!locationIdError"
          >
            <option value="">{{ $t('createProduct.placeholders.selectLocation') }}</option>
            <option v-for="location in locations" :key="location.id" :value="location.id">
              {{ location.name }}
            </option>
          </select>
          <span class="error-message" v-if="locationIdError" role="alert">{{
            locationIdError
          }}</span>
        </div>

        <div class="form-group">
          <label for="shipping">{{ $t('createProduct.fields.shipping') }}</label>
          <select
            id="shipping"
            v-model="shippingOptionId"
            :class="{ error: shippingOptionIdError }"
            aria-required="true"
            :aria-invalid="!!shippingOptionIdError"
          >
            <option value="">{{ $t('createProduct.placeholders.selectShipping') }}</option>
            <option v-for="option in shippingOptions" :key="option.id" :value="option.id">
              {{ option.name }}
            </option>
          </select>
          <span class="error-message" v-if="shippingOptionIdError" role="alert">{{
            shippingOptionIdError
          }}</span>
        </div>

        <div class="form-group">
          <label class="checkbox-label" for="vipps-payment">
            <input type="checkbox" id="vipps-payment" v-model="isVippsPaymentEnabled" />
            {{ $t('createProduct.fields.vippsPayment') }}
          </label>
        </div>
      </section>

      <div class="form-actions">
        <button
          type="button"
          @click="router.back()"
          class="cancel-button"
          :aria-label="$t('createProduct.buttons.cancel')"
        >
          {{ $t('createProduct.buttons.cancel') }}
        </button>
        <button
          type="submit"
          class="submit-button"
          :disabled="!isFormValid || isSubmitting"
          :aria-busy="isSubmitting"
        >
          {{
            isSubmitting
              ? isEditMode
                ? $t('createProduct.buttons.updating')
                : $t('createProduct.buttons.creating')
              : isEditMode
                ? $t('createProduct.buttons.update')
                : $t('createProduct.buttons.create')
          }}
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

.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}
</style>
