import { describe, it, expect, beforeEach, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import App from '../App.vue'
import LoginRegisterModal from '../components/LoginRegisterModal.vue'

// Mock the LoginRegisterModal component
vi.mock('../components/LoginRegisterModal.vue', () => ({
  default: {
    name: 'LoginRegisterModal',
    template: '<div class="mock-modal"></div>',
  },
}))

describe('App.vue', () => {
  let wrapper

  beforeEach(() => {
    wrapper = mount(App)
  })

  it('renders welcome message', () => {
    expect(wrapper.text()).toContain('Welcome to Clozet!')
  })

  it('shows login/register button', () => {
    expect(wrapper.find('button').text()).toBe('Login / Register')
  })

  it('toggles modal visibility when button is clicked', async () => {
    expect(wrapper.findComponent(LoginRegisterModal).exists()).toBe(false)

    await wrapper.find('button').trigger('click')
    expect(wrapper.findComponent(LoginRegisterModal).exists()).toBe(true)
  })

  it('applies blur class when modal is shown', async () => {
    expect(wrapper.find('.blurred').exists()).toBe(false)

    await wrapper.find('button').trigger('click')
    expect(wrapper.find('.blurred').exists()).toBe(true)
  })

  it('removes blur when modal is closed', async () => {
    await wrapper.find('button').trigger('click')

    await wrapper.findComponent(LoginRegisterModal).vm.$emit('close')
    expect(wrapper.find('.blurred').exists()).toBe(false)
  })
})
