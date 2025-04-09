import { defineStore } from 'pinia'
import { ref, computed, watch } from 'vue' // Added watch
import { FavoritesService } from '@/api/services/FavoritesService'
import { useAuthStore } from './AuthStore'

interface Favorite {
  id: number
  userId: number
  itemId: number
  createdAt: string
}

export const useFavoritesStore = defineStore('favorites', () => {
  const favoritesMap = ref<Map<number, number>>(new Map()) // Map<itemId, favoriteId>
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const authStore = useAuthStore()

  const favoriteItemIds = computed(() => new Set(favoritesMap.value.keys()))

  function isFavorite(itemId: number): boolean {
    return favoritesMap.value.has(itemId)
  }

  async function fetchUserFavorites() {
    if (!authStore.user?.id) {
      console.log('User not logged in, cannot fetch favorites.')
      favoritesMap.value.clear() // Clear favorites if user logs out
      return
    }

    isLoading.value = true
    error.value = null
    console.log(`Fetching favorites for user ${authStore.user.id}`)
    try {
      const response = await FavoritesService.getUserFavorites(authStore.user.id)
      const favorites: Favorite[] = response.data || []
      const newFavoritesMap = new Map<number, number>()
      favorites.forEach((fav) => {
        newFavoritesMap.set(fav.itemId, fav.id)
      })
      favoritesMap.value = newFavoritesMap
      console.log('Favorites fetched:', favoritesMap.value)
    } catch (err: any) {
      console.error('Failed to fetch user favorites:', err)
      error.value = 'Failed to load favorites.'
    } finally {
      isLoading.value = false
    }
  }

  async function addFavorite(itemId: number) {
    if (!authStore.user?.id || isFavorite(itemId)) {
      return
    }

    isLoading.value = true
    error.value = null
    console.log(`Adding favorite for item ${itemId} by user ${authStore.user.id}`)
    try {
      const response = await FavoritesService.addFavorite(authStore.user.id, itemId)
      const newFavorite: Favorite = response.data
      if (newFavorite && newFavorite.id) {
        favoritesMap.value.set(itemId, newFavorite.id)
        console.log(`Favorite added: Item ${itemId}, Favorite ID ${newFavorite.id}`)
      } else {
        throw new Error('Invalid response data received from addFavorite API.')
      }
    } catch (err: any) {
      console.error('Failed to add favorite:', err)
      error.value = 'Failed to add favorite.'
      // Optionally revert state change if API call fails
      if (favoritesMap.value.has(itemId)) {
        favoritesMap.value.delete(itemId)
      }
    } finally {
      isLoading.value = false
    }
  }

  async function removeFavorite(itemId: number) {
    const favoriteId = favoritesMap.value.get(itemId)
    if (!authStore.user?.id || !favoriteId) {
      return
    }

    isLoading.value = true
    error.value = null
    console.log(`Removing favorite ${favoriteId} for item ${itemId}`)
    try {
      await FavoritesService.removeFavorite(favoriteId)
      favoritesMap.value.delete(itemId)
      console.log(`Favorite removed: Item ${itemId}`)
    } catch (err: any) {
      console.error('Failed to remove favorite:', err)
      error.value = 'Failed to remove favorite.'
      // Optionally revert state if API call fails, though deletion is less critical to revert
    } finally {
      isLoading.value = false
    }
  }

  // Watch for changes in user login status
  watch(
    () => authStore.user,
    (newUser) => {
      if (newUser) {
        fetchUserFavorites() // Fetch favorites when user logs in
      } else {
        // Clear favorites when user logs out
        favoritesMap.value.clear()
        console.log('User logged out, favorites cleared.')
      }
    },
    { immediate: true },
  ) // immediate: true to run on store initialization if user is already logged in

  return {
    favoritesMap,
    favoriteItemIds,
    isLoading,
    error,
    isFavorite,
    fetchUserFavorites,
    addFavorite,
    removeFavorite,
  }
})
