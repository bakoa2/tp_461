package com.bankingsystem.services;

import com.bankingsystem.dto.TransactionRequest;
import com.bankingsystem.model.Account;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.model.User;
import com.bankingsystem.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des transactions
 */
@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Créer une transaction
     */
    public Transaction createTransaction(Long userId, TransactionRequest transactionRequest) {
        User user = userService.getUserById(userId);
        
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setTransactionType(Transaction.TransactionType.valueOf(transactionRequest.getTransactionType().toUpperCase()));
        transaction.setDescription(transactionRequest.getDescription());
        transaction.setUser(user);
        
        // Traiter selon le type de transaction
        switch (transaction.getTransactionType()) {
            case DEPOSIT:
                return processDeposit(transaction, transactionRequest);
            case WITHDRAWAL:
                return processWithdrawal(transaction, transactionRequest);
            case TRANSFER:
                return processTransfer(transaction, transactionRequest);
            case PAYMENT:
                return processPayment(transaction, transactionRequest);
            default:
                throw new RuntimeException("Type de transaction non supporté");
        }
    }
    
    /**
     * Traiter un dépôt
     */
    private Transaction processDeposit(Transaction transaction, TransactionRequest request) {
        Account account = accountService.getAccountByNumber(request.getAccountNumber())
            .orElseThrow(() -> new RuntimeException("Compte non trouvé: " + request.getAccountNumber()));
        
        transaction.setDestinationAccount(account);
        
        // Exécuter le dépôt
        accountService.creditAccount(request.getAccountNumber(), transaction.getAmount());
        
        // Marquer comme complétée
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Envoyer notification
        sendTransactionNotification(savedTransaction, "Dépôt effectué avec succès");
        
        return savedTransaction;
    }
    
    /**
     * Traiter un retrait
     */
    private Transaction processWithdrawal(Transaction transaction, TransactionRequest request) {
        Account account = accountService.getAccountByNumber(request.getAccountNumber())
            .orElseThrow(() -> new RuntimeException("Compte non trouvé: " + request.getAccountNumber()));
        
        transaction.setSourceAccount(account);
        
        // Vérifier les fonds
        if (!account.hasSufficientFunds(transaction.getAmount())) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Fonds insuffisants pour le retrait");
        }
        
        // Exécuter le retrait
        accountService.debitAccount(request.getAccountNumber(), transaction.getAmount());
        
        // Marquer comme complétée
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Envoyer notification
        sendTransactionNotification(savedTransaction, "Retrait effectué avec succès");
        
        return savedTransaction;
    }
    
    /**
     * Traiter un transfert
     */
    private Transaction processTransfer(Transaction transaction, TransactionRequest request) {
        Account sourceAccount = accountService.getAccountByNumber(request.getSourceAccountNumber())
            .orElseThrow(() -> new RuntimeException("Compte source non trouvé: " + request.getSourceAccountNumber()));
        
        Account destinationAccount = accountService.getAccountByNumber(request.getDestinationAccountNumber())
            .orElseThrow(() -> new RuntimeException("Compte destination non trouvé: " + request.getDestinationAccountNumber()));
        
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        
        // Vérifier les fonds
        if (!sourceAccount.hasSufficientFunds(transaction.getAmount())) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Fonds insuffisants pour le transfert");
        }
        
        // Calculer la commission (2% du montant)
        BigDecimal commission = transaction.getAmount().multiply(BigDecimal.valueOf(0.02));
        transaction.setCommission(commission);
        
        // Exécuter le transfert
        accountService.debitAccount(request.getSourceAccountNumber(), transaction.getAmount());
        accountService.creditAccount(request.getDestinationAccountNumber(), transaction.getAmount().subtract(commission));
        
        // Marquer comme complétée
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Envoyer notifications aux deux parties
        sendTransactionNotification(savedTransaction, "Transfert envoyé avec succès");
        
        try {
            notificationService.envoyerNotification(
                destinationAccount.getUser().getPhoneNumber(),
                "Vous avez reçu un transfert de " + transaction.getAmount() + " " + 
                sourceAccount.getCurrency() + " de " + sourceAccount.getAccountNumber()
            );
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification au destinataire: " + e.getMessage());
        }
        
        return savedTransaction;
    }
    
    /**
     * Traiter un paiement
     */
    private Transaction processPayment(Transaction transaction, TransactionRequest request) {
        Account account = accountService.getAccountByNumber(request.getAccountNumber())
            .orElseThrow(() -> new RuntimeException("Compte non trouvé: " + request.getAccountNumber()));
        
        transaction.setSourceAccount(account);
        
        // Vérifier les fonds
        if (!account.hasSufficientFunds(transaction.getAmount())) {
            transaction.setStatus(Transaction.TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new RuntimeException("Fonds insuffisants pour le paiement");
        }
        
        // Exécuter le paiement
        accountService.debitAccount(request.getAccountNumber(), transaction.getAmount());
        
        // Calculer la commission (1.5% du montant)
        BigDecimal commission = transaction.getAmount().multiply(BigDecimal.valueOf(0.015));
        transaction.setCommission(commission);
        
        // Marquer comme complétée
        transaction.setStatus(Transaction.TransactionStatus.COMPLETED);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        // Envoyer notification
        sendTransactionNotification(savedTransaction, "Paiement effectué avec succès");
        
        return savedTransaction;
    }
    
    /**
     * Envoyer une notification de transaction
     */
    private void sendTransactionNotification(Transaction transaction, String message) {
        try {
            String phoneNumber = transaction.getUser().getPhoneNumber();
            String fullMessage = String.format("%s. Référence: %s, Montant: %s %s", 
                message, 
                transaction.getReference(), 
                transaction.getAmount(), 
                transaction.getSourceAccount() != null ? transaction.getSourceAccount().getCurrency() : "XAF"
            );
            
            notificationService.envoyerNotification(phoneNumber, fullMessage);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la notification de transaction: " + e.getMessage());
        }
    }
    
    /**
     * Obtenir une transaction par son ID
     */
    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
            .orElseThrow(() -> new RuntimeException("Transaction non trouvée avec l'ID: " + transactionId));
    }
    
    /**
     * Obtenir une transaction par sa référence
     */
    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionByReference(String reference) {
        return transactionRepository.findByReference(reference);
    }
    
    /**
     * Lister les transactions d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    /**
     * Lister les dernières transactions d'un utilisateur
     */
    @Transactional(readOnly = true)
    public List<Transaction> getLatestUserTransactions(Long userId) {
        return transactionRepository.findLatestByUser(userId);
    }
    
    /**
     * Lister les transactions par type
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByType(Transaction.TransactionType transactionType) {
        return transactionRepository.findByTransactionType(transactionType);
    }
    
    /**
     * Lister les transactions par statut
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByStatus(Transaction.TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }
    
    /**
     * Lister les transactions par utilisateur et type
     */
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactionsByType(Long userId, Transaction.TransactionType transactionType) {
        return transactionRepository.findByUserAndTransactionType(userId, transactionType);
    }
    
    /**
     * Lister les transactions par période
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateRange(startDate, endDate);
    }
    
    /**
     * Lister les transactions d'un utilisateur par période
     */
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactionsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByUserAndDateRange(userId, startDate, endDate);
    }
    
    /**
     * Calculer le total des transactions par utilisateur et type
     */
    @Transactional(readOnly = true)
    public BigDecimal sumTransactionsByUserAndType(Long userId, Transaction.TransactionType transactionType) {
        BigDecimal total = transactionRepository.sumByUserAndTransactionType(userId, transactionType);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Compter les transactions par statut
     */
    @Transactional(readOnly = true)
    public long countTransactionsByStatus(Transaction.TransactionStatus status) {
        return transactionRepository.countByStatus(status);
    }
    
    /**
     * Calculer le total des commissions pour une période
     */
    @Transactional(readOnly = true)
    public BigDecimal sumCommissionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal total = transactionRepository.sumCommissionsByDateRange(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Annuler une transaction
     */
    public Transaction cancelTransaction(Long transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        
        if (!transaction.isPending()) {
            throw new RuntimeException("Seules les transactions en attente peuvent être annulées");
        }
        
        transaction.setStatus(Transaction.TransactionStatus.CANCELLED);
        return transactionRepository.save(transaction);
    }
    
    /**
     * Marquer une transaction comme échouée
     */
    public Transaction failTransaction(Long transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        transaction.markAsFailed();
        return transactionRepository.save(transaction);
    }
}