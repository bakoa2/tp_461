import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { accountsAPI, transactionsAPI, formatCurrency } from '../services/api';

const Dashboard = () => {
  const { user } = useAuth();
  const [balance, setBalance] = useState(0);
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [accountStats, setAccountStats] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        // Fetch total balance
        const balanceResponse = await accountsAPI.getTotalBalance();
        setBalance(balanceResponse.data.totalBalance || 0);

        // Fetch recent transactions
        const transactionsResponse = await transactionsAPI.getLatest();
        setRecentTransactions(transactionsResponse.data.slice(0, 5) || []);

        // Fetch account statistics
        const statsResponse = await accountsAPI.getStats();
        setAccountStats(statsResponse.data || {});
      } catch (error) {
        console.error('Error fetching dashboard data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

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
        <h1 className="text-2xl font-bold text-gray-900 mb-2">
          Bienvenue, {user?.username || 'Utilisateur'}!
        </h1>
        <p className="text-gray-600">
          Tableau de bord de votre système bancaire multi-opérateurs
        </p>
      </div>

      {/* Balance Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            💰 Solde Total
          </h3>
          <p className="text-3xl font-bold text-blue-600">
            {formatCurrency(balance)}
          </p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            🏦 Comptes Bancaires
          </h3>
          <p className="text-2xl font-bold text-green-600">
            {accountStats.totalBankAccounts || 0}
          </p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            📱 Mobile Money
          </h3>
          <p className="text-2xl font-bold text-purple-600">
            {accountStats.totalMobileMoneyAccounts || 0}
          </p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            🌍 International
          </h3>
          <p className="text-2xl font-bold text-orange-600">
            {accountStats.totalInternationalAccounts || 0}
          </p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            📊 Total Transactions
          </h3>
          <p className="text-2xl font-bold text-indigo-600">
            {accountStats.totalTransactions || 0}
          </p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            💵 Commissions Mensuelles
          </h3>
          <p className="text-2xl font-bold text-red-600">
            {formatCurrency(accountStats.monthlyCommissions || 0)}
          </p>
        </div>
      </div>

      {/* Recent Transactions */}
      <div className="card">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">
          📋 Transactions Récentes
        </h3>
        
        {recentTransactions.length === 0 ? (
          <p className="text-gray-500 text-center py-8">
            Aucune transaction récente
          </p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Référence
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Type
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Montant
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Statut
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Date
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {recentTransactions.map((transaction) => (
                  <tr key={transaction.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {transaction.reference}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">
                        {transaction.transactionType}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {formatCurrency(transaction.amount)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                        transaction.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                        transaction.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                        'bg-red-100 text-red-800'
                      }`}>
                        {transaction.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {new Date(transaction.createdAt).toLocaleDateString('fr-FR')}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
        
        <div className="mt-4 text-center">
          <button
            onClick={() => window.location.href = '/transactions'}
            className="btn-secondary"
          >
            Voir toutes les transactions
          </button>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;