import API from './api'

export const orderService = {
  // Customer operations
  createOrder: async (username, razorpayOrderId, amount) => {
    const response = await API.post('/orders/create', null, {
      params: { username, razorpayOrderId, amount }
    })
    return response.data
  },

  getUserOrders: async (username) => {
    const response = await API.get(`/orders/user/${username}`)
    return response.data
  },

  getOrderById: async (id) => {
    const response = await API.get(`/orders/${id}`)
    return response.data
  },

  cancelOrder: async (razorpayOrderId) => {
    const response = await API.put(`/orders/${razorpayOrderId}/cancel`)
    return response.data
  },

  // Admin operations
  getAllOrders: async () => {
    const response = await API.get('/orders')
    return response.data
  },

  updateOrderStatus: async (razorpayOrderId, status) => {
    const response = await API.put(`/orders/${razorpayOrderId}/status`, null, {
      params: { status }
    })
    return response.data
  }
}