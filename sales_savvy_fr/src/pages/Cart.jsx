import { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { cartService } from '../services/cart'
import { paymentService } from '../services/payment'
import Loading from '../components/common/Loading'

const Cart = () => {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [cart, setCart] = useState(null)
  const [loading, setLoading] = useState(true)
  const [checkoutLoading, setCheckoutLoading] = useState(false)

  useEffect(() => {
    if (user) loadCart()
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [user])

  const loadCart = async () => {
    try {
      const cartData = await cartService.getCart(user.username)
      setCart(cartData)
    } catch (error) {
      console.error('Error loading cart:', error)
      alert('Failed to load cart')
    } finally {
      setLoading(false)
    }
  }

  const updateQuantity = async (productId, newQuantity) => {
    if (newQuantity < 1) return
    try {
      await cartService.updateCartItem(user.username, productId, newQuantity)
      loadCart()
    } catch {
      alert('Failed to update quantity')
    }
  }

  const removeItem = async (productId) => {
    try {
      await cartService.removeFromCart(user.username, productId)
      loadCart()
    } catch {
      alert('Failed to remove item')
    }
  }

  const handleCheckout = async () => {
    if (!user) return alert('Please login to proceed with checkout')
    if (!cart?.items?.length) return alert('Your cart is empty')

    setCheckoutLoading(true)
    try {
      const totalAmount = Math.round(cart.totalPrice * 100)
      const orderResponse = await paymentService.createOrder(totalAmount, user.username)
      await loadRazorpayScript()

      const options = {
        key: orderResponse.key,
        amount: orderResponse.amount,
        currency: orderResponse.currency,
        name: 'SalesSavvy',
        description: 'Order Payment',
        order_id: orderResponse.orderId,
        handler: async (response) => handlePaymentSuccess(response, orderResponse.orderId),
        prefill: { name: user.username, email: user.email || '' },
        theme: { color: '#0d6efd' }
      }

      new window.Razorpay(options).open()
    } catch (error) {
      console.error('Checkout error:', error)
      alert('Failed to initialize payment. Please try again.')
    } finally {
      setCheckoutLoading(false)
    }
  }

  const loadRazorpayScript = () => {
    return new Promise((resolve, reject) => {
      if (window.Razorpay) {
        resolve()
        return
      }
      const script = document.createElement('script')
      script.src = 'https://checkout.razorpay.com/v1/checkout.js'
      script.async = true
      script.onload = resolve
      script.onerror = () => reject(new Error('Failed to load payment gateway'))
      document.body.appendChild(script)
    })
  }

  const handlePaymentSuccess = async (paymentResponse, razorpayOrderId) => {
    try {
      const verifyData = {
        razorpay_order_id: razorpayOrderId,
        razorpay_payment_id: paymentResponse.razorpay_payment_id,
        razorpay_signature: paymentResponse.razorpay_signature,
        amount: Math.round(cart.totalPrice * 100)
      }

      const verification = await paymentService.verifyPayment(verifyData)
      if (verification.status === 'success') {
        await cartService.clearCart(user.username)
        alert('Payment successful! Order placed.')
        navigate('/orders')
      } else {
        alert('Payment verification failed.')
      }
    } catch (error) {
      console.error('Payment success handling error:', error)
      alert('Payment was successful but order creation failed.')
    }
  }

  const total = cart?.totalPrice || 0
  const itemCount = cart?.items?.reduce((sum, i) => sum + i.quantity, 0) || 0

  if (loading) return <Loading />
  if (!user) return <div className="text-center mt-5">Please login to view your cart.</div>

  return (
    <div className="container py-4">
      <h2 className="fw-bold mb-4">🛒 Shopping Cart</h2>

      {!cart?.items?.length ? (
        <div className="text-center mt-5">
          <h4 className="text-muted">Your cart is empty</h4>
          <Link to="/products" className="btn btn-primary mt-3">
            <i className="bi bi-bag"></i> Continue Shopping
          </Link>
        </div>
      ) : (
        <div className="row">
          {/* Cart Items */}
          <div className="col-lg-8 mb-4">
            <div className="card shadow-sm border-0">
              <div className="card-body">
                {cart.items.map((item) => (
                  <div key={item.itemId} className="row align-items-center border-bottom py-3">
                    <div className="col-2">
                      <img
                        src={item.photo || '/placeholder-image.jpg'}
                        alt={item.productName}
                        className="img-fluid rounded"
                      />
                    </div>
                    <div className="col-4">
                      <h5 className="mb-1">{item.productName}</h5>
                      <small className="text-muted">₹{item.price}</small>
                    </div>
                    <div className="col-3 d-flex align-items-center">
                      <button
                        className="btn btn-outline-secondary btn-sm me-2"
                        onClick={() => updateQuantity(item.productId, item.quantity - 1)}
                        disabled={item.quantity <= 1}
                      >
                        <i className="bi bi-dash"></i>
                      </button>
                      <span>{item.quantity}</span>
                      <button
                        className="btn btn-outline-secondary btn-sm ms-2"
                        onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                      >
                        <i className="bi bi-plus"></i>
                      </button>
                    </div>
                    <div className="col-2 fw-semibold">₹{item.subtotal}</div>
                    <div className="col-1 text-end">
                      <button
                        className="btn btn-sm btn-outline-danger"
                        onClick={() => removeItem(item.productId)}
                      >
                        <i className="bi bi-trash"></i>
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Summary */}
          <div className="col-lg-4">
            <div className="card shadow-sm border-0">
              <div className="card-body">
                <h5 className="card-title border-bottom pb-2 mb-3">Order Summary</h5>
                <div className="d-flex justify-content-between mb-2">
                  <span>Items</span>
                  <span>{itemCount}</span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                  <span>Subtotal</span>
                  <span>₹{total}</span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                  <span>Shipping</span>
                  <span className="text-success">FREE</span>
                </div>
                <hr />
                <div className="d-flex justify-content-between fw-bold fs-5 mb-3">
                  <span>Total</span>
                  <span>₹{total}</span>
                </div>
                <button
                  className="btn btn-primary w-100 mb-2"
                  onClick={handleCheckout}
                  disabled={checkoutLoading || !itemCount}
                >
                  {checkoutLoading ? (
                    <span><i className="bi bi-arrow-repeat me-2 spin"></i>Processing...</span>
                  ) : (
                    <span><i className="bi bi-credit-card me-2"></i>Proceed to Checkout</span>
                  )}
                </button>
                <small className="text-muted d-block text-center">
                  🔒 Secure checkout powered by Razorpay
                </small>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Cart;