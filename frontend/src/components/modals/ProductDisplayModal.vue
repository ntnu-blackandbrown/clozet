<script setup>
import { ref } from 'vue'
import ProductDisplay from '../ProductDisplay.vue'
import BaseModal from './BaseModal.vue'

// Use ref for the productId instead of defineProps
const productId = ref('')

// Expose the method to set the productId when the component is used
const setProductId = (id) => {
  productId.value = id
}

// Emit events for parent components
const emit = defineEmits(['close'])

// Expose methods to parent component
defineExpose({
  setProductId,
})
</script>

<template>
  <BaseModal @close="emit('close')">
    <ProductDisplay :productId="productId" />
  </BaseModal>
</template>

<style scoped>
/* All styles are now in BaseModal component */
</style>
