import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../../services/api';
import './Header.css';

interface User {
  userId: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  userType: string;
}

const Header: React.FC = () => {
  const [currentUser, setCurrentUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const response = await api.get('/user/current', {
        withCredentials: true
      });
      
      if (response.data.success && response.data.authenticated) {
        setCurrentUser(response.data.user);
      }
    } catch (error) {
      // User not authenticated
      setCurrentUser(null);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      await api.post('/user/logout', {}, {
        withCredentials: true
      });
      setCurrentUser(null);
      window.location.reload(); // Refresh to update app state
    } catch (error) {
      console.error('Logout error:', error);
    }
  };

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/products?search=${encodeURIComponent(searchQuery.trim())}`);
      setSearchQuery(''); // Clear search after navigating
    }
  };

  return (
    <header className="header">
      <div className="header-container">
        <div className="header-left">
            {/* Show welcome message when logged in */}
            {currentUser && (
                <div className="welcome-message" style={{ marginLeft: '20px', color: '#333', fontSize: '14px' }}>
                    Hi {currentUser.firstName || currentUser.username}!
                </div>
            )}
          <Link to="/" className="logo">
            <img src="/logo.png" alt="eBay" className="logo-img" />
          </Link>

        </div>
        
        <div className="header-center">
          <form onSubmit={handleSearch} className="search-bar">
            <input 
              type="text" 
              placeholder="Search for anything"
              className="search-input"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            <button type="submit" className="search-button">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="M21 21l-4.35-4.35M19 11A8 8 0 1 1 3 11a8 8 0 0 1 16 0z" stroke="currentColor" strokeWidth="2" fill="none"/>
              </svg>
            </button>
          </form>
        </div>
        
        <div className="header-right">
          <nav className="nav-links">
            <Link to="/products">Browse</Link>
            <Link to="/sell">Sell</Link>
            <Link to="/cart">Cart</Link>
            
            {/* Conditional rendering based on auth status */}
            {loading ? (
              <span>Loading...</span>
            ) : currentUser ? (
              // Logged in user options
              <>
                <Link to="/my-ebay">My eBay</Link>
                <button
                  className="logout-btn"
                  onClick={handleLogout}
                >
                  Logout
                </button>
              </>
            ) : (
              // Guest user options
              <>
                <Link to="/login">Sign In</Link>
                <Link to="/register" className="register-btn">Register</Link>
              </>
            )}
          </nav>
        </div>
      </div>
    </header>
  );
};

export default Header;