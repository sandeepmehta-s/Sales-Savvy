/* eslint-disable no-useless-catch */
import { createContext, useState, useContext, useEffect } from 'react';
import { authService } from '../services/auth';

// Create the context
const AuthContext = createContext();

// Hook to access AuthContext easily
// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    checkAuth();
  }, []);

  // 🔹 Check user authentication on app load
  const checkAuth = async () => {
    const token = localStorage.getItem('token');
    if (!token) {
      setLoading(false);
      return;
    }

    try {
      const userData = await authService.getProfile();
      setUser(userData);
    } catch (error) {
      console.error('Auth check failed:', error);
      localStorage.removeItem('token');
    } finally {
      setLoading(false);
    }
  };

  // 🔹 Login method
  const login = async (username, password) => {
    try {
      const data = await authService.login(username, password);
      localStorage.setItem('token', data.token);
      setUser(data);
      return data;
    } catch (error) {
      throw error; // Let component handle showing error message
    }
  };

  // 🔹 Register method
  const register = async (userData) => {
    const registrationData = {
      username: userData.username,
      email: userData.email,
      password: userData.password,
      role: 'ROLE_CUSTOMER',
      gender: userData.gender || null,
      dob: userData.dob || null,
    };

     
    try {
      const data = await authService.register(registrationData);
      return data;
    } catch (error) {
      throw error;
    }
  };

  // 🔹 Logout method
  const logout = () => {
    localStorage.removeItem('token');
    setUser(null);
  };

  const value = {
    user,
    loading,
    login,
    register,
    logout,
    isAuthenticated: !!user, // Handy boolean shortcut
  };

  return (
    <AuthContext.Provider value={value}>
      {!loading ? children : (
        <div className="d-flex justify-content-center align-items-center vh-100">
          <div className="spinner-border text-primary" role="status" />
          <span className="ms-2">Loading authentication...</span>
        </div>
      )}
    </AuthContext.Provider>
  );
};
