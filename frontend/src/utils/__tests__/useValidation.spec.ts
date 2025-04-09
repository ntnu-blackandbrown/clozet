import { mount } from '@vue/test-utils'
import { defineComponent, nextTick } from 'vue'
import { describe, it, expect } from 'vitest'
import * as yup from 'yup'
import { useValidatedForm, useValidatedField } from '@/utils/validation/useValidation'

// Create a simple Yup schema for testing.
const schema = yup.object({
  name: yup.string().required('Name is required'),
  email: yup.string().email('Email must be valid').required('Email is required'),
})

// Initial values for the form.
const initialValues = {
  name: '',
  email: '',
}

describe('useValidatedForm', () => {
  // A dummy component that uses the validated form hook.
  const DummyForm = defineComponent({
    setup() {
      const form = useValidatedForm(schema, initialValues)
      return { form }
    },
    template: `<div></div>`,
  })



  it('resetForm resets form values to the initial values', async () => {
    const wrapper = mount(DummyForm)
    const form = wrapper.vm.form

    // Change the form values.
    form.values.name = 'Changed Name'
    form.values.email = 'changed@example.com'
    form.resetForm()
    expect(form.values.name).toBe('')
    expect(form.values.email).toBe('')
  })
})

describe('useValidatedField', () => {
  // A dummy component that uses the validated field hook.
  const DummyField = defineComponent({
    setup() {
      const field = useValidatedField('name')
      return { field }
    },
    template: `<div></div>`,
  })

  it('returns a reactive value and errorMessage for a field', () => {
    const wrapper = mount(DummyField)
    const { field } = wrapper.vm

    // By default, the field value may be undefined or an empty string depending on vee-validate defaults.
    // We simply check that the returned refs are defined.
    expect(field.value).toBeDefined()
    expect(field.errorMessage).toBeDefined()
  })
})
