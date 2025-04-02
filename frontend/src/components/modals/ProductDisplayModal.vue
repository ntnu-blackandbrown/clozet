<script setup lang="ts">
import { ref } from 'vue'
import ProductDisplay from '@/components/product/ProductDisplay.vue'
import BaseModal from '@/components/modals/BaseModal.vue'

interface ProductDisplayModalProps {
  productId: number
}

const props = defineProps<ProductDisplayModalProps>()

const emit = defineEmits(['close'])
</script>

<template>
  <BaseModal @close="emit('close')">
    <ProductDisplay :id="props.productId" />
  </BaseModal>
</template>

<style scoped>
/* All styles are now in BaseModal component */
</style>
