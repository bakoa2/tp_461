import React, { useState, useEffect } from 'react';
import { formatCurrency, formatDate, getTransactionTypeLabel, getTransactionStatusLabel } from '../services/api';

const AdminTransactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');
  const [page, setPage] = useState(1);

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        // Simuler les données pour le moment
        const mockTransactions = [
          {
            id: 1,
            reference: 'TXN-2024-001',
            transactionType: 'DEPOSIT',
            amount: 50000,
            status: 'COMPLETED',
            description: 'Dépôt initial',
            user: {
              id: 1,
              username: 'john_doe',
              firstName: 'John',
              lastName: 'Doe'
            },
            sourceAccount: null,
            destinationAccount: {
              id: 1,
              accountNumber: '1234567890',
              accountType: 'CURRENT'
            },
            commission: 0,
            createdAt: '2024-01-01T10:30:00'
          },
          {
            id: 2,
            reference: 'TXN-2024-002',
            transactionType: 'TRANSFER',
            amount: 25000,
            status: 'COMPLETED',
            description: 'Virement vers Jane',
            user: {
              id: 1,
              username: 'john_doe',
              firstName: 'John',
              lastName: 'Doe'
            },
            sourceAccount: {
              id: 1,
              accountNumber: '1234567890',
              accountType: 'CURRENT'
            },
            destinationAccount: {
              id: 2,
              accountNumber: '0987654321',
              accountType: 'SAVINGS'
            },
            commission: 500,
            createdAt: '2024-01-02T14:15:00'
          },
          {
            id: 3,
            reference: 'TXN-2024-003',
            transactionType: 'WITHDRAWAL',
            amount: 10000,
            status: 'PENDING',
            description: 'Retrait guichet',
            user: {
              id: 2,
              username: 'jane_smith',
              firstName: 'Jane',
              lastName: 'Smith'
            },
            sourceAccount: {
              id: 2,
              accountNumber: '0987654321',
              accountType: 'SAVINGS'
            },
            destinationAccount: null,
            commission: 0,
            createdAt: '2024-01-03T09:45:00'
          },
          {
            id: 4,
            reference: 'TXN-2024-004',
            transactionType: 'PAYMENT',
            amount: 15000,
            status: 'FAILED',
            description: 'Paiement facture ENEO',
            user: {
              id: 3,
              username: 'bob_wilson',
              firstName: 'Bob',
              lastName: 'Wilson'
            },
            sourceAccount: {
              id: 3,
              accountNumber: '1122334455',
              accountType: 'BUSINESS'
            },
            destinationAccount: null,
            commission: 225,
            createdAt: '2024-01-04T16:20:00'
          }
        ];
        setTransactions(mockTransactions);
      } catch (error) {
        console.error('Error fetching transactions:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchTransactions();
  }, []);

  const filteredTransactions = transactions.filter(transaction => {
    const matchesSearch = 
      transaction.reference.toLowerCase().includes(searchTerm.toLowerCase()) ||
      transaction.user?.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      `${transaction.user?.firstName} ${transaction.user?.lastName}`.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesType = filter === 'ALL' || transaction.transactionType === filter;

    return matchesSearch && matchesType;
  });

  const paginatedTransactions = filteredTransactions.slice((page - 1) * 10, page * 10);

  const handleFilterChange = (newFilter) => {
    setFilter(newFilter);
    setPage(1);
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
          📊 Gestion des Transactions
        </h1>
        
        <div className="flex items-center space-x-4">
          <label className="text-sm font-medium text-gray-700">
            Filtrer par type:
          </label>
          <select
            value={filter}
            onChange={(e) => handleFilterChange(e.target.value)}
            className="border-gray-300 rounded-md shadow-sm px-4 py-2 text-sm"
          >
            <option value="ALL">Tous</option>
            <option value="DEPOSIT">Dépôts</option>
            <option value="WITHDRAWAL">Retraits</option>
            <option value="TRANSFER">Virements</option>
            <option value="PAYMENT">Paiements</option>
          </select>
        </div>
      </div>

      {/* Search Bar */}
      <div className="card">
        <div className="flex items-center space-x-4">
          <div className="flex-1">
            <input
              type="text"
              placeholder="Rechercher par référence, utilisateur..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="form-input"
            />
          </div>
          <div className="text-sm text-gray-600">
            {filteredTransactions.length} transaction{filteredTransactions.length !== 1 ? 's' : ''} trouvée{filteredTransactions.length !== 1 ? 's' : ''}
          </div>
        </div>
      </div>

      {/* Transactions Table */}
      <div className="card">
        {filteredTransactions.length === 0 ? (
          <p className="text-gray-500 text-center py-8">
            Aucune transaction trouvée
          </p>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Référence
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Utilisateur
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Type
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Montant
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Commission
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Statut
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Description
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Date
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {paginatedTransactions.map((transaction) => (
                    <tr key={transaction.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {transaction.reference}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        <div>
                          <div className="font-medium">
                            {transaction.user?.firstName} {transaction.user?.lastName}
                          </div>
                          <div className="text-gray-500">
                            @{transaction.user?.username}
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <span className="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-blue-100 text-blue-800">
                          {getTransactionTypeLabel(transaction.transactionType)}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-semibold">
                        {formatCurrency(transaction.amount)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {transaction.commission > 0 ? formatCurrency(transaction.commission) : '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">
                        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                          transaction.status === 'COMPLETED' ? 'bg-green-100 text-green-800' :
                          transaction.status === 'PENDING' ? 'bg-yellow-100 text-yellow-800' :
                          'bg-red-100 text-red-800'
                        }`}>
                          {getTransactionStatusLabel(transaction.status)}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {transaction.description || '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {formatDate(transaction.createdAt)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {filteredTransactions.length > 10 && (
              <div className="flex justify-between items-center mt-4">
                <div className="text-sm text-gray-700">
                  Affichage de {Math.min(10, filteredTransactions.length - (page - 1) * 10)} sur {filteredTransactions.length} transactions
                </div>
                <div className="flex space-x-2">
                  <button
                    onClick={() => setPage(Math.max(1, page - 1))}
                    disabled={page === 1}
                    className="btn-secondary"
                  >
                    Précédent
                  </button>
                  <button
                    onClick={() => setPage(page + 1)}
                    disabled={page * 10 >= filteredTransactions.length}
                    className="btn-secondary"
                  >
                    Suivant
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>

      {/* Summary Statistics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            📊 Total Transactions
          </h3>
          <p className="text-2xl font-bold text-blue-600">
            {transactions.length}
          </p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            ✅ Transactions Complétées
          </h3>
          <p className="text-2xl font-bold text-green-600">
            {transactions.filter(t => t.status === 'COMPLETED').length}
          </p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            ⏳ Transactions en Attente
          </h3>
          <p className="text-2xl font-bold text-yellow-600">
            {transactions.filter(t => t.status === 'PENDING').length}
          </p>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-2">
            ❌ Transactions Échouées
          </h3>
          <p className="text-2xl font-bold text-red-600">
            {transactions.filter(t => t.status === 'FAILED').length}
          </p>
        </div>
      </div>

      {/* Financial Summary */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            💰 Volume des Transactions
          </h3>
          <div className="space-y-2">
            {['DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'PAYMENT'].map(type => {
              const typeTransactions = transactions.filter(t => t.transactionType === type && t.status === 'COMPLETED');
              const total = typeTransactions.reduce((sum, t) => sum + t.amount, 0);
              return (
                <div key={type} className="flex justify-between">
                  <span className="text-gray-700">
                    {getTransactionTypeLabel(type)}
                  </span>
                  <span className="font-semibold text-gray-900">
                    {formatCurrency(total)}
                  </span>
                </div>
              );
            })}
          </div>
        </div>

        <div className="card">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">
            💵 Total des Commissions
          </h3>
          <p className="text-2xl font-bold text-purple-600">
            {formatCurrency(transactions.reduce((sum, t) => sum + (t.commission || 0), 0))}
          </p>
        </div>
      </div>
    </div>
  );
};

export default AdminTransactions;