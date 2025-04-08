import axios from '../axios'

export const FavoritesService = {
  /**
   * Get favorites for a specific user
   */
  getUserFavorites: (userId: number) => {
    return axios.get(`api/favorites/user/${userId}`)
  },

  /**
   * Add an item to favorites
   */
  addFavorite: (userId: number, itemId: number) => {
    return axios.post(`api/favorites`, { userId, itemId })
  },

  /**
   * Remove an item from favorites
   */
  removeFavorite: (favoriteId: number) => {
    return axios.delete(`api/favorites/${favoriteId}`)
  }
}

export default FavoritesService
