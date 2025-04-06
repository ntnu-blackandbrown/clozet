<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from '@/api/axios'
import { useAuthStore } from '@/stores/AuthStore'

const authStore = useAuthStore()

interface WishlistButtonProps {
  productId?: number
  isWishlisted?: boolean
}

const props = withDefaults(defineProps<WishlistButtonProps>(), {
  productId: 0,
  isWishlisted: false,
})

const isWishlisted = ref(props.isWishlisted)
const favoriteId = ref<number | null>(null)

const fetchFavoriteId = async () => {
  try {
    const response = await axios.get(`api/favorites/user/${authStore.user?.id}`)
    const favorites = response.data
    const favorite = favorites.find((f: any) => f.itemId === props.productId)
    if (favorite) {
      favoriteId.value = favorite.id
    }
  } catch (error) {
    console.error('Failed to fetch favorite ID:', error)
  }
}

onMounted(async () => {
  if (props.isWishlisted) {
    await fetchFavoriteId()
  }
})

const toggleWishlist = async () => {
  isWishlisted.value = !isWishlisted.value
  // TODO: Implement actual wishlist functionality with backend
  console.log('current state of isWishlisted: ', isWishlisted.value)
  if(isWishlisted.value){
    const response = await axios.post(`api/favorites`, {
      userId: authStore.user?.id,
      itemId: props.productId,
    })
    console.log('response: ', response)
    favoriteId.value = response.data.id // Store the favorite ID from the response
  } else {
    if (favoriteId.value) {
      const response = await axios.delete(`api/favorites/${favoriteId.value}`)
      console.log('response: ', response)
      favoriteId.value = null
    }
  }
}
</script>

<template>
  <button
    class="wishlist-button"
    :class="{ wishlisted: isWishlisted }"
    @click="toggleWishlist"
    aria-label="Add to wishlist"
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

.wishlist-button:hover {
  transform: scale(1.1);
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
