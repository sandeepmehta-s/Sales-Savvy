/* eslint-disable no-unused-vars */
import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import API from '../../services/api';

const ProductDetail = () => {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadProduct();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const loadProduct = async () => {
    try {
      const response = await API.get(`/products/${id}`);
      setProduct(response.data);
    } catch (error) {
      console.error('Failed to load product');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async () => {
    try {
      await API.post('/cart/add', null, {
        params: { productId: product.id, quantity: 1 },
      });
      alert('✅ Product added to cart!');
    } catch (error) {
      alert('❌ Failed to add product to cart');
    }
  };

  if (loading)
    return (
      <div className="d-flex justify-content-center align-items-center vh-100">
        <div className="spinner-border text-primary" role="status" />
        <span className="ms-2 fs-5">Loading...</span>
      </div>
    );

  if (!product)
    return (
      <div className="text-center mt-5 text-danger fw-bold">
        Product not found!
      </div>
    );

  return (
    <div className="container py-5">
      <div className="row g-4">
        {/* Product Image */}
        <div className="col-md-6 d-flex justify-content-center">
          <img
            src={product.photo || '/placeholder-image.jpg'}
            alt={product.name}
            className="img-fluid rounded shadow-sm"
            style={{ maxHeight: '400px', objectFit: 'cover' }}
          />
        </div>

        {/* Product Details */}
        <div className="col-md-6">
          <h1 className="fw-bold mb-3">{product.name}</h1>
          <p className="text-muted mb-2">Category: {product.category}</p>
          <h3 className="text-primary mb-3">₹{product.price}</h3>

          <p className="text-secondary mb-4" style={{ whiteSpace: 'pre-line' }}>
            {product.description || 'No description available.'}
          </p>

          <button
            className="btn btn-primary btn-lg px-4"
            onClick={handleAddToCart}
          >
            <i className="bi bi-cart-plus me-2"></i> Add to Cart
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;