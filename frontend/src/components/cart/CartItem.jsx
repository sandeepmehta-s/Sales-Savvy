import React from 'react';

const CartItem = ({ item, onUpdateQuantity, onRemove }) => {
  const handleDecrease = () => {
    if (item.quantity > 1) {
      onUpdateQuantity(item.product.id, item.quantity - 1);
    }
  };

  const handleIncrease = () => {
    onUpdateQuantity(item.product.id, item.quantity + 1);
  };

  return (
    <div className="card mb-3 shadow-sm border-0">
      <div className="row g-0 align-items-center p-2">
        {/* Product Image */}
        <div className="col-3 col-md-2 text-center">
          <img
            src={item.product.photo || '/placeholder-image.jpg'}
            alt={item.product.name}
            className="img-fluid rounded"
            style={{ maxHeight: '80px', objectFit: 'cover' }}
          />
        </div>

        {/* Product Details */}
        <div className="col-5 col-md-6">
          <div className="card-body py-2">
            <h6 className="card-title mb-1">{item.product.name}</h6>
            <p className="card-text text-muted mb-1">
              ₹{item.product.price.toLocaleString()}
            </p>
            <p className="card-text">
              <small className="text-success fw-semibold">
                Total: ₹{(item.product.price * item.quantity).toLocaleString()}
              </small>
            </p>
          </div>
        </div>

        {/* Quantity Controls */}
        <div className="col-4 col-md-4 d-flex flex-column align-items-center justify-content-center">
          <div className="btn-group mb-2" role="group">
            <button
              className="btn btn-outline-secondary btn-sm"
              onClick={handleDecrease}
              disabled={item.quantity <= 1}
            >
              −
            </button>
            <button className="btn btn-light btn-sm" disabled>
              {item.quantity}
            </button>
            <button
              className="btn btn-outline-secondary btn-sm"
              onClick={handleIncrease}
            >
              +
            </button>
          </div>

          <button
            className="btn btn-outline-danger btn-sm"
            onClick={() => onRemove(item.product.id)}
          >
            <i className="bi bi-trash me-1"></i> Remove
          </button>
        </div>
      </div>
    </div>
  );
};

export default CartItem;
