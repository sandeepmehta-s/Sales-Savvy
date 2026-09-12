import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Register = () => {
  const [formData, setFormData] = useState({ username: '', email: '', password: '', gender: '', dob: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    setError('');
    try {
      await register(formData);
      navigate('/login', { state: { message: 'Account created. Sign in to continue.' } });
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Registration failed. Please check your details.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="auth-layout auth-layout-reverse">
      <div className="auth-aside">
        <span className="auth-kicker">ShopSphere / 02</span>
        <h1>Make room for<br /><em>better finds.</em></h1>
        <p>Join a store built around discovery, confidence, and less noise.</p>
      </div>
      <div className="auth-panel">
        <div className="auth-panel-heading">
          <span className="eyebrow">Create your account</span>
          <h2>Start shopping</h2>
          <p>A few details and your next favourite product is closer.</p>
        </div>
        {error && <div className="error" role="alert">{error}</div>}
        <form onSubmit={handleSubmit} className="auth-form-modern">
          <div className="form-two-col">
            <label>
              Username
              <input
                name="username"
                maxLength="50"
                value={formData.username}
                onChange={(event) => setFormData({ ...formData, username: event.target.value })}
                required
                disabled={loading}
              />
            </label>
            <label>
              Email
              <input
                type="email"
                name="email"
                autoComplete="email"
                value={formData.email}
                onChange={(event) => setFormData({ ...formData, email: event.target.value })}
                required
                disabled={loading}
              />
            </label>
          </div>
          <label>
            Password
            <input
              type="password"
              name="password"
              minLength="8"
              autoComplete="new-password"
              value={formData.password}
              onChange={(event) => setFormData({ ...formData, password: event.target.value })}
              required
              disabled={loading}
            />
          </label>
          <div className="form-two-col">
            <label>
              Gender
              <select
                name="gender"
                value={formData.gender}
                onChange={(event) => setFormData({ ...formData, gender: event.target.value })}
                disabled={loading}
              >
                <option value="">Prefer not to say</option>
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
            </label>
            <label>
              Date of birth
              <input
                type="date"
                name="dob"
                value={formData.dob}
                onChange={(event) => setFormData({ ...formData, dob: event.target.value })}
                disabled={loading}
              />
            </label>
          </div>
          <button className="btn btn-primary btn-lg" type="submit" disabled={loading}>
            {loading ? 'Creating account...' : 'Create account'}
          </button>
        </form>
        <p className="auth-footnote">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </div>
    </section>
  );
};

export default Register;
