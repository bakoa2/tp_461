package com.bankingsystem.controller;

import com.bankingsystem.dto.TransactionRequest;
import com.bankingsystem.model.Transaction;
import com.bankingsystem.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour la gestion des transactions
 */
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    /**
     * Créer une nouvelle transaction
     */
    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionRequest transactionRequest) {
        try {
            // Obtenir l'ID de l'utilisateur authentifié (temporaire)
            Long userId = getCurrentUserId();
            Transaction transaction = transactionService.createTransaction(userId, transactionRequest);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir une transaction par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable Long id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            
            // Vérifier que l'utilisateur est le propriétaire de la transaction
            if (!isTransactionOwner(transaction)) {
                return ResponseEntity.status(403).body("Accès non autorisé à cette transaction");
            }
            
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir une transaction par sa référence
     */
    @GetMapping("/reference/{reference}")
    public ResponseEntity<?> getTransactionByReference(@PathVariable String reference) {
        try {
            return transactionService.getTransactionByReference(reference)
                .map(transaction -> {
                    // Vérifier que l'utilisateur est le propriétaire de la transaction
                    if (!isTransactionOwner(transaction)) {
                        return ResponseEntity.status(403).<Transaction>body(null);
                    }
                    return ResponseEntity.ok(transaction);
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Lister les transactions de l'utilisateur authentifié
     */
    @GetMapping
    public ResponseEntity<?> getUserTransactions() {
        try {
            Long userId = getCurrentUserId();
            List<Transaction> transactions = transactionService.getUserTransactions(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Lister les dernières transactions de l'utilisateur
     */
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestTransactions() {
        try {
            Long userId = getCurrentUserId();
            List<Transaction> transactions = transactionService.getLatestUserTransactions(userId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Lister les transactions par type
     */
    @GetMapping("/type/{transactionType}")
    public ResponseEntity<?> getTransactionsByType(@PathVariable String transactionType) {
        try {
            Transaction.TransactionType type = Transaction.TransactionType.valueOf(transactionType.toUpperCase());
            Long userId = getCurrentUserId();
            List<Transaction> transactions = transactionService.getUserTransactionsByType(userId, type);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Lister les transactions par période
     */
    @GetMapping("/daterange")
    public ResponseEntity<?> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Long userId = getCurrentUserId();
            List<Transaction> transactions = transactionService.getUserTransactionsByDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir le résumé des transactions de l'utilisateur
     */
    @GetMapping("/summary")
    public ResponseEntity<?> getTransactionSummary() {
        try {
            Long userId = getCurrentUserId();
            
            Map<String, Object> summary = new HashMap<>();
            
            // Total des dépôts
            BigDecimal totalDeposits = transactionService.sumTransactionsByUserAndType(
                userId, Transaction.TransactionType.DEPOSIT);
            summary.put("totalDeposits", totalDeposits);
            
            // Total des retraits
            BigDecimal totalWithdrawals = transactionService.sumTransactionsByUserAndType(
                userId, Transaction.TransactionType.WITHDRAWAL);
            summary.put("totalWithdrawals", totalWithdrawals);
            
            // Total des transferts sortants
            BigDecimal totalTransfersOut = transactionService.sumTransactionsByUserAndType(
                userId, Transaction.TransactionType.TRANSFER);
            summary.put("totalTransfersOut", totalTransfersOut);
            
            // Total des paiements
            BigDecimal totalPayments = transactionService.sumTransactionsByUserAndType(
                userId, Transaction.TransactionType.PAYMENT);
            summary.put("totalPayments", totalPayments);
            
            // Solde net
            BigDecimal netBalance = totalDeposits.subtract(totalWithdrawals)
                .subtract(totalTransfersOut).subtract(totalPayments);
            summary.put("netBalance", netBalance);
            
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Annuler une transaction
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransaction(@PathVariable Long id) {
        try {
            Transaction transaction = transactionService.getTransactionById(id);
            
            // Vérifier que l'utilisateur est le propriétaire de la transaction
            if (!isTransactionOwner(transaction)) {
                return ResponseEntity.status(403).body("Accès non autorisé à cette transaction");
            }
            
            Transaction cancelledTransaction = transactionService.cancelTransaction(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Transaction annulée avec succès");
            response.put("transactionId", cancelledTransaction.getId().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir les statistiques des transactions (endpoint admin)
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getTransactionStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Compter les transactions par statut
            long completedCount = transactionService.countTransactionsByStatus(Transaction.TransactionStatus.COMPLETED);
            long pendingCount = transactionService.countTransactionsByStatus(Transaction.TransactionStatus.PENDING);
            long failedCount = transactionService.countTransactionsByStatus(Transaction.TransactionStatus.FAILED);
            long cancelledCount = transactionService.countTransactionsByStatus(Transaction.TransactionStatus.CANCELLED);
            
            stats.put("completedTransactions", completedCount);
            stats.put("pendingTransactions", pendingCount);
            stats.put("failedTransactions", failedCount);
            stats.put("cancelledTransactions", cancelledCount);
            stats.put("totalTransactions", completedCount + pendingCount + failedCount + cancelledCount);
            
            // Total des commissions du mois
            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth())
                .withHour(23).withMinute(59).withSecond(59);
            
            BigDecimal monthlyCommissions = transactionService.sumCommissionsByDateRange(startOfMonth, endOfMonth);
            stats.put("monthlyCommissions", monthlyCommissions);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir l'ID de l'utilisateur authentifié (temporaire)
     */
    private Long getCurrentUserId() {
        // Temporaire - à implémenter avec le vrai système d'authentification
        return 1L;
    }
    
    /**
     * Vérifier si l'utilisateur est le propriétaire de la transaction
     */
    private boolean isTransactionOwner(Transaction transaction) {
        try {
            Long currentUserId = getCurrentUserId();
            return transaction.getUser().getId().equals(currentUserId);
        } catch (Exception e) {
            return false;
        }
    }
}