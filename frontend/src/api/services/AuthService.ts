import axios from '../axios'

export const AuthService = {
  /**
   * Login a user
   */
  login: (usernameOrEmail: string, password: string) => {
    return axios.post('/api/auth/login', { usernameOrEmail, password })
  },

  /**
   * Register a new user
   */
  register: (usernameOrEmail: string, password: string, email: string, firstName: string, lastName: string) => {
    return axios.post('/api/auth/register', { usernameOrEmail, password, email, firstName, lastName })
  },

  /**
   * Logout the current user
   */
  logout: () => {
    return axios.post('/api/auth/logout', {})
  },

  /**
   * Get the current user profile
   */
  getCurrentUser: () => {
    return axios.get('/api/me')
  },

  /**
   * Change user password
   */
  changePassword: (currentPassword: string, newPassword: string, confirmPassword: string) => {
    return axios.post('/api/me/change-password', {
      currentPassword,
      newPassword,
      confirmPassword
    })
  },

  /**
   * Verify a token (email verification, password reset, etc.)
   */
  verifyToken: (endpoint: string, token: string) => {
    return axios.get(`${import.meta.env.VITE_API_BASE_URL}${endpoint}?token=${token}`)
  }
}

export default AuthService
