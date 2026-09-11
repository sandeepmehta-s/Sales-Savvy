import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Register = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    gender: '',
    dob: '',
    role: 'ROLE_CUSTOMER'
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    if (!formData.username || !formData.email || !formData.password) {
      setError('Username, email, and password are required');
      setLoading(false);
      return;
    }

    try {
      await register(formData);
      navigate('/login', { state: { message: 'Registration successful! Please login.' } });
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
      <div className="card shadow-lg p-4" style={{ width: '100%', maxWidth: '450px' }}>
        <h3 className="text-center mb-4">Create Your Account</h3>

        {error && (
          <div className="alert alert-danger text-center py-2" role="alert">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          {/* Username */}
          <div className="mb-3">
            <label className="form-label">Username *</label>
            <input
              type="text"
              name="username"
              className="form-control"
              placeholder="Enter username"
              value={formData.username}
              onChange={handleChange}
              required
              disabled={loading}
            />
          </div>

          {/* Email */}
          <div className="mb-3">
            <label className="form-label">Email *</label>
            <input
              type="email"
              name="email"
              className="form-control"
              placeholder="Enter email"
              value={formData.email}
              onChange={handleChange}
              required
              disabled={loading}
            />
          </div>

          {/* Password */}
          <div className="mb-3">
            <label className="form-label">Password *</label>
            <input
              type="password"
              name="password"
              className="form-control"
              placeholder="Enter password (min 8 characters)"
              value={formData.password}
              onChange={handleChange}
              required
              disabled={loading}
              minLength="8"
            />
          </div>

          {/* Gender */}
          <div className="mb-3">
            <label className="form-label">Gender</label>
            <select
              name="gender"
              className="form-select"
              value={formData.gender}
              onChange={handleChange}
              disabled={loading}
            >
              <option value="">Select Gender</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
          </div>

          {/* Date of Birth */}
          <div className="mb-3">
            <label className="form-label">Date of Birth</label>
            <input
              type="date"
              name="dob"
              className="form-control"
              value={formData.dob}
              onChange={handleChange}
              disabled={loading}
            />
          </div>

          {/* Submit */}
          <button
            type="submit"
            disabled={loading}
            className="btn btn-success w-100 mt-2"
          >
            {loading ? (
              <>
                <span
                  className="spinner-border spinner-border-sm me-2"
                  role="status"
                  aria-hidden="true"
                ></span>
                Creating Account...
              </>
            ) : (
              'Create Account'
            )}
          </button>
        </form>

        <p className="text-center mt-3 mb-0">
          Already have an account?{' '}
          <Link to="/login" className="text-decoration-none">
            Login here
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Register;
