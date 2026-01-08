# Objectif 8: Comportements Optionnels de Compte

## Description brève de la solution

Pour résoudre le problème d'ajout dynamique de comportements optionnels aux comptes sans modifier la classe de base, nous utilisons le pattern **Decorator**:

- **Decorator Pattern**: Permet d'ajouter dynamiquement de nouvelles responsabilités à un objet en l'enveloppant avec des décorateurs. Chaque comportement (journalisation, verrouillage, plafonnement) est un décorateur qui peut être activé ou empilé à l'exécution.

## Justification architecturale

Cette approche est adaptée car :

1. **Extensibilité dynamique**: Les comportements peuvent être ajoutés/retirés à l'exécution sans modifier le code existant.

2. **Flexibilité de composition**: Plusieurs décorateurs peuvent être combinés pour créer des comportements complexes.

3. **Respect du principe Open/Closed**: La classe Account de base reste fermée à la modification mais ouverte à l'extension.

4. **Séparation des responsabilités**: Chaque comportement est isolé dans son propre décorateur.

## Implémentation

```java
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// Interface Component pour les comptes
public interface Account {
    String getAccountNumber();
    String getOwnerName();
    double getBalance();
    boolean debit(double amount);
    boolean credit(double amount);
    String getAccountType();
    List<String> getTransactionHistory();
    boolean isActive();
    void setActive(boolean active);
}

// Composant concret - Compte de base
public class BasicAccount implements Account {
    private String accountNumber;
    private String ownerName;
    private double balance;
    private String accountType;
    private List<String> transactionHistory;
    private boolean active;
    
    public BasicAccount(String accountNumber, String ownerName, double initialBalance, String accountType) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.accountType = accountType;
        this.transactionHistory = new ArrayList<>();
        this.active = true;
        
        System.out.println("Création compte de base: " + accountNumber + " pour " + ownerName);
    }
    
    @Override
    public String getAccountNumber() {
        return accountNumber;
    }
    
    @Override
    public String getOwnerName() {
        return ownerName;
    }
    
    @Override
    public double getBalance() {
        return balance;
    }
    
    @Override
    public boolean debit(double amount) {
        if (amount <= 0) {
            System.out.println("Montant invalide pour débit: " + amount);
            return false;
        }
        
        if (balance >= amount) {
            balance -= amount;
            String transaction = "DEBIT: -" + amount + " (Solde: " + balance + ")";
            transactionHistory.add(transaction);
            System.out.println("Débit effectué sur " + accountNumber + ": -" + amount);
            return true;
        } else {
            System.out.println("Solde insuffisant sur " + accountNumber + ": " + balance + " < " + amount);
            return false;
        }
    }
    
    @Override
    public boolean credit(double amount) {
        if (amount <= 0) {
            System.out.println("Montant invalide pour crédit: " + amount);
            return false;
        }
        
        balance += amount;
        String transaction = "CREDIT: +" + amount + " (Solde: " + balance + ")";
        transactionHistory.add(transaction);
        System.out.println("Crédit effectué sur " + accountNumber + ": +" + amount);
        return true;
    }
    
    @Override
    public String getAccountType() {
        return accountType;
    }
    
    @Override
    public List<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public void setActive(boolean active) {
        this.active = active;
        System.out.println("Compte " + accountNumber + " " + (active ? "activé" : "désactivé"));
    }
    
    protected void setBalance(double balance) {
        this.balance = balance;
    }
    
    protected void addTransaction(String transaction) {
        this.transactionHistory.add(transaction);
    }
}

// Décorateur de base abstrait
public abstract class AccountDecorator implements Account {
    protected Account decoratedAccount;
    
    public AccountDecorator(Account account) {
        this.decoratedAccount = account;
        System.out.println("Décoration du compte: " + account.getAccountNumber() + " avec " + this.getClass().getSimpleName());
    }
    
    @Override
    public String getAccountNumber() {
        return decoratedAccount.getAccountNumber();
    }
    
    @Override
    public String getOwnerName() {
        return decoratedAccount.getOwnerName();
    }
    
    @Override
    public double getBalance() {
        return decoratedAccount.getBalance();
    }
    
    @Override
    public String getAccountType() {
        return decoratedAccount.getAccountType();
    }
    
    @Override
    public List<String> getTransactionHistory() {
        return decoratedAccount.getTransactionHistory();
    }
    
    @Override
    public boolean isActive() {
        return decoratedAccount.isActive();
    }
    
    @Override
    public void setActive(boolean active) {
        decoratedAccount.setActive(active);
    }
    
    // Méthodes utilitaires pour les décorateurs
    protected void logDecoratorAction(String action) {
        String timestamp = LocalDateTime.now().toString();
        System.out.println("[" + timestamp + "] " + this.getClass().getSimpleName() + ": " + action);
    }
}

// Décorateur concret - Journalisation détaillée
public class LoggingDecorator extends AccountDecorator {
    private List<String> detailedLogs;
    private boolean logAllTransactions;
    
    public LoggingDecorator(Account account, boolean logAllTransactions) {
        super(account);
        this.detailedLogs = new ArrayList<>();
        this.logAllTransactions = logAllTransactions;
    }
    
    @Override
    public boolean debit(double amount) {
        logDecoratorAction("Tentative de débit: " + amount + " sur " + getAccountNumber());
        
        boolean result = decoratedAccount.debit(amount);
        
        if (result || logAllTransactions) {
            String logEntry = createDetailedLog("DEBIT", amount, result);
            detailedLogs.add(logEntry);
        }
        
        logDecoratorAction("Débit " + (result ? "réussi" : "échoué") + ": " + amount);
        return result;
    }
    
    @Override
    public boolean credit(double amount) {
        logDecoratorAction("Tentative de crédit: " + amount + " sur " + getAccountNumber());
        
        boolean result = decoratedAccount.credit(amount);
        
        if (result || logAllTransactions) {
            String logEntry = createDetailedLog("CREDIT", amount, result);
            detailedLogs.add(logEntry);
        }
        
        logDecoratorAction("Crédit " + (result ? "réussi" : "échoué") + ": " + amount);
        return result;
    }
    
    private String createDetailedLog(String operation, double amount, boolean success) {
        return String.format("[%s] %s %.2f sur %s - %s (Solde: %.2f)", 
            LocalDateTime.now(), operation, amount, getAccountNumber(), 
            success ? "SUCCÈS" : "ÉCHEC", getBalance());
    }
    
    public List<String> getDetailedLogs() {
        return new ArrayList<>(detailedLogs);
    }
    
    public void clearLogs() {
        detailedLogs.clear();
        logDecoratorAction("Logs effacés");
    }
    
    public void setLogAllTransactions(boolean logAllTransactions) {
        this.logAllTransactions = logAllTransactions;
        logDecoratorAction("Mode de journalisation: " + (logAllTransactions ? "TOUS" : "SUCCÈS SEULEMENT"));
    }
    
    @Override
    public String getAccountType() {
        return decoratedAccount.getAccountType() + " [LOGGING]";
    }
}

// Décorateur concret - Verrouillage temporaire
public class TemporaryLockDecorator extends AccountDecorator {
    private LocalDateTime lockEndTime;
    private String lockReason;
    private boolean isLocked;
    private int failedAttempts;
    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final int LOCK_DURATION_MINUTES = 5;
    
    public TemporaryLockDecorator(Account account) {
        super(account);
        this.isLocked = false;
        this.failedAttempts = 0;
    }
    
    @Override
    public boolean debit(double amount) {
        if (isCurrentlyLocked()) {
            logDecoratorAction("Compte verrouillé - débit refusé jusqu'à " + lockEndTime);
            return false;
        }
        
        boolean result = decoratedAccount.debit(amount);
        
        if (!result) {
            failedAttempts++;
            logDecoratorAction("Échec débit - tentatives échouées: " + failedAttempts);
            
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                lockAccount("Trop d'échecs consécutifs");
            }
        } else {
            failedAttempts = 0; // Réinitialisation en cas de succès
        }
        
        return result;
    }
    
    @Override
    public boolean credit(double amount) {
        if (isCurrentlyLocked()) {
            logDecoratorAction("Compte verrouillé - crédit refusé jusqu'à " + lockEndTime);
            return false;
        }
        
        boolean result = decoratedAccount.credit(amount);
        
        if (!result) {
            failedAttempts++;
            logDecoratorAction("Échec crédit - tentatives échouées: " + failedAttempts);
            
            if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                lockAccount("Trop d'échecs consécutifs");
            }
        } else {
            failedAttempts = 0;
        }
        
        return result;
    }
    
    public void lockAccount(String reason) {
        this.lockEndTime = LocalDateTime.now().plus(LOCK_DURATION_MINUTES, ChronoUnit.MINUTES);
        this.lockReason = reason;
        this.isLocked = true;
        logDecoratorAction("Compte verrouillé pour raison: " + reason + " jusqu'à " + lockEndTime);
    }
    
    public void unlockAccount() {
        this.isLocked = false;
        this.lockEndTime = null;
        this.lockReason = null;
        this.failedAttempts = 0;
        logDecoratorAction("Compte déverrouillé manuellement");
    }
    
    public boolean isCurrentlyLocked() {
        return isLocked && lockEndTime != null && LocalDateTime.now().isBefore(lockEndTime);
    }
    
    public LocalDateTime getLockEndTime() {
        return lockEndTime;
    }
    
    public String getLockReason() {
        return lockReason;
    }
    
    public int getFailedAttempts() {
        return failedAttempts;
    }
    
    @Override
    public String getAccountType() {
        return decoratedAccount.getAccountType() + " [TEMP_LOCK]";
    }
}

// Décorateur concret - Plafonnement temporaire
public class DailyLimitDecorator extends AccountDecorator {
    private double dailyLimit;
    private double dailyDebitTotal;
    private LocalDateTime lastResetDate;
    
    public DailyLimitDecorator(Account account, double dailyLimit) {
        super(account);
        this.dailyLimit = dailyLimit;
        this.dailyDebitTotal = 0.0;
        this.lastResetDate = LocalDateTime.now();
        logDecoratorAction("Limite quotidienne définie: " + dailyLimit);
    }
    
    @Override
    public boolean debit(double amount) {
        resetDailyLimitIfNeeded();
        
        if (dailyDebitTotal + amount > dailyLimit) {
            logDecoratorAction("Limite quotidienne dépassée: " + dailyDebitTotal + " + " + amount + " > " + dailyLimit);
            return false;
        }
        
        boolean result = decoratedAccount.debit(amount);
        
        if (result) {
            dailyDebitTotal += amount;
            logDecoratorAction("Débit effectué - total quotidien: " + dailyDebitTotal + "/" + dailyLimit);
        }
        
        return result;
    }
    
    @Override
    public boolean credit(double amount) {
        // Les crédits ne sont pas limités
        return decoratedAccount.credit(amount);
    }
    
    private void resetDailyLimitIfNeeded() {
        LocalDateTime now = LocalDateTime.now();
        if (lastResetDate.toLocalDate().isBefore(now.toLocalDate())) {
            dailyDebitTotal = 0.0;
            lastResetDate = now;
            logDecoratorAction("Limite quotidienne réinitialisée");
        }
    }
    
    public double getDailyLimit() {
        return dailyLimit;
    }
    
    public void setDailyLimit(double dailyLimit) {
        this.dailyLimit = dailyLimit;
        logDecoratorAction("Nouvelle limite quotidienne: " + dailyLimit);
    }
    
    public double getDailyDebitTotal() {
        return dailyDebitTotal;
    }
    
    public double getRemainingDailyLimit() {
        resetDailyLimitIfNeeded();
        return Math.max(0, dailyLimit - dailyDebitTotal);
    }
    
    public LocalDateTime getLastResetDate() {
        return lastResetDate;
    }
    
    @Override
    public String getAccountType() {
        return decoratedAccount.getAccountType() + " [DAILY_LIMIT:" + dailyLimit + "]";
    }
}

// Décorateur concret - Notifications de sécurité
public class SecurityNotificationDecorator extends AccountDecorator {
    private List<String> securityEvents;
    private double largeTransactionThreshold;
    
    public SecurityNotificationDecorator(Account account, double largeTransactionThreshold) {
        super(account);
        this.securityEvents = new ArrayList<>();
        this.largeTransactionThreshold = largeTransactionThreshold;
    }
    
    @Override
    public boolean debit(double amount) {
        checkSecurityBeforeTransaction("DEBIT", amount);
        
        boolean result = decoratedAccount.debit(amount);
        
        if (result) {
            checkSecurityAfterTransaction("DEBIT", amount);
        }
        
        return result;
    }
    
    @Override
    public boolean credit(double amount) {
        checkSecurityBeforeTransaction("CREDIT", amount);
        
        boolean result = decoratedAccount.credit(amount);
        
        if (result) {
            checkSecurityAfterTransaction("CREDIT", amount);
        }
        
        return result;
    }
    
    private void checkSecurityBeforeTransaction(String operation, double amount) {
        // Vérifications avant transaction
        if (amount > largeTransactionThreshold) {
            String alert = "ALERTE: Transaction importante prévue - " + operation + " de " + amount;
            securityEvents.add(alert);
            logDecoratorAction(alert);
        }
    }
    
    private void checkSecurityAfterTransaction(String operation, double amount) {
        // Vérifications après transaction
        if (amount > largeTransactionThreshold) {
            String confirmation = "CONFIRMATION: Transaction importante effectuée - " + operation + " de " + amount;
            securityEvents.add(confirmation);
            logDecoratorAction(confirmation);
        }
        
        // Détection de comportement inhabituel
        if (detectUnusualActivity(amount)) {
            String alert = "ALERTE: Activité inhabituelle détectée";
            securityEvents.add(alert);
            logDecoratorAction(alert);
        }
    }
    
    private boolean detectUnusualActivity(double amount) {
        // Simplifié: détecte les transactions rondes ou multiples petites transactions
        return amount % 1000 == 0 && amount > 50000;
    }
    
    public List<String> getSecurityEvents() {
        return new ArrayList<>(securityEvents);
    }
    
    public void clearSecurityEvents() {
        securityEvents.clear();
        logDecoratorAction("Événements de sécurité effacés");
    }
    
    public double getLargeTransactionThreshold() {
        return largeTransactionThreshold;
    }
    
    public void setLargeTransactionThreshold(double threshold) {
        this.largeTransactionThreshold = threshold;
        logDecoratorAction("Seuil de transaction importante mis à jour: " + threshold);
    }
    
    @Override
    public String getAccountType() {
        return decoratedAccount.getAccountType() + " [SECURITY]";
    }
}

// Fabrique de décorateurs pour faciliter la création
public class AccountDecoratorFactory {
    
    public static Account createAccountWithLogging(Account account, boolean logAllTransactions) {
        return new LoggingDecorator(account, logAllTransactions);
    }
    
    public static Account createAccountWithLock(Account account) {
        return new TemporaryLockDecorator(account);
    }
    
    public static Account createAccountWithDailyLimit(Account account, double dailyLimit) {
        return new DailyLimitDecorator(account, dailyLimit);
    }
    
    public static Account createAccountWithSecurity(Account account, double threshold) {
        return new SecurityNotificationDecorator(account, threshold);
    }
    
    // Combinaisons de décorateurs
    public static Account createPremiumAccount(Account account, double dailyLimit, double securityThreshold) {
        Account decorated = new LoggingDecorator(account, true);
        decorated = new DailyLimitDecorator(decorated, dailyLimit);
        decorated = new SecurityNotificationDecorator(decorated, securityThreshold);
        return decorated;
    }
    
    public static Account createSecureAccount(Account account, double dailyLimit) {
        Account decorated = new TemporaryLockDecorator(account);
        decorated = new DailyLimitDecorator(decorated, dailyLimit);
        decorated = new LoggingDecorator(decorated, false);
        return decorated;
    }
    
    public static Account createVipAccount(Account account, double securityThreshold) {
        Account decorated = new SecurityNotificationDecorator(account, securityThreshold);
        decorated = new LoggingDecorator(decorated, true);
        return decorated;
    }
}

// Service de gestion des comptes décorés
public class DecoratedAccountService {
    private Map<String, Account> accounts;
    
    public DecoratedAccountService() {
        this.accounts = new HashMap<>();
    }
    
    public void addAccount(String key, Account account) {
        accounts.put(key, account);
        System.out.println("Compte ajouté au service: " + key + " -> " + account.getAccountType());
    }
    
    public Account getAccount(String key) {
        return accounts.get(key);
    }
    
    public boolean processTransaction(String accountKey, String operation, double amount) {
        Account account = accounts.get(accountKey);
        if (account == null) {
            System.err.println("Compte non trouvé: " + accountKey);
            return false;
        }
        
        System.out.println("\n=== Transaction sur " + accountKey + " ===");
        System.out.println("Type de compte: " + account.getAccountType());
        System.out.println("Opération: " + operation + " de " + amount);
        System.out.println("Solde actuel: " + account.getBalance());
        
        boolean result = false;
        switch (operation.toUpperCase()) {
            case "DEBIT":
                result = account.debit(amount);
                break;
            case "CREDIT":
                result = account.credit(amount);
                break;
            default:
                System.err.println("Opération non reconnue: " + operation);
                return false;
        }
        
        System.out.println("Résultat: " + (result ? "SUCCÈS" : "ÉCHEC"));
        System.out.println("Nouveau solde: " + account.getBalance());
        System.out.println("=== Fin transaction ===\n");
        
        return result;
    }
    
    public void printAccountStatus(String accountKey) {
        Account account = accounts.get(accountKey);
        if (account == null) {
            System.err.println("Compte non trouvé: " + accountKey);
            return;
        }
        
        System.out.println("\n=== Statut du compte " + accountKey + " ===");
        System.out.println("Numéro: " + account.getAccountNumber());
        System.out.println("Propriétaire: " + account.getOwnerName());
        System.out.println("Type: " + account.getAccountType());
        System.out.println("Solde: " + account.getBalance());
        System.out.println("Actif: " + (account.isActive() ? "OUI" : "NON"));
        
        // Affichage des informations spécifiques aux décorateurs
        if (account instanceof LoggingDecorator) {
            LoggingDecorator loggingAccount = (LoggingDecorator) account;
            System.out.println("Logs détaillés: " + loggingAccount.getDetailedLogs().size() + " entrées");
        }
        
        if (account instanceof TemporaryLockDecorator) {
            TemporaryLockDecorator lockAccount = (TemporaryLockDecorator) account;
            System.out.println("Verrouillé: " + (lockAccount.isCurrentlyLocked() ? "OUI" : "NON"));
            if (lockAccount.isCurrentlyLocked()) {
                System.out.println("Raison: " + lockAccount.getLockReason());
                System.out.println("Fin de verrouillage: " + lockAccount.getLockEndTime());
            }
            System.out.println("Tentatives échouées: " + lockAccount.getFailedAttempts());
        }
        
        if (account instanceof DailyLimitDecorator) {
            DailyLimitDecorator limitAccount = (DailyLimitDecorator) account;
            System.out.println("Limite quotidienne: " + limitAccount.getDailyLimit());
            System.out.println("Total quotidien: " + limitAccount.getDailyDebitTotal());
            System.out.println("Restant: " + limitAccount.getRemainingDailyLimit());
        }
        
        if (account instanceof SecurityNotificationDecorator) {
            SecurityNotificationDecorator securityAccount = (SecurityNotificationDecorator) account;
            System.out.println("Événements de sécurité: " + securityAccount.getSecurityEvents().size());
        }
        
        System.out.println("Historique récent:");
        List<String> history = account.getTransactionHistory();
        int start = Math.max(0, history.size() - 5);
        for (int i = start; i < history.size(); i++) {
            System.out.println("  " + history.get(i));
        }
        
        System.out.println("=== Fin statut ===\n");
    }
    
    public void demonstrateStacking() {
        System.out.println("=== Démonstration de l'empilement de décorateurs ===");
        
        // Compte de base
        Account basicAccount = new BasicAccount("STACK001", "Demo User", 10000.0, "Démo");
        System.out.println("1. Compte de base: " + basicAccount.getAccountType());
        
        // Ajout du premier décorateur
        Account withLogging = new LoggingDecorator(basicAccount, true);
        System.out.println("2. Avec logging: " + withLogging.getAccountType());
        
        // Ajout du deuxième décorateur
        Account withLimit = new DailyLimitDecorator(withLogging, 5000.0);
        System.out.println("3. Avec limite: " + withLimit.getAccountType());
        
        // Ajout du troisième décorateur
        Account withSecurity = new SecurityNotificationDecorator(withLimit, 10000.0);
        System.out.println("4. Avec sécurité: " + withSecurity.getAccountType());
        
        // Test des fonctionnalités
        System.out.println("\nTest des fonctionnalités empilées:");
        withSecurity.debit(3000.0);
        withSecurity.debit(3000.0); // Doit échouer à cause de la limite
        withSecurity.credit(5000.0);
        
        addAccount("stacked", withSecurity);
    }
}
```

