<script setup>
import { ref, onMounted } from 'vue'
import { LocationService } from '@/api/services/LocationService'

// State
const locations = ref([])
const isLoading = ref(true)
const error = ref(null)

// Form for adding/editing locations
const locationForm = ref({
  id: null,
  city: '',
  region: '',
  latitude: 0,
  longitude: 0,
})

const formMode = ref('add') // 'add' or 'edit'
const showForm = ref(false)
const formErrors = ref({})

// Fetch all locations
const fetchLocations = async () => {
  try {
    isLoading.value = true
    error.value = null
    const response = await LocationService.getAllLocations()
    locations.value = response.data
    isLoading.value = false
  } catch (err) {
    console.error('Error fetching locations:', err)
    error.value = 'Failed to load locations'
    isLoading.value = false
  }
}

// Create a new location
const createLocation = async () => {
  if (!validateForm()) return

  try {
    isLoading.value = true
    await LocationService.createLocation(locationForm.value)
    await fetchLocations()
    isLoading.value = false
    resetForm()
    showForm.value = false
  } catch (err) {
    console.error('Error creating location:', err)
    error.value = 'Failed to create location'
    isLoading.value = false
  }
}

// Update an existing location
const updateLocation = async () => {
  if (!validateForm()) return

  try {
    isLoading.value = true
    await LocationService.updateLocation(locationForm.value.id, locationForm.value)
    await fetchLocations()
    isLoading.value = false
    resetForm()
    showForm.value = false
  } catch (err) {
    console.error('Error updating location:', err)
    error.value = 'Failed to update location'
    isLoading.value = false
  }
}

// Delete a location
const deleteLocation = async (id) => {
  if (!confirm('Are you sure you want to delete this location?')) return

  try {
    isLoading.value = true
    await LocationService.deleteLocation(id)
    await fetchLocations()
    isLoading.value = false
  } catch (err) {
    console.error('Error deleting location:', err)
    error.value = 'Failed to delete location'
    isLoading.value = false
  }
}

// Form submission handler
const handleSubmit = () => {
  if (formMode.value === 'add') {
    createLocation()
  } else {
    updateLocation()
  }
}

// Open form to add a new location
const addLocation = () => {
  formMode.value = 'add'
  resetForm()
  showForm.value = true
}

// Open form to edit an existing location
const editLocation = (location) => {
  formMode.value = 'edit'
  locationForm.value = {
    id: location.id,
    city: location.city,
    region: location.region,
    latitude: location.latitude,
    longitude: location.longitude,
  }
  showForm.value = true
}

// Reset form fields
const resetForm = () => {
  locationForm.value = {
    id: null,
    city: '',
    region: '',
    latitude: 0,
    longitude: 0,
  }
  formErrors.value = {}
}

// Validate form inputs
const validateForm = () => {
  formErrors.value = {}

  if (!locationForm.value.city.trim()) {
    formErrors.value.city = 'City name is required'
  }

  if (!locationForm.value.region.trim()) {
    formErrors.value.region = 'Region is required'
  }

  if (locationForm.value.latitude < -90 || locationForm.value.latitude > 90) {
    formErrors.value.latitude = 'Latitude must be between -90 and 90'
  }

  if (locationForm.value.longitude < -180 || locationForm.value.longitude > 180) {
    formErrors.value.longitude = 'Longitude must be between -180 and 180'
  }

  return Object.keys(formErrors.value).length === 0
}

// Load locations on component mount
onMounted(() => {
  fetchLocations()
})
</script>

