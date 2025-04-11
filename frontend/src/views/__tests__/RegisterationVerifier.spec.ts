import { shallowMount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import RegisterationVerifier from '@/views/verification/RegisterationVerifier.vue' // Adjust the path as needed

describe('RegistrationVerifier.vue', () => {
  it('renders a TokenVerifier component with the correct props', () => {
    const wrapper = shallowMount(RegisterationVerifier)
    // Since we're using shallowMount, TokenVerifier is automatically stubbed.
    const tokenVerifierStub = wrapper.findComponent({ name: 'TokenVerifier' })
    expect(tokenVerifierStub.exists()).toBe(true)
    expect(tokenVerifierStub.props('verifyEndpoint')).toBe('/api/auth/verify')
    expect(tokenVerifierStub.props('redirectPath')).toBe('/login')
    expect(tokenVerifierStub.props('includeTokenInRedirect')).toBe(false)
  })
})
