import { useField, useForm } from 'vee-validate'
import { computed, ref } from 'vue'
import type { Schema } from 'yup'

/**
 * Hook for easier form validation with vee-validate and yup
 * @param schema Yup validation schema
 * @param initialValues Optional initial values for the form
 * @returns Form validation utilities
 */
export function useValidatedForm<T extends Record<string, any>>(schema: Schema, initialValues = {} as T) {
  const isSubmitting = ref(false)
  const statusMessage = ref('')
  const statusType = ref('')

  // Initialize form with validation schema
  const { handleSubmit, errors, resetForm, values } = useForm<T>({
    validationSchema: schema,
    initialValues
  })

  // Computes if the form is valid by checking all errors and required fields
  const isFormValid = computed(() => {
    return Object.keys(errors.value).length === 0 &&
           Object.entries(values).every(([_, value]) => value !== undefined && value !== '')
  })

  // Set form status (for displaying status messages)
  const setStatus = (message: string, type: 'success' | 'error' | 'info' = 'info') => {
    statusMessage.value = message
    statusType.value = type
  }

  // Clear form status
  const clearStatus = () => {
    statusMessage.value = ''
    statusType.value = ''
  }

  return {
    handleSubmit,
    errors,
    resetForm,
    isFormValid,
    isSubmitting,
    statusMessage,
    statusType,
    setStatus,
    clearStatus,
    values
  }
}

/**
 * Get a validated field with error message
 * @param fieldName Name of the field to validate
 * @returns Validated field value and error message
 */
export function useValidatedField(fieldName: string) {
  const { value, errorMessage } = useField(fieldName)

  return {
    value,
    errorMessage,
  }
}
