import API from './api'

export const userService = {
  getAllUsers: async () => {
    const response = await API.get('/users')
    return response.data
  },

  getUserByUsername: async (username) => {
    const response = await API.get(`/users/${username}`)
    return response.data
  },

  getCurrentUser: async () => {
    const response = await API.get('/users/profile')
    return response.data
  },

  updateUser: async (id, userData) => {
    const response = await API.put(`/users/${id}`, userData)
    return response.data
  },

  deleteUser: async (id) => {
    const response = await API.delete(`/users/${id}`)
    return response.data
  }
}