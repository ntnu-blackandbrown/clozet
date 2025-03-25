// tests/unit/LoginRegisterModal.spec.ts
import { mount } from '@vue/test-utils'
import LoginRegisterModal from '@/components/LoginRegisterModal.vue'
import { describe, it, expect, vi, beforeEach } from 'vitest'

// Mock console.log to prevent noise in test output
vi.spyOn(console, 'log').mockImplementation(() => {})
vi.spyOn(console, 'error').mockImplementation(() => {})

describe('LoginRegisterModal.vue', () => {
  let wrapper
  let toggleBtn

  beforeEach(() => {
    wrapper = mount(LoginRegisterModal)
    toggleBtn = wrapper.find('button[data-testid="toggle-form-btn"]')
  })

  it('renders login form by default', () => {
    expect(wrapper.find('h2').text()).toBe('Login')
    expect(wrapper.find('input[placeholder="Email or Username"]').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="Password"]').exists()).toBe(true)
    expect(wrapper.find('button').text()).toBe('Login')
    expect(toggleBtn.text()).toBe('Need an account? Register')
  })

  it('toggles to register form', async () => {
    await toggleBtn.trigger('click')
    expect(wrapper.find('h2').text()).toBe('Register')
    expect(wrapper.find('input[placeholder="Username"]').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="First Name"]').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="Last Name"]').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="Email"]').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="Password"]').exists()).toBe(true)
    expect(wrapper.find('input[placeholder="Confirm Password"]').exists()).toBe(true)
    expect(wrapper.find('button').text()).toBe('Register')
    expect(toggleBtn.text()).toBe('Already have an account? Login')
  })

  it('emits close on backdrop click', async () => {
    const backdrop = wrapper.find('.backdrop')
    await backdrop.trigger('click')
    expect(wrapper.emitted('close')).toBeTruthy()
    expect(wrapper.emitted()).toHaveProperty('close')
  })

  it('check if the login button is disabled when the form is invalid', async () => {
    const submitBtn = wrapper.find('button')
    expect(submitBtn.attributes('disabled')).toBeDefined()

    // Fill in required fields
    await wrapper.find('input[placeholder="Email or Username"]').setValue('test@test.com')
    await wrapper.find('input[placeholder="Password"]').setValue('Password123')

    // Button should be enabled when form is valid
    expect(submitBtn.attributes('disabled')).toBeUndefined()
  })
})
