import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../services/api';

interface Product {
  productId: number;
  title: string;
  description: string;
  currentPrice: number;
  buyNowPrice?: number;
  imageUrl?: string;
  endTime?: string;
  condition: string;
  listingType: 'AUCTION' | 'BUY_NOW' | 'AUCTION_WITH_BUY_NOW';
  status: string;
  sellerId: number;
  createdDate: string;
  itemLocation?: string;
  shippingCost?: number;
  category?: {
    categoryId: number;
    categoryName: string;
  };
  seller?: {
    userId: number;
    username: string;
    firstName?: string;
    lastName?: string;
  };
}

const ProductDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (id) {
      fetchProduct(parseInt(id));
    }
  }, [id]);

  const fetchProduct = async (productId: number) => {
    setLoading(true);
    try {
      const response = await api.get(`/products/${productId}`);
      if (response.data.success) {
        setProduct(response.data.product);
        setError('');
      } else {
        setError(response.data.error || 'Product not found');
      }
    } catch (err: any) {
      setError('Product not found or failed to load');
      console.error('Failed to fetch product:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatPrice = (price: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  };

  const formatTimeRemaining = (endTime: string) => {
    const now = new Date();
    const end = new Date(endTime);
    const diff = end.getTime() - now.getTime();
    
    if (diff <= 0) return 'Auction ended';
    
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    
    if (days > 0) return `${days} day${days > 1 ? 's' : ''} ${hours} hour${hours > 1 ? 's' : ''}`;
    if (hours > 0) return `${hours} hour${hours > 1 ? 's' : ''} ${minutes} minute${minutes > 1 ? 's' : ''}`;
    return `${minutes} minute${minutes > 1 ? 's' : ''}`;
  };

  if (loading) {
    return (
      <div style={{ 
        padding: '40px', 
        textAlign: 'center',
        maxWidth: '1200px',
        margin: '0 auto'
      }}>
        <div style={{ fontSize: '18px', color: '#666' }}>Loading product...</div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div style={{ 
        padding: '40px', 
        textAlign: 'center',
        maxWidth: '1200px',
        margin: '0 auto'
      }}>
        <div style={{ fontSize: '18px', color: '#d32f2f', marginBottom: '20px' }}>
          {error || 'Product not found'}
        </div>
        <button
          onClick={() => navigate('/products')}
          style={{
            padding: '12px 24px',
            backgroundColor: '#0066cc',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Back to Browse
        </button>
      </div>
    );
  }

  return (
    <div style={{ 
      maxWidth: '1200px', 
      margin: '0 auto', 
      padding: '20px'
    }}>
      {/* Breadcrumb */}
      <div style={{ 
        marginBottom: '20px', 
        fontSize: '14px', 
        color: '#666' 
      }}>
        <button
          onClick={() => navigate('/products')}
          style={{
            background: 'none',
            border: 'none',
            color: '#0066cc',
            cursor: 'pointer',
            textDecoration: 'underline'
          }}
        >
          ← Back to Browse
        </button>
        {product.category && (
          <span> / {product.category.categoryName}</span>
        )}
      </div>

      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: '1fr 1fr', 
        gap: '40px',
        alignItems: 'start'
      }}>
        {/* Product Image */}
        <div style={{
          backgroundColor: '#f8f9fa',
          borderRadius: '8px',
          padding: '20px',
          textAlign: 'center',
          minHeight: '400px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}>
          {product.imageUrl ? (
            <img
              src={product.imageUrl}
              alt={product.title}
              style={{
                maxWidth: '100%',
                maxHeight: '500px',
                objectFit: 'contain'
              }}
            />
          ) : (
            <div style={{ 
              color: '#666',
              fontSize: '18px'
            }}>
              No Image Available
            </div>
          )}
        </div>

        {/* Product Info */}
        <div>
          <h1 style={{ 
            fontSize: '28px', 
            marginBottom: '16px',
            lineHeight: 1.3
          }}>
            {product.title}
          </h1>

          <div style={{ 
            fontSize: '14px', 
            color: '#666',
            marginBottom: '20px'
          }}>
            Condition: <strong>{product.condition}</strong>
            {product.itemLocation && (
              <span> • Item location: {product.itemLocation}</span>
            )}
          </div>

          {/* Price Section */}
          <div style={{ 
            backgroundColor: '#f8f9fa',
            padding: '20px',
            borderRadius: '8px',
            marginBottom: '20px'
          }}>
            <div style={{ 
              fontSize: '32px', 
              fontWeight: 'bold',
              color: '#0066cc',
              marginBottom: '8px'
            }}>
              {formatPrice(product.currentPrice)}
            </div>

            {product.buyNowPrice && product.buyNowPrice !== product.currentPrice && (
              <div style={{ 
                fontSize: '18px', 
                color: '#666',
                marginBottom: '8px'
              }}>
                Buy It Now: {formatPrice(product.buyNowPrice)}
              </div>
            )}

            {product.shippingCost !== undefined && (
              <div style={{ 
                fontSize: '14px', 
                color: '#666'
              }}>
                + {product.shippingCost === 0 ? 'Free shipping' : `${formatPrice(product.shippingCost)} shipping`}
              </div>
            )}
          </div>

          {/* Listing Type & Time */}
          <div style={{ marginBottom: '20px' }}>
            <div style={{ 
              fontSize: '16px',
              fontWeight: 'bold',
              marginBottom: '8px'
            }}>
              {product.listingType === 'BUY_NOW' ? 'Buy It Now' : 
               product.listingType === 'AUCTION' ? 'Auction' : 'Auction with Buy It Now'}
            </div>

            {product.listingType !== 'BUY_NOW' && product.endTime && (
              <div style={{ 
                fontSize: '14px',
                color: product.status === 'ACTIVE' ? '#d32f2f' : '#666'
              }}>
                {formatTimeRemaining(product.endTime)} remaining
              </div>
            )}
          </div>

          {/* Action Buttons */}
          <div style={{ 
            display: 'flex', 
            gap: '12px',
            marginBottom: '30px'
          }}>
            {product.listingType === 'BUY_NOW' || product.buyNowPrice ? (
              <button
                style={{
                  flex: 1,
                  padding: '16px',
                  backgroundColor: '#0066cc',
                  color: 'white',
                  border: 'none',
                  borderRadius: '8px',
                  fontSize: '16px',
                  fontWeight: 'bold',
                  cursor: 'pointer'
                }}
                onClick={() => alert('Buy It Now functionality coming soon!')}
              >
                Buy It Now
              </button>
            ) : null}

            {product.listingType === 'AUCTION' && product.status === 'ACTIVE' && (
              <button
                style={{
                  flex: 1,
                  padding: '16px',
                  backgroundColor: '#2e7d32',
                  color: 'white',
                  border: 'none',
                  borderRadius: '8px',
                  fontSize: '16px',
                  fontWeight: 'bold',
                  cursor: 'pointer'
                }}
                onClick={() => alert('Bidding functionality coming soon!')}
              >
                Place Bid
              </button>
            )}

            <button
              style={{
                padding: '16px 24px',
                backgroundColor: '#f8f9fa',
                color: '#333',
                border: '1px solid #dee2e6',
                borderRadius: '8px',
                fontSize: '16px',
                cursor: 'pointer'
              }}
              onClick={() => alert('Add to watchlist functionality coming soon!')}
            >
              Add to Watchlist
            </button>
          </div>

          {/* Seller Information */}
          {product.seller && (
            <div style={{ marginBottom: '30px' }}>
              <h3 style={{ marginBottom: '16px' }}>Seller Information</h3>
              <div style={{ 
                backgroundColor: '#f8f9fa',
                padding: '20px',
                borderRadius: '8px',
                border: '1px solid #dee2e6'
              }}>
                <div style={{ fontSize: '16px', fontWeight: 'bold', marginBottom: '8px' }}>
                  {product.seller.firstName && product.seller.lastName 
                    ? `${product.seller.firstName} ${product.seller.lastName}` 
                    : product.seller.username}
                </div>
                <div style={{ fontSize: '14px', color: '#666' }}>
                  Username: {product.seller.username}
                </div>
              </div>
            </div>
          )}

          {/* Description */}
          <div>
            <h3 style={{ marginBottom: '16px' }}>Description</h3>
            <div style={{ 
              lineHeight: 1.6,
              color: '#333',
              backgroundColor: '#f8f9fa',
              padding: '20px',
              borderRadius: '8px',
              whiteSpace: 'pre-wrap'
            }}>
              {product.description}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetail;