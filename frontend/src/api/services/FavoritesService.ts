import axiosInstance from '../axios'

export const FavoritesService = {
  /**
   * Get favorites for a specific user
   */
  getUserFavorites: (userId: number) => {
    console.log('📡 API: Fetching favorites for user', userId)
    return axiosInstance.get(`/api/favorites/user/${userId}`)
  },

  /**
   * Add an item to favorites
   */
  addFavorite: (userId: number, itemId: number) => {
    console.log('📡 API: Adding item to favorites', { userId, itemId })
    return axiosInstance.post(`/api/favorites`, { userId, itemId })
  },

  /**
   * Remove an item from favorites
   */
  removeFavorite: (favoriteId: number) => {
    console.log('📡 API: Removing item from favorites', favoriteId)
    return axiosInstance.delete(`/api/favorites/${favoriteId}`)
  },
}

export default FavoritesService
