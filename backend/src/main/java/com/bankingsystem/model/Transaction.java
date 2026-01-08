package com.bankingsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String reference;
    
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le type de transaction est obligatoire")
    private TransactionType transactionType;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le statut est obligatoire")
    private TransactionStatus status;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "commission", precision = 19, scale = 2)
    
    private BigDecimal commission = BigDecimal.ZERO;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_account_id")
    private Account destinationAccount;
    
    // Constructeurs
    public Transaction() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
        this.reference = generateReference();
    }
    
    public Transaction(BigDecimal amount, TransactionType transactionType, User user) {
        this();
        this.amount = amount;
        this.transactionType = transactionType;
        this.user = user;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }
    
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getCommission() { return commission; }
    public void setCommission(BigDecimal commission) { this.commission = commission; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Account getSourceAccount() { return sourceAccount; }
    public void setSourceAccount(Account sourceAccount) { this.sourceAccount = sourceAccount; }
    
    public Account getDestinationAccount() { return destinationAccount; }
    public void setDestinationAccount(Account destinationAccount) { this.destinationAccount = destinationAccount; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Méthodes métier
    private String generateReference() {
        return "TXN" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }
    
    public boolean isCompleted() {
        return TransactionStatus.COMPLETED.equals(status);
    }
    
    public boolean isPending() {
        return TransactionStatus.PENDING.equals(status);
    }
    
    public boolean hasFailed() {
        return TransactionStatus.FAILED.equals(status);
    }
    
    public void markAsCompleted() {
        this.status = TransactionStatus.COMPLETED;
    }
    
    public void markAsFailed() {
        this.status = TransactionStatus.FAILED;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", commission=" + commission +
                ", createdAt=" + createdAt +
                '}';
    }
    
    /**
     * Types de transactions disponibles
     */
    public enum TransactionType {
        DEPOSIT("Dépôt"),
        WITHDRAWAL("Retrait"),
        TRANSFER("Virement"),
        PAYMENT("Paiement"),
        FEE("Frais");
        
        private final String description;
        
        TransactionType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * Statuts de transaction
     */
    public enum TransactionStatus {
        PENDING("En attente"),
        COMPLETED("Complétée"),
        FAILED("Échouée"),
        CANCELLED("Annulée");
        
        private final String description;
        
        TransactionStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}