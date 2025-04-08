<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/AuthStore'
import { useFavoritesStore } from '@/stores/FavoritesStore'

const authStore = useAuthStore()
const favoritesStore = useFavoritesStore()

interface WishlistButtonProps {
  productId: number
  isAvailable?: boolean
}

const props = withDefaults(defineProps<WishlistButtonProps>(), {
  isAvailable: true,
})

const isWishlisted = computed(() => favoritesStore.isFavorite(props.productId))

const toggleWishlist = async () => {
  if (!authStore.isLoggedIn) {
    console.log('User not logged in, cannot toggle wishlist.')
    return
  }

  if (isWishlisted.value) {
    console.log(`Attempting to remove favorite for product ID: ${props.productId}`)
    await favoritesStore.removeFavorite(props.productId)
  } else {
    console.log(`Attempting to add favorite for product ID: ${props.productId}`)
    await favoritesStore.addFavorite(props.productId)
  }
}
</script>

<template>
  <button
    class="wishlist-button"
    :class="{
      wishlisted: isWishlisted,
      disabled: !authStore.isLoggedIn || !props.isAvailable,
    }"
    @click="toggleWishlist"
    :disabled="!authStore.isLoggedIn || !props.isAvailable"
    :aria-label="props.isAvailable ? 'Toggle wishlist' : 'Item is sold'"
  >
    <svg
      xmlns="http://www.w3.org/2000/svg"
      viewBox="0 0 24 24"
      fill="currentColor"
      class="wishlist-icon"
    >
      <path
        d="M11.645 20.91l-.007-.003-.022-.012a15.247 15.247 0 01-.383-.218 25.18 25.18 0 01-4.244-3.17C4.688 15.36 2.25 12.174 2.25 8.25 2.25 5.322 4.714 3 7.688 3A5.5 5.5 0 0112 5.052 5.5 5.5 0 0116.313 3c2.973 0 5.437 2.322 5.437 5.25 0 3.925-2.438 7.111-4.739 9.256a25.175 25.175 0 01-4.244 3.17 15.247 15.247 0 01-.383.219l-.022.012-.007.004-.003.001a.752.752 0 01-.704 0l-.003-.001z"
      />
    </svg>
  </button>
</template>

<style scoped>
.wishlist-button {
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  transition: transform 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.wishlist-button:not(.disabled):hover {
  transform: scale(1.1);
}

.wishlist-button.disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.wishlist-icon {
  width: 24px;
  height: 24px;
  color: #666;
  transition: color 0.2s ease;
}

.wishlist-button.wishlisted .wishlist-icon {
  color: #ff4b4b;
}
</style>
