import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import OrderCard from '../components/orders/OrderCard'
import API from '../services/api'
import 'bootstrap/dist/css/bootstrap.min.css'

const Orders = () => {
  const { user } = useAuth()
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (user) {
      loadOrders()
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user])

  const loadOrders = async () => {
    try {
      const response = await API.get(`/orders/user/${user.username}`)
      setOrders(response.data)
    } catch (error) {
      console.error('Failed to load orders', error)
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

  if (!user)
    return (
      <div className="container text-center mt-5">
        <h4>Please login to view your orders</h4>
        <a href="/login" className="btn btn-primary mt-3">
          Go to Login
        </a>
      </div>
    )

  return (
    <div className="container py-5">
      <div className="text-center mb-5">
        <h2 className="fw-bold">🛍️ Your Orders</h2>
        <p className="text-muted">Track all your recent purchases here</p>
      </div>

      {orders.length === 0 ? (
        <div className="text-center mt-5">
          <img
            src="https://cdn-icons-png.flaticon.com/512/4076/4076500.png"
            alt="No orders"
            style={{ width: '120px', opacity: 0.7 }}
          />
          <h5 className="mt-3">No orders found</h5>
          <p className="text-muted">Looks like you haven’t placed any orders yet.</p>
          <a href="/products" className="btn btn-outline-primary mt-2">
            Browse Products
          </a>
        </div>
      ) : (
        <div className="row g-4">
          {orders.map(order => (
            <div className="col-md-6 col-lg-4" key={order.id}>
              <OrderCard order={order} />
            </div>
          ))}
        </div>
      )}
    </div>
  )
}

export default Orders;
