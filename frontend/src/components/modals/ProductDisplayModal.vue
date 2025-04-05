<script setup lang="ts">
import { ref } from 'vue'
import ProductDisplay from '@/components/product/ProductDisplay.vue'
import BaseModal from '@/components/modals/BaseModal.vue'
import { useRouter } from 'vue-router'

interface ProductDisplayModalProps {
  productId: number
}

const props = defineProps<ProductDisplayModalProps>()
const router = useRouter()

const emit = defineEmits(['close'])

const handleClose = () => {
  // Update the URL to remove the product ID
  router.replace('/')
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