<template>
  <div class="location-management">
    <div class="page-header">
      <h1 id="location-management-title">Location Management</h1>
      <button @click="addLocation" class="btn-primary" aria-label="Add new location">Add New Location</button>
    </div>

    <div v-if="error" class="error-message" role="alert">
      {{ error }}
      <button @click="fetchLocations" class="btn-secondary" aria-label="Retry loading locations">Retry</button>
    </div>

    <!-- Location Table -->
    <div class="table-container">
      <div style="overflow-x: auto;" v-if="!isLoading && locations.length > 0">
        <table class="admin-table" aria-labelledby="location-management-title">
          <thead>
            <tr>
              <th scope="col">ID</th>
              <th scope="col">City</th>
              <th scope="col">Region</th>
              <th scope="col">Coordinates</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="location in locations" :key="location.id">
              <td>{{ location.id }}</td>
              <td>{{ location.city }}</td>
              <td>{{ location.region }}</td>
              <td>{{ location.latitude.toFixed(4) }}, {{ location.longitude.toFixed(4) }}</td>
              <td class="actions">
                <button @click="editLocation(location)" class="btn-icon edit" aria-label="Edit location: {{ location.city }}">✎</button>
                <button @click="deleteLocation(location.id)" class="btn-icon delete" aria-label="Delete location: {{ location.city }}">🗑</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-else-if="isLoading" class="loading-container" role="status" aria-live="polite">
        <div class="loading-spinner" aria-hidden="true"></div>
        <p>Loading locations...</p>
      </div>

      <div v-else class="empty-state">
        <p>No locations found</p>
        <button @click="addLocation" class="btn-primary" aria-label="Add first location">Add Your First Location</button>
      </div>
    </div>

    <!-- Location Form Modal -->
    <div v-if="showForm" class="modal-backdrop" @click="showForm = false" aria-modal="true" role="dialog" aria-labelledby="location-form-title">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 id="location-form-title">{{ formMode === 'add' ? 'Add New Location' : 'Edit Location' }}</h3>
          <button @click="showForm = false" class="btn-close" aria-label="Close form">×</button>
        </div>

        <form @submit.prevent="handleSubmit" class="location-form">
          <div class="form-group">
            <label for="city">City</label>
            <input
              type="text"
              id="city"
              v-model="locationForm.city"
              :class="{ 'input-error': formErrors.city }"
              aria-required="true"
              :aria-invalid="formErrors.city ? 'true' : 'false'"
              :aria-describedby="formErrors.city ? 'city-error' : undefined"
            />
            <span v-if="formErrors.city" class="error-text" id="city-error" role="alert">{{ formErrors.city }}</span>
          </div>

          <div class="form-group">
            <label for="region">Region</label>
            <input
              type="text"
              id="region"
              v-model="locationForm.region"
              :class="{ 'input-error': formErrors.region }"
              aria-required="true"
              :aria-invalid="formErrors.region ? 'true' : 'false'"
              :aria-describedby="formErrors.region ? 'region-error' : undefined"
            />
            <span v-if="formErrors.region" class="error-text" id="region-error" role="alert">{{ formErrors.region }}</span>
          </div>

          <div class="form-row">
            <div class="form-group half">
              <label for="latitude">Latitude</label>
              <input
                type="number"
                id="latitude"
                v-model.number="locationForm.latitude"
                step="0.0001"
                :class="{ 'input-error': formErrors.latitude }"
                aria-required="true"
                :aria-invalid="formErrors.latitude ? 'true' : 'false'"
                :aria-describedby="formErrors.latitude ? 'latitude-error' : undefined"
              />
              <span v-if="formErrors.latitude" class="error-text" id="latitude-error" role="alert">{{ formErrors.latitude }}</span>
            </div>

            <div class="form-group half">
              <label for="longitude">Longitude</label>
              <input
                type="number"
                id="longitude"
                v-model.number="locationForm.longitude"
                step="0.0001"
                :class="{ 'input-error': formErrors.longitude }"
                aria-required="true"
                :aria-invalid="formErrors.longitude ? 'true' : 'false'"
                :aria-describedby="formErrors.longitude ? 'longitude-error' : undefined"
              />
              <span v-if="formErrors.longitude" class="error-text" id="longitude-error" role="alert">{{ formErrors.longitude }}</span>
            </div>
          </div>

          <div class="form-actions">
            <button type="button" @click="showForm = false" class="btn-secondary" aria-label="Cancel">Cancel</button>
            <button type="submit" class="btn-primary" aria-label="Submit location form">
              {{ formMode === 'add' ? 'Add Location' : 'Update Location' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.location-management {
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

.location-form {
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

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #d1d5db;
  border-radius: 0.25rem;
  font-size: 1rem;
}

.form-group input:focus {
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
