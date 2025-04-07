import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

interface Location {
  id: number
  name: string
}

export const useLocationStore = defineStore('locations', () => {
  const locations = ref<Location[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchLocations = async () => {
    try {
      loading.value = true
      const response = await axios.get('/api/locations')
      locations.value = response.data
    } catch (err) {
      error.value = 'Failed to fetch locations'
        console.error('Error fetching locations:', err)
    } finally {
      loading.value = false
    }
  }

  return { locations, loading, error, fetchLocations }
})