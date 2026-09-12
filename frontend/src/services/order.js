import API from './api'

export const orderService = {
  // Customer operations

  /** Fetch all orders for the authenticated user */
  getUserOrders: async (username) => {
    const response = await API.get(`/orders/user/${username}`)
    return response.data
  },

  getOrderById: async (id) => {
    const response = await API.get(`/orders/${id}`)
    return response.data
  },

  /** Cancel an order by its Stripe PaymentIntent ID */
  cancelOrder: async (stripePaymentIntentId) => {
    const response = await API.put(`/orders/${stripePaymentIntentId}/cancel`)
    return response.data
  },

  // Admin operations

  getAllOrders: async () => {
    const response = await API.get('/orders')
    return response.data
  },

  /** Update order status by Stripe PaymentIntent ID */
  updateOrderStatus: async (stripePaymentIntentId, status) => {
    const response = await API.put(`/orders/${stripePaymentIntentId}/status`, null, {
      params: { status }
    })
    return response.data
  }
}