import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import api from '../../services/api';

interface Product {
  productId: number;
  title: string;
  description: string;
  currentPrice: number;
  startingPrice: number;
  buyNowPrice?: number;
  reservePrice?: number;
  imageUrl?: string;
  endTime?: string;
  startTime?: string;
  condition: string;
  listingType: 'AUCTION' | 'BUY_NOW' | 'BOTH';
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

interface BidInfo {
  currentPrice: number;
  startingPrice: number;
  buyNowPrice?: number;
  reservePrice?: number;
  highestBidAmount: number;
  bidCount: number;
  minNextBid: number;
  timeRemaining: string;
  timeRemainingMillis: number;
  isAuctionActive: boolean;
  isAuctionEnded: boolean;
  isBuyNowAvailable: boolean;
  isAuction: boolean;
  listingType: string;
  status: string;
  userHasBid: boolean;
  userIsWinning: boolean;
  userLastBid?: {
    bidId: number;
    bidAmount: number;
    bidTime: string;
    isWinningBid: boolean;
  };
  winningBidder?: string;
  finalPrice?: number;
}

interface Bid {
  bidId: number;
  bidAmount: number;
  bidTime: string;
  bidType: string;
  bidStatus: string;
  isWinningBid: boolean;
  bidder: {
    userId: number;
    username: string;
  };
  product: {
    productId: number;
    title: string;
    currentPrice: number;
    status: string;
    isAuctionActive: boolean;
    timeRemaining: string;
  };
}

interface User {
  userId: number;
  username: string;
  firstName?: string;
  lastName?: string;
}

const ProductDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [showImageUpload, setShowImageUpload] = useState(false);
  const [uploadingImages, setUploadingImages] = useState(false);
  const [bidInfo, setBidInfo] = useState<BidInfo | null>(null);
  const [showBidModal, setShowBidModal] = useState(false);
  const [bidAmount, setBidAmount] = useState('');
  const [maxProxyAmount, setMaxProxyAmount] = useState('');
  const [useProxyBidding, setUseProxyBidding] = useState(false);
  const [placingBid, setPlacingBid] = useState(false);
  const [showBidHistory, setShowBidHistory] = useState(false);
  const [bidHistory, setBidHistory] = useState<Bid[]>([]);
  const [loadingBidHistory, setLoadingBidHistory] = useState(false);
  const [isInWatchlist, setIsInWatchlist] = useState(false);
  const [watchlistLoading, setWatchlistLoading] = useState(false);
  const [watchersCount, setWatchersCount] = useState(0);

  useEffect(() => {
    if (id) {
      fetchProduct(parseInt(id));
    }
    checkAuthStatus();
  }, [id]);

  const checkAuthStatus = async () => {
    try {
      const response = await api.get('/user/current', {
        withCredentials: true
      });
      
      if (response.data.success && response.data.authenticated) {
        setCurrentUser(response.data.user);
      }
    } catch (error) {
      setCurrentUser(null);
    }
  };

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

