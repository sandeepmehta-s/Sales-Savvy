import { Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { cartService } from '../../services/cart';

const ProductCard = ({ product }) => {
  const { user } = useAuth();

  const handleAddToCart = async () => {
    if (!user) {
      alert('Please login to add items to cart');
      return;
    }

    try {
      await cartService.addToCart(user.username, product.id, 1);
      alert('✅ Product added to cart successfully!');
    } catch (error) {
      console.error('Error adding to cart:', error);
      alert('❌ Failed to add product to cart');
    }
  };

  return (
    <div className="card product-card h-100 shadow-sm border-0">
        <Link to={`/products/${product.id}`} className="text-decoration-none text-dark">
          <img
            src={product.photo || '/placeholder-image.jpg'}
            alt={product.name}
            className="card-img-top"
            style={{
              height: '220px',
              objectFit: 'cover',
              borderTopLeftRadius: '0.5rem',
              borderTopRightRadius: '0.5rem',
            }}
          />
          <div className="card-body">
            <h5 className="card-title text-truncate">{product.name}</h5>
            <p className="card-text text-muted mb-1">
              Category: <strong>{product.category}</strong>
            </p>
            <h6 className="text-primary fw-bold">₹{product.price}</h6>
          </div>
        </Link>

        <div className="card-footer bg-transparent border-0 text-center pb-3">
          <button
            onClick={handleAddToCart}
            className={`btn ${user ? 'btn-primary' : 'btn-outline-secondary'} w-75`}
            disabled={!user}
          >
            {user ? 'Add to Cart' : 'Login to Add'}
          </button>
        </div>
    </div>
  );
};

export default ProductCard;