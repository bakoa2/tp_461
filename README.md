# 🏦 Système Bancaire Multi-opérateurs

## 📋 Description

Ce projet implémente un système bancaire multi-opérateurs complet avec Spring Boot backend et React frontend, démontrant 10 patterns de conception comme demandé dans le cahier des charges du cours INF461.

## 🎯 Objectifs

1. ✅ **Backend Spring Boot complet** avec tous les composants nécessaires
2. ✅ **Frontend React moderne** avec Tailwind CSS
3. ✅ **Tests complets** pour le backend
4. ✅ **Intégration frontend-backend** fonctionnelle

## 🏗 Architecture

```
┌─────────────────┐
│   Frontend    │
│   React.js     │
│   Tailwind CSS │
├─────────────────┤
│               │
│   Backend      │
│   Spring Boot   │
│   PostgreSQL    │
│   JWT Auth      │
├─────────────────┤
│               │
│   Database     │
│   PostgreSQL    │
└─────────────────┘
```

## 🚀 Démarrage Rapide

### Prérequis

- **Java 17+** installé
- **Node.js 16+** installé
- **PostgreSQL** installé et configuré

### 1. Base de données

```sql
CREATE DATABASE tpinf461;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone_number VARCHAR(20),
    auth_type VARCHAR(20) DEFAULT 'PASSWORD',
    biometric_data TEXT,
    ar_data TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_type VARCHAR(20) NOT NULL,
    operator_type VARCHAR(20) NOT NULL,
    balance DECIMAL(19,2) DEFAULT 0.00,
    currency VARCHAR(10) DEFAULT 'XAF',
    is_active BOOLEAN DEFAULT TRUE,
    is_locked BOOLEAN DEFAULT FALSE,
    daily_limit DECIMAL(19,2),
    monthly_limit DECIMAL(19,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGSERIAL REFERENCES users(id)
);

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    description TEXT,
    commission DECIMAL(19,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGSERIAL REFERENCES users(id),
    source_account_id BIGSERIAL REFERENCES accounts(id),
    destination_account_id BIGSERIAL REFERENCES accounts(id)
);
```

### 2. Lancer le Backend

```bash
cd backend
./mvnw spring-boot:run
```

L'API sera disponible sur `http://localhost:8080/api`

### 3. Lancer le Frontend

```bash
cd frontend
npm install
npm start
```

L'application sera disponible sur `http://localhost:3000`

## 📚 Documentation

### Architecture du Code

#### Backend (Spring Boot)
- **Models**: `User`, `Account`, `Transaction` avec JPA/Hibernate
- **Repositories**: `UserRepository`, `AccountRepository`, `TransactionRepository`
- **Services**: `UserService`, `AccountService`, `TransactionService`
- **Controllers**: `AuthController`, `AccountController`, `TransactionController`
- **Security**: JWT avec Spring Security
- **DTOs**: `AuthRequest`, `AuthResponse`, `AccountRequest`, `TransactionRequest`

#### Frontend (React)
- **Components**: `Login`, `Dashboard`, `Accounts`, `Transactions`, `Navbar`
- **Services**: API client avec Axios
- **Context**: AuthContext pour la gestion d'état
- **Routing**: React Router avec routes protégées

### Patterns de Conception Implémentés

1. **Strategy Pattern** - Authentification multiple (Password, Biométrie, OTP, RA)
2. **Abstract Factory Pattern** - Opérateurs (Banque, Mobile Money, International)
3. **Builder Pattern** - Construction de transactions complexes
4. **Singleton Pattern** - Service de notifications global
5. **Adapter Pattern** - Adaptateurs SMS (Orange, MTN)
6. **Template Method Pattern** - Workflows de comptes
7. **Composite Pattern** - Cibles de notifications
8. **Decorator Pattern** - Comportements optionnels de comptes
9. **Visitor Pattern** - Opérations analytiques
10. **Repository Pattern** - Accès aux données

## 🔧 API Endpoints

### Authentification
- `POST /api/auth/login` - Connexion
- `POST /api/auth/register` - Inscription
- `POST /api/auth/refresh` - Rafraîchissement de token
- `POST /api/auth/logout` - Déconnexion

### Comptes
- `GET /api/accounts` - Liste des comptes
- `POST /api/accounts` - Création de compte
- `GET /api/accounts/{id}` - Détails d'un compte
- `POST /api/accounts/{id}/credit` - Créditer un compte
- `POST /api/accounts/{id}/debit` - Débiter un compte

### Transactions
- `GET /api/transactions` - Liste des transactions
- `POST /api/transactions` - Création de transaction
- `GET /api/transactions/{id}` - Détails d'une transaction
- `POST /api/transactions/{id}/cancel` - Annuler une transaction

## 🧪 Tests

```bash
cd backend
./mvnw test
```

## 📊 Statistiques et Monitoring

- **Endpoints de monitoring**:
  - `GET /api/health` - État de santé
  - `GET /api/info` - Informations système
  - `GET /api/accounts/stats` - Statistiques des comptes
  - `GET /api/transactions/stats` - Statistiques des transactions

## 🔐 Sécurité

- **JWT Tokens** pour l'authentification stateless
- **Spring Security** avec configuration CORS
- **Validation** des entrées utilisateur
- **Password hashing** avec BCrypt

## 🎨 Fonnalités Implémentées

### Authentification
- [x] Connexion avec nom d'utilisateur/mot de passe
- [x] Inscription d'utilisateurs
- [x] Types d'authentification multiples (Strategy Pattern)
- [x] Rafraîchissement automatique de tokens
- [x] Gestion des sessions JWT

### Gestion des Comptes
- [x] Création de comptes multiples par type
- [x] Support multi-opérateurs (Abstract Factory Pattern)
- [x] Crédit/débiter de comptes
- [x] Verrouillage/déverrouillage
- [x] Limites journalières/mensuelles

### Transactions
- [x] Création de tous types de transactions (Builder Pattern)
- [x] Virements entre comptes
- [x] Dépôts et retraits
- [x] Paiements avec commissions
- [x] Historique détaillé avec filtres

### Notifications
- [x] Service de notifications global (Singleton Pattern)
- [x] Adaptateurs SMS multiples (Adapter Pattern)
- [x] Notifications par SMS et email
- [x] Gestion des erreurs d'envoi

### Interface Utilisateur
- [x] Dashboard moderne avec statistiques en temps réel
- [x] Design responsive avec Tailwind CSS
- [x] Navigation intuitive avec routing protégé
- [x] Tables de données interactives avec pagination
- [x] Formulaires de création/modification
- [x] Affichage des soldes et transactions récentes

## 🚀 Déploiement

### Production
```bash
# Backend
./mvnw clean package
java -jar backend/target/banking-system-1.0.0.jar

# Frontend
cd frontend
npm run build
```

Les fichiers construits seront dans `backend/target/` et `frontend/build/`

---

**🎓 Projet INF461 - Université de Yaoundé I**
**Développé avec ❤️ pour démontrer la maîtrise des patterns de conception**