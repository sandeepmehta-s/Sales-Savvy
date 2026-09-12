import React from 'react';

const OrderCard = ({ order }) => {
  // Amount stored in paise; convert to rupees for display
  const amountRupees = order.amount ? (order.amount / 100).toFixed(2) : '0.00';

  const statusColor =
    order.status === 'DELIVERED' ? 'success'
    : order.status === 'SHIPPED'   ? 'info'
    : order.status === 'PAID'      ? 'primary'
    : order.status === 'CANCELLED' ? 'danger'
    : 'secondary'; // CREATED or anything else

  return (
    <div className="card mb-4 shadow-sm border-0">
      <div className="card-header d-flex justify-content-between align-items-center bg-light">
        <h5 className="mb-0">Order #{order.id?.slice(-6)}</h5>
        <span className={`badge text-uppercase px-3 py-2 bg-${statusColor}`}>
          {order.status}
        </span>
      </div>

      <div className="card-body">
        <div className="mb-2">
          <strong>Amount:</strong> ₹{amountRupees}
        </div>
        <div className="mb-2">
          <strong>Items:</strong> {order.itemCount}
        </div>
        <div className="mb-3">
          <strong>Date:</strong>{' '}
          {order.createdAt ? new Date(order.createdAt).toLocaleDateString('en-IN') : '—'}
        </div>

        {order.items && order.items.length > 0 && (
          <div className="table-responsive">
            <table className="table table-sm align-middle">
              <thead className="table-light">
                <tr>
                  <th>Product</th>
                  <th className="text-center">Qty</th>
                  <th className="text-end">Price</th>
                </tr>
              </thead>
              <tbody>
                {order.items.map((item) => (
                  <tr key={item.productId}>
                    <td>{item.productName}</td>
                    <td className="text-center">{item.quantity}</td>
                    <td className="text-end">₹{item.price}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      <div className="card-footer text-end bg-light">
        <span className="text-muted small">
          Payment ID: {order.paymentId ? order.paymentId.slice(-8) : '—'}
        </span>
      </div>
    </div>
  );
};

export default OrderCard;