import axios from 'axios'

const isProd = import.meta.env.PROD
const defaultUrl = isProd 
  ? 'https://shopsphere-3e2o.onrender.com' 
  : 'http://localhost:8080'

const API = axios.create({
  baseURL: import.meta.env.VITE_API_URL || defaultUrl,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Add token to all requests
API.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor to handle errors
API.interceptors.response.use(
  (response) => response,
  (error) => {
    const isAuthRequest = error.config?.url?.startsWith('/auth/')
    if (error.response?.status === 401 && !isAuthRequest) {
      localStorage.removeItem('token')
      if (window.location.pathname !== '/login') {
        window.location.assign('/login')
      }
    }
    return Promise.reject(error)
  }
)

export default API