import React, { useState, useEffect } from 'react';
import axios from 'axios';

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
}

interface Category {
  categoryId: number;
  name: string;
  description: string;
}

const ProductList: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [sortBy, setSortBy] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalCount, setTotalCount] = useState(0);

  const pageSize = 20;

  useEffect(() => {
    fetchCategories();
    fetchProducts();
  }, [currentPage, selectedCategory, sortBy]);

  const fetchCategories = async () => {
    try {
      const response = await axios.get('/api/products/categories');
      if (response.data.success) {
        setCategories(response.data.categories);
      }
    } catch (err) {
      console.error('Failed to fetch categories:', err);
    }
  };

  const fetchProducts = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        page: currentPage.toString(),
        size: pageSize.toString(),
      });

      if (selectedCategory) {
        params.append('category', selectedCategory);
      }
      if (sortBy) {
        params.append('sort', sortBy);
      }

      const response = await axios.get(`/api/products?${params}`);
      
      if (response.data.success) {
        setProducts(response.data.products);
        setTotalCount(response.data.totalCount);
        setError('');
      } else {
        setError(response.data.error || 'Failed to fetch products');
      }
    } catch (err: any) {
      setError('Failed to fetch products. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!searchQuery.trim()) {
      fetchProducts();
      return;
    }

    setLoading(true);
    try {
      const response = await axios.get(`/api/products/search?query=${encodeURIComponent(searchQuery)}&page=${currentPage}&size=${pageSize}`);
      
      if (response.data.success) {
        setProducts(response.data.products);
        setTotalCount(response.data.totalCount);
        setError('');
      } else {
        setError(response.data.error || 'Search failed');
      }
    } catch (err: any) {
      setError('Search failed. Please try again.');
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
    
    if (diff <= 0) return 'Ended';
    
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    
    if (days > 0) return `${days}d ${hours}h`;
    if (hours > 0) return `${hours}h ${minutes}m`;
    return `${minutes}m`;
  };

  const totalPages = Math.ceil(totalCount / pageSize);

  return (
    <div style={{ padding: '20px' }}>
      <h1>Products</h1>
      
      {/* Search and Filters */}
      <div style={{ marginBottom: '20px', backgroundColor: '#f5f5f5', padding: '15px', borderRadius: '4px' }}>
        <form onSubmit={handleSearch} style={{ display: 'flex', gap: '10px', marginBottom: '15px' }}>
          <input
            type="text"
            placeholder="Search products..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{
              flex: 1,
              padding: '10px',
              border: '1px solid #ddd',
              borderRadius: '4px'
            }}
          />
          <button
            type="submit"
            style={{
              padding: '10px 20px',
              backgroundColor: '#0066cc',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            Search
          </button>
        </form>
        
        <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
          <div>
            <label htmlFor="category">Category: </label>
            <select
              id="category"
              value={selectedCategory}
              onChange={(e) => setSelectedCategory(e.target.value)}
              style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}
            >
              <option value="">All Categories</option>
              {categories.map(category => (
                <option key={category.categoryId} value={category.name}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>
          
          <div>
            <label htmlFor="sort">Sort by: </label>
            <select
              id="sort"
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              style={{ padding: '8px', border: '1px solid #ddd', borderRadius: '4px' }}
            >
              <option value="">Default</option>
              <option value="price_asc">Price: Low to High</option>
              <option value="price_desc">Price: High to Low</option>
              <option value="newest">Newest First</option>
              <option value="ending_soon">Ending Soon</option>
            </select>
          </div>
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div style={{ color: 'red', marginBottom: '15px', padding: '10px', backgroundColor: '#ffe6e6', borderRadius: '4px' }}>
          {error}
        </div>
      )}

      {/* Loading */}
      {loading && (
        <div style={{ textAlign: 'center', padding: '40px' }}>
          Loading products...
        </div>
      )}

      {/* Products Grid */}
      {!loading && products.length > 0 && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '20px', marginBottom: '30px' }}>
          {products.map(product => (
            <div
              key={product.productId}
              style={{
                border: '1px solid #ddd',
                borderRadius: '8px',
                padding: '15px',
                backgroundColor: 'white',
                boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                cursor: 'pointer',
                transition: 'transform 0.2s',
              }}
              onMouseEnter={(e) => e.currentTarget.style.transform = 'translateY(-2px)'}
              onMouseLeave={(e) => e.currentTarget.style.transform = 'translateY(0)'}
              onClick={() => window.location.href = `/products/${product.productId}`}
            >
              {/* Product Image */}
              <div style={{ height: '200px', backgroundColor: '#f0f0f0', marginBottom: '10px', borderRadius: '4px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                {product.imageUrl ? (
                  <img
                    src={product.imageUrl}
                    alt={product.title}
                    style={{ maxWidth: '100%', maxHeight: '100%', objectFit: 'contain' }}
                  />
                ) : (
                  <span style={{ color: '#666' }}>No Image</span>
                )}
              </div>
              
              {/* Product Info */}
              <h3 style={{ margin: '0 0 10px 0', fontSize: '16px', fontWeight: 'bold', lineHeight: '1.3' }}>
                {product.title}
              </h3>
              
              <div style={{ marginBottom: '8px' }}>
                <span style={{ fontSize: '18px', fontWeight: 'bold', color: '#0066cc' }}>
                  {formatPrice(product.currentPrice)}
                </span>
                {product.buyNowPrice && product.buyNowPrice !== product.currentPrice && (
                  <span style={{ marginLeft: '10px', fontSize: '14px', color: '#666' }}>
                    Buy Now: {formatPrice(product.buyNowPrice)}
                  </span>
                )}
              </div>
              
              <div style={{ fontSize: '12px', color: '#666', marginBottom: '8px' }}>
                Condition: {product.condition}
              </div>
              
              {product.listingType === 'AUCTION' && product.endTime && (
                <div style={{ fontSize: '12px', color: '#d32f2f', fontWeight: 'bold' }}>
                  {formatTimeRemaining(product.endTime)} remaining
                </div>
              )}
              
              {product.listingType === 'BUY_NOW' && (
                <div style={{ fontSize: '12px', color: '#2e7d32', fontWeight: 'bold' }}>
                  Buy It Now
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* No Products */}
      {!loading && products.length === 0 && (
        <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
          No products found. Try adjusting your search or filters.
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '10px', marginTop: '30px' }}>
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
    </div>
  );
};

export default ProductList;