/*
    private Long id;
    private String name;
    private String description;
    private int estimatedDays;
    private double price;
    private boolean isTracked;
*/

import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from 'axios'

interface ShippingOption {
  id: number
  name: string
  description: string
  estimatedDays: number
  price: number
  isTracked: boolean
}

export const useShippingOptionStore = defineStore('shippingOption', () => {
  const shippingOptions = ref<ShippingOption[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchShippingOptions = async () => {
    try {
      loading.value = true
      const response = await axios.get('/api/shipping-options')
      shippingOptions.value = response.data
    } catch (err) {
      error.value = 'Failed to fetch shipping options'
      console.error('Error fetching shipping options:', err)
    } finally {
      loading.value = false
    }
  }

  return {
    shippingOptions,
    loading,
    error,
    fetchShippingOptions,
  }
})
