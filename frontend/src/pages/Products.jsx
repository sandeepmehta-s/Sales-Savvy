import { useState, useEffect } from 'react'
import { productService } from '../services/product'
import ProductCard from '../components/products/ProductCard'
import Loading from '../components/common/Loading'
import 'bootstrap/dist/css/bootstrap.min.css'

const Products = () => {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    loadProducts()
  }, [])

  const loadProducts = async () => {
    try {
      const data = await productService.getAllProducts()
      setProducts(data)
    } catch (err) {
      setError('Failed to load products')
      console.error('Error loading products:', err)
    } finally {
      setLoading(false)
    }
  }

  if (loading)
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ height: '70vh' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    )

  if (error)
    return (
      <div className="container text-center mt-5">
        <div className="alert alert-danger" role="alert">
          {error}
        </div>
      </div>
    )

  return (
    <div className="container py-5">
      {/* Header Section */}
      <div className="text-center mb-5">
        <h2 className="fw-bold">🛒 All Products</h2>
        <p className="text-muted">Discover our latest collection and best deals</p>
      </div>

      {/* Product Grid */}
      {products.length === 0 ? (
        <div className="text-center mt-5">
          <img
            src="https://cdn-icons-png.flaticon.com/512/4076/4076500.png"
            alt="No products"
            style={{ width: '120px', opacity: 0.7 }}
          />
          <h5 className="mt-3">No products found</h5>
          <p className="text-muted">Try again later or explore another category.</p>
        </div>
      ) : (
        <div className="row g-4">
          {products.map(product => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      )}
    </div>
  )
}

export default Products;