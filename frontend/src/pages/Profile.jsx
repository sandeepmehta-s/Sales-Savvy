import { useState, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { userService } from '../services/user'
import Loading from '../components/common/Loading'
import 'bootstrap/dist/css/bootstrap.min.css'

const Profile = () => {
  const { user } = useAuth()
  const [userData, setUserData] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (user) {
      loadUserProfile()
    }
  }, [user])

  const loadUserProfile = async () => {
    try {
      const data = await userService.getCurrentUser()
      setUserData(data)
    } catch (error) {
      console.error('Error loading profile:', error)
    } finally {
      setLoading(false)
    }
  }

  if (loading)
    return (
      <div className="d-flex justify-content-center align-items-center" style={{ height: '70vh' }}>
        <div className="spinner-border text-primary" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    )

  if (!user)
    return (
      <div className="container text-center mt-5">
        <div className="alert alert-warning" role="alert">
          Please login to view your profile.
        </div>
      </div>
    )

  return (
    <div className="container py-5">
      <div className="row justify-content-center">
        <div className="col-md-8">
          <div className="card shadow-lg border-0">
            <div className="card-header bg-primary text-white text-center">
              <h3 className="mb-0">
                <i className="bi bi-person-circle me-2"></i>Profile Information
              </h3>
            </div>

            <div className="card-body p-4">
              <div className="d-flex align-items-center mb-4">
                <img
                  src="https://cdn-icons-png.flaticon.com/512/149/149071.png"
                  alt="Profile"
                  className="rounded-circle me-3"
                  style={{ width: '80px', height: '80px' }}
                />
                <div>
                  <h4 className="mb-1">{userData?.username}</h4>
                  <span className={`badge bg-${userData?.role === 'ROLE_ADMIN' ? 'danger' : 'success'}`}>
                    {userData?.role?.replace('ROLE_', '')}
                  </span>
                </div>
              </div>

              <table className="table table-borderless mb-0">
                <tbody>
                  <tr>
                    <th scope="row" className="text-muted">Email</th>
                    <td>{userData?.email}</td>
                  </tr>
                  {userData?.gender && (
                    <tr>
                      <th scope="row" className="text-muted">Gender</th>
                      <td>{userData.gender}</td>
                    </tr>
                  )}
                  {userData?.dob && (
                    <tr>
                      <th scope="row" className="text-muted">Date of Birth</th>
                      <td>{userData.dob}</td>
                    </tr>
                  )}
                  <tr>
                    <th scope="row" className="text-muted">User ID</th>
                    <td>{userData?.id}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <div className="card-footer text-center bg-light">
              <a href="/edit-profile" className="btn btn-outline-primary me-2">
                <i className="bi bi-pencil-square me-1"></i>Edit Profile
              </a>
              <a href="/orders" className="btn btn-outline-success">
                <i className="bi bi-bag-check me-1"></i>My Orders
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Profile;