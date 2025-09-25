import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Layout/Header';
import Home from './pages/Home';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import ProductList from './components/Products/ProductList';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <main>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/products" element={<ProductList />} />
            <Route path="/products/:id" element={<div style={{padding: '40px', textAlign: 'center'}}>Product Detail - Coming Soon</div>} />
            <Route path="/cart" element={<div style={{padding: '40px', textAlign: 'center'}}>Cart - Coming Soon</div>} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route path="/sell" element={<div style={{padding: '40px', textAlign: 'center'}}>Sell - Coming Soon</div>} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
