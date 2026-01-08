#!/bin/bash

echo "🚀 Starting Banking System Frontend Development..."
echo "📦 Installing dependencies..."

# Check if package.json exists
if [ ! -f "package.json" ]; then
    echo "❌ Error: package.json not found!"
    exit 1
fi

# Check if Node.js is available
if ! command -v node &> /dev/null; then
    echo "❌ Error: Node.js is not installed!"
    echo "Please install Node.js from https://nodejs.org/"
    exit 1
fi

echo "✅ Node.js version: $(node --version)"

# Install dependencies
echo "📦 Installing npm dependencies..."
npm install

if [ $? -eq 0 ]; then
    echo "✅ Dependencies installed successfully!"
    echo "🚀 Starting development server..."
    echo "🌐 Frontend will be available at: http://localhost:3000"
    echo "👤 Admin Panel: http://localhost:3000/admin/dashboard"
    echo "👥 Customer Dashboard: http://localhost:3000/dashboard"
    echo ""
    echo "📋 Available routes:"
    echo "   • /dashboard - Customer Dashboard"
    echo "   • /accounts - Customer Accounts"
    echo "   • /transactions - Customer Transactions"
    echo "   • /admin/dashboard - Admin Dashboard"
    echo "   • /admin/users - User Management"
    echo "   • /admin/accounts - Account Management"
    echo "   • /admin/transactions - Transaction Management"
    echo ""
    echo "🔐 Test credentials:"
    echo "   • Admin: username='admin', password='admin123'"
    echo "   • User: username='user', password='user123'"
    echo ""
    echo "Press Ctrl+C to stop the server"
    echo ""
    
    # Start the development server
    npm start
else
    echo "❌ Error installing dependencies!"
    exit 1
fi