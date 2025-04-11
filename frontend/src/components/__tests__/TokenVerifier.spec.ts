import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import { vi, describe, it, expect, beforeEach } from 'vitest'
import TokenVerifier from '@/components/TokenVerifier.vue'
import { AuthService } from '@/api/services/AuthService'

// We'll override vue-router's useRoute and useRouter
let mockRoute: any
let mockRouterPush: any

vi.mock('vue-router', () => ({
  useRoute: () => mockRoute,
  useRouter: () => ({
    push: mockRouterPush,
  }),
}))

// Mock AuthService.verifyToken
vi.mock('@/api/services/AuthService', () => ({
  AuthService: {
    verifyToken: vi.fn(),
  },
}))

describe('TokenVerifier.vue', () => {
  beforeEach(() => {
    // Reset our mocks and set default values before each test
    mockRoute = { query: {} }
    mockRouterPush = vi.fn()
    vi.clearAllMocks()
  })

  it('displays error if no token is provided in the URL', async () => {
    // Arrange: route has no token
    mockRoute.query = {} // no token

    const wrapper = mount(TokenVerifier, {
      props: {
        verifyEndpoint: '/verify',
        redirectPath: '/home',
        includeTokenInRedirect: false,
      },
    })

    // Wait for onMounted hook to run
    await nextTick()

    // Look for specific error message text in the component
    const errorDiv = wrapper.find('.error')
    expect(errorDiv.exists()).toBe(true)
    expect(wrapper.find('.error p').text()).toBe('Token is missing in URL.')

    // Loading message should not be present after onMounted completes
    expect(wrapper.text()).not.toContain('Validerer token...')
  })

  it('verifies token and redirects without token in query when includeTokenInRedirect is false', async () => {
    // Arrange: set a valid token on the route query
    mockRoute.query = { token: 'valid-token' }
    // Simulate a successful verification
    ;(AuthService.verifyToken as any).mockResolvedValue({})

    const wrapper = mount(TokenVerifier, {
      props: {
        verifyEndpoint: '/verify',
        redirectPath: '/home',
        includeTokenInRedirect: false,
      },
    })

    // Wait for the onMounted hook to finish
    await nextTick()
    await nextTick() // additional tick to allow promises to resolve

    // Assert that AuthService.verifyToken was called with the correct parameters
    expect(AuthService.verifyToken).toHaveBeenCalledWith('valid-token')
    // Assert that router.push was called with the given redirectPath (without the token)
    expect(mockRouterPush).toHaveBeenCalledWith('/home')
    // The error message should not be visible
    const errorDiv = wrapper.find('.error')
    expect(errorDiv.exists()).toBe(false)
  })

  it('verifies token and redirects with token in query when includeTokenInRedirect is true', async () => {
    // Arrange: set a valid token in the query
    mockRoute.query = { token: 'valid-token' }
    // Simulate successful verification
    ;(AuthService.verifyToken as any).mockResolvedValue({})

    const wrapper = mount(TokenVerifier, {
      props: {
        verifyEndpoint: '/verify',
        redirectPath: '/dashboard',
        includeTokenInRedirect: true,
      },
    })

    await nextTick()
    await nextTick()

    // Assert that the verification function was called as expected
    expect(AuthService.verifyToken).toHaveBeenCalledWith('valid-token')
    // When includeTokenInRedirect is true, router.push gets an object with the token in query parameters
    expect(mockRouterPush).toHaveBeenCalledWith({
      path: '/dashboard',
      query: { token: 'valid-token' },
    })
    // No error should be shown
    const errorDiv = wrapper.find('.error')
    expect(errorDiv.exists()).toBe(false)
  })

  it('displays error when token verification fails', async () => {
    // Arrange: set a valid token that will fail verification
    mockRoute.query = { token: 'invalid-token' }
    // Simulate a failed verification by rejecting the promise
    ;(AuthService.verifyToken as any).mockRejectedValue(new Error('Invalid token'))

    const wrapper = mount(TokenVerifier, {
      props: {
        verifyEndpoint: '/verify',
        redirectPath: '/login',
        includeTokenInRedirect: false,
      },
    })

    await nextTick()
    await nextTick()

    // Assert: error message for failed verification is rendered
    const errorDiv = wrapper.find('.error')
    expect(errorDiv.exists()).toBe(true)
    expect(wrapper.find('.error p').text()).toBe('Token is invalid or has expired. This may be because the server expects the token to be in cookies, but it is sent as a URL parameter.')
    // Assert that router.push was not called (no redirection on failure)
    expect(mockRouterPush).not.toHaveBeenCalled()
  })
})
