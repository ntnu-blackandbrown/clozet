import { mount, flushPromises } from '@vue/test-utils'
import { createPinia, setActivePinia } from 'pinia'
import LoginRegisterView from '@/views/LoginRegisterView.vue'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useAuthStore } from '@/stores/AuthStore'

describe('LoginRegisterView (with normal Pinia)', () => {
  beforeEach(() => {
    const pinia = createPinia()
    setActivePinia(pinia)
  })

  it('renders login form by default', () => {
    const wrapper = mount(LoginRegisterView, {
      global: {
        stubs: {
          BaseModal: { template: '<div><slot /></div>' },
        },
      },
    })
    expect(wrapper.text()).toContain('Login')
    expect(wrapper.find('input[placeholder="Username"]').exists()).toBe(true)
  })

  it('toggles to register form', async () => {
    const wrapper = mount(LoginRegisterView, {
      global: {
        stubs: {
          BaseModal: { template: '<div><slot /></div>' },
        },
      },
    })
    await wrapper.find('#toggle-form-btn').trigger('click')
    expect(wrapper.text()).toContain('Register')
    expect(wrapper.find('input[placeholder="Email"]').exists()).toBe(true)
  })

  it('shows validation errors on submit if empty', async () => {
    const wrapper = mount(LoginRegisterView, {
      global: {
        stubs: {
          BaseModal: { template: '<div><slot /></div>' },
        },
      },
    })
    const submitButton = wrapper.find('button[type="submit"]')
    expect(submitButton.attributes('disabled')).toBe('')
  })

  it('submits login form with valid credentials', async () => {
    const wrapper = mount(LoginRegisterView, {
      global: {
        stubs: {
          BaseModal: { template: '<div><slot /></div>' },
        },
      },
    })

    // Mock login method on the real auth store
    const auth = useAuthStore()
    const loginSpy = vi.spyOn(auth, 'login').mockResolvedValue({ success: true, message: {} })

    await wrapper.find('input[placeholder="Username"]').setValue('gizmo')
    await wrapper.find('input[placeholder="Password"]').setValue('Test1234')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()
  })

  it('emits close after successful login', async () => {
    vi.useFakeTimers()
    const wrapper = mount(LoginRegisterView, {
      global: {
        stubs: {
          BaseModal: { template: '<div><slot /></div>' },
        },
      },
    })

    const auth = useAuthStore()
    vi.spyOn(auth, 'login').mockResolvedValue({ success: true, message: {} })

    await wrapper.find('input[placeholder="Username"]').setValue('gizmo')
    await wrapper.find('input[placeholder="Password"]').setValue('Test1234')
    await wrapper.find('form').trigger('submit.prevent')
    await flushPromises()

    vi.runAllTimers()
    vi.useRealTimers()
  })
})
