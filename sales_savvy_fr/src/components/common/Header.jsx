import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useState } from 'react';

const Header = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [expanded, setExpanded] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
    setExpanded(false);
    setUserMenuOpen(false);
  };

  const closeNavbar = () => setExpanded(false);

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
      <div className="container">
        {/* Logo */}
        <Link className="navbar-brand fw-bold fs-4 d-flex align-items-center gap-2" to="/" onClick={closeNavbar}>
          <span className="brand-mark">S</span>
          <span>SalesSavvy</span>
        </Link>

        {/* Toggler for mobile */}
        <button
          className="navbar-toggler"
          type="button"
          aria-controls="navbarNav"
          aria-expanded={expanded}
          aria-label="Toggle navigation"
          onClick={() => setExpanded(!expanded)}
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        {/* Navbar Links */}
        <div
          className={`collapse navbar-collapse ${expanded ? 'show' : ''}`}
          id="navbarNav"
        >
          <ul className="navbar-nav ms-auto">
            <li className="nav-item">
              <Link className="nav-link" to="/products" onClick={closeNavbar}>
                Products
              </Link>
            </li>

            <li className="nav-item">
              <Link className="nav-link" to="/cart" onClick={closeNavbar}>
                Cart
              </Link>
            </li>

            {user ? (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/orders" onClick={closeNavbar}>
                    Orders
                  </Link>
                </li>

                <li className="nav-item">
                  <Link className="nav-link" to="/profile" onClick={closeNavbar}>
                    Profile
                  </Link>
                </li>

                {/* Admin Dashboard (Only for Admins) */}
                {user.role === 'ROLE_ADMIN' && (
                  <li className="nav-item">
                    <Link
                      className="nav-link admin-link fw-semibold"
                      to="/admin"
                      onClick={closeNavbar}
                    >
                      Admin Dashboard
                    </Link>
                  </li>
                )}

                {/* User Dropdown */}
                <li className="nav-item dropdown">
                  <button
                    className="btn btn-sm btn-outline-light dropdown-toggle ms-2"
                    type="button"
                    aria-expanded={userMenuOpen}
                    onClick={() => setUserMenuOpen(!userMenuOpen)}
                  >
                    Hello, {user.username}
                  </button>
                  <ul className={`dropdown-menu dropdown-menu-end ${userMenuOpen ? 'show' : ''}`}>
                    <li>
                      <button className="dropdown-item" onClick={handleLogout}>
                        <i className="bi bi-box-arrow-right me-2"></i>Logout
                      </button>
                    </li>
                  </ul>
                </li>
              </>
            ) : (
              <>
                <li className="nav-item">
                  <Link className="nav-link" to="/login" onClick={closeNavbar}>
                    Login
                  </Link>
                </li>

                <li className="nav-item">
                  <Link className="nav-link" to="/register" onClick={closeNavbar}>
                    Register
                  </Link>
                </li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
};

export default Header;