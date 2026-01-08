package com.bankingsystem.services;

import com.bankingsystem.dto.AccountRequest;
import com.bankingsystem.model.Account;
import com.bankingsystem.model.User;
import com.bankingsystem.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Service pour la gestion des comptes bancaires
 */
@Service
@Transactional
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Créer un nouveau compte
     */
    public Account createAccount(Long userId, AccountRequest accountRequest) {
        User user = userService.getUserById(userId);
        
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountType(Account.AccountType.valueOf(accountRequest.getAccountType().toUpperCase()));
        account.setOperatorType(Account.OperatorType.valueOf(accountRequest.getOperatorType().toUpperCase()));
        account.setBalance(accountRequest.getInitialBalance());
        account.setCurrency(accountRequest.getCurrency());
        account.setDailyLimit(accountRequest.getDailyLimit());
        account.setMonthlyLimit(accountRequest.getMonthlyLimit());
        account.setUser(user);
        
        Account savedAccount = accountRepository.save(account);
        
        // Envoyer une notification de création de compte
        try {
            notificationService.envoyerNotification(
                user.getPhoneNumber(),
                "Votre compte " + savedAccount.getAccountNumber() + " a été créé avec succès. Solde initial: " + savedAccount.getBalance() + " " + savedAccount.getCurrency()
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
        
        return savedAccount;
    }
    
    /**
     * Générer un numéro de compte unique
     */
    private String generateAccountNumber() {
        Random random = new Random();
        String accountNumber;
        
        do {
            // Générer un numéro de compte à 10 chiffres
            accountNumber = String.format("%010d", random.nextInt(1000000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        
        return accountNumber;
    }
    
    /**
     * Obtenir un compte par son ID
     */
    @Transactional(readOnly = true)
    public Account getAccountById(Long accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Compte non trouvé avec l'ID: " + accountId));
    }
    
    /**
     * Obtenir un compte par son numéro
     */
    @Transactional(readOnly = true)
    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
    
    /**
     * Lister les comptes d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Account> getUserAccounts(Long userId) {
        return accountRepository.findByUserId(userId);
    }
    
    /**
     * Lister les comptes par type
     */
    @Transactional(readOnly = true)
    public List<Account> getAccountsByType(Account.AccountType accountType) {
        return accountRepository.findByAccountType(accountType);
    }
    
    /**
     * Lister les comptes par opérateur
     */
    @Transactional(readOnly = true)
    public List<Account> getAccountsByOperator(Account.OperatorType operatorType) {
        return accountRepository.findByOperatorType(operatorType);
    }
    
    /**
     * Lister tous les comptes actifs
     */
    @Transactional(readOnly = true)
    public List<Account> getActiveAccounts() {
        return accountRepository.findActiveAccounts();
    }
    
    /**
     * Lister tous les comptes non verrouillés
     */
    @Transactional(readOnly = true)
    public List<Account> getUnlockedAccounts() {
        return accountRepository.findUnlockedAccounts();
    }
    
    /**
     * Calculer le solde total d'un utilisateur
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalBalanceByUser(Long userId) {
        BigDecimal total = accountRepository.getTotalBalanceByUser(userId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Créditer un compte
     */
    public Account creditAccount(String accountNumber, BigDecimal amount) {
        Account account = getAccountByNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Compte non trouvé: " + accountNumber));
        
        account.credit(amount);
        Account savedAccount = accountRepository.save(account);
        
        // Envoyer une notification de crédit
        try {
            notificationService.envoyerNotification(
                account.getUser().getPhoneNumber(),
                "Crédit de " + amount + " " + account.getCurrency() + " sur votre compte " + accountNumber + ". Nouveau solde: " + account.getBalance()
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
        
        return savedAccount;
    }
    
    /**
     * Débiter un compte
     */
    public Account debitAccount(String accountNumber, BigDecimal amount) {
        Account account = getAccountByNumber(accountNumber)
            .orElseThrow(() -> new RuntimeException("Compte non trouvé: " + accountNumber));
        
        if (!account.hasSufficientFunds(amount)) {
            throw new RuntimeException("Fonds insuffisants sur le compte " + accountNumber);
        }
        
        account.debit(amount);
        Account savedAccount = accountRepository.save(account);
        
        // Envoyer une notification de débit
        try {
            notificationService.envoyerNotification(
                account.getUser().getPhoneNumber(),
                "Débit de " + amount + " " + account.getCurrency() + " sur votre compte " + accountNumber + ". Nouveau solde: " + account.getBalance()
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
        
        return savedAccount;
    }
    
    /**
     * Verrouiller un compte
     */
    public void lockAccount(Long accountId) {
        Account account = getAccountById(accountId);
        account.setLocked(true);
        accountRepository.save(account);
        
        // Envoyer une notification de verrouillage
        try {
            notificationService.envoyerNotification(
                account.getUser().getPhoneNumber(),
                "Votre compte " + account.getAccountNumber() + " a été verrouillé."
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }
    
    /**
     * Déverrouiller un compte
     */
    public void unlockAccount(Long accountId) {
        Account account = getAccountById(accountId);
        account.setLocked(false);
        accountRepository.save(account);
        
        // Envoyer une notification de déverrouillage
        try {
            notificationService.envoyerNotification(
                account.getUser().getPhoneNumber(),
                "Votre compte " + account.getAccountNumber() + " a été déverrouillé."
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }
    
    /**
     * Désactiver un compte
     */
    public void deactivateAccount(Long accountId) {
        Account account = getAccountById(accountId);
        account.setActive(false);
        accountRepository.save(account);
        
        // Envoyer une notification de désactivation
        try {
            notificationService.envoyerNotification(
                account.getUser().getPhoneNumber(),
                "Votre compte " + account.getAccountNumber() + " a été désactivé."
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }
    
    /**
     * Activer un compte
     */
    public void activateAccount(Long accountId) {
        Account account = getAccountById(accountId);
        account.setActive(true);
        accountRepository.save(account);
        
        // Envoyer une notification d'activation
        try {
            notificationService.envoyerNotification(
                account.getUser().getPhoneNumber(),
                "Votre compte " + account.getAccountNumber() + " a été activé."
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
    }
    
    /**
     * Supprimer un compte
     */
    public void deleteAccount(Long accountId) {
        Account account = getAccountById(accountId);
        
        // Vérifier si le solde est nul avant suppression
        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException("Impossible de supprimer un compte avec un solde non nul");
        }
        
        accountRepository.delete(account);
    }
    
    /**
     * Compter les comptes par opérateur
     */
    @Transactional(readOnly = true)
    public long countAccountsByOperator(Account.OperatorType operatorType) {
        return accountRepository.countByOperatorType(operatorType);
    }
}