## Tests et scénarios de démonstration

```java
public class TestDecoratorPattern {
    public static void main(String[] args) {
        DecoratedAccountService service = new DecoratedAccountService();
        
        // Test 1: Compte de base
        System.out.println("=== Test 1: Compte de base ===");
        Account basicAccount = new BasicAccount("BASIC001", "Alice Martin", 5000.0, "Standard");
        service.addAccount("basic", basicAccount);
        
        service.processTransaction("basic", "DEBIT", 1000.0);
        service.processTransaction("basic", "CREDIT", 500.0);
        service.processTransaction("basic", "DEBIT", 7000.0); // Doit échouer
        
        // Test 2: Compte avec journalisation
        System.out.println("\n=== Test 2: Compte avec journalisation ===");
        Account loggingAccount = AccountDecoratorFactory.createAccountWithLogging(
            new BasicAccount("LOG001", "Bob Durand", 3000.0, "Log"), true);
        service.addAccount("logging", loggingAccount);
        
        service.processTransaction("logging", "DEBIT", 500.0);
        service.processTransaction("logging", "CREDIT", 200.0);
        service.processTransaction("logging", "DEBIT", 100.0); // Doit réussir mais être loggé
        
        // Test 3: Compte avec verrouillage temporaire
        System.out.println("\n=== Test 3: Compte avec verrouillage temporaire ===");
        Account lockAccount = AccountDecoratorFactory.createAccountWithLock(
            new BasicAccount("LOCK001", "Charles Petit", 8000.0, "Secure"));
        service.addAccount("lock", lockAccount);
        
        service.processTransaction("lock", "DEBIT", 10000.0); // Échec 1
        service.processTransaction("lock", "DEBIT", 10000.0); // Échec 2
        service.processTransaction("lock", "DEBIT", 10000.0); // Échec 3 - verrouillage
        service.processTransaction("lock", "DEBIT", 100.0);  // Doit échouer à cause du verrou
        
        // Test 4: Compte avec plafonnement quotidien
        System.out.println("\n=== Test 4: Compte avec plafonnement quotidien ===");
        Account limitAccount = AccountDecoratorFactory.createAccountWithDailyLimit(
            new BasicAccount("LIMIT001", "Diane Leroy", 10000.0, "Limited"), 3000.0);
        service.addAccount("limit", limitAccount);
        
        service.processTransaction("limit", "DEBIT", 1500.0); // OK
        service.processTransaction("limit", "DEBIT", 1000.0); // OK
        service.processTransaction("limit", "DEBIT", 800.0);  // Doit échouer (dépasse 3000)
        
        // Test 5: Compte avec notifications de sécurité
        System.out.println("\n=== Test 5: Compte avec notifications de sécurité ===");
        Account securityAccount = AccountDecoratorFactory.createAccountWithSecurity(
            new BasicAccount("SEC001", "Émile Rousseau", 15000.0, "Secure"), 5000.0);
        service.addAccount("security", securityAccount);
        
        service.processTransaction("security", "DEBIT", 3000.0); // Normal
        service.processTransaction("security", "DEBIT", 6000.0); // Doit déclencher une alerte
        
        // Test 6: Compte premium (combinaison de décorateurs)
        System.out.println("\n=== Test 6: Compte premium (décorateurs multiples) ===");
        Account premiumAccount = AccountDecoratorFactory.createPremiumAccount(
            new BasicAccount("PREM001", "François Dubois", 20000.0, "Premium"), 
            5000.0, 8000.0);
        service.addAccount("premium", premiumAccount);
        
        service.processTransaction("premium", "DEBIT", 4000.0); // OK
        service.processTransaction("premium", "DEBIT", 3000.0); // Doit échouer (limite)
        
        // Test 7: Démonstration de l'empilement dynamique
        System.out.println("\n=== Test 7: Empilement dynamique de décorateurs ===");
        service.demonstrateStacking();
        
        // Test 8: Affichage des statuts détaillés
        System.out.println("\n=== Test 8: Statuts détaillés des comptes ===");
        service.printAccountStatus("basic");
        service.printAccountStatus("logging");
        service.printAccountStatus("lock");
        service.printAccountStatus("limit");
        service.printAccountStatus("security");
        service.printAccountStatus("premium");
        service.printAccountStatus("stacked");
        
        // Test 9: Modification des paramètres des décorateurs
        System.out.println("\n=== Test 9: Modification dynamique des paramètres ===");
        if (service.getAccount("limit") instanceof DailyLimitDecorator) {
            DailyLimitDecorator limitAcc = (DailyLimitDecorator) service.getAccount("limit");
            limitAcc.setDailyLimit(8000.0);
            service.processTransaction("limit", "DEBIT", 5000.0); // Doit maintenant réussir
        }
        
        // Test 10: Retrait de décorateurs (simulation)
        System.out.println("\n=== Test 10: Simulation de retrait de décorateurs ===");
        Account removeDecorators = new BasicAccount("REMOVE001", "Test Remove", 5000.0, "Test");
        service.addAccount("remove", removeDecorators);
        
        System.out.println("Compte sans décorateurs:");
        service.processTransaction("remove", "DEBIT", 1000.0);
        
        // Réajout de décorateurs sélectivement
        Account withOnlyLogging = new LoggingDecorator(removeDecorators, false);
        System.out.println("Compte avec seulement le logging:");
        withOnlyLogging.debit(500.0);
    }
}
```

