package com.bankingsystem.repository;

import com.bankingsystem.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour les comptes (Pattern Repository)
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * Trouver un compte par son numéro
     */
    Optional<Account> findByAccountNumber(String accountNumber);
    
    /**
     * Trouver des comptes par utilisateur
     */
    List<Account> findByUserId(Long userId);
    
    /**
     * Trouver des comptes par type de compte
     */
    List<Account> findByAccountType(Account.AccountType accountType);
    
    /**
     * Trouver des comptes par opérateur
     */
    List<Account> findByOperatorType(Account.OperatorType operatorType);
    
    /**
     * Trouver des comptes actifs
     */
    @Query("SELECT a FROM Account a WHERE a.isActive = true")
    List<Account> findActiveAccounts();
    
    /**
     * Trouver des comptes non verrouillés
     */
    @Query("SELECT a FROM Account a WHERE a.isLocked = false")
    List<Account> findUnlockedAccounts();
    
    /**
     * Trouver des comptes par utilisateur et par type
     */
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.accountType = :accountType")
    List<Account> findByUserAndAccountType(@Param("userId") Long userId, @Param("accountType") Account.AccountType accountType);
    
    /**
     * Calculer le solde total pour un utilisateur
     */
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user.id = :userId AND a.isActive = true")
    BigDecimal getTotalBalanceByUser(@Param("userId") Long userId);
    
    /**
     * Compter les comptes par opérateur
     */
    @Query("SELECT COUNT(a) FROM Account a WHERE a.operatorType = :operatorType")
    long countByOperatorType(@Param("operatorType") Account.OperatorType operatorType);
    
    /**
     * Vérifier si un numéro de compte existe déjà
     */
    boolean existsByAccountNumber(String accountNumber);
}