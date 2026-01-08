import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import Dashboard from './components/Dashboard';
import Accounts from './components/Accounts';
import Transactions from './components/Transactions';
import AdminDashboard from './components/AdminDashboard';
import AdminUsers from './components/AdminUsers';
import AdminAccounts from './components/AdminAccounts';
import AdminTransactions from './components/AdminTransactions';
import Navbar from './components/Navbar';
import { AuthProvider } from './contexts/AuthContext';
import PrivateRoute from './components/PrivateRoute';
import './App.css';

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-50">
          <Navbar />
          <main className="container mx-auto px-4 py-8">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route 
                path="/dashboard" 
                element={
                  <PrivateRoute>
                    <Dashboard />
                  </PrivateRoute>
                } 
              />
              <Route 
                path="/accounts" 
                element={
                  <PrivateRoute>
                    <Accounts />
                  </PrivateRoute>
                } 
              />
              <Route 
                path="/transactions" 
                element={
                  <PrivateRoute>
                    <Transactions />
                  </PrivateRoute>
                } 
              />
              {/* Admin Routes */}
              <Route 
                path="/admin/dashboard" 
                element={
                  <PrivateRoute>
                    <AdminDashboard />
                  </PrivateRoute>
                } 
              />
              <Route 
                path="/admin/users" 
                element={
                  <PrivateRoute>
                    <AdminUsers />
                  </PrivateRoute>
                } 
              />
              <Route 
                path="/admin/accounts" 
                element={
                  <PrivateRoute>
                    <AdminAccounts />
                  </PrivateRoute>
                } 
              />
              <Route 
                path="/admin/transactions" 
                element={
                  <PrivateRoute>
                    <AdminTransactions />
                  </PrivateRoute>
                } 
              />
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;