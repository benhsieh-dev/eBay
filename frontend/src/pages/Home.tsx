import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import './Home.css';

interface Product {
  productId: number;
  title: string;
  currentPrice: number;
  imageUrl?: string;
  description: string;
  category?: {
    categoryName: string;
  };
}

const Home: React.FC = () => {
  const [featuredProducts, setFeaturedProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchFeaturedProducts = async () => {
      try {
        const response = await api.get('/products/featured');
        if (response.data.success) {
          setFeaturedProducts(response.data.products);
        } else {
          throw new Error(response.data.error || 'Failed to fetch featured products');
        }
      } catch (err) {
        console.error('Error fetching featured products:', err);
        setError('Failed to load featured products');
        // Set some mock data for now
        setFeaturedProducts([
          {
            productId: 1,
            title: 'iPhone 14 Pro',
            currentPrice: 999,
            description: 'Latest iPhone with advanced camera system',
            category: { categoryName: 'Electronics' }
          },
          {
            productId: 2,
            title: 'Nike Air Jordan 1',
            currentPrice: 179,
            description: 'Classic basketball sneakers',
            category: { categoryName: 'Fashion' }
          },
          {
            productId: 3,
            title: 'MacBook Pro M3',
            currentPrice: 1999,
            description: 'Powerful laptop for professionals',
            category: { categoryName: 'Electronics' }
          }
        ]);
      } finally {
        setLoading(false);
      }
    };

    fetchFeaturedProducts();
  }, []);

  if (loading) {
    return (
      <div className="home-container">
        <div className="loading">Loading featured products...</div>
      </div>
    );
  }

  return (
    <div className="home-container">
      {/* Hero Section */}
      <section className="hero">
        <div className="hero-content">
          <h1>Find it. Love it. Buy it.</h1>
          <p>Discover amazing deals on millions of items</p>
          <div className="hero-actions">
            <Link to="/products" className="btn btn-primary">Start Shopping</Link>
            <Link to="/sell" className="btn btn-secondary">Start Selling</Link>
          </div>
        </div>
      </section>

      {/* Categories Section */}
      <section className="categories">
        <h2>Shop by Category</h2>
        <div className="category-grid">
          <Link to="/products?category=electronics" className="category-card">
            <div className="category-icon">📱</div>
            <h3>Electronics</h3>
          </Link>
          <Link to="/products?category=fashion" className="category-card">
            <div className="category-icon">👕</div>
            <h3>Fashion</h3>
          </Link>
          <Link to="/products?category=home" className="category-card">
            <div className="category-icon">🏠</div>
            <h3>Home & Garden</h3>
          </Link>
          <Link to="/products?category=sports" className="category-card">
            <div className="category-icon">⚽</div>
            <h3>Sports</h3>
          </Link>
        </div>
      </section>

      {/* Featured Products */}
      <section className="featured-products">
        <h2>Featured Products</h2>
        {error && <div className="error-message">{error}</div>}
        <div className="products-grid">
          {featuredProducts.map(product => (
            <Link 
              key={product.productId} 
              to={`/products/${product.productId}`} 
              className="product-card"
            >
              <div className="product-image">
                {product.imageUrl ? (
                  <img src={product.imageUrl} alt={product.title} />
                ) : (
                  <div className="placeholder-image">📦</div>
                )}
              </div>
              <div className="product-info">
                <h3>{product.title}</h3>
                <p className="product-price">${product.currentPrice.toFixed(2)}</p>
                <p className="product-description">{product.description}</p>
                <span className="product-category">{product.category?.categoryName || 'Uncategorized'}</span>
              </div>
            </Link>
          ))}
        </div>
      </section>
    </div>
  );
};

export default Home;