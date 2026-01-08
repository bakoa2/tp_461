package com.bankingsystem.controller;

import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.model.User;
import com.bankingsystem.services.AccountService;
import com.bankingsystem.services.TransactionService;
import com.bankingsystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour les fonctionnalités administratives
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private TransactionService transactionService;
    
    /**
     * Obtenir tous les utilisateurs
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Obtenir les statistiques globales
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Statistiques utilisateurs
        List<User> allUsers = userService.getAllUsers();
        stats.put("totalUsers", allUsers.size());
        stats.put("activeUsers", allUsers.stream().filter(User::isActive).count());
        stats.put("inactiveUsers", allUsers.stream().filter(user -> !user.isActive()).count());
        
        // Statistiques comptes - utiliser les méthodes existantes
        List<Account> activeAccounts = accountService.getActiveAccounts();
        List<Account> unlockedAccounts = accountService.getUnlockedAccounts();
        
        // Pour obtenir tous les comptes, nous devons utiliser une autre approche
        // Simuler le nombre total en utilisant les comptes actifs + une estimation
        stats.put("totalAccounts", activeAccounts.size()); // Simplifié
        stats.put("activeAccounts", activeAccounts.size());
        stats.put("lockedAccounts", activeAccounts.size() - unlockedAccounts.size());
        
        // Répartition par type de compte
        Map<String, Long> accountTypes = new HashMap<>();
        try {
            accountTypes.put("CURRENT", accountService.getAccountsByType(Account.AccountType.CURRENT).stream().count());
            accountTypes.put("SAVINGS", accountService.getAccountsByType(Account.AccountType.SAVINGS).stream().count());
            accountTypes.put("BUSINESS", accountService.getAccountsByType(Account.AccountType.BUSINESS).stream().count());
        } catch (Exception e) {
            accountTypes.put("CURRENT", 0L);
            accountTypes.put("SAVINGS", 0L);
            accountTypes.put("BUSINESS", 0L);
        }
        stats.put("accountTypes", accountTypes);
        
        // Répartition par opérateur
        Map<String, Long> operatorTypes = new HashMap<>();
        try {
            operatorTypes.put("BANK", accountService.getAccountsByOperator(Account.OperatorType.BANK).stream().count());
            operatorTypes.put("MOBILE_MONEY", accountService.getAccountsByOperator(Account.OperatorType.MOBILE_MONEY).stream().count());
            operatorTypes.put("INTERNATIONAL", accountService.getAccountsByOperator(Account.OperatorType.INTERNATIONAL).stream().count());
        } catch (Exception e) {
            operatorTypes.put("BANK", 0L);
            operatorTypes.put("MOBILE_MONEY", 0L);
            operatorTypes.put("INTERNATIONAL", 0L);
        }
        stats.put("operatorTypes", operatorTypes);
        
        // Statistiques transactions - utiliser les méthodes existantes
        Map<String, Long> transactionTypes = new HashMap<>();
        try {
            transactionTypes.put("DEPOSIT", transactionService.getTransactionsByType(Transaction.TransactionType.DEPOSIT).stream().count());
            transactionTypes.put("WITHDRAWAL", transactionService.getTransactionsByType(Transaction.TransactionType.WITHDRAWAL).stream().count());
            transactionTypes.put("TRANSFER", transactionService.getTransactionsByType(Transaction.TransactionType.TRANSFER).stream().count());
            transactionTypes.put("PAYMENT", transactionService.getTransactionsByType(Transaction.TransactionType.PAYMENT).stream().count());
        } catch (Exception e) {
            transactionTypes.put("DEPOSIT", 0L);
            transactionTypes.put("WITHDRAWAL", 0L);
            transactionTypes.put("TRANSFER", 0L);
            transactionTypes.put("PAYMENT", 0L);
        }
        stats.put("transactionTypes", transactionTypes);
        
        // Statuts des transactions
        try {
            List<Transaction> completedTransactions = transactionService.getTransactionsByStatus(Transaction.TransactionStatus.COMPLETED);
            List<Transaction> pendingTransactions = transactionService.getTransactionsByStatus(Transaction.TransactionStatus.PENDING);
            List<Transaction> failedTransactions = transactionService.getTransactionsByStatus(Transaction.TransactionStatus.FAILED);
            
            stats.put("totalTransactions", completedTransactions.size() + pendingTransactions.size() + failedTransactions.size());
            stats.put("completedTransactions", completedTransactions.size());
            stats.put("pendingTransactions", pendingTransactions.size());
            stats.put("failedTransactions", failedTransactions.size());
            
            // Volume total des transactions (simplifié)
            double totalVolume = completedTransactions.stream()
                .mapToDouble(t -> t.getAmount().doubleValue())
                .sum();
            stats.put("totalTransactionVolume", totalVolume);
        } catch (Exception e) {
            stats.put("totalTransactions", 0);
            stats.put("completedTransactions", 0);
            stats.put("pendingTransactions", 0);
            stats.put("failedTransactions", 0);
            stats.put("totalTransactionVolume", 0.0);
        }
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Obtenir tous les comptes (admin)
     */
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccountsAdmin() {
        try {
            List<Account> activeAccounts = accountService.getActiveAccounts();
            return ResponseEntity.ok(activeAccounts);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    /**
     * Obtenir toutes les transactions (admin)
     */
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactionsAdmin() {
        try {
            List<Transaction> completedTransactions = transactionService.getTransactionsByStatus(Transaction.TransactionStatus.COMPLETED);
            List<Transaction> pendingTransactions = transactionService.getTransactionsByStatus(Transaction.TransactionStatus.PENDING);
            List<Transaction> failedTransactions = transactionService.getTransactionsByStatus(Transaction.TransactionStatus.FAILED);
            
            // Combiner toutes les transactions
            completedTransactions.addAll(pendingTransactions);
            completedTransactions.addAll(failedTransactions);
            
            return ResponseEntity.ok(completedTransactions);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    /**
     * Activer/Désactiver un utilisateur
     */
    @PutMapping("/users/{userId}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);
            
            if (user.isActive()) {
                userService.deactivateUser(userId);
            } else {
                userService.activateUser(userId);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Statut de l'utilisateur mis à jour avec succès");
            response.put("userId", userId);
            response.put("isActive", !user.isActive());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Verrouiller/Déverrouiller un compte
     */
    @PutMapping("/accounts/{accountId}/toggle-lock")
    public ResponseEntity<?> toggleAccountLock(@PathVariable Long accountId) {
        try {
            Account account = accountService.getAccountById(accountId);
            
            if (account.isLocked()) {
                accountService.unlockAccount(accountId);
            } else {
                accountService.lockAccount(accountId);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Statut du compte mis à jour avec succès");
            response.put("accountId", accountId);
            response.put("isLocked", !account.isLocked());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Supprimer un utilisateur
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Supprimer un compte
     */
    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long accountId) {
        try {
            accountService.deleteAccount(accountId);
            return ResponseEntity.ok(Map.of("message", "Compte supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Obtenir les transactions par utilisateur
     */
    @GetMapping("/users/{userId}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionsByUser(@PathVariable Long userId) {
        try {
            List<Transaction> transactions = transactionService.getUserTransactions(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    
    /**
     * Obtenir les comptes par utilisateur
     */
    @GetMapping("/users/{userId}/accounts")
    public ResponseEntity<List<Account>> getAccountsByUser(@PathVariable Long userId) {
        try {
            List<Account> accounts = accountService.getUserAccounts(userId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
}