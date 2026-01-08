package com.bankingsystem.controller;

import com.bankingsystem.dto.AccountRequest;
import com.bankingsystem.model.Account;
import com.bankingsystem.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour la gestion des comptes bancaires
 */
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    /**
     * Créer un nouveau compte
     */
    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountRequest accountRequest) {
        try {
            // Obtenir l'utilisateur authentifié
            Long userId = getCurrentUserId();
            Account account = accountService.createAccount(userId, accountRequest);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir un compte par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAccount(@PathVariable Long id) {
        try {
            Account account = accountService.getAccountById(id);
            
            // Vérifier que l'utilisateur est le propriétaire du compte
            if (!isAccountOwner(account)) {
                return ResponseEntity.status(403).body("Accès non autorisé à ce compte");
            }
            
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir un compte par son numéro
     */
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<?> getAccountByNumber(@PathVariable String accountNumber) {
        try {
            return accountService.getAccountByNumber(accountNumber)
                .map(account -> {
                    // Vérifier que l'utilisateur est le propriétaire du compte
                    if (!isAccountOwner(account)) {
                        return ResponseEntity.status(403).<Account>body(null);
                    }
                    return ResponseEntity.ok(account);
                })
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Lister les comptes de l'utilisateur authentifié
     */
    @GetMapping
    public ResponseEntity<?> getUserAccounts() {
        try {
            Long userId = getCurrentUserId();
            List<Account> accounts = accountService.getUserAccounts(userId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Créditer un compte
     */
    @PostMapping("/{accountNumber}/credit")
    public ResponseEntity<?> creditAccount(@PathVariable String accountNumber, @RequestBody Map<String, BigDecimal> request) {
        try {
            Account account = accountService.getAccountByNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));
            
            // Vérifier que l'utilisateur est le propriétaire du compte
            if (!isAccountOwner(account)) {
                return ResponseEntity.status(403).body("Accès non autorisé à ce compte");
            }
            
            BigDecimal amount = request.get("amount");
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Le montant doit être positif");
            }
            
            Account updatedAccount = accountService.creditAccount(accountNumber, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Débiter un compte
     */
    @PostMapping("/{accountNumber}/debit")
    public ResponseEntity<?> debitAccount(@PathVariable String accountNumber, @RequestBody Map<String, BigDecimal> request) {
        try {
            Account account = accountService.getAccountByNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Compte non trouvé"));
            
            // Vérifier que l'utilisateur est le propriétaire du compte
            if (!isAccountOwner(account)) {
                return ResponseEntity.status(403).body("Accès non autorisé à ce compte");
            }
            
            BigDecimal amount = request.get("amount");
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Le montant doit être positif");
            }
            
            Account updatedAccount = accountService.debitAccount(accountNumber, amount);
            return ResponseEntity.ok(updatedAccount);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir le solde total de l'utilisateur
     */
    @GetMapping("/balance/total")
    public ResponseEntity<?> getTotalBalance() {
        try {
            Long userId = getCurrentUserId();
            BigDecimal totalBalance = accountService.getTotalBalanceByUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalBalance", totalBalance);
            response.put("currency", "XAF");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Lister les comptes par type (endpoint admin)
     */
    @GetMapping("/type/{accountType}")
    public ResponseEntity<?> getAccountsByType(@PathVariable String accountType) {
        try {
            Account.AccountType type = Account.AccountType.valueOf(accountType.toUpperCase());
            List<Account> accounts = accountService.getAccountsByType(type);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Lister les comptes par opérateur (endpoint admin)
     */
    @GetMapping("/operator/{operatorType}")
    public ResponseEntity<?> getAccountsByOperator(@PathVariable String operatorType) {
        try {
            Account.OperatorType type = Account.OperatorType.valueOf(operatorType.toUpperCase());
            List<Account> accounts = accountService.getAccountsByOperator(type);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Verrouiller un compte
     */
    @PostMapping("/{id}/lock")
    public ResponseEntity<?> lockAccount(@PathVariable Long id) {
        try {
            Account account = accountService.getAccountById(id);
            
            // Vérifier que l'utilisateur est le propriétaire du compte
            if (!isAccountOwner(account)) {
                return ResponseEntity.status(403).body("Accès non autorisé à ce compte");
            }
            
            accountService.lockAccount(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Compte verrouillé avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Déverrouiller un compte
     */
    @PostMapping("/{id}/unlock")
    public ResponseEntity<?> unlockAccount(@PathVariable Long id) {
        try {
            Account account = accountService.getAccountById(id);
            
            // Vérifier que l'utilisateur est le propriétaire du compte
            if (!isAccountOwner(account)) {
                return ResponseEntity.status(403).body("Accès non autorisé à ce compte");
            }
            
            accountService.unlockAccount(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Compte déverrouillé avec succès");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir les statistiques des comptes (endpoint admin)
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getAccountStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            long bankCount = accountService.countAccountsByOperator(Account.OperatorType.BANK);
            long mobileMoneyCount = accountService.countAccountsByOperator(Account.OperatorType.MOBILE_MONEY);
            long internationalCount = accountService.countAccountsByOperator(Account.OperatorType.INTERNATIONAL);
            
            stats.put("totalBankAccounts", bankCount);
            stats.put("totalMobileMoneyAccounts", mobileMoneyCount);
            stats.put("totalInternationalAccounts", internationalCount);
            stats.put("totalAccounts", bankCount + mobileMoneyCount + internationalCount);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Obtenir l'ID de l'utilisateur authentifié
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User user = 
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            // Extraire l'ID du username (assumant que le username contient l'ID ou une recherche supplémentaire est nécessaire)
            return 1L; // Temporaire, à améliorer avec une vraie extraction
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }
    
    /**
     * Vérifier si l'utilisateur est le propriétaire du compte
     */
    private boolean isAccountOwner(Account account) {
        try {
            Long currentUserId = getCurrentUserId();
            return account.getUser().getId().equals(currentUserId);
        } catch (Exception e) {
            return false;
        }
    }
}