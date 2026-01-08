# Banking System Frontend

A comprehensive React.js frontend for a multi-operator banking system with both customer interfaces and admin panel for monitoring accounts and transactions.

## 🚀 Quick Start

### Prerequisites
- Node.js (v14 or higher)
- npm or yarn

### Setup Instructions

1. **Install Dependencies**:
   ```bash
   npm install
   # or
   yarn install
   ```

2. **Start Development Server**:
   ```bash
   npm start
   # or use the provided script
   ./start-dev.sh
   ```

3. **Access the Application**:
   - **Customer Dashboard**: http://localhost:3000/dashboard
   - **Admin Panel**: http://localhost:3000/admin/dashboard

### Test Credentials
- **Admin User**: username=`admin`, password=`admin123`
- **Regular User**: username=`user`, password=`user123`

## 🏗️ Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── Login.js                 # Authentication component
│   │   ├── Dashboard.js              # Customer dashboard
│   │   ├── Accounts.js               # Customer account management
│   │   ├── Transactions.js           # Customer transaction view
│   │   ├── Navbar.js                 # Navigation component
│   │   ├── PrivateRoute.js           # Route protection
│   │   ├── AdminDashboard.js         # Admin overview dashboard
│   │   ├── AdminUsers.js             # User management
│   │   ├── AdminAccounts.js          # Account management (admin)
│   │   └── AdminTransactions.js      # Transaction management (admin)
│   ├── contexts/
│   │   └── AuthContext.js           # Authentication context
│   ├── services/
│   │   └── api.js                   # API service layer
│   ├── App.js                        # Main application component
│   ├── App.css                       # Global styles
│   └── index.js                      # Application entry point
├── package.json
├── tailwind.config.js               # Tailwind CSS configuration
├── postcss.config.js               # PostCSS configuration
└── start-dev.sh                   # Development startup script
```

## 🎯 Features Implemented

### Customer Interface
- **Dashboard**: Overview of accounts, recent transactions, and statistics
- **Account Management**: Create, view, and manage personal accounts
- **Transaction History**: View and filter transaction history
- **Multi-Operator Support**: Bank, Mobile Money, International accounts
- **Real-time Updates**: Live balance and transaction updates

### Admin Panel
- **Admin Dashboard**: System-wide statistics and financial overview
- **User Management**: Create, activate/deactivate, and delete users
- **Account Monitoring**: Manage all system accounts, lock/unlock functionality
- **Transaction Oversight**: View all transactions with advanced filtering
- **Role-Based Access**: Secure admin-only routes and functions
- **Bulk Operations**: Mass status updates and account management
- **Advanced Analytics**: Transaction volumes, success rates, commission tracking

### Technical Features
- **Authentication**: JWT-based secure authentication
- **Authorization**: Role-based access control (User/Admin)
- **Responsive Design**: Mobile-friendly interface with Tailwind CSS
- **State Management**: React Context for global state
- **API Integration**: RESTful API communication
- **Error Handling**: Comprehensive error management
- **Loading States**: User feedback during operations
- **Pagination**: Efficient data display for large datasets

## 🔐 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: Admin vs User permissions
- **Protected Routes**: Authentication guards for sensitive pages
- **Session Management**: Automatic token validation and refresh
- **Input Validation**: Form validation and sanitization
- **XSS Protection**: Safe data rendering practices

## 🎨 UI/UX Features

- **Responsive Design**: Works on desktop, tablet, and mobile
- **Modern Interface**: Clean, intuitive design with Tailwind CSS
- **Interactive Components**: Real-time updates and feedback
- **Accessibility**: Semantic HTML and ARIA labels
- **Loading States**: Spinner and skeleton components
- **Error Messages**: Clear, actionable error information
- **Status Indicators**: Visual badges for account/transaction status
- **Search & Filtering**: Advanced data filtering capabilities
- **Pagination**: Efficient navigation through large datasets

## 📊 Admin Dashboard Analytics

- **User Statistics**: Total, active, inactive users
- **Account Analytics**: Total, active, locked accounts by type
- **Transaction Metrics**: Volume, success rates, failure analysis
- **Financial Overview**: Total transaction volume and commissions
- **Operator Distribution**: Breakdown by account type and operator
- **Real-time Monitoring**: Live system status updates

## 🔧 Development Scripts

### Available Scripts
```bash
npm start          # Start development server
npm run build       # Build for production
npm test           # Run tests
```

### Custom Script
```bash
./start-dev.sh     # Enhanced startup with setup validation
```

## 🌐 Configuration

### Environment Variables
- `REACT_APP_API_URL`: Backend API endpoint (default: http://localhost:8080)
- `REACT_APP_ENVIRONMENT`: Development/Production environment

### Tailwind CSS Configuration
- Custom color scheme for banking theme
- Responsive breakpoints
- Custom component utilities
- Form styling variants

## 📱 Mobile Support

- **Responsive Layout**: Adapts to all screen sizes
- **Touch-Friendly**: Optimized for mobile interactions
- **Mobile Navigation**: Collapsible menu for small screens
- **Swipe Actions**: Touch-optimized buttons and controls

## 🔗 API Integration

### Endpoints Used
- `/api/auth/*` - Authentication operations
- `/api/accounts/*` - Account management
- `/api/transactions/*` - Transaction operations
- `/api/admin/*` - Admin-only operations

### Data Flow
1. **Authentication**: JWT token management
2. **User Data**: Profile and preferences
3. **Account Data**: Balance, limits, status
4. **Transaction Data**: History, details, status
5. **Admin Data**: System-wide statistics and management

## 🧪 Testing

### Test Coverage
- Component unit tests
- Integration tests for API calls
- Authentication flow testing
- Role-based access testing
- Responsive design testing

### Manual Testing Checklist
- [ ] Login/logout functionality
- [ ] Account creation and management
- [ ] Transaction viewing and filtering
- [ ] Admin dashboard loading
- [ ] User management operations
- [ ] Role-based access control
- [ ] Mobile responsiveness
- [ ] Error handling scenarios

## 🚀 Deployment

### Production Build
```bash
npm run build
```

### Build Output
- Optimized static files in `build/` directory
- Minified CSS and JavaScript
- Asset optimization for fast loading

### Environment Setup
- **Development**: Local development with hot reload
- **Staging**: Pre-production testing environment
- **Production**: Optimized build for deployment

## 📚 Documentation

### Component Documentation
Each component includes:
- Purpose and functionality description
- Props interface
- State management details
- Usage examples
- Accessibility considerations

### API Documentation
- Endpoint descriptions
- Request/response formats
- Authentication requirements
- Error handling procedures

## 🔄 Continuous Integration

### Pre-commit Hooks
- Code linting with ESLint
- Prettier code formatting
- Type checking (if using TypeScript)

### Build Process
- Automated testing on push
- Build artifact generation
- Deployment pipeline integration

## 🐛 Troubleshooting

### Common Issues
1. **CORS Errors**: Check backend CORS configuration
2. **Authentication Failures**: Verify JWT token handling
3. **Build Errors**: Clear node_modules and reinstall
4. **Styling Issues**: Verify Tailwind CSS build process

### Debug Mode
```bash
DEBUG=true npm start
```

### Performance Optimization
- Code splitting for faster loading
- Image optimization
- Bundle size analysis
- Memory leak prevention

## 📄 License

This project is licensed under the MIT License.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new features
5. Submit a pull request

## 📞 Support

For issues and questions:
- Create an issue in the repository
- Check existing issues for solutions
- Review documentation before asking questions

---

**Note**: This frontend is designed to work with the corresponding backend API. Ensure both frontend and backend are running for full functionality.