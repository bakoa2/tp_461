import React, { useState, useEffect } from 'react';
import { transactionsAPI, formatCurrency, formatDate, getTransactionTypeLabel, getTransactionStatusLabel } from '../services/api';

const Transactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('ALL');
  const [page, setPage] = useState(1);

  useEffect(() => {
    const fetchTransactions = async () => {
      try {
        let response;
        if (filter === 'ALL') {
          response = await transactionsAPI.getAll();
        } else {
          response = await transactionsAPI.getByType(filter);
        }
        setTransactions(response.data || []);
      } catch (error) {
        console.error('Error fetching transactions:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchTransactions();
  }, [filter]);

  const handleFilterChange = (newFilter) => {
    setFilter(newFilter);
    setPage(1);
  };

  const paginatedTransactions = transactions.slice((page - 1) * 10, page * 10);

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900">
          📋 Transactions
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

      {loading ? (
        <div className="flex justify-center h-32">
          <div className="loading-spinner"></div>
        </div>
      ) : (
        <div className="card">
          {transactions.length === 0 ? (
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
                        Type
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Montant
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
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {getTransactionTypeLabel(transaction.transactionType)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 font-semibold">
                          {formatCurrency(transaction.amount)}
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
              {transactions.length > 10 && (
                <div className="flex justify-between items-center mt-4">
                  <div className="text-sm text-gray-700">
                    Affichage de {Math.min(10, transactions.length - (page - 1) * 10)} sur {transactions.length} transactions
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
                      disabled={page * 10 >= transactions.length}
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
      )}
    </div>
  );
};

export default Transactions;