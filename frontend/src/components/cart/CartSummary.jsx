import React from 'react';

const CartSummary = ({ total, itemCount, onCheckout }) => {
  return (
    <div className="card shadow-sm border-0 mt-3">
      <div className="card-body">
        <h5 className="card-title text-center mb-3">🛒 Cart Summary</h5>
        <hr />

        <div className="d-flex justify-content-between mb-2">
          <span className="text-muted">Items:</span>
          <span className="fw-semibold">{itemCount}</span>
        </div>

        <div className="d-flex justify-content-between mb-3">
          <span className="text-muted">Total:</span>
          <span className="fw-bold text-success fs-5">
            ₹{total.toLocaleString()}
          </span>
        </div>

        <button
          onClick={onCheckout}
          className="btn btn-primary w-100 mt-2"
          disabled={itemCount === 0}
        >
          <i className="bi bi-bag-check-fill me-2"></i>
          Proceed to Checkout
        </button>
      </div>
    </div>
  );
};

export default CartSummary;
