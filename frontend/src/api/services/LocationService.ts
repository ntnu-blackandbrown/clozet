import axios from '../axios'

export const LocationService = {
  /**
   * Get all locations
   */
  getAllLocations: () => {
    return axios.get('/api/locations')
  },

  /**
   * Get a location by ID
   */
  getLocationById: (locationId: number) => {
    return axios.get(`/api/locations/${locationId}`)
  },

  /**
   * Create a new location (admin)
   */
  createLocation: (locationData: any) => {
    return axios.post('/api/locations', locationData)
  },

  /**
   * Update a location (admin)
   */
  updateLocation: (locationId: number, locationData: any) => {
    return axios.put(`/api/locations/${locationId}`, locationData)
  },

  /**
   * Delete a location (admin)
   */
  deleteLocation: (locationId: number) => {
    return axios.delete(`/api/locations/${locationId}`)
  },
}

export default LocationService
