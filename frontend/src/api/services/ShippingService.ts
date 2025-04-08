import axios from '../axios'

export const ShippingService = {
  /**
   * Get all shipping options
   */
  getAllShippingOptions: () => {
    return axios.get('/api/shipping-options')
  },

  /**
   * Get a shipping option by ID
   */
  getShippingOptionById: (shippingId: number) => {
    return axios.get(`/api/shipping-options/${shippingId}`)
  },

  /**
   * Create a new shipping option (admin)
   */
  createShippingOption: (shippingData: any) => {
    return axios.post('/api/shipping-options', shippingData)
  },

  /**
   * Update a shipping option (admin)
   */
  updateShippingOption: (shippingId: number, shippingData: any) => {
    return axios.put(`/api/shipping-options/${shippingId}`, shippingData)
  },

  /**
   * Delete a shipping option (admin)
   */
  deleteShippingOption: (shippingId: number) => {
    return axios.delete(`/api/shipping-options/${shippingId}`)
  },
}

export default ShippingService
