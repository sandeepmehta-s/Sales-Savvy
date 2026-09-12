import API from './api'

export const authService = {
  login: async (username, password) => {
    const response = await API.post('/auth/signin', { username, password })
    return response.data
  },

  register: async (userData) => {
    const response = await API.post('/auth/signup', userData)
    return response.data
  },

  getProfile: async () => {
    const response = await API.get('/users/profile')
    return response.data
  }
}