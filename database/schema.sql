-- eBay Marketplace Database Schema
-- This script creates all necessary tables for a complete eBay-like marketplace
-- PostgreSQL version

-- Note: Create database separately: CREATE DATABASE "eBay";
-- \c eBay

-- Drop tables in reverse order to handle foreign key constraints
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS bids;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS product_images;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS user_ratings;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS watchlist;
DROP TABLE IF EXISTS users;

-- Users table - Enhanced user management
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    address_line1 VARCHAR(100),
    address_line2 VARCHAR(100),
    city VARCHAR(50),
    state VARCHAR(50),
    zip_code VARCHAR(10),
    country VARCHAR(50) DEFAULT 'USA',
    user_type VARCHAR(10) CHECK (user_type IN ('BUYER', 'SELLER', 'BOTH')) DEFAULT 'BOTH',
    account_status VARCHAR(20) CHECK (account_status IN ('ACTIVE', 'SUSPENDED', 'PENDING')) DEFAULT 'ACTIVE',
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    email_verified BOOLEAN DEFAULT FALSE,
    profile_image_url VARCHAR(255),
    seller_rating DECIMAL(3,2) DEFAULT 0.00,
    buyer_rating DECIMAL(3,2) DEFAULT 0.00,
    total_sales_count INT DEFAULT 0,
    total_purchase_count INT DEFAULT 0
);

-- Categories table - Product organization
CREATE TABLE categories (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    parent_category_id INT,
    description TEXT,
    image_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_category_id) REFERENCES categories(category_id)
);

-- Products table - Core product information
CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    seller_id INT NOT NULL,
    category_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    starting_price DECIMAL(10,2) NOT NULL,
    current_price DECIMAL(10,2) NOT NULL,
    buy_now_price DECIMAL(10,2),
    reserve_price DECIMAL(10,2),
    condition_type VARCHAR(20) CHECK (condition_type IN ('NEW', 'LIKE_NEW', 'VERY_GOOD', 'GOOD', 'ACCEPTABLE', 'FOR_PARTS')) NOT NULL,
    listing_type VARCHAR(20) CHECK (listing_type IN ('AUCTION', 'BUY_NOW', 'BOTH')) NOT NULL,
    auction_start_time TIMESTAMP,
    auction_end_time TIMESTAMP,
    quantity_available INT DEFAULT 1,
    quantity_sold INT DEFAULT 0,
    shipping_cost DECIMAL(8,2) DEFAULT 0.00,
    shipping_method VARCHAR(100),
    return_policy TEXT,
    item_location VARCHAR(100),
    status VARCHAR(20) CHECK (status IN ('DRAFT', 'ACTIVE', 'SOLD', 'ENDED', 'CANCELLED')) DEFAULT 'DRAFT',
    view_count INT DEFAULT 0,
    watch_count INT DEFAULT 0,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(user_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- Product Images table - Multiple images per product
CREATE TABLE product_images (
    image_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    alt_text VARCHAR(200),
    is_primary BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    uploaded_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- Bids table - Auction bidding system
CREATE TABLE bids (
    bid_id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    bidder_id INT NOT NULL,
    bid_amount DECIMAL(10,2) NOT NULL,
    bid_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_winning_bid BOOLEAN DEFAULT FALSE,
    bid_type VARCHAR(20) CHECK (bid_type IN ('REGULAR', 'PROXY', 'BUY_NOW')) DEFAULT 'REGULAR',
    max_proxy_amount DECIMAL(10,2),
    bid_status VARCHAR(20) CHECK (bid_status IN ('ACTIVE', 'OUTBID', 'WINNING', 'WON', 'CANCELLED')) DEFAULT 'ACTIVE',
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (bidder_id) REFERENCES users(user_id)
);

-- Shopping Cart table - For Buy Now items
CREATE TABLE cart_items (
    cart_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    UNIQUE (user_id, product_id)
);

-- Orders table - Purchase transactions
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    buyer_id INT NOT NULL,
    seller_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    shipping_cost DECIMAL(8,2) DEFAULT 0.00,
    tax_amount DECIMAL(8,2) DEFAULT 0.00,
    payment_method VARCHAR(50),
    payment_status VARCHAR(20) CHECK (payment_status IN ('PENDING', 'PAID', 'FAILED', 'REFUNDED')) DEFAULT 'PENDING',
    shipping_status VARCHAR(20) CHECK (shipping_status IN ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'RETURNED')) DEFAULT 'PENDING',
    shipping_address TEXT,
    billing_address TEXT,
    tracking_number VARCHAR(100),
    notes TEXT,
    FOREIGN KEY (buyer_id) REFERENCES users(user_id),
    FOREIGN KEY (seller_id) REFERENCES users(user_id)
);

