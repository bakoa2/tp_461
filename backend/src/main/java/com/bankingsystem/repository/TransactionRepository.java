package com.bankingsystem.repository;

import com.bankingsystem.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour les transactions (Pattern Repository)
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Trouver une transaction par sa référence
     */
    Optional<Transaction> findByReference(String reference);
    
    /**
     * Trouver des transactions par utilisateur
     */
    List<Transaction> findByUserId(Long userId);
    
    /**
     * Trouver des transactions par compte source
     */
    List<Transaction> findBySourceAccountId(Long sourceAccountId);
    
    /**
     * Trouver des transactions par compte destination
     */
    List<Transaction> findByDestinationAccountId(Long destinationAccountId);
    
    /**
     * Trouver des transactions par type
     */
    List<Transaction> findByTransactionType(Transaction.TransactionType transactionType);
    
    /**
     * Trouver des transactions par statut
     */
    List<Transaction> findByStatus(Transaction.TransactionStatus status);
    
    /**
     * Trouver des transactions par utilisateur et type
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.transactionType = :transactionType")
    List<Transaction> findByUserAndTransactionType(@Param("userId") Long userId, 
                                                   @Param("transactionType") Transaction.TransactionType transactionType);
    
    /**
     * Trouver des transactions par période
     */
    @Query("SELECT t FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    /**
     * Trouver des transactions par utilisateur et période
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.createdAt BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserAndDateRange(@Param("userId") Long userId, 
                                             @Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
    
    /**
     * Calculer le total des transactions par utilisateur et type
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.transactionType = :transactionType AND t.status = 'COMPLETED'")
    BigDecimal sumByUserAndTransactionType(@Param("userId") Long userId, 
                                          @Param("transactionType") Transaction.TransactionType transactionType);
    
    /**
     * Compter les transactions par statut
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.status = :status")
    long countByStatus(@Param("status") Transaction.TransactionStatus status);
    
    /**
     * Trouver les dernières transactions d'un utilisateur
     */
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findLatestByUser(@Param("userId") Long userId);
    
    /**
     * Calculer le total des commissions pour une période
     */
    @Query("SELECT SUM(t.commission) FROM Transaction t WHERE t.createdAt BETWEEN :startDate AND :endDate AND t.status = 'COMPLETED'")
    BigDecimal sumCommissionsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Vérifier si une référence existe déjà
     */
    boolean existsByReference(String reference);
}