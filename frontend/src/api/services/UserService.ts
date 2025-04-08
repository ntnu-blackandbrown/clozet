import axios from '../axios'

export const UserService = {
  /**
   * Get all users (admin)
   */
  getAllUsers: () => {
    return axios.get('/api/users')
  },

  /**
   * Get a specific user by ID
   */
  getUserById: (userId: number) => {
    return axios.get(`/api/users/${userId}`)
  },

  /**
   * Update a user's information
   */
  updateUser: (userId: number, userData: any) => {
    return axios.put(`/api/users/${userId}`, userData)
  },

  /**
   * Update a user's role (admin)
   */
  updateUserRole: (userId: number, role: string) => {
    return axios.put(`/api/users/${userId}`, { role })
  },

  /**
   * Toggle a user's active status (admin)
   */
  toggleUserStatus: (userId: number, isActive: boolean) => {
    return axios.put(`/api/users/${userId}`, { active: isActive })
  },
}

export default UserService
