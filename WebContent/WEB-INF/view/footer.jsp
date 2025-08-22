<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<footer class="main-footer">
    <div class="footer-content">
        <div class="container">
            <div class="footer-sections">
                <div class="footer-section">
                    <h4>Buy</h4>
                    <ul>
                        <li><a href="/eBay/products">Browse Products</a></li>
                        <li><a href="/eBay/products/search">Advanced Search</a></li>
                        <li><a href="/eBay/categories">Categories</a></li>
                        <li><a href="/eBay/watchlist">Watchlist</a></li>
                    </ul>
                </div>
                
                <div class="footer-section">
                    <h4>Sell</h4>
                    <ul>
                        <li><a href="/eBay/seller/dashboard">Seller Dashboard</a></li>
                        <li><a href="/eBay/seller/listings/create">List an Item</a></li>
                        <li><a href="/eBay/seller/orders">Manage Orders</a></li>
                        <li><a href="/eBay/seller/analytics">Sales Analytics</a></li>
                    </ul>
                </div>
                
                <div class="footer-section">
                    <h4>Support</h4>
                    <ul>
                        <li><a href="/eBay/help">Help Center</a></li>
                        <li><a href="/eBay/contact">Contact Us</a></li>
                        <li><a href="/eBay/messages">Messages</a></li>
                        <li><a href="/eBay/support/disputes">Resolution Center</a></li>
                    </ul>
                </div>
                
                <div class="footer-section">
                    <h4>Account</h4>
                    <ul>
                        <li><a href="/eBay/profile">My Profile</a></li>
                        <li><a href="/eBay/orders">Purchase History</a></li>
                        <li><a href="/eBay/payment/tracking">Payment History</a></li>
                        <li><a href="/eBay/settings">Account Settings</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    
    <div class="footer-bottom">
        <div class="container">
            <div class="footer-bottom-content">
                <div class="copyright">
                    <p>&copy; 2024 eBay Marketplace. All rights reserved.</p>
                </div>
                
                <div class="footer-links">
                    <a href="/eBay/privacy">Privacy Policy</a>
                    <a href="/eBay/terms">Terms of Service</a>
                    <a href="/eBay/cookies">Cookie Policy</a>
                </div>
                
                <div class="social-links">
                    <a href="#" title="Facebook">📘</a>
                    <a href="#" title="Twitter">🐦</a>
                    <a href="#" title="Instagram">📷</a>
                    <a href="#" title="YouTube">📹</a>
                </div>
            </div>
        </div>
    </div>
</footer>

<style>
.main-footer {
    background: #2c3e50;
    color: white;
    margin-top: auto;
}

.footer-content {
    padding: 40px 0;
}

.footer-sections {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 30px;
}

.footer-section h4 {
    margin-bottom: 15px;
    color: #3498db;
    font-size: 16px;
}

.footer-section ul {
    list-style: none;
}

.footer-section li {
    margin-bottom: 8px;
}

.footer-section a {
    color: #bdc3c7;
    text-decoration: none;
    font-size: 14px;
    transition: color 0.2s;
}

.footer-section a:hover {
    color: #3498db;
}

.footer-bottom {
    background: #1a252f;
    padding: 20px 0;
    border-top: 1px solid #34495e;
}

.footer-bottom-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 20px;
}

.copyright p {
    margin: 0;
    color: #95a5a6;
    font-size: 14px;
}

.footer-links {
    display: flex;
    gap: 20px;
}

.footer-links a {
    color: #95a5a6;
    text-decoration: none;
    font-size: 14px;
    transition: color 0.2s;
}

.footer-links a:hover {
    color: #3498db;
}

.social-links {
    display: flex;
    gap: 15px;
}

.social-links a {
    font-size: 20px;
    text-decoration: none;
    transition: transform 0.2s;
}

.social-links a:hover {
    transform: scale(1.2);
}

@media (max-width: 768px) {
    .footer-sections {
        grid-template-columns: repeat(2, 1fr);
        gap: 20px;
    }
    
    .footer-bottom-content {
        flex-direction: column;
        text-align: center;
        gap: 15px;
    }
    
    .footer-links {
        flex-wrap: wrap;
        justify-content: center;
    }
}

@media (max-width: 480px) {
    .footer-sections {
        grid-template-columns: 1fr;
    }
}
</style>