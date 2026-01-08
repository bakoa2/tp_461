package com.bankingsystem.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO pour les requêtes de création/compte
 */
public class AccountRequest {
    
    @NotBlank(message = "Le type de compte est obligatoire")
    private String accountType;
    
    @NotBlank(message = "L'opérateur est obligatoire")
    private String operatorType;
    
    @DecimalMin(value = "0.0", message = "Le solde initial ne peut pas être négatif")
    private BigDecimal initialBalance = BigDecimal.ZERO;
    
    private String currency = "XAF";
    
    private BigDecimal dailyLimit;
    
    private BigDecimal monthlyLimit;
    
    public AccountRequest() {}
    
    public AccountRequest(String accountType, String operatorType, BigDecimal initialBalance) {
        this.accountType = accountType;
        this.operatorType = operatorType;
        this.initialBalance = initialBalance;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public String getOperatorType() {
        return operatorType;
    }
    
    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }
    
    public BigDecimal getInitialBalance() {
        return initialBalance;
    }
    
    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public BigDecimal getDailyLimit() {
        return dailyLimit;
    }
    
    public void setDailyLimit(BigDecimal dailyLimit) {
        this.dailyLimit = dailyLimit;
    }
    
    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }
    
    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }
}