import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from '@/api/axios'

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

export const useCategoryStore = defineStore('categories', () => {
  const categories = ref<Category[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  const fetchCategories = async () => {
    try {
      loading.value = true
      const response = await axios.get('/api/categories')
      categories.value = response.data
    } catch (err) {
      error.value = 'Failed to fetch categories'
      console.error('Error fetching categories:', err)
    } finally {
      loading.value = false
    }
  }

  const getCategoryById = async (id: number) => {
    try {
      const response = await axios.get(`/api/categories/${id}`)
      return response.data
    } catch (err) {
      error.value = 'Failed to fetch category'
      console.error('Error fetching category:', err)
    } finally {
      loading.value = false
    }
  }

  const createCategory = async (category: Category) => {
    try {
      const response = await axios.post('/api/categories', category)
      return response.data
    } catch (err) {
      error.value = 'Failed to create category'
      console.error('Error creating category:', err)
    } finally {
      loading.value = false
    }
  }

  const getTopFiveCategories = async () => {
    try {
      const response = await axios.get('/api/categories/top-five')
      return response.data
    } catch (err) {
      error.value = 'Failed to fetch top five categories'
      console.error('Error fetching top five categories:', err)
    } finally {
      loading.value = false
    }
  }

  return {
    categories,
    loading,
    error,
    fetchCategories,
    getCategoryById,
    createCategory,
    getTopFiveCategories,
  }
})
