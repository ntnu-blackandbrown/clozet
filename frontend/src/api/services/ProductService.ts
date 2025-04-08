import axios from '../axios'

export const ProductService = {
  /**
   * Get all marketplace items
   */
  getAllItems: () => {
    return axios.get('api/marketplace/items')
  },

  /**
   * Get a specific item by ID
   */
  getItemById: (itemId: number) => {
    return axios.get(`api/items/${itemId}`)
  },

  /**
   * Get items by seller ID
   */
  getItemsBySeller: (sellerId: number) => {
    return axios.get(`api/items/seller/${sellerId}`)
  },

  /**
   * Create a new item
   */
  createItem: (itemData: any) => {
    return axios.post('/api/items', itemData)
  },

  /**
   * Update an existing item
   */
  updateItem: (itemId: number, itemData: any) => {
    return axios.put(`/api/items/${itemId}`, itemData)
  },

  /**
   * Delete an item
   */
  deleteItem: (itemId: number) => {
    return axios.delete(`/api/items/${itemId}`)
  },

  /**
   * Upload images for an item
   */
  uploadImages: (formData: FormData) => {
    return axios.post('/api/images/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },

  /**
   * Get images for an item
   */
  getItemImages: (itemId: number) => {
    return axios.get(`api/images/item/${itemId}`)
  },
}

export default ProductService