  const handleImageUpload = async (files: FileList | null) => {
    if (!files || files.length === 0 || !product) return;

    setUploadingImages(true);
    try {
      const formData = new FormData();
      Array.from(files).forEach(file => {
        formData.append('images', file);
      });

      const response = await api.post(`/products/${product.productId}/images`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
        withCredentials: true
      });

      if (response.data.success) {
        // Refresh the product to show updated images
        fetchProduct(product.productId);
        setShowImageUpload(false);
        toast.success('Images uploaded successfully!');
      } else {
        toast.error(response.data.error || 'Failed to upload images');
      }
    } catch (err: any) {
      toast.error('Failed to upload images. Please try again.');
      console.error('Image upload error:', err);
    } finally {
      setUploadingImages(false);
    }
  };

  const fetchBidInfo = async (productId: number) => {
    try {
      const response = await api.get(`/bids/product/${productId}/info`, {
        withCredentials: true
      });
      if (response.data.success) {
        setBidInfo(response.data);
      }
    } catch (err) {
      console.error('Failed to fetch bid info:', err);
    }
  };

  const fetchBidHistory = async (productId: number) => {
    setLoadingBidHistory(true);
    try {
      const response = await api.get(`/bids/history/${productId}`, {
        withCredentials: true
      });
      if (response.data.success) {
        setBidHistory(response.data.bids);
      }
    } catch (err) {
      console.error('Failed to fetch bid history:', err);
    } finally {
      setLoadingBidHistory(false);
    }
  };

  const handlePlaceBid = async () => {
    if (!product || !currentUser || !bidAmount) return;

    if (!currentUser) {
      alert('Please log in to place a bid.');
      return;
    }

    if (isOwner) {
      alert('You cannot bid on your own item.');
      return;
    }

    const bidAmountNum = parseFloat(bidAmount);
    if (isNaN(bidAmountNum) || bidAmountNum <= 0) {
      alert('Please enter a valid bid amount.');
      return;
    }

    if (useProxyBidding && maxProxyAmount) {
      const maxProxyAmountNum = parseFloat(maxProxyAmount);
      if (isNaN(maxProxyAmountNum) || maxProxyAmountNum < bidAmountNum) {
        alert('Maximum proxy amount must be greater than or equal to bid amount.');
        return;
      }
    }

    setPlacingBid(true);
    try {
      const bidRequest = {
        productId: product.productId,
        bidAmount: bidAmountNum,
        bidType: useProxyBidding ? 'PROXY' : 'REGULAR',
        maxProxyAmount: useProxyBidding && maxProxyAmount ? parseFloat(maxProxyAmount) : null
      };

      const response = await api.post('/bids/place', bidRequest, {
        withCredentials: true
      });

      if (response.data.success) {
        alert(response.data.message);
        setShowBidModal(false);
        setBidAmount('');
        setMaxProxyAmount('');
        setUseProxyBidding(false);
        try {
            // Refresh product and bid info
            await fetchProduct(product.productId);
            await fetchBidInfo(product.productId);
        } catch (refreshError) {
            console.error('Failed to refresh product and bid info:', refreshError);
        }
      } else {
        alert(response.data.error || 'Failed to place bid');
      }
    } catch (err: any) {
      alert(err.response?.data?.error || 'Failed to place bid. Please try again.');
      console.error('Bid placement error:', err);
    } finally {
      setPlacingBid(false);
    }
  };

  const handleBuyNow = async () => {
    if (!product || !currentUser) return;

    if (!currentUser) {
      alert('Please log in to buy this item.');
      return;
    }

    if (isOwner) {
      alert('You cannot buy your own item.');
      return;
    }

    if (window.confirm(`Are you sure you want to buy this item for ${formatPrice(product.buyNowPrice || product.currentPrice)}?`)) {
      setPlacingBid(true);
      try {
        const response = await api.post('/bids/buy-now', {
          productId: product.productId
        }, {
          withCredentials: true
        });

        if (response.data.success) {
          alert(response.data.message);
          if (response.data.redirect) {
            navigate(response.data.redirect);
          } else {
            // Refresh product info
            fetchProduct(product.productId);
            fetchBidInfo(product.productId);
          }
        } else {
          alert(response.data.error || 'Failed to complete purchase');
        }
      } catch (err: any) {
        alert(err.response?.data?.error || 'Failed to complete purchase. Please try again.');
        console.error('Buy now error:', err);
      } finally {
        setPlacingBid(false);
      }
    }
  };

  const openBidModal = () => {
    if (!currentUser) {
      alert('Please log in to place a bid.');
      return;
    }
    
    if (isOwner) {
      alert('You cannot bid on your own item.');
      return;
    }

    // Fetch current bid info when opening modal
    fetchBidInfo(product!.productId);
    setShowBidModal(true);
  };

  const openBidHistory = () => {
    if (product) {
      fetchBidHistory(product.productId);
      setShowBidHistory(true);
    }
  };

  const checkWatchlistStatus = useCallback(async (productId: number) => {
    if (!currentUser) {
      setIsInWatchlist(false);
      return;
    }

    try {
      const response = await api.get(`/watchlist/check/${productId}`, {
        withCredentials: true
      });
      
      setIsInWatchlist(response.data.inWatchlist || false);
    } catch (error) {
      console.error('Failed to check watchlist status:', error);
      setIsInWatchlist(false);
    }
  }, [currentUser]);

  const fetchWatchersCount = useCallback(async (productId: number) => {
    try {
      const response = await api.get(`/watchlist/watchers/${productId}`, {
        withCredentials: true
      });
      
      setWatchersCount(response.data.watchersCount || 0);
    } catch (error) {
      console.error('Failed to fetch watchers count:', error);
      setWatchersCount(0);
    }
  }, []);

  // Auto-refresh bid info for auctions
  useEffect(() => {
    if (product && (product.listingType === 'AUCTION' || product.listingType === 'BOTH') && product.status === 'ACTIVE') {
      fetchBidInfo(product.productId);
      
      // Set up auto-refresh every 30 seconds for active auctions
      const interval = setInterval(() => {
        fetchBidInfo(product.productId);
      }, 30000);

      return () => clearInterval(interval);
    }
  }, [product]);

  // Check watchlist status and watchers count
  useEffect(() => {
    if (product) {
      checkWatchlistStatus(product.productId);
      fetchWatchersCount(product.productId);
    }
  }, [product, currentUser, checkWatchlistStatus, fetchWatchersCount]);

  const handleToggleWatchlist = async () => {
    if (!currentUser) {
      alert('Please log in to add items to your watchlist.');
      return;
    }

    if (isOwner) {
      alert('You cannot add your own item to watchlist.');
      return;
    }

    setWatchlistLoading(true);
    try {
      const response = await api.post('/watchlist/toggle', {
        productId: product?.productId
      }, {
        withCredentials: true
      });

      if (response.data.success) {
        setIsInWatchlist(response.data.inWatchlist);
        // Refresh watchers count
        if (product) {
          fetchWatchersCount(product.productId);
        }
        
        // Show success message
        const message = response.data.inWatchlist 
          ? 'Item added to your watchlist!' 
          : 'Item removed from your watchlist!';
        alert(message);
      } else {
        alert(response.data.error || 'Failed to update watchlist');
      }
    } catch (err: any) {
      alert(err.response?.data?.error || 'Failed to update watchlist. Please try again.');
      console.error('Watchlist toggle error:', err);
    } finally {
      setWatchlistLoading(false);
    }
  };

  const isOwner = currentUser && product?.seller && currentUser.userId === product.seller.userId;

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
        <div>
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

          {/* Image Management for Sellers */}
          {isOwner && (
            <div style={{
              marginTop: '20px',
              padding: '20px',
              backgroundColor: '#e3f2fd',
              borderRadius: '8px',
              border: '2px dashed #0066cc'
            }}>
              <h4 style={{ 
                margin: '0 0 16px 0', 
                color: '#0066cc',
                fontSize: '16px'
              }}>
                Manage Your Listing Images
              </h4>
              <p style={{ 
                margin: '0 0 16px 0', 
                fontSize: '14px',
                color: '#666'
              }}>
                {product.imageUrl 
                  ? 'Add more images to showcase your item better.'
                  : 'Add images to make your listing more attractive to buyers.'}
              </p>
              
              {!showImageUpload ? (
                <button
                  onClick={() => setShowImageUpload(true)}
                  style={{
                    padding: '12px 24px',
                    backgroundColor: product.imageUrl ? '#28a745' : '#ffc107',
                    color: 'white',
                    border: 'none',
                    borderRadius: '6px',
                    fontSize: '14px',
                    cursor: 'pointer',
                    fontWeight: 'bold'
                  }}
                >
                  {product.imageUrl ? '📷 Add More Images' : '📷 Add Images'}
                </button>
              ) : (
                <div>
                  <input
                    type="file"
                    accept="image/*"
                    multiple
                    onChange={(e) => handleImageUpload(e.target.files)}
                    disabled={uploadingImages}
                    style={{
                      width: '100%',
                      padding: '12px',
                      border: '2px dashed #0066cc',
                      borderRadius: '4px',
                      backgroundColor: uploadingImages ? '#f5f5f5' : '#ffffff',
                      cursor: uploadingImages ? 'not-allowed' : 'pointer',
                      marginBottom: '12px'
                    }}
                  />
                  
                  {uploadingImages && (
                    <div style={{
                      textAlign: 'center',
                      padding: '12px',
                      color: '#666',
                      fontSize: '14px'
                    }}>
                      📤 Uploading images...
                    </div>
                  )}
                  
                  <div style={{ display: 'flex', gap: '8px' }}>
                    <button
                      onClick={() => setShowImageUpload(false)}
                      disabled={uploadingImages}
                      style={{
                        padding: '8px 16px',
                        border: '1px solid #ddd',
                        backgroundColor: 'white',
                        borderRadius: '4px',
                        cursor: uploadingImages ? 'not-allowed' : 'pointer',
                        color: uploadingImages ? '#999' : '#666',
                        fontSize: '14px'
                      }}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              )}
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
              {formatPrice(bidInfo?.currentPrice || product.currentPrice)}
            </div>

            {/* Bid Information for Auctions */}
            {(product.listingType === 'AUCTION' || product.listingType === 'BOTH') && bidInfo && (
              <div style={{ marginBottom: '12px' }}>
                <div style={{ fontSize: '14px', color: '#666', marginBottom: '4px' }}>
                  {bidInfo.bidCount > 0 ? (
                    <>
                      {bidInfo.bidCount} bid{bidInfo.bidCount > 1 ? 's' : ''} • 
                      Minimum bid: {formatPrice(bidInfo.minNextBid)}
                    </>
                  ) : (
                    <>Starting bid: {formatPrice(bidInfo.startingPrice)}</>
                  )}
                </div>
                
                {bidInfo.userHasBid && (
                  <div style={{ 
                    fontSize: '14px', 
                    color: bidInfo.userIsWinning ? '#2e7d32' : '#f57c00',
                    fontWeight: 'bold'
                  }}>
                    {bidInfo.userIsWinning ? '🎉 You are winning this auction!' : '⚠️ You have been outbid'}
                  </div>
                )}

                {bidInfo.isAuctionEnded && bidInfo.winningBidder && (
                  <div style={{ 
                    fontSize: '14px', 
                    color: '#666',
                    fontWeight: 'bold'
                  }}>
                    Winner: {bidInfo.winningBidder} • Final price: {formatPrice(bidInfo.finalPrice || bidInfo.currentPrice)}
                  </div>
                )}

                <button
                  onClick={openBidHistory}
                  style={{
                    marginTop: '8px',
                    padding: '4px 8px',
                    background: 'none',
                    border: 'none',
                    color: '#0066cc',
                    cursor: 'pointer',
                    fontSize: '12px',
                    textDecoration: 'underline'
                  }}
                >
                  View bid history ({bidInfo.bidCount})
                </button>
              </div>
            )}

            {product.buyNowPrice && product.buyNowPrice !== (bidInfo?.currentPrice || product.currentPrice) && (
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
            {!isOwner && (product.listingType === 'BUY_NOW' || product.buyNowPrice) && product.status === 'ACTIVE' && (
              <button
                style={{
                  flex: 1,
                  padding: '16px',
                  backgroundColor: placingBid ? '#999' : '#0066cc',
                  color: 'white',
                  border: 'none',
                  borderRadius: '8px',
                  fontSize: '16px',
                  fontWeight: 'bold',
                  cursor: placingBid ? 'not-allowed' : 'pointer'
                }}
                onClick={handleBuyNow}
                disabled={placingBid}
              >
                {placingBid ? 'Processing...' : 'Buy It Now'}
              </button>
            )}

            {!isOwner && (product.listingType === 'AUCTION' || product.listingType === 'BOTH') && 
             product.status === 'ACTIVE' && bidInfo?.isAuctionActive && (
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
                onClick={openBidModal}
              >
                Place Bid
              </button>
            )}

            {!isOwner && (
              <button
                style={{
                  padding: '16px 24px',
                  backgroundColor: watchlistLoading ? '#999' : (isInWatchlist ? '#dc3545' : '#f8f9fa'),
                  color: watchlistLoading ? 'white' : (isInWatchlist ? 'white' : '#333'),
                  border: isInWatchlist ? 'none' : '1px solid #dee2e6',
                  borderRadius: '8px',
                  fontSize: '16px',
                  cursor: watchlistLoading ? 'not-allowed' : 'pointer',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px'
                }}
                onClick={handleToggleWatchlist}
                disabled={watchlistLoading}
              >
                {watchlistLoading ? (
                  <>Processing...</>
                ) : isInWatchlist ? (
                  <>Remove from Watchlist</>
                ) : (
                  <>Add to Watchlist</>
                )}
              </button>
            )}

            {isOwner && (
              <div style={{
                flex: 1,
                padding: '16px',
                backgroundColor: '#e3f2fd',
                borderRadius: '8px',
                border: '2px dashed #0066cc',
                textAlign: 'center'
              }}>
                <div style={{ fontSize: '16px', fontWeight: 'bold', color: '#0066cc', marginBottom: '4px' }}>
                  Your Listing
                </div>
                <div style={{ fontSize: '14px', color: '#666' }}>
                  {product.status === 'ACTIVE' ? 'Currently active and accepting bids' : `Status: ${product.status}`}
                </div>
              </div>
            )}
          </div>

          {/* Watchlist Information */}
          {watchersCount > 0 && (
            <div style={{ marginBottom: '20px' }}>
              <div style={{ 
                backgroundColor: '#fff3e0',
                padding: '16px',
                borderRadius: '8px',
                border: '1px solid #ffcc80',
                display: 'flex',
                alignItems: 'center',
                gap: '8px'
              }}>
                <span style={{ fontSize: '18px' }}>👀</span>
                <div style={{ fontSize: '14px', color: '#e65100' }}>
                  <strong>{watchersCount}</strong> {watchersCount === 1 ? 'person is' : 'people are'} watching this item
                </div>
              </div>
            </div>
          )}

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

      {/* Bid Modal */}
      {showBidModal && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000
        }}>
          <div style={{
            backgroundColor: 'white',
            borderRadius: '8px',
            padding: '24px',
            maxWidth: '500px',
            width: '90%',
            maxHeight: '80vh',
            overflow: 'auto'
          }}>
            <h2 style={{ fontSize: '24px', marginBottom: '20px', textAlign: 'center' }}>
              Place Your Bid
            </h2>

            <div style={{ marginBottom: '20px', textAlign: 'center' }}>
              <div style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>
                {product?.title}
              </div>
              {bidInfo && (
                <div style={{ fontSize: '14px', color: '#666' }}>
                  Current price: {formatPrice(bidInfo.currentPrice)} • 
                  Minimum bid: {formatPrice(bidInfo.minNextBid)}
                </div>
              )}
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>
                Your bid amount:
              </label>
              <input
                type="number"
                value={bidAmount}
                onChange={(e) => setBidAmount(e.target.value)}
                placeholder={bidInfo ? `Minimum: ${bidInfo.minNextBid}` : 'Enter bid amount'}
                style={{
                  width: '100%',
                  padding: '12px',
                  border: '1px solid #ddd',
                  borderRadius: '4px',
                  fontSize: '16px'
                }}
              />
            </div>

            <div style={{ marginBottom: '20px' }}>
              <label style={{ display: 'flex', alignItems: 'center', marginBottom: '12px' }}>
                <input
                  type="checkbox"
                  checked={useProxyBidding}
                  onChange={(e) => setUseProxyBidding(e.target.checked)}
                  style={{ marginRight: '8px' }}
                />
                Use proxy bidding (automatic bidding up to a maximum amount)
              </label>

              {useProxyBidding && (
                <div>
                  <label style={{ display: 'block', marginBottom: '8px', fontWeight: 'bold' }}>
                    Maximum bid amount:
                  </label>
                  <input
                    type="number"
                    value={maxProxyAmount}
                    onChange={(e) => setMaxProxyAmount(e.target.value)}
                    placeholder="Maximum amount you're willing to bid"
                    style={{
                      width: '100%',
                      padding: '12px',
                      border: '1px solid #ddd',
                      borderRadius: '4px',
                      fontSize: '16px'
                    }}
                  />
                  <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>
                    The system will automatically bid for you up to this amount to help you win the auction.
                  </div>
                </div>
              )}
            </div>

            <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
              <button
                onClick={() => setShowBidModal(false)}
                disabled={placingBid}
                style={{
                  padding: '12px 24px',
                  border: '1px solid #ddd',
                  backgroundColor: 'white',
                  borderRadius: '4px',
                  cursor: placingBid ? 'not-allowed' : 'pointer',
                  fontSize: '16px',
                  color: placingBid ? '#999' : '#666'
                }}
              >
                Cancel
              </button>
              <button
                onClick={handlePlaceBid}
                disabled={placingBid || !bidAmount}
                style={{
                  padding: '12px 24px',
                  backgroundColor: placingBid || !bidAmount ? '#999' : '#2e7d32',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: placingBid || !bidAmount ? 'not-allowed' : 'pointer',
                  fontSize: '16px',
                  fontWeight: 'bold'
                }}
              >
                {placingBid ? 'Placing Bid...' : 'Place Bid'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Bid History Modal */}
      {showBidHistory && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000
        }}>
          <div style={{
            backgroundColor: 'white',
            borderRadius: '8px',
            padding: '24px',
            maxWidth: '700px',
            width: '90%',
            maxHeight: '80vh',
            overflow: 'auto'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
              <h2 style={{ fontSize: '24px', margin: 0 }}>
                Bid History
              </h2>
              <button
                onClick={() => setShowBidHistory(false)}
                style={{
                  background: 'none',
                  border: 'none',
                  fontSize: '24px',
                  cursor: 'pointer',
                  color: '#666'
                }}
              >
                ×
              </button>
            </div>

            <div style={{ marginBottom: '20px', textAlign: 'center' }}>
              <div style={{ fontSize: '18px', fontWeight: 'bold', marginBottom: '8px' }}>
                {product?.title}
              </div>
              {bidInfo && (
                <div style={{ fontSize: '14px', color: '#666' }}>
                  Total bids: {bidInfo.bidCount} • 
                  Current price: {formatPrice(bidInfo.currentPrice)}
                </div>
              )}
            </div>

            {loadingBidHistory ? (
              <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
                Loading bid history...
              </div>
            ) : bidHistory.length > 0 ? (
              <div style={{ border: '1px solid #ddd', borderRadius: '4px' }}>
                <div style={{
                  display: 'grid',
                  gridTemplateColumns: '1fr 1fr 1fr 2fr',
                  gap: '12px',
                  padding: '16px',
                  backgroundColor: '#f8f9fa',
                  borderBottom: '1px solid #ddd',
                  fontWeight: 'bold',
                  fontSize: '14px'
                }}>
                  <div>Bidder</div>
                  <div>Amount</div>
                  <div>Status</div>
                  <div>Time</div>
                </div>
                {bidHistory.map((bid, index) => (
                  <div
                    key={bid.bidId}
                    style={{
                      display: 'grid',
                      gridTemplateColumns: '1fr 1fr 1fr 2fr',
                      gap: '12px',
                      padding: '16px',
                      borderBottom: index < bidHistory.length - 1 ? '1px solid #eee' : 'none',
                      backgroundColor: bid.isWinningBid ? '#e8f5e8' : 'white'
                    }}
                  >
                    <div style={{ fontSize: '14px' }}>
                      {bid.bidder.username}
                    </div>
                    <div style={{ fontSize: '14px', fontWeight: 'bold' }}>
                      {formatPrice(bid.bidAmount)}
                    </div>
                    <div style={{
                      fontSize: '12px',
                      color: bid.isWinningBid ? '#2e7d32' : '#666',
                      fontWeight: bid.isWinningBid ? 'bold' : 'normal'
                    }}>
                      {bid.isWinningBid ? '🏆 Winning' : bid.bidStatus}
                    </div>
                    <div style={{ fontSize: '12px', color: '#666' }}>
                      {new Date(bid.bidTime).toLocaleDateString()} {new Date(bid.bidTime).toLocaleTimeString()}
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
                No bids have been placed yet.
              </div>
            )}

            <div style={{ marginTop: '20px', textAlign: 'center' }}>
              <button
                onClick={() => setShowBidHistory(false)}
                style={{
                  padding: '12px 24px',
                  backgroundColor: '#0066cc',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontSize: '16px'
                }}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductDetail;