import React from 'react';
import { Link } from 'react-router-dom';
import './Header.css';

const Header: React.FC = () => {
  return (
    <header className="header">
      <div className="header-container">
        <div className="header-left">
          <Link to="/" className="logo">
            <img src="/logo.png" alt="eBay" className="logo-img" />
            eBay
          </Link>
        </div>
        
        <div className="header-center">
          <div className="search-bar">
            <input 
              type="text" 
              placeholder="Search for anything"
              className="search-input"
            />
            <button className="search-button">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
                <path d="21 21l-4.35-4.35M19 11A8 8 0 1 1 3 11a8 8 0 0 1 16 0z" stroke="currentColor" strokeWidth="2"/>
              </svg>
            </button>
          </div>
        </div>
        
        <div className="header-right">
          <nav className="nav-links">
            <Link to="/products">Browse</Link>
            <Link to="/sell">Sell</Link>
            <Link to="/cart">Cart</Link>
            <Link to="/login">Sign In</Link>
            <Link to="/register" className="register-btn">Register</Link>
          </nav>
        </div>
      </div>
    </header>
  );
};

export default Header;