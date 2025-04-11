import axios from 'axios'
import axiosInstance from '../axios'

export const AuthService = {
  /**
   * Login a user
   */
  login: (usernameOrEmail: string, password: string) => {
    console.log('📡 API: Attempting login')
    return axiosInstance.post('/api/auth/login', { usernameOrEmail, password })
  },

  /**
   * Register a new user
   */
  register: (
    username: string,
    password: string,
    email: string,
    firstName: string,
    lastName: string,
  ) => {
    console.log('📡 API: Registering user')
    return axiosInstance.post('/api/auth/register', {
      username,
      password,
      email,
      firstName,
      lastName,
    })
  },

  /**
   * Logout the current user
   */
  logout: () => {
    console.log('📡 API: Logging out')
    return axiosInstance.post('/api/auth/logout')
  },

  /**
   * Get the current user profile
   */
  getCurrentUser: () => {
    console.log('📡 API: Fetching current user')
    return axiosInstance.get('/api/me')
  },

  /**
   * Change user password
   */
  changePassword: (currentPassword: string, newPassword: string) => {
    console.log('📡 API: Changing password')
    return axiosInstance.post('/api/me/change-password', {
      currentPassword,
      newPassword,
    })
  },

  /**
   * Request password reset
   */
  requestPasswordReset: (email: string) => {
    console.log('📡 API: Requesting password reset')
    return axiosInstance.post('/api/auth/forgot-password', { email })
  },

  /**
   * Reset password with token
   */
  resetPassword: (token: string, password: string) => {
    console.log('📡 API: Resetting password')
    return axiosInstance.post('/api/auth/reset-password', { token, password })
  },

  /**
   * Delete the current user
   */
  deleteUser: (id: string) => {
    console.log('📡 API: Deleting user')
    return axiosInstance.delete(`/api/users/${id}`)
  },

  /**
   * Verify a token (email verification, password reset, etc.)
   */
  verifyToken: (token: string) => {
    console.log('📡 API: Verifying token')
    return axiosInstance.get(`/api/auth/verify?token=${token}`)
  },

  /**
   * Validate password reset token
   */
  validateResetToken: (token: string) => {
    console.log('📡 API: Validating reset token')
    return axiosInstance.get(`/api/auth/reset-password/validate?token=${token}`)
  },

  /**
   * Refresh the access token
   */
  refreshToken: () => {
    console.log('📡 API: Refreshing token')
    return axiosInstance.post('/api/auth/refresh-token')
  },

  /**
   * Update user profile information
   */
  updateUser: (userId: number | string, userData: any) => {
    console.log('📡 API: Updating user profile')
    return axiosInstance.put(`/api/users/${userId}`, userData)
  }
}

export default AuthService
