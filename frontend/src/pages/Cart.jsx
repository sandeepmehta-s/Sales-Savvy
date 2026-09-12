import { useState, useEffect } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { loadStripe } from '@stripe/stripe-js'
import {
  CardElement,
  Elements,
  useStripe,
  useElements,
} from '@stripe/react-stripe-js'
import { useAuth } from '../context/AuthContext'
import { cartService } from '../services/cart'
import { paymentService } from '../services/payment'
import Loading from '../components/common/Loading'

// ---------------------------------------------------------------------------
// Stripe Elements card form — rendered inside a Bootstrap modal
// ---------------------------------------------------------------------------
const CheckoutForm = ({ clientSecret, paymentIntentId, totalPaise, onSuccess, onCancel }) => {
  const stripe = useStripe()
  const elements = useElements()
  const [processing, setProcessing] = useState(false)
  const [cardError, setCardError] = useState(null)

  const handlePay = async (e) => {
    e.preventDefault()
    if (!stripe || !elements) return

    setProcessing(true)
    setCardError(null)

    const { error, paymentIntent } = await stripe.confirmCardPayment(clientSecret, {
      payment_method: {
        card: elements.getElement(CardElement),
      },
    })

    if (error) {
      setCardError(error.message)
      setProcessing(false)
    } else if (paymentIntent && paymentIntent.status === 'succeeded') {
      onSuccess(paymentIntentId, paymentIntent.payment_method, totalPaise)
    }
  }

  return (
    <form onSubmit={handlePay}>
      <div className="modal-body">
        <p className="text-muted mb-3">Enter your card details to complete the purchase.</p>
        <div className="border rounded p-3 bg-light" style={{ minHeight: '42px' }}>
          <CardElement
            options={{
              style: {
                base: {
                  fontSize: '16px',
                  color: '#212529',
                  '::placeholder': { color: '#6c757d' },
                },
              },
            }}
          />
        </div>
        {cardError && (
          <div className="alert alert-danger mt-3 py-2 mb-0" role="alert">
            {cardError}
          </div>
        )}
      </div>
      <div className="modal-footer">
        <button
          type="button"
          className="btn btn-outline-secondary"
          onClick={onCancel}
          disabled={processing}
        >
          Cancel
        </button>
        <button
          type="submit"
          className="btn btn-primary"
          disabled={processing || !stripe}
        >
          {processing ? (
            <span><i className="bi bi-arrow-repeat me-2 spin" />Processing…</span>
          ) : (
            <span><i className="bi bi-lock-fill me-1" />Pay Now</span>
          )}
        </button>
      </div>
    </form>
  )
}

