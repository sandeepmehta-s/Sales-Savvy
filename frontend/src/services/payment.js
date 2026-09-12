import API from './api'

export const paymentService = {
  /**
   * Step 1: Create a Stripe PaymentIntent on the backend.
   * Returns { clientSecret, paymentIntentId, publishableKey, amount, currency }
   */
  createPaymentIntent: async (amount, username) => {
    const response = await API.post('/payment/create-intent', {
      amount,          // in paise (e.g. totalPrice * 100)
      username,
      currency: 'INR'
    })
    return response.data
  },

  /**
   * Step 2: After Stripe.js confirms payment, notify backend to create the order.
   * Returns { status, orderId, amount }
   */
  confirmPayment: async (paymentIntentId, paymentId, amount) => {
    const response = await API.post('/payment/confirm', {
      paymentIntentId,  // Stripe pi_xxx
      paymentId,        // Stripe charge/payment method ID
      amount            // BigDecimal in paise
    })
    return response.data
  },

  /** Returns { publishableKey } for Stripe.js initialization */
  getKey: async () => {
    const response = await API.get('/payment/key')
    return response.data
  }
}