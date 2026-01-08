package com.bankingsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO pour les requêtes de transaction
 */
public class TransactionRequest {
    
    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", message = "Le montant doit être supérieur à 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Le type de transaction est obligatoire")
    private String transactionType;
    
    private String description;
    
    // Pour les transferts et virements
    private String sourceAccountNumber;
    private String destinationAccountNumber;
    
    // Pour les dépôts et retraits
    private String accountNumber;
    
    public TransactionRequest() {}
    
    public TransactionRequest(BigDecimal amount, String transactionType, String description) {
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }
    
    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }
    
    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }
    
    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}