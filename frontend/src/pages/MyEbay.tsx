import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';

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
}

const MyEbay: React.FC = () => {
  const [myListings, setMyListings] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalCount, setTotalCount] = useState(0);
  const [activeTab, setActiveTab] = useState<'selling' | 'buying' | 'watching'>('selling');
  const navigate = useNavigate();

  const pageSize = 10;

  useEffect(() => {
    fetchMyListings();
  }, [currentPage]);

  const fetchMyListings = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        page: currentPage.toString(),
        size: pageSize.toString(),
      });

      const response = await api.get(`/products/my-listings?${params}`, {
        withCredentials: true
      });
      
      if (response.data.success) {
        setMyListings(response.data.products);
        setTotalCount(response.data.totalCount);
        setError('');
      } else {
        setError(response.data.error || 'Failed to fetch your listings');
      }
    } catch (err: any) {
      if (err.response?.status === 401) {
        setError('Please log in to view your listings');
        navigate('/login');
      } else {
        setError('Failed to fetch your listings. Please try again.');
      }
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

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const formatTimeRemaining = (endTime: string) => {
    const now = new Date();
    const end = new Date(endTime);
    const diff = end.getTime() - now.getTime();
    
    if (diff <= 0) return 'Ended';
    
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    
    if (days > 0) return `${days}d ${hours}h`;
    if (hours > 0) return `${hours}h ${minutes}m`;
    return `${minutes}m`;
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ACTIVE': return '#2e7d32';
      case 'SOLD': return '#1976d2';
      case 'ENDED': return '#d32f2f';
      case 'CANCELLED': return '#757575';
      default: return '#666';
    }
  };

  const totalPages = Math.ceil(totalCount / pageSize);


  return (
    <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '30px' }}>
        <h1>My eBay</h1>
        <button
          onClick={() => navigate('/sell')}
          style={{
            padding: '12px 24px',
            backgroundColor: '#0066cc',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            fontSize: '16px',
            cursor: 'pointer',
            fontWeight: 'bold'
          }}
        >
          + List New Item
        </button>
      </div>

      {/* Navigation Tabs */}
      <div style={{ 
        marginBottom: '30px',
        borderBottom: '2px solid #dee2e6'
      }}>
        <div style={{ display: 'flex', gap: '0' }}>
          <button
            onClick={() => setActiveTab('selling')}
            style={{
              padding: '12px 24px',
              border: 'none',
              backgroundColor: 'transparent',
              cursor: 'pointer',
              fontSize: '16px',
              fontWeight: activeTab === 'selling' ? 'bold' : 'normal',
              color: activeTab === 'selling' ? '#0066cc' : '#666',
              borderBottom: activeTab === 'selling' ? '2px solid #0066cc' : '2px solid transparent',
              marginBottom: '-2px'
            }}
          >
            Selling
          </button>
          <button
            onClick={() => setActiveTab('buying')}
            style={{
              padding: '12px 24px',
              border: 'none',
              backgroundColor: 'transparent',
              cursor: 'pointer',
              fontSize: '16px',
              fontWeight: activeTab === 'buying' ? 'bold' : 'normal',
              color: activeTab === 'buying' ? '#0066cc' : '#666',
              borderBottom: activeTab === 'buying' ? '2px solid #0066cc' : '2px solid transparent',
              marginBottom: '-2px'
            }}
          >
            Buying
          </button>
          <button
            onClick={() => setActiveTab('watching')}
            style={{
              padding: '12px 24px',
              border: 'none',
              backgroundColor: 'transparent',
              cursor: 'pointer',
              fontSize: '16px',
              fontWeight: activeTab === 'watching' ? 'bold' : 'normal',
              color: activeTab === 'watching' ? '#0066cc' : '#666',
              borderBottom: activeTab === 'watching' ? '2px solid #0066cc' : '2px solid transparent',
              marginBottom: '-2px'
            }}
          >
            Watching
          </button>
        </div>
      </div>

      {/* Tab Content */}
      {activeTab === 'selling' && (
        <>
          {/* Summary Stats */}
          <div style={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', 
            gap: '20px', 
            marginBottom: '30px' 
          }}>
            <div style={{ 
              backgroundColor: '#f8f9fa', 
              padding: '20px', 
              borderRadius: '8px', 
              textAlign: 'center',
              border: '1px solid #dee2e6' 
            }}>
              <h3 style={{ margin: '0 0 10px 0', color: '#0066cc' }}>Total Listings</h3>
              <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold' }}>{totalCount}</p>
            </div>
            <div style={{ 
              backgroundColor: '#f8f9fa', 
              padding: '20px', 
              borderRadius: '8px', 
              textAlign: 'center',
              border: '1px solid #dee2e6' 
            }}>
              <h3 style={{ margin: '0 0 10px 0', color: '#2e7d32' }}>Active Listings</h3>
              <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold' }}>
                {myListings.filter(p => p.status === 'ACTIVE').length}
              </p>
            </div>
            <div style={{ 
              backgroundColor: '#f8f9fa', 
              padding: '20px', 
              borderRadius: '8px', 
              textAlign: 'center',
              border: '1px solid #dee2e6' 
            }}>
              <h3 style={{ margin: '0 0 10px 0', color: '#1976d2' }}>Sold Items</h3>
              <p style={{ margin: 0, fontSize: '24px', fontWeight: 'bold' }}>
                {myListings.filter(p => p.status === 'SOLD').length}
              </p>
            </div>
          </div>

          {/* Error Message */}
          {error && (
            <div style={{ 
              color: 'red', 
              marginBottom: '20px', 
              padding: '15px', 
              backgroundColor: '#ffe6e6', 
              borderRadius: '4px',
              border: '1px solid #ffcccc'
            }}>
              {error}
            </div>
          )}

          {/* Loading */}
          {loading && (
            <div style={{ textAlign: 'center', padding: '40px' }}>
              <div style={{ fontSize: '18px', color: '#666' }}>Loading your listings...</div>
            </div>
          )}

          {/* Listings Table */}
          {!loading && myListings.length > 0 && (
            <div style={{ backgroundColor: 'white', borderRadius: '8px', overflow: 'hidden', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
              <div style={{ overflowX: 'auto' }}>
                <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                  <thead>
                    <tr style={{ backgroundColor: '#f8f9fa', borderBottom: '2px solid #dee2e6' }}>
                      <th style={{ padding: '15px', textAlign: 'left', fontWeight: 'bold', color: '#495057' }}>Item</th>
                      <th style={{ padding: '15px', textAlign: 'left', fontWeight: 'bold', color: '#495057' }}>Price</th>
                      <th style={{ padding: '15px', textAlign: 'left', fontWeight: 'bold', color: '#495057' }}>Type</th>
                      <th style={{ padding: '15px', textAlign: 'left', fontWeight: 'bold', color: '#495057' }}>Status</th>
                      <th style={{ padding: '15px', textAlign: 'left', fontWeight: 'bold', color: '#495057' }}>Time Remaining</th>
                      <th style={{ padding: '15px', textAlign: 'left', fontWeight: 'bold', color: '#495057' }}>Created</th>
                    </tr>
                  </thead>
                  <tbody>
                    {myListings.map((product, index) => (
                      <tr 
                        key={product.productId}
                        style={{ 
                          borderBottom: '1px solid #dee2e6',
                          backgroundColor: index % 2 === 0 ? '#ffffff' : '#f8f9fa',
                          cursor: 'pointer'
                        }}
                        onClick={() => navigate(`/products/${product.productId}`)}
                        onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#e3f2fd'}
                        onMouseLeave={(e) => e.currentTarget.style.backgroundColor = index % 2 === 0 ? '#ffffff' : '#f8f9fa'}
                      >
                        <td style={{ padding: '15px' }}>
                          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                            <div style={{ 
                              width: '60px', 
                              height: '60px', 
                              backgroundColor: '#f0f0f0', 
                              borderRadius: '4px',
                              display: 'flex',
                              alignItems: 'center',
                              justifyContent: 'center',
                              flexShrink: 0
                            }}>
                              {product.imageUrl ? (
                                <img
                                  src={product.imageUrl}
                                  alt={product.title}
                                  style={{ maxWidth: '100%', maxHeight: '100%', objectFit: 'contain' }}
                                />
                              ) : (
                                <span style={{ color: '#666', fontSize: '12px' }}>No Image</span>
                              )}
                            </div>
                            <div>
                              <div style={{ fontWeight: 'bold', marginBottom: '4px', lineHeight: 1.4 }}>
                                {product.title}
                              </div>
                              <div style={{ fontSize: '12px', color: '#666' }}>
                                Condition: {product.condition}
                              </div>
                            </div>
                          </div>
                        </td>
                        <td style={{ padding: '15px' }}>
                          <div style={{ fontWeight: 'bold', color: '#0066cc' }}>
                            {formatPrice(product.currentPrice)}
                          </div>
                          {product.buyNowPrice && product.buyNowPrice !== product.currentPrice && (
                            <div style={{ fontSize: '12px', color: '#666' }}>
                              Buy Now: {formatPrice(product.buyNowPrice)}
                            </div>
                          )}
                        </td>
                        <td style={{ padding: '15px' }}>
                          <span style={{ 
                            fontSize: '12px',
                            padding: '4px 8px',
                            borderRadius: '12px',
                            backgroundColor: product.listingType === 'AUCTION' ? '#e3f2fd' : '#e8f5e8',
                            color: product.listingType === 'AUCTION' ? '#1976d2' : '#2e7d32'
                          }}>
                            {product.listingType === 'BUY_NOW' ? 'Buy It Now' : 
                             product.listingType === 'AUCTION' ? 'Auction' : 'Auction + BIN'}
                          </span>
                        </td>
                        <td style={{ padding: '15px' }}>
                          <span style={{ 
                            fontWeight: 'bold',
                            color: getStatusColor(product.status)
                          }}>
                            {product.status}
                          </span>
                        </td>
                        <td style={{ padding: '15px' }}>
                          {product.listingType !== 'BUY_NOW' && product.endTime ? (
                            <span style={{ 
                              fontSize: '14px',
                              color: product.status === 'ACTIVE' ? '#d32f2f' : '#666'
                            }}>
                              {formatTimeRemaining(product.endTime)}
                            </span>
                          ) : (
                            <span style={{ fontSize: '14px', color: '#666' }}>N/A</span>
                          )}
                        </td>
                        <td style={{ padding: '15px', fontSize: '14px', color: '#666' }}>
                          {formatDate(product.createdDate)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}

          {/* No Listings */}
          {!loading && myListings.length === 0 && !error && (
            <div style={{ 
              textAlign: 'center', 
              padding: '60px 20px',
              backgroundColor: 'white',
              borderRadius: '8px',
              boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}>
              <div style={{ fontSize: '24px', marginBottom: '16px' }}>📦</div>
              <h3 style={{ color: '#666', marginBottom: '16px' }}>No listings yet</h3>
              <p style={{ color: '#666', marginBottom: '24px' }}>
                You haven't listed any items for sale yet. Start selling by creating your first listing!
              </p>
              <button
                onClick={() => navigate('/sell')}
                style={{
                  padding: '12px 24px',
                  backgroundColor: '#0066cc',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  fontSize: '16px',
                  cursor: 'pointer',
                  fontWeight: 'bold'
                }}
              >
                List Your First Item
              </button>
            </div>
          )}

          {/* Pagination */}
          {totalPages > 1 && (
            <div style={{ 
              display: 'flex', 
              justifyContent: 'center', 
              alignItems: 'center', 
              gap: '10px', 
              marginTop: '30px' 
            }}>
              <button
                onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                disabled={currentPage === 0}
                style={{
                  padding: '8px 12px',
                  border: '1px solid #ddd',
                  backgroundColor: currentPage === 0 ? '#f5f5f5' : 'white',
                  cursor: currentPage === 0 ? 'not-allowed' : 'pointer',
                  borderRadius: '4px'
                }}
              >
                Previous
              </button>
              
              <span style={{ padding: '0 15px' }}>
                Page {currentPage + 1} of {totalPages}
              </span>
              
              <button
                onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                disabled={currentPage === totalPages - 1}
                style={{
                  padding: '8px 12px',
                  border: '1px solid #ddd',
                  backgroundColor: currentPage === totalPages - 1 ? '#f5f5f5' : 'white',
                  cursor: currentPage === totalPages - 1 ? 'not-allowed' : 'pointer',
                  borderRadius: '4px'
                }}
              >
                Next
              </button>
            </div>
          )}
        </>
      )}

      {activeTab === 'buying' && (
        <div style={{ 
          textAlign: 'center', 
          padding: '60px 20px',
          backgroundColor: 'white',
          borderRadius: '8px',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
        }}>
          <div style={{ fontSize: '24px', marginBottom: '16px' }}>🛒</div>
          <h3 style={{ color: '#666', marginBottom: '16px' }}>Buying Features Coming Soon</h3>
          <p style={{ color: '#666' }}>
            Track your purchases, bids, and order history here.
          </p>
        </div>
      )}

      {activeTab === 'watching' && (
        <div style={{ 
          textAlign: 'center', 
          padding: '60px 20px',
          backgroundColor: 'white',
          borderRadius: '8px',
          boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
        }}>
          <div style={{ fontSize: '24px', marginBottom: '16px' }}>👀</div>
          <h3 style={{ color: '#666', marginBottom: '16px' }}>Watchlist Coming Soon</h3>
          <p style={{ color: '#666' }}>
            Keep track of items you're interested in bidding on or buying.
          </p>
        </div>
      )}

    </div>
  );
};

export default MyEbay;