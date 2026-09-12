import { useState, useEffect } from 'react';
import { orderService } from '../services/order';
import Loading from '../components/common/Loading';

const AdminOrders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      const data = await orderService.getAllOrders();
      setOrders(data);
    } catch (error) {
      alert('Failed to load orders');
      console.error('Error:', error);
    } finally {
      setLoading(false);
    }
  };

  const updateOrderStatus = async (orderId, newStatus) => {
    try {
      await orderService.updateOrderStatus(orderId, newStatus);
      alert('Order status updated successfully!');
      loadOrders();
    } catch (error) {
      alert('Failed to update order status');
      console.error('Error:', error);
    }
  };

  if (loading) return <Loading />;

  return (
    <div className="container py-4">
      {/* Header Section */}
      <div className="mb-4 text-center">
        <h2 className="fw-bold text-primary">📋 Order Management</h2>
        <p className="text-muted">Manage and track customer orders efficiently</p>
      </div>

      {/* Orders Table */}
      <div className="table-responsive shadow-sm rounded bg-white p-3">
        <table className="table table-hover align-middle">
          <thead className="table-primary">
            <tr>
              <th scope="col">#</th>
              <th scope="col">Customer</th>
              <th scope="col">Amount</th>
              <th scope="col">Items</th>
              <th scope="col">Status</th>
              <th scope="col">Date</th>
              <th scope="col">Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.length > 0 ? (
              orders.map((order) => (
                <tr key={order.id}>
                  <td>#{order.id}</td>
                  <td>{order.username}</td>
                  <td>₹{order.amount}</td>
                  <td>{order.itemCount} items</td>
                  <td>
                    <span
                      className={`badge text-bg-${
                        order.status === 'DELIVERED'
                          ? 'success'
                          : order.status === 'CANCELLED'
                          ? 'danger'
                          : order.status === 'SHIPPED'
                          ? 'info'
                          : order.status === 'PAID'
                          ? 'primary'
                          : 'secondary'
                      }`}
                    >
                      {order.status}
                    </span>
                  </td>
                  <td>{new Date(order.createdAt).toLocaleDateString()}</td>
                  <td>
                    <select
                      value={order.status}
                      onChange={(e) =>
                        updateOrderStatus(order.razorpayOrderId, e.target.value)
                      }
                      className="form-select form-select-sm"
                    >
                      <option value="CREATED">Created</option>
                      <option value="PAID">Paid</option>
                      <option value="ACCEPTED">Accepted</option>
                      <option value="SHIPPED">Shipped</option>
                      <option value="DELIVERED">Delivered</option>
                      <option value="CANCELLED">Cancelled</option>
                    </select>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="7" className="text-center text-muted py-4">
                  No orders found 😕
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default AdminOrders;