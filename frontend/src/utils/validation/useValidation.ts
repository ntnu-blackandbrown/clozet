import { useField, useForm } from 'vee-validate'
import { computed, ref } from 'vue'
import type { Schema } from 'yup'

/**
 * Hook for easier form validation with vee-validate and yup
 * @param schema Yup validation schema
 * @param initialValues Optional initial values for the form
 * @returns Form validation utilities
 */
export function useValidatedForm<T extends Record<string, any>>(schema: Schema, initialValues: T) {
  const isSubmitting = ref(false)
  const statusMessage = ref('')
  const statusType = ref('')

  // Initialize form with validation schema and handle the type casting
  const { handleSubmit, errors, resetForm, values } = useForm({
    validationSchema: schema,
    initialValues: initialValues as any,
  })

  // Computes if the form is valid by checking all errors and required fields
  const isFormValid = computed(() => {
    if (Object.keys(errors.value).length > 0) {
      return false
    }

    // Check if all form values are filled
    for (const key in values) {
      const value = values[key as keyof typeof values]
      if (value === undefined || value === '') {
        return false
      }
    }

    return true
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
    // Cast the handleSubmit to accept our type T as an argument
    handleSubmit: handleSubmit as (handler: (values: T) => any) => any,
    errors,
    resetForm,
    isFormValid,
    isSubmitting,
    statusMessage,
    statusType,
    setStatus,
    clearStatus,
    values: values as T,
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
