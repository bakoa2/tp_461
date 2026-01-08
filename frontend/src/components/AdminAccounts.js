import React, { useState, useEffect } from 'react';
import { formatCurrency, formatDate, getAccountTypeLabel, getOperatorTypeLabel } from '../services/api';

const AdminAccounts = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('ALL');
  const [filterOperator, setFilterOperator] = useState('ALL');

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        // Simuler les données pour le moment
        const mockAccounts = [
          {
            id: 1,
            accountNumber: '1234567890',
            accountType: 'CURRENT',
            operatorType: 'BANK',
            balance: 500000,
            currency: 'XAF',
            isActive: true,
            isLocked: false,
            dailyLimit: 500000,
            monthlyLimit: 2000000,
            user: {
              id: 1,
              username: 'john_doe',
              firstName: 'John',
              lastName: 'Doe'
            },
            createdAt: '2024-01-01T00:00:00'
          },
          {
            id: 2,
            accountNumber: '0987654321',
            accountType: 'SAVINGS',
            operatorType: 'MOBILE_MONEY',
            balance: 250000,
            currency: 'XAF',
            isActive: true,
            isLocked: false,
            dailyLimit: 300000,
            monthlyLimit: 1500000,
            user: {
              id: 2,
              username: 'jane_smith',
              firstName: 'Jane',
              lastName: 'Smith'
            },
            createdAt: '2024-01-15T00:00:00'
          },
          {
            id: 3,
            accountNumber: '1122334455',
            accountType: 'BUSINESS',
            operatorType: 'INTERNATIONAL',
            balance: 1000000,
            currency: 'XAF',
            isActive: false,
            isLocked: true,
            dailyLimit: 1000000,
            monthlyLimit: 5000000,
            user: {
              id: 1,
              username: 'john_doe',
              firstName: 'John',
              lastName: 'Doe'
            },
            createdAt: '2024-02-01T00:00:00'
          }
        ];
        setAccounts(mockAccounts);
      } catch (error) {
        console.error('Error fetching accounts:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchAccounts();
  }, []);

  const filteredAccounts = accounts.filter(account => {
    const matchesSearch = 
      account.accountNumber.includes(searchTerm) ||
      account.user?.firstName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      account.user?.lastName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      account.user?.username?.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesType = filterType === 'ALL' || account.accountType === filterType;
    const matchesOperator = filterOperator === 'ALL' || account.operatorType === filterOperator;

    return matchesSearch && matchesType && matchesOperator;
  });

  const handleToggleAccountLock = async (accountId, currentLocked) => {
    try {
      // Simuler l'appel API
      setAccounts(accounts.map(account =>
        account.id === accountId ? { ...account, isLocked: !currentLocked } : account
      ));
      console.log(`Account ${accountId} lock status toggled`);
    } catch (error) {
      console.error('Error toggling account lock:', error);
    }
  };

  const handleToggleAccountStatus = async (accountId, currentActive) => {
    try {
      // Simuler l'appel API
      setAccounts(accounts.map(account =>
        account.id === accountId ? { ...account, isActive: !currentActive } : account
      ));
      console.log(`Account ${accountId} status toggled`);
    } catch (error) {
      console.error('Error toggling account status:', error);
    }
  };

  const handleDeleteAccount = async (accountId) => {
    if (window.confirm('Êtes-vous sûr de vouloir supprimer ce compte?')) {
      try {
        // Simuler l'appel API
        setAccounts(accounts.filter(account => account.id !== accountId));
        console.log(`Account ${accountId} deleted`);
      } catch (error) {
        console.error('Error deleting account:', error);
      }
    }
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
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900">
          🏦 Gestion des Comptes
        </h1>
        <button className="btn-primary">
          ➕ Ajouter un compte
        </button>
      </div>

      {/* Filters */}
      <div className="card">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div>
            <label className="form-label">Recherche</label>
            <input
              type="text"
              placeholder="Numéro, nom ou username..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="form-input"
            />
          </div>
          
          <div>
            <label className="form-label">Type de compte</label>
            <select
              value={filterType}
              onChange={(e) => setFilterType(e.target.value)}
              className="form-input"
            >
              <option value="ALL">Tous</option>
              <option value="CURRENT">Compte Courant</option>
              <option value="SAVINGS">Compte Épargne</option>
              <option value="BUSINESS">Compte Professionnel</option>
            </select>
          </div>
          
          <div>
            <label className="form-label">Opérateur</label>
            <select
              value={filterOperator}
              onChange={(e) => setFilterOperator(e.target.value)}
              className="form-input"
            >
              <option value="ALL">Tous</option>
              <option value="BANK">Banque</option>
              <option value="MOBILE_MONEY">Mobile Money</option>
              <option value="INTERNATIONAL">International</option>
            </select>
          </div>
          
          <div className="flex items-end">
            <div className="text-sm text-gray-600">
              {filteredAccounts.length} compte{filteredAccounts.length !== 1 ? 's' : ''} trouvé{filteredAccounts.length !== 1 ? 's' : ''}
            </div>
          </div>
        </div>
      </div>

      {/* Accounts Table */}
      <div className="card">
        {filteredAccounts.length === 0 ? (
          <p className="text-gray-500 text-center py-8">
            Aucun compte trouvé
          </p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Numéro
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Propriétaire
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Type
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Opérateur
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Solde
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Statut
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Date de création
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {filteredAccounts.map((account) => (
                  <tr key={account.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {account.accountNumber}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      <div>
                        <div className="font-medium">
                          {account.user?.firstName} {account.user?.lastName}
                        </div>
                        <div className="text-gray-500">
                          @{account.user?.username}
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                        {getAccountTypeLabel(account.accountType)}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-purple-100 text-purple-800">
                        {getOperatorTypeLabel(account.operatorType)}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-semibold">
                      {formatCurrency(account.balance)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm">
                      <div className="space-y-1">
                        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                          account.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                        }`}>
                          {account.isActive ? 'Actif' : 'Inactif'}
                        </span>
                        {account.isLocked && (
                          <span className="ml-1 px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-orange-100 text-orange-800">
                            🔒 Verrouillé
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {formatDate(account.createdAt)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex space-x-2">
                        <button
                          onClick={() => handleToggleAccountStatus(account.id, account.isActive)}
                          className={`px-3 py-1 text-xs rounded ${
                            account.isActive 
                              ? 'bg-yellow-100 text-yellow-800 hover:bg-yellow-200' 
                              : 'bg-green-100 text-green-800 hover:bg-green-200'
                          }`}
                        >
                          {account.isActive ? 'Désactiver' : 'Activer'}
                        </button>
                        <button
                          onClick={() => handleToggleAccountLock(account.id, account.isLocked)}
                          className={`px-3 py-1 text-xs rounded ${
                            account.isLocked 
                              ? 'bg-green-100 text-green-800 hover:bg-green-200' 
                              : 'bg-orange-100 text-orange-800 hover:bg-orange-200'
                          }`}
                        >
                          {account.isLocked ? 'Déverrouiller' : 'Verrouiller'}
                        </button>
                        <button
                          onClick={() => handleDeleteAccount(account.id)}
                          className="px-3 py-1 text-xs bg-red-100 text-red-800 rounded hover:bg-red-200"
                        >
                          Supprimer
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Summary Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            🏦 Total Comptes
          </h3>
          <p className="text-2xl font-bold text-blue-600">
            {accounts.length}
          </p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            ✅ Comptes Actifs
          </h3>
          <p className="text-2xl font-bold text-green-600">
            {accounts.filter(account => account.isActive).length}
          </p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            🔒 Comptes Verrouillés
          </h3>
          <p className="text-2xl font-bold text-orange-600">
            {accounts.filter(account => account.isLocked).length}
          </p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            💰 Solde Total
          </h3>
          <p className="text-2xl font-bold text-purple-600">
            {formatCurrency(accounts.reduce((sum, account) => sum + account.balance, 0))}
          </p>
        </div>
      </div>
    </div>
  );
};

export default AdminAccounts;