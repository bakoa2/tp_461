package com.bankingsystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Column(unique = true)
    private String username;
    
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(unique = true)
    private String email;
    
    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthType authType = AuthType.PASSWORD;
    
    @Column(name = "biometric_data")
    private String biometricData;
    
    @Column(name = "ar_data")
    private String arData;
    
    @Column(name = "is_active")
    private boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Account> accounts = new HashSet<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new HashSet<>();
    
    // Constructeurs
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public User(String username, String email, String password) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public AuthType getAuthType() { return authType; }
    public void setAuthType(AuthType authType) { this.authType = authType; }
    
    public String getBiometricData() { return biometricData; }
    public void setBiometricData(String biometricData) { this.biometricData = biometricData; }
    
    public String getArData() { return arData; }
    public void setArData(String arData) { this.arData = arData; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Set<Account> getAccounts() { return accounts; }
    public void setAccounts(Set<Account> accounts) { this.accounts = accounts; }
    
    public Set<Transaction> getTransactions() { return transactions; }
    public void setTransactions(Set<Transaction> transactions) { this.transactions = transactions; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", authType=" + authType +
                ", isActive=" + isActive +
                '}';
    }
    
    /**
     * Énumération des types d'authentification (Pattern Strategy)
     */
    public enum AuthType {
        PASSWORD("Mot de passe"),
        BIOMETRIC("Biométrie"),
        OTP("OTP"),
        REALITE_AUGMENTEE("Réalité Augmentée");
        
        private final String description;
        
        AuthType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
}