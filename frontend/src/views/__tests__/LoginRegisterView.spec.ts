import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import LoginRegisterView from '../LoginRegisterView.vue'

// Form data interface
interface FormData {
  username: string
  password: string
  firstName?: string
  lastName?: string
  email?: string
  confirmPassword?: string
}

// Mock the external dependencies
vi.mock('@/components/modals/BaseModal.vue', () => ({
  default: {
    name: 'BaseModal',
    render(this: { $slots: { default?: () => any } }) {
      return this.$slots.default ? this.$slots.default() : null
    },
    props: ['maxWidth', 'padding', 'hideCloseButton'],
    emits: ['close'],
  },
}))

vi.mock('@/stores/AuthStore', () => ({
  useAuthStore: () => ({
    login: vi.fn().mockResolvedValue({ success: true }),
    register: vi.fn().mockResolvedValue({ success: true, data: { username: 'testuser' } }),
  }),
}))

// Mock vee-validate
vi.mock('vee-validate', () => ({
  useForm: () => ({
    handleSubmit: (cb: (values: any) => Promise<void>) => () => Promise.resolve(),
    errors: { value: {} },
    resetForm: vi.fn(),
  }),
  useField: () => ({
    value: '',
    errorMessage: { value: '' },
  }),
}))

describe('LoginRegisterView', () => {
  it('renders properly in login mode', () => {
    const wrapper = mount(LoginRegisterView)
    expect(wrapper.find('h2').text()).toBe('Login')
    expect(wrapper.find('#toggle-form-btn').text()).toBe('Need an account? Register')
  })

  it('toggles form mode when button is clicked', async () => {
    const wrapper = mount(LoginRegisterView)

    // Initial state is login
    expect(wrapper.find('h2').text()).toBe('Login')

    // Click toggle button
    await wrapper.find('#toggle-form-btn').trigger('click')

    // Should switch to register mode
    expect(wrapper.find('h2').text()).toBe('Register')
    expect(wrapper.find('#toggle-form-btn').text()).toBe('Already have an account? Login')

    // Click again to toggle back
    await wrapper.find('#toggle-form-btn').trigger('click')

    // Should switch back to login mode
    expect(wrapper.find('h2').text()).toBe('Login')
  })

  it('emits a close event when BaseModal emits close', async () => {
    const wrapper = mount(LoginRegisterView)
    await wrapper.findComponent({ name: 'BaseModal' }).vm.$emit('close')
    expect(wrapper.emitted('close')).toBeTruthy()
  })
})
