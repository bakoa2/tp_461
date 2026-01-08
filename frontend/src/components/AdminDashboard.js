import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';

const AdminDashboard = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAdminStats = async () => {
      try {
        // Simuler les données pour le moment
        const mockStats = {
          totalUsers: 150,
          activeUsers: 120,
          inactiveUsers: 30,
          totalAccounts: 450,
          activeAccounts: 420,
          lockedAccounts: 15,
          totalTransactions: 2500,
          completedTransactions: 2300,
          pendingTransactions: 150,
          failedTransactions: 50,
          totalTransactionVolume: 45000000,
          accountTypes: {
            CURRENT: 200,
            SAVINGS: 150,
            BUSINESS: 100
          },
          operatorTypes: {
            BANK: 250,
            MOBILE_MONEY: 150,
            INTERNATIONAL: 50
          },
          transactionTypes: {
            DEPOSIT: 800,
            WITHDRAWAL: 700,
            TRANSFER: 600,
            PAYMENT: 400
          }
        };
        setStats(mockStats);
      } catch (error) {
        console.error('Error fetching admin stats:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchAdminStats();
  }, []);

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'XAF'
    }).format(amount);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="loading-spinner"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Welcome Section */}
      <div className="card">
        <h1 className="text-3xl font-bold text-gray-900 mb-2">
          🛠️ Panneau d'Administration
        </h1>
        <p className="text-gray-600">
          Bienvenue, {user?.username || 'Admin'}! Voici un aperçu du système.
        </p>
      </div>

      {/* User Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            👥 Total Utilisateurs
          </h3>
          <p className="text-3xl font-bold text-blue-600">
            {stats.totalUsers || 0}
          </p>
          <div className="mt-2 text-sm text-gray-600">
            <span className="text-green-600">{stats.activeUsers || 0} actifs</span> • 
            <span className="text-red-600"> {stats.inactiveUsers || 0} inactifs</span>
          </div>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            🏦 Total Comptes
          </h3>
          <p className="text-3xl font-bold text-green-600">
            {stats.totalAccounts || 0}
          </p>
          <div className="mt-2 text-sm text-gray-600">
            <span className="text-green-600">{stats.activeAccounts || 0} actifs</span> • 
            <span className="text-orange-600"> {stats.lockedAccounts || 0} verrouillés</span>
          </div>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            📊 Total Transactions
          </h3>
          <p className="text-3xl font-bold text-purple-600">
            {stats.totalTransactions || 0}
          </p>
          <div className="mt-2 text-sm text-gray-600">
            <span className="text-green-600">{stats.completedTransactions || 0} complétées</span> • 
            <span className="text-yellow-600"> {stats.pendingTransactions || 0} en attente</span>
          </div>
        </div>
      </div>

      {/* Financial Overview */}
      <div className="card">
        <h3 className="text-xl font-semibold text-gray-900 mb-4">
          💰 Vue d'ensemble financière
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <h4 className="text-lg font-medium text-gray-700 mb-2">
              Volume total des transactions
            </h4>
            <p className="text-2xl font-bold text-indigo-600">
              {formatCurrency(stats.totalTransactionVolume || 0)}
            </p>
          </div>
          
          <div>
            <h4 className="text-lg font-medium text-gray-700 mb-2">
              Taux de réussite des transactions
            </h4>
            <p className="text-2xl font-bold text-green-600">
              {stats.totalTransactions > 0 
                ? Math.round((stats.completedTransactions / stats.totalTransactions) * 100)
                : 0}%
            </p>
          </div>
        </div>
      </div>

      {/* Account Types Distribution */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            🏦 Répartition par type de compte
          </h3>
          <div className="space-y-3">
            {stats.accountTypes && Object.entries(stats.accountTypes).map(([type, count]) => (
              <div key={type} className="flex justify-between items-center">
                <span className="text-gray-700 capitalize">
                  {type === 'CURRENT' ? 'Compte Courant' : 
                   type === 'SAVINGS' ? 'Compte Épargne' : 'Compte Professionnel'}
                </span>
                <span className="font-semibold text-gray-900">{count}</span>
              </div>
            ))}
          </div>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            📱 Répartition par opérateur
          </h3>
          <div className="space-y-3">
            {stats.operatorTypes && Object.entries(stats.operatorTypes).map(([type, count]) => (
              <div key={type} className="flex justify-between items-center">
                <span className="text-gray-700 capitalize">
                  {type === 'BANK' ? 'Banque' : 
                   type === 'MOBILE_MONEY' ? 'Mobile Money' : 'International'}
                </span>
                <span className="font-semibold text-gray-900">{count}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Transaction Types */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          📋 Répartition par type de transaction
        </h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {stats.transactionTypes && Object.entries(stats.transactionTypes).map(([type, count]) => (
            <div key={type} className="text-center p-4 bg-gray-50 rounded-lg">
              <div className="text-2xl font-bold text-blue-600">{count}</div>
              <div className="text-sm text-gray-600 mt-1">
                {type === 'DEPOSIT' ? 'Dépôts' : 
                 type === 'WITHDRAWAL' ? 'Retraits' : 
                 type === 'TRANSFER' ? 'Virements' : 'Paiements'}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Quick Actions */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          ⚡ Actions rapides
        </h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <button
            onClick={() => window.location.href = '/admin/users'}
            className="btn-secondary p-4 text-center"
          >
            <div className="text-2xl mb-2">👥</div>
            <div>Gérer les utilisateurs</div>
          </button>
          
          <button
            onClick={() => window.location.href = '/admin/accounts'}
            className="btn-secondary p-4 text-center"
          >
            <div className="text-2xl mb-2">🏦</div>
            <div>Gérer les comptes</div>
          </button>
          
          <button
            onClick={() => window.location.href = '/admin/transactions'}
            className="btn-secondary p-4 text-center"
          >
            <div className="text-2xl mb-2">📊</div>
            <div>Voir les transactions</div>
          </button>
          
          <button
            onClick={() => window.location.href = '/admin/reports'}
            className="btn-secondary p-4 text-center"
          >
            <div className="text-2xl mb-2">📈</div>
            <div>Rapports</div>
          </button>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;