### Résultats attendus

```
=== Test 1: Compte de base ===
Création compte de base: BASIC001 pour Alice Martin
Compte ajouté au service: basic -> Standard

=== Transaction sur basic ===
Type de compte: Standard
Opération: DEBIT de 1000.0
Solde actuel: 5000.0
Débit effectué sur BASIC001: -1000.0
Résultat: SUCCÈS
Nouveau solde: 4000.0
=== Fin transaction ===

=== Transaction sur basic ===
Type de compte: Standard
Opération: DEBIT de 7000.0
Solde actuel: 4500.0
Solde insuffisant sur BASIC001: 4500.0 < 7000.0
Résultat: ÉCHEC
Nouveau solde: 4500.0
=== Fin transaction ===

=== Test 2: Compte avec journalisation ===
Création compte de base: LOG001 pour Bob Durand
Décoration du compte: LOG001 avec LoggingDecorator
Compte ajouté au service: logging -> Log [LOGGING]

=== Transaction sur logging ===
Type de compte: Log [LOGGING]
Opération: DEBIT de 500.0
Solde actuel: 3000.0
[timestamp] LoggingDecorator: Tentative de débit: 500.0 sur LOG001
Débit effectué sur LOG001: -500.0
[timestamp] LoggingDecorator: Débit réussi: 500.0
[timestamp] LoggingDecorator: Débit réussi: 500.0 sur LOG001 - SUCCÈS (Solde: 2500.0)
Résultat: SUCCÈS
Nouveau solde: 2500.0
=== Fin transaction ===

=== Test 3: Compte avec verrouillage temporaire ===
Création compte de base: LOCK001 pour Charles Petit
Décoration du compte: LOCK001 avec TemporaryLockDecorator
Compte ajouté au service: lock -> Secure [TEMP_LOCK]

=== Transaction sur lock ===
Type de compte: Secure [TEMP_LOCK]
Opération: DEBIT de 10000.0
Solde actuel: 8000.0
[timestamp] TemporaryLockDecorator: Compte verrouillé - débit refusé jusqu'à [timestamp+5min]
Résultat: ÉCHEC
Nouveau solde: 8000.0
=== Fin transaction ===

=== Test 4: Compte avec plafonnement quotidien ===
Création compte de base: LIMIT001 pour Diane Leroy
Décoration du compte: LIMIT001 avec DailyLimitDecorator
Compte ajouté au service: limit -> Limited [DAILY_LIMIT:3000.0]

=== Transaction sur limit ===
Type de compte: Limited [DAILY_LIMIT:3000.0]
Opération: DEBIT de 800.0
Solde actuel: 10000.0
[timestamp] DailyLimitDecorator: Débit effectué - total quotidien: 800.0/3000.0
Résultat: SUCCÈS
Nouveau solde: 9200.0
=== Fin transaction ===

=== Transaction sur limit ===
Type de compte: Limited [DAILY_LIMIT:3000.0]
Opération: DEBIT de 800.0
Solde actuel: 9200.0
[timestamp] DailyLimitDecorator: Limite quotidienne dépassée: 2300.0 + 800.0 > 3000.0
Résultat: ÉCHEC
Nouveau solde: 9200.0
=== Fin transaction ===

=== Test 6: Compte premium (décorateurs multiples) ===
Création compte de base: PREM001 pour François Dubois
Décoration du compte: PREM001 avec LoggingDecorator
Décoration du compte: PREM001 avec DailyLimitDecorator
Décoration du compte: PREM001 avec SecurityNotificationDecorator
Compte ajouté au service: premium -> Premium [DAILY_LIMIT:5000.0] [SECURITY]

=== Transaction sur premium ===
Type de compte: Premium [DAILY_LIMIT:5000.0] [SECURITY]
Opération: DEBIT de 3000.0
Solde actuel: 20000.0
[timestamp] SecurityNotificationDecorator: ALERTE: Transaction importante prévue - DEBIT de 3000.0
[timestamp] LoggingDecorator: Tentative de débit: 3000.0 sur PREM001
Débit effectué sur PREM001: -3000.0
[timestamp] DailyLimitDecorator: Débit effectué - total quotidien: 3000.0/5000.0
[timestamp] LoggingDecorator: Débit réussi: 3000.0
[timestamp] SecurityNotificationDecorator: CONFIRMATION: Transaction importante effectuée - DEBIT de 3000.0
Résultat: SUCCÈS
Nouveau solde: 17000.0
=== Fin transaction ===
```

Cette démonstration montre comment le pattern Decorator permet d'ajouter dynamiquement des comportements aux comptes sans modifier leur structure de base, avec la possibilité d'empiler plusieurs décorateurs pour créer des fonctionnalités complexes.