// ---------------------------------------------------------------------------
// Main Cart page
// ---------------------------------------------------------------------------
const Cart = () => {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [cart, setCart] = useState(null)
  const [loading, setLoading] = useState(true)
  const [checkoutLoading, setCheckoutLoading] = useState(false)

  // Stripe state
  const [stripePromise, setStripePromise] = useState(null)
  const [clientSecret, setClientSecret] = useState(null)
  const [paymentIntentId, setPaymentIntentId] = useState(null)
  const [showPayModal, setShowPayModal] = useState(false)

  // Load Stripe publishable key once on mount
  useEffect(() => {
    const envKey = import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY
    if (envKey && envKey !== 'pk_test_...') {
      setStripePromise(loadStripe(envKey))
      return
    }
    // Fallback: fetch from backend /payment/key (requires auth, so only after user loads)
    paymentService.getKey()
      .then(({ publishableKey }) => {
        if (publishableKey && !publishableKey.startsWith('pk_test_placeholder')) {
          setStripePromise(loadStripe(publishableKey))
        }
      })
      .catch((err) => console.warn('Could not fetch Stripe key:', err))
  }, [])

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

  /** Step 1: Create PaymentIntent on backend, then open Stripe card modal */
  const handleCheckout = async () => {
    if (!user) return alert('Please login to proceed with checkout')
    if (!cart?.items?.length) return alert('Your cart is empty')
    if (!stripePromise) {
      return alert('Payment system is not configured yet. Please add VITE_STRIPE_PUBLISHABLE_KEY to your .env file.')
    }

    setCheckoutLoading(true)
    try {
      const totalPaise = Math.round(cart.totalPrice * 100)
      const data = await paymentService.createPaymentIntent(totalPaise, user.username)
      setClientSecret(data.clientSecret)
      setPaymentIntentId(data.paymentIntentId)
      setShowPayModal(true)
    } catch (error) {
      console.error('Checkout error:', error)
      alert('Failed to initialize payment. Please try again.')
    } finally {
      setCheckoutLoading(false)
    }
  }

  /** Step 2: Called by CheckoutForm after stripe.confirmCardPayment succeeds */
  const handlePaymentSuccess = async (intentId, paymentMethodId, totalPaise) => {
    setShowPayModal(false)
    try {
      const result = await paymentService.confirmPayment(intentId, paymentMethodId, totalPaise)
      if (result.status === 'success') {
        await cartService.clearCart(user.username)
        alert(`Payment successful! Order placed.`)
        navigate('/orders')
      } else {
        alert('Payment confirmation failed. Please contact support.')
      }
    } catch (error) {
      console.error('Payment confirm error:', error)
      alert('Payment was processed but order creation failed. Please contact support.')
    }
  }

  const handleCancelModal = () => {
    setShowPayModal(false)
    setClientSecret(null)
    setPaymentIntentId(null)
  }

  const total = cart?.totalPrice || 0
  const totalPaise = Math.round(total * 100)
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
            <i className="bi bi-bag me-1" />Continue Shopping
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
                        <i className="bi bi-dash" />
                      </button>
                      <span>{item.quantity}</span>
                      <button
                        className="btn btn-outline-secondary btn-sm ms-2"
                        onClick={() => updateQuantity(item.productId, item.quantity + 1)}
                      >
                        <i className="bi bi-plus" />
                      </button>
                    </div>
                    <div className="col-2 fw-semibold">
                      ₹{(item.price * item.quantity).toFixed(2)}
                    </div>
                    <div className="col-1 text-end">
                      <button
                        className="btn btn-sm btn-outline-danger"
                        onClick={() => removeItem(item.productId)}
                      >
                        <i className="bi bi-trash" />
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          {/* Order Summary */}
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
                    <span><i className="bi bi-arrow-repeat me-2 spin" />Processing…</span>
                  ) : (
                    <span><i className="bi bi-credit-card me-2" />Proceed to Checkout</span>
                  )}
                </button>
                <small className="text-muted d-block text-center">
                  🔒 Secure checkout powered by Stripe
                </small>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* ------------------------------------------------------------------ */}
      {/* Stripe Payment Modal                                                */}
      {/* ------------------------------------------------------------------ */}
      {showPayModal && clientSecret && stripePromise && (
        <>
          <div
            className="modal-backdrop fade show"
            style={{ zIndex: 1040 }}
            onClick={handleCancelModal}
          />
          <div
            className="modal fade show d-block"
            tabIndex="-1"
            style={{ zIndex: 1050 }}
            aria-modal="true"
            role="dialog"
          >
            <div className="modal-dialog modal-dialog-centered">
              <div className="modal-content shadow">
                <div className="modal-header">
                  <h5 className="modal-title">
                    <i className="bi bi-lock-fill me-2 text-success" />
                    Complete Payment — ₹{total}
                  </h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={handleCancelModal}
                  />
                </div>
                <Elements stripe={stripePromise} options={{ clientSecret }}>
                  <CheckoutForm
                    clientSecret={clientSecret}
                    paymentIntentId={paymentIntentId}
                    totalPaise={totalPaise}
                    onSuccess={handlePaymentSuccess}
                    onCancel={handleCancelModal}
                  />
                </Elements>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  )
}

export default Cart