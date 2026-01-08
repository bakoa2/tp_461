import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const Register = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: '',
    phoneNumber: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    // Validate passwords match
    if (formData.password !== formData.confirmPassword) {
      setError('Les mots de passe ne correspondent pas');
      setLoading(false);
      return;
    }

    try {
      // For now, just redirect to login with success message
      // In a real implementation, you'd call an API to register the user
      console.log('Registration data:', formData);
      
      // Simulate successful registration
      setTimeout(() => {
        alert('Inscription réussie! Vous pouvez maintenant vous connecter.');
        navigate('/login');
      }, 1000);
    } catch (err) {
      setError(err.response?.data?.error || 'Erreur lors de l\'inscription');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            🏦 Créer un Compte
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Rejoignez notre système bancaire
          </p>
        </div>

        <div className="card">
          <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            {error && (
              <div className="bg-red-50 border border-red-500 text-red-700 px-4 py-3 rounded mb-4">
                {error}
              </div>
            )}

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label htmlFor="firstName" className="form-label">
                  Prénom
                </label>
                <input
                  id="firstName"
                  name="firstName"
                  type="text"
                  required
                  className="form-input"
                  placeholder="Entrez votre prénom"
                  value={formData.firstName}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="lastName" className="form-label">
                  Nom
                </label>
                <input
                  id="lastName"
                  name="lastName"
                  type="text"
                  required
                  className="form-input"
                  placeholder="Entrez votre nom"
                  value={formData.lastName}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div>
              <label htmlFor="username" className="form-label">
                Nom d'utilisateur
              </label>
              <input
                id="username"
                name="username"
                type="text"
                required
                className="form-input"
                placeholder="Choisissez un nom d'utilisateur"
                value={formData.username}
                onChange={handleChange}
              />
            </div>

            <div>
              <label htmlFor="email" className="form-label">
                Email
              </label>
              <input
                id="email"
                name="email"
                type="email"
                required
                className="form-input"
                placeholder="Entrez votre email"
                value={formData.email}
                onChange={handleChange}
              />
            </div>

            <div>
              <label htmlFor="phoneNumber" className="form-label">
                Téléphone
              </label>
              <input
                id="phoneNumber"
                name="phoneNumber"
                type="tel"
                className="form-input"
                placeholder="Entrez votre numéro de téléphone"
                value={formData.phoneNumber}
                onChange={handleChange}
              />
            </div>

            <div>
              <label htmlFor="password" className="form-label">
                Mot de passe
              </label>
              <input
                id="password"
                name="password"
                type="password"
                required
                className="form-input"
                placeholder="Choisissez un mot de passe"
                value={formData.password}
                onChange={handleChange}
              />
            </div>

            <div>
              <label htmlFor="confirmPassword" className="form-label">
                Confirmer le mot de passe
              </label>
              <input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                required
                className="form-input"
                placeholder="Confirmez votre mot de passe"
                value={formData.confirmPassword}
                onChange={handleChange}
              />
            </div>

            <div>
              <button
                type="submit"
                disabled={loading}
                className="btn-primary w-full flex justify-center"
              >
                {loading ? (
                  <>
                    <div className="loading-spinner mr-2"></div>
                    Inscription...
                  </>
                ) : (
                  'S\'inscrire'
                )}
              </button>
            </div>
          </form>

          <div className="mt-6">
            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-300"></div>
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-gray-50 text-gray-500">
                  Déjà un compte?
                </span>
              </div>
            </div>
            <div className="mt-4 text-center">
              <button
                onClick={() => navigate('/login')}
                className="btn-secondary"
              >
                Se connecter
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;