import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import AdminProducts from './AdminProducts';
import AdminUsers from './AdminUsers';
import AdminOrders from './AdminOrders';

const AdminDashboard = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('products');

  // Restrict non-admins
  if (!user || user.role !== 'ROLE_ADMIN') {
    return (
      <div className="container py-5 text-center">
        <div className="alert alert-danger shadow-sm" role="alert">
          <h4 className="alert-heading mb-2">Access Denied 🚫</h4>
          <p>You need administrator privileges to access this page.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container py-5">
      {/* Header Section */}
      <div className="text-center mb-5 admin-dashboard-heading">
        <span className="eyebrow">Control room</span>
        <h1 className="fw-bold">Admin Dashboard</h1>
        <p className="text-muted">Manage your store’s operations and data efficiently</p>
      </div>

      {/* Tab Navigation */}
      <ul className="nav nav-tabs justify-content-center mb-4" role="tablist">
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'products' ? 'active' : ''}`}
            onClick={() => setActiveTab('products')}
          >
            <i className="bi bi-box-seam me-2"></i>Products
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'users' ? 'active' : ''}`}
            onClick={() => setActiveTab('users')}
          >
            <i className="bi bi-people me-2"></i>Users
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'orders' ? 'active' : ''}`}
            onClick={() => setActiveTab('orders')}
          >
            <i className="bi bi-receipt-cutoff me-2"></i>Orders
          </button>
        </li>
      </ul>

      {/* Tab Content */}
      <div className="admin-tab-panel">
        {activeTab === 'products' && <AdminProducts />}
        {activeTab === 'users' && <AdminUsers />}
        {activeTab === 'orders' && <AdminOrders />}
      </div>
    </div>
  );
};

export default AdminDashboard;
