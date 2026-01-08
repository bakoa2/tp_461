package com.bankingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour les requêtes d'authentification
 */
public class AuthRequest {
    
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    private String username;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
    
    // Type d'authentification optionnel
    private String authType;
    
    // Données biométriques ou OTP si nécessaire
    private String authData;
    
    public AuthRequest() {}
    
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getAuthType() {
        return authType;
    }
    
    public void setAuthType(String authType) {
        this.authType = authType;
    }
    
    public String getAuthData() {
        return authData;
    }
    
    public void setAuthData(String authData) {
        this.authData = authData;
    }
}