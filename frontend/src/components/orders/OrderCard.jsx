import React from 'react';

const OrderCard = ({ order }) => {
  return (
    <div className="card mb-4 shadow-sm border-0">
      <div className="card-header d-flex justify-content-between align-items-center bg-light">
        <h5 className="mb-0">Order #{order.id}</h5>
        <span
          className={`badge text-uppercase px-3 py-2 ${
            order.status === 'DELIVERED'
              ? 'bg-success'
              : order.status === 'PENDING'
              ? 'bg-warning text-dark'
              : order.status === 'CANCELLED'
              ? 'bg-danger'
              : 'bg-secondary'
          }`}
        >
          {order.status}
        </span>
      </div>

      <div className="card-body">
        <div className="mb-2">
          <strong>Amount:</strong> ₹{order.amount}
        </div>
        <div className="mb-2">
          <strong>Items:</strong> {order.itemCount}
        </div>
        <div className="mb-3">
          <strong>Date:</strong>{' '}
          {new Date(order.createdAt).toLocaleDateString('en-IN')}
        </div>

        <div className="table-responsive">
          <table className="table table-sm align-middle">
            <thead className="table-light">
              <tr>
                <th>Product</th>
                <th className="text-center">Qty</th>
                <th className="text-end">Subtotal</th>
              </tr>
            </thead>
            <tbody>
              {order.items.map((item) => (
                <tr key={item.productId}>
                  <td>{item.productName}</td>
                  <td className="text-center">{item.quantity}</td>
                  <td className="text-end">₹{item.subtotal}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="card-footer text-end bg-light">
        <button className="btn btn-outline-primary btn-sm">
          View Details
        </button>
      </div>
    </div>
  );
};

export default OrderCard;