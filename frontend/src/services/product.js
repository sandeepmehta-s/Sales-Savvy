import API from './api'

export const productService = {
  getAllProducts: async () => {
    const response = await API.get('/products')
    return response.data
  },

  getProductById: async (id) => {
    const response = await API.get(`/products/${id}`)
    return response.data
  },

  addProduct: async (productData) => {
    const response = await API.post('/products', productData)
    return response.data
  },

  updateProduct: async (id, productData) => {
    const response = await API.put(`/products/${id}`, productData)
    return response.data
  },

  deleteProduct: async (id) => {
    const response = await API.delete(`/products/${id}`)
    return response.data
  },

  searchProducts: async (keyword) => {
    const response = await API.get(`/products/search?keyword=${keyword}`)
    return response.data
  },

  getProductsByCategory: async (category) => {
    const response = await API.get(`/products/category/${category}`)
    return response.data
  }
}