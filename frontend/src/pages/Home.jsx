import { Link } from 'react-router-dom'
import 'bootstrap/dist/css/bootstrap.min.css'

const Home = () => {
  return (
    <div className="home">
      {/* Hero Section */}
      <section 
        className="hero d-flex flex-column justify-content-center align-items-center text-center"
      >
        {/* Overlay for better text contrast */}
        <div 
          className="overlay position-absolute top-0 start-0 w-100 h-100"
          aria-hidden="true"
        ></div>

        {/* Content */}
        <div className="container position-relative" style={{ zIndex: 1 }}>
          <p className="hero-kicker">A sharper way to shop</p>
          <h1 className="display-3 fw-bold mb-3">Find the things that move you forward.</h1>
          <p className="lead mb-4">Thoughtfully selected products, clear prices, and a checkout that gets out of your way.</p>
          <div className="d-flex justify-content-center gap-3 flex-wrap"><Link to="/products" className="btn btn-primary btn-lg px-5 py-2">Browse products</Link><Link to="/register" className="btn btn-outline-light btn-lg px-4 py-2">Create account</Link></div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-5 bg-white">
        <div className="container text-center">
          <div className="row g-4">
            <div className="col-md-4">
              <div className="feature-card p-4">
                <span className="feature-number">01</span>
                <i className="bi bi-grid-3x3-gap display-4 text-primary mb-3"></i>
                <h4>Fast Delivery</h4>
                <p>Get your products delivered quickly and safely to your doorstep.</p>
              </div>
            </div>

            <div className="col-md-4">
              <div className="feature-card p-4">
                <span className="feature-number">02</span>
                <i className="bi bi-shield-check display-4 text-success mb-3"></i>
                <h4>Secure Payments</h4>
                <p>We use Stripe for 100% secure and trusted transactions.</p>
              </div>
            </div>

            <div className="col-md-4">
              <div className="feature-card p-4">
                <span className="feature-number">03</span>
                <i className="bi bi-tags display-4 text-warning mb-3"></i>
                <h4>Best Prices</h4>
                <p>Find amazing deals and discounts on your favorite products.</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  )
}

export default Home;