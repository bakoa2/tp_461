package com.bankingsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le numéro de compte est obligatoire")
    @Column(unique = true)
    private String accountNumber;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le type de compte est obligatoire")
    private AccountType accountType;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "L'opérateur est obligatoire")
    private OperatorType operatorType;
    
    @NotNull(message = "Le solde est obligatoire")
    @DecimalMin(value = "0.0", message = "Le solde ne peut pas être négatif")
    @Column(precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "currency")
    private String currency = "XAF";
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Column(name = "is_locked")
    private boolean isLocked = false;

    @Column(name = "daily_limit", precision = 19, scale = 2)
    private BigDecimal dailyLimit;
    
    @Column(name = "monthly_limit", precision = 19, scale = 2)
    private BigDecimal monthlyLimit;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "sourceAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.Set<Transaction> outgoingTransactions = new java.util.HashSet<>();
    
    @OneToMany(mappedBy = "destinationAccount", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.Set<Transaction> incomingTransactions = new java.util.HashSet<>();
    
    // Constructeurs
    public Account() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Account(String accountNumber, AccountType accountType, OperatorType operatorType, User user) {
        this();
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.operatorType = operatorType;
        this.user = user;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public AccountType getAccountType() { return accountType; }
    public void setAccountType(AccountType accountType) { this.accountType = accountType; }
    
    public OperatorType getOperatorType() { return operatorType; }
    public void setOperatorType(OperatorType operatorType) { this.operatorType = operatorType; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }
    
    public BigDecimal getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(BigDecimal dailyLimit) { this.dailyLimit = dailyLimit; }
    
    public BigDecimal getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public java.util.Set<Transaction> getOutgoingTransactions() { return outgoingTransactions; }
    public void setOutgoingTransactions(java.util.Set<Transaction> outgoingTransactions) { 
        this.outgoingTransactions = outgoingTransactions; 
    }
    
    public java.util.Set<Transaction> getIncomingTransactions() { return incomingTransactions; }
    public void setIncomingTransactions(java.util.Set<Transaction> incomingTransactions) { 
        this.incomingTransactions = incomingTransactions; 
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Méthodes métier
    public void credit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            this.balance = this.balance.add(amount);
        }
    }
    
    public void debit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && 
            amount.compareTo(this.balance) <= 0 && 
            !isLocked) {
            this.balance = this.balance.subtract(amount);
        }
    }
    
    public boolean hasSufficientFunds(BigDecimal amount) {
        return !isLocked && balance.compareTo(amount) >= 0;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType=" + accountType +
                ", operatorType=" + operatorType +
                ", balance=" + balance +
                ", currency='" + currency + '\'' +
                ", isActive=" + isActive +
                ", isLocked=" + isLocked +
                '}';
    }
    
    /**
     * Types de comptes disponibles
     */
    public enum AccountType {
        CURRENT("Compte Courant"),
        SAVINGS("Compte Épargne"),
        BUSINESS("Compte Professionnel");
        
        private final String description;
        
        AccountType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Types d'opérateurs (Pattern Abstract Factory)
     */
    public enum OperatorType {
        BANK("Banque"),
        MOBILE_MONEY("Mobile Money"),
        INTERNATIONAL("International");
        
        private final String description;
        
        OperatorType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}