import { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Login = () => {
  const [formData, setFormData] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      const user = await login(formData.username.trim(), formData.password);
      navigate(user.role === 'ROLE_ADMIN' ? '/admin' : '/products');
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Invalid username or password.');
    } finally {
      setLoading(false);
    }
  };

  return <section className="auth-layout"><div className="auth-aside"><span className="auth-kicker">SalesSavvy / 01</span><h1>Good products.<br /><em>Clear choices.</em></h1><p>Your personal shelf for thoughtful finds and simple checkout.</p></div><div className="auth-panel"><div className="auth-panel-heading"><span className="eyebrow">Welcome back</span><h2>Sign in</h2><p>{location.state?.message || 'Pick up where you left off.'}</p></div>{error && <div className="error" role="alert">{error}</div>}<form onSubmit={handleSubmit} className="auth-form-modern"><label>Username<input type="text" name="username" autoComplete="username" value={formData.username} onChange={(event) => setFormData({ ...formData, username: event.target.value })} required disabled={loading} /></label><label>Password<input type="password" name="password" autoComplete="current-password" value={formData.password} onChange={(event) => setFormData({ ...formData, password: event.target.value })} required disabled={loading} /></label><button className="btn btn-primary btn-lg" type="submit" disabled={loading}>{loading ? 'Signing in...' : 'Enter storefront'}</button></form><p className="auth-footnote">New to SalesSavvy? <Link to="/register">Create an account</Link></p></div></section>;
};

export default Login;
