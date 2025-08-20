#!/bin/bash

# eBay Database Setup Script
echo "Setting up eBay marketplace database..."

# Check if MySQL is running
if ! command -v mysql &> /dev/null; then
    echo "Error: MySQL is not installed or not in PATH"
    echo "Please install MySQL first: brew install mysql"
    exit 1
fi

# Run the database schema
echo "Creating database and tables..."
mysql -u root -p < database/schema.sql

if [ $? -eq 0 ]; then
    echo "✅ Database setup completed successfully!"
    echo ""
    echo "Database: eBay"
    echo "Tables created:"
    echo "  - users (with admin user)"
    echo "  - categories (with sample data)"
    echo "  - products"
    echo "  - product_images"
    echo "  - bids"
    echo "  - orders"
    echo "  - order_items" 
    echo "  - cart_items"
    echo "  - watchlist"
    echo "  - user_ratings"
    echo "  - messages"
    echo ""
    echo "Default admin login:"
    echo "  Username: admin"
    echo "  Password: admin123"
    echo ""
    echo "You can now start the application with: mvn jetty:run"
else
    echo "❌ Database setup failed!"
    echo "Please check your MySQL connection and try again."
    exit 1
fi