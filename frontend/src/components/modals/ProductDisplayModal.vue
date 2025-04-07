<script setup lang="ts">
import { ref } from 'vue'
import ProductDisplay from '@/components/product/ProductDisplay.vue'
import BaseModal from '@/components/modals/BaseModal.vue'
import { useRouter, useRoute } from 'vue-router'

interface ProductDisplayModalProps {
  productId: number
}

const props = defineProps<ProductDisplayModalProps>()
const router = useRouter()
const route = useRoute()

const emit = defineEmits(['close'])

const handleClose = () => {
  // Get the base path from the current route
  const currentPath = route.path
  const basePath = currentPath.substring(0, currentPath.lastIndexOf('/') + 1)

  // If we're on a profile page, go back to that profile page
  if (currentPath.includes('/profile/')) {
    router.replace(basePath.substring(0, basePath.length - 1)) // Remove trailing slash
  } else {
    // Default behavior - go to home
    router.replace('/')
  }

  emit('close')
}
</script>

<template>
  <BaseModal @close="handleClose">
    <ProductDisplay :id="props.productId" />
  </BaseModal>
</template>

<style scoped>
/* All styles are now in BaseModal component */
</style>
