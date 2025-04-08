import axios from '../axios'

export const CategoryService = {
  /**
   * Get all categories
   */
  getAllCategories: () => {
    return axios.get('/api/categories')
  },

  /**
   * Get a specific category by ID
   */
  getCategoryById: (categoryId: number) => {
    return axios.get(`/api/categories/${categoryId}`)
  },

  /**
   * Get top five popular categories
   */
  getTopCategories: () => {
    return axios.get('/api/categories/top-five')
  },

  /**
   * Create a new category (admin)
   */
  createCategory: (categoryData: any) => {
    return axios.post('/api/categories', categoryData)
  },

  /**
   * Update a category (admin)
   */
  updateCategory: (categoryId: number, categoryData: any) => {
    return axios.put(`/api/categories/${categoryId}`, categoryData)
  },

  /**
   * Delete a category (admin)
   */
  deleteCategory: (categoryId: number) => {
    return axios.delete(`/api/categories/${categoryId}`)
  }
}

export default CategoryService
