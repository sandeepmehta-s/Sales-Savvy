import API from './api'

export const cartService = {
  getCart: async (username) => {
    const response = await API.get(`/cart/items?username=${username}`)
    return response.data
  },

  addToCart: async (username, productId, quantity = 1) => {
    const response = await API.post('/cart/add', null, {
      params: { username, productId, quantity }
    })
    return response.data
  },

  updateCartItem: async (username, productId, quantity) => {
    const response = await API.put('/cart/update', null, {
      params: { username, productId, quantity }
    })
    return response.data
  },

  removeFromCart: async (username, productId) => {
    const response = await API.delete('/cart/remove', {
      params: { username, productId }
    })
    return response.data
  },

  clearCart: async (username) => {
    const response = await API.delete('/cart/clear', {
      params: { username }
    })
    return response.data
  },

  getCartTotal: async (username) => {
    const response = await API.get(`/cart/total?username=${username}`)
    return response.data
  }
}