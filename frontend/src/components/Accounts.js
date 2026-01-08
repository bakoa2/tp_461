import React, { useState, useEffect } from 'react';
import { accountsAPI, formatCurrency, getAccountTypeLabel, getOperatorTypeLabel } from '../services/api';

const Accounts = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [formData, setFormData] = useState({
    accountType: 'CURRENT',
    operatorType: 'BANK',
    initialBalance: 0,
    currency: 'XAF',
    dailyLimit: 500000,
    monthlyLimit: 2000000,
  });

  useEffect(() => {
    const fetchAccounts = async () => {
      try {
        const response = await accountsAPI.getAll();
        setAccounts(response.data || []);
      } catch (error) {
        console.error('Error fetching accounts:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchAccounts();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      await accountsAPI.create(formData);
      setShowCreateForm(false);
      setFormData({
        accountType: 'CURRENT',
        operatorType: 'BANK',
        initialBalance: 0,
        currency: 'XAF',
        dailyLimit: 500000,
        monthlyLimit: 2000000,
      });
      
      // Refresh accounts list
      const response = await accountsAPI.getAll();
      setAccounts(response.data || []);
    } catch (error) {
      console.error('Error creating account:', error);
      alert('Erreur lors de la création du compte: ' + (error.response?.data?.error || error.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900">
          🏦 Mes Comptes
        </h1>
        <button
          onClick={() => setShowCreateForm(!showCreateForm)}
          className="btn-primary"
        >
          {showCreateForm ? 'Annuler' : '➕ Nouveau Compte'}
        </button>
      </div>

      {showCreateForm && (
        <div className="card">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">
            Créer un Nouveau Compte
          </h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="form-label">
                  Type de Compte
                </label>
                <select
                  name="accountType"
                  value={formData.accountType}
                  onChange={handleChange}
                  className="form-input"
                >
                  <option value="CURRENT">Compte Courant</option>
                  <option value="SAVINGS">Compte Épargne</option>
                  <option value="BUSINESS">Compte Professionnel</option>
                </select>
              </div>

              <div>
                <label className="form-label">
                  Opérateur
                </label>
                <select
                  name="operatorType"
                  value={formData.operatorType}
                  onChange={handleChange}
                  className="form-input"
                >
                  <option value="BANK">Banque</option>
                  <option value="MOBILE_MONEY">Mobile Money</option>
                  <option value="INTERNATIONAL">International</option>
                </select>
              </div>
            </div>

            <div>
              <label className="form-label">
                Solde Initial (XAF)
              </label>
              <input
                type="number"
                name="initialBalance"
                value={formData.initialBalance}
                onChange={handleChange}
                min="0"
                step="1000"
                className="form-input"
                placeholder="0"
              />
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="form-label">
                  Limite Quotidienne (XAF)
                </label>
                <input
                  type="number"
                  name="dailyLimit"
                  value={formData.dailyLimit}
                  onChange={handleChange}
                  min="0"
                  step="10000"
                  className="form-input"
                  placeholder="500000"
                />
              </div>

              <div>
                <label className="form-label">
                  Limite Mensuelle (XAF)
                </label>
                <input
                  type="number"
                  name="monthlyLimit"
                  value={formData.monthlyLimit}
                  onChange={handleChange}
                  min="0"
                  step="100000"
                  className="form-input"
                  placeholder="2000000"
                />
              </div>
            </div>

            <div className="flex space-x-4">
              <button
                type="submit"
                className="btn-primary flex-1"
              >
                Créer le Compte
              </button>
              <button
                type="button"
                onClick={() => setShowCreateForm(false)}
                className="btn-secondary flex-1"
              >
                Annuler
              </button>
            </div>
          </form>
        </div>
      )}

      {loading ? (
        <div className="flex justify-center h-32">
          <div className="loading-spinner"></div>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {accounts.map((account) => (
            <div key={account.id} className="card">
              <div className="flex justify-between items-start mb-2">
                <h3 className="text-lg font-semibold text-gray-900">
                  {account.accountNumber}
                </h3>
                <span className={`px-2 py-1 text-xs rounded-full ${
                  account.isActive ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                }`}>
                  {account.isActive ? 'Actif' : 'Inactif'}
                </span>
              </div>
              
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span className="text-sm text-gray-500">Type:</span>
                  <span className="text-sm font-medium text-gray-900">
                    {getAccountTypeLabel(account.accountType)}
                  </span>
                </div>
                
                <div className="flex justify-between">
                  <span className="text-sm text-gray-500">Opérateur:</span>
                  <span className="text-sm font-medium text-gray-900">
                    {getOperatorTypeLabel(account.operatorType)}
                  </span>
                </div>
                
                <div className="flex justify-between">
                  <span className="text-sm text-gray-500">Solde:</span>
                  <span className="text-lg font-bold text-blue-600">
                    {formatCurrency(account.balance)}
                  </span>
                </div>

                {account.isLocked && (
                  <div className="text-sm text-red-600 font-medium">
                    🔒 Compte Verrouillé
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Accounts;