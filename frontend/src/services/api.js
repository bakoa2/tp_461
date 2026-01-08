import axios from 'axios';

// Create axios instance with default config
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Authentication API
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
  validateToken: (token) => api.post('/auth/validate', null, {
    headers: { Authorization: `Bearer ${token}` }
  }),
  refreshToken: () => api.post('/auth/refresh', null, {
    headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
  }),
  logout: () => api.post('/auth/logout'),
};

// Accounts API
export const accountsAPI = {
  getAll: () => api.get('/accounts'),
  getById: (id) => api.get(`/accounts/${id}`),
  getByNumber: (accountNumber) => api.get(`/accounts/number/${accountNumber}`),
  create: (accountData) => api.post('/accounts', accountData),
  update: (id, accountData) => api.put(`/accounts/${id}`, accountData),
  delete: (id) => api.delete(`/accounts/${id}`),
  lock: (id) => api.post(`/accounts/${id}/lock`),
  unlock: (id) => api.post(`/accounts/${id}/unlock`),
  credit: (accountNumber, amount) => api.post(`/accounts/${accountNumber}/credit`, { amount }),
  debit: (accountNumber, amount) => api.post(`/accounts/${accountNumber}/debit`, { amount }),
  getTotalBalance: () => api.get('/accounts/balance/total'),
  getStats: () => api.get('/accounts/stats'),
};

// Transactions API
export const transactionsAPI = {
  getAll: () => api.get('/transactions'),
  getById: (id) => api.get(`/transactions/${id}`),
  getByReference: (reference) => api.get(`/transactions/reference/${reference}`),
  getLatest: () => api.get('/transactions/latest'),
  getByType: (type) => api.get(`/transactions/type/${type}`),
  getByDateRange: (startDate, endDate) => api.get('/transactions/daterange', {
    params: { startDate, endDate }
  }),
  create: (transactionData) => api.post('/transactions', transactionData),
  cancel: (id) => api.post(`/transactions/${id}/cancel`),
  getSummary: () => api.get('/transactions/summary'),
  getStats: () => api.get('/transactions/stats'),
};

// Users API
export const usersAPI = {
  getProfile: () => api.get('/users/profile'),
  updateProfile: (userData) => api.put('/users/profile', userData),
  changePassword: (passwordData) => api.put('/users/password', passwordData),
  deactivate: () => api.put('/users/deactivate'),
  activate: () => api.put('/users/activate'),
};

// Utility functions
export const formatDate = (dateString) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('fr-FR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

export const formatCurrency = (amount, currency = 'XAF') => {
  return new Intl.NumberFormat('fr-FR', {
    style: 'currency',
    currency: currency,
  }).format(amount);
};

export const getTransactionTypeLabel = (type) => {
  const labels = {
    DEPOSIT: 'Dépôt',
    WITHDRAWAL: 'Retrait',
    TRANSFER: 'Virement',
    PAYMENT: 'Paiement',
    FEE: 'Frais',
  };
  return labels[type] || type;
};

export const getAccountTypeLabel = (type) => {
  const labels = {
    CURRENT: 'Compte Courant',
    SAVINGS: 'Compte Épargne',
    BUSINESS: 'Compte Professionnel',
  };
  return labels[type] || type;
};

export const getOperatorTypeLabel = (type) => {
  const labels = {
    BANK: 'Banque',
    MOBILE_MONEY: 'Mobile Money',
    INTERNATIONAL: 'International',
  };
  return labels[type] || type;
};

export const getTransactionStatusLabel = (status) => {
  const labels = {
    PENDING: 'En attente',
    COMPLETED: 'Complétée',
    FAILED: 'Échouée',
    CANCELLED: 'Annulée',
  };
  return labels[status] || status;
};

export default api;