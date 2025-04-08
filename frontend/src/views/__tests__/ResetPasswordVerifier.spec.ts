import { shallowMount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import ResetPasswordVerifier from '@/views/verification/ResetPasswordVerifier.vue' // Adjust path if needed

describe('ResetPasswordVerifier.vue', () => {
  it('renders a TokenVerifier component with the correct props', () => {
    const wrapper = shallowMount(ResetPasswordVerifier)
    // Find the stubbed TokenVerifier component.
    const tokenVerifierStub = wrapper.findComponent({ name: 'TokenVerifier' })
    expect(tokenVerifierStub.exists()).toBe(true)
    // Verify the props.
    expect(tokenVerifierStub.props('verifyEndpoint')).toBe('/api/auth/reset-password/validate')
    expect(tokenVerifierStub.props('redirectPath')).toBe('/forgot-password')
    expect(tokenVerifierStub.props('includeTokenInRedirect')).toBe(false)
  })
})
