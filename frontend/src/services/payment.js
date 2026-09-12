import API from './api'

export const paymentService = {
  createOrder: async (amount, username) => {
    const response = await API.post('/payment/create-order', {
      amount: amount,
      username
    })
    return response.data
  },

  verifyPayment: async (paymentData) => {
    const requestData = {
      orderId: paymentData.razorpay_order_id,
      paymentId: paymentData.razorpay_payment_id, 
      signature: paymentData.razorpay_signature,
      amount: paymentData.amount
    };

    const response = await API.post('/payment/verify', requestData);
    return response.data;
  },

  getKey: async () => {
    const response = await API.get('/payment/key')
    return response.data
  }
}