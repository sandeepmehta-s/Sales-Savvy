import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { userService } from '../services/user';
import Loading from '../components/common/Loading';

const AdminUsers = () => {
  const { user } = useAuth();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (user?.role === 'ROLE_ADMIN') {
      userService.getAllUsers().then(setUsers).catch(() => setError('Failed to load users.')).finally(() => setLoading(false));
    }
  }, [user]);

  const handleDeleteUser = async (userId, username) => {
    if (username === 'admin' || !window.confirm(`Delete user ${username}?`)) return;
    try {
      await userService.deleteUser(userId);
      setUsers((currentUsers) => currentUsers.filter((item) => item.id !== userId));
    } catch {
      setError('Failed to delete user.');
    }
  };

  if (!user || user.role !== 'ROLE_ADMIN') return <div className="access-state"><span className="eyebrow">Restricted</span><h2>Admin access required.</h2><p>This area is only available to store administrators.</p></div>;
  if (loading) return <Loading />;
  if (error) return <div className="error-page"><span className="eyebrow">Something went wrong</span><h2>{error}</h2></div>;

  const admins = users.filter((item) => item.role === 'ROLE_ADMIN').length;
  return <section className="admin-page"><div className="admin-page-heading"><div><span className="eyebrow">People / Directory</span><h1>User management</h1><p>Keep an eye on the people using your storefront.</p></div><div className="admin-stat"><strong>{users.length}</strong><span>Total accounts</span></div></div><div className="admin-stat-row"><div><strong>{users.length}</strong><span>All users</span></div><div><strong>{admins}</strong><span>Administrators</span></div><div><strong>{users.length - admins}</strong><span>Customers</span></div></div>{users.length === 0 ? <div className="empty-state">No users found.</div> : <div className="user-card-grid">{users.map((item) => <article className="user-card" key={item.id}><div className="user-avatar">{item.username?.slice(0, 1).toUpperCase()}</div><div className="user-card-main"><div className="user-card-title"><h3>{item.username}</h3><span className={`role-pill ${item.role === 'ROLE_ADMIN' ? 'role-admin' : ''}`}>{item.role?.replace('ROLE_', '')}</span></div><p>{item.email}</p><dl><div><dt>Gender</dt><dd>{item.gender || 'Not set'}</dd></div><div><dt>Date of birth</dt><dd>{item.dob || 'Not set'}</dd></div></dl></div><button className="icon-delete" type="button" onClick={() => handleDeleteUser(item.id, item.username)} disabled={item.username === 'admin'} aria-label={`Delete ${item.username}`}><i className="bi bi-trash3" /></button></article>)}</div>}</section>;
};

export default AdminUsers;