-- Order Items table - Items within each order
CREATE TABLE order_items (
    order_item_id SERIAL PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Watchlist table - User's watched items
CREATE TABLE watchlist (
    watchlist_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    added_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    UNIQUE (user_id, product_id)
);

-- Messages table - Communication between users
CREATE TABLE messages (
    message_id SERIAL PRIMARY KEY,
    sender_id INT NOT NULL,
    recipient_id INT NOT NULL,
    product_id INT,
    subject VARCHAR(200),
    message_text TEXT NOT NULL,
    sent_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    message_type VARCHAR(20) CHECK (message_type IN ('INQUIRY', 'OFFER', 'GENERAL', 'SYSTEM')) DEFAULT 'GENERAL',
    FOREIGN KEY (sender_id) REFERENCES users(user_id),
    FOREIGN KEY (recipient_id) REFERENCES users(user_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- User Ratings table - Feedback system
CREATE TABLE user_ratings (
    rating_id SERIAL PRIMARY KEY,
    rater_id INT NOT NULL,
    rated_user_id INT NOT NULL,
    order_id INT,
    product_id INT,
    rating_score INT NOT NULL CHECK (rating_score BETWEEN 1 AND 5),
    comment TEXT,
    rating_type VARCHAR(10) CHECK (rating_type IN ('BUYER', 'SELLER')) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rater_id) REFERENCES users(user_id),
    FOREIGN KEY (rated_user_id) REFERENCES users(user_id),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    UNIQUE (rater_id, rated_user_id, order_id, rating_type)
);

-- Insert sample categories
INSERT INTO categories (category_name, description) VALUES
('Electronics', 'Computers, phones, and electronic devices'),
('Fashion', 'Clothing, shoes, and accessories'),
('Home & Garden', 'Furniture, decor, and garden supplies'),
('Sports & Outdoors', 'Sports equipment and outdoor gear'),
('Automotive', 'Car parts and automotive accessories'),
('Books & Media', 'Books, movies, music, and games'),
('Toys & Hobbies', 'Toys, collectibles, and hobby items'),
('Health & Beauty', 'Health products and beauty supplies');

-- Insert subcategories for Electronics
INSERT INTO categories (category_name, parent_category_id, description) VALUES
('Smartphones', 1, 'Mobile phones and accessories'),
('Laptops', 1, 'Laptop computers and accessories'),
('Gaming', 1, 'Video games and gaming consoles'),
('Audio', 1, 'Headphones, speakers, and audio equipment');

-- Create indexes for better performance
CREATE INDEX idx_products_title ON products(title);
CREATE INDEX idx_products_price ON products(current_price);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_seller ON products(seller_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_end_time ON products(auction_end_time);
CREATE INDEX idx_bids_amount ON bids(bid_amount);
CREATE INDEX idx_bids_product_amount ON bids(product_id, bid_amount DESC);
CREATE INDEX idx_bids_bidder ON bids(bidder_id);
CREATE INDEX idx_bids_time ON bids(bid_time);
CREATE INDEX idx_orders_buyer ON orders(buyer_id);
CREATE INDEX idx_orders_seller ON orders(seller_id);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_messages_recipient ON messages(recipient_id, is_read);
CREATE INDEX idx_messages_sender ON messages(sender_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);

-- Create admin user with SHA-256 hashed password for 'admin123'
INSERT INTO users (username, email, password_hash, first_name, last_name, user_type, account_status, email_verified) 
VALUES ('admin', 'admin@ebay.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Admin', 'User', 'BOTH', 'ACTIVE', TRUE);

-- COMMIT; -- PostgreSQL uses implicit transactions for DDL