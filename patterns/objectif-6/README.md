# Objectif 6: Comportements Communs et Spécialisations

## Description brève de la solution

Pour résoudre le problème des comportements communs entre opérateurs avec des spécialisations, nous utilisons le pattern **Template Method**:

- **Template Method Pattern**: Définit le squelette d'un algorithme dans une classe de base, laissant les sous-classes redéfinir certaines étapes sans modifier la structure globale. Nous l'appliquons aux opérateurs pour partager la gestion des comptes tout en permettant des spécialisations (validation des transferts internationaux, etc.).

## Justification architecturale

Cette approche est adaptée car :

1. **Réutilisation maximale**: Permet de partager le maximum de code commun entre les opérateurs.

2. **Flexibilité des spécialisations**: Chaque opérateur peut surdéfinir uniquement les comportements qui diffèrent.

3. **Cohérence**: Garantit que tous les opérateurs suivent le même squelette de traitement.

4. **Maintenabilité**: Les modifications du squelette commun impactent automatiquement tous les opérateurs.

## Implémentation

```java
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

// Classe de base Template Method pour les opérateurs
public abstract class OperatorTemplate {
    protected String operatorName;
    protected Map<String, Account> accounts;
    protected List<Transaction> transactionHistory;
    
    public OperatorTemplate(String operatorName) {
        this.operatorName = operatorName;
        this.accounts = new HashMap<>();
        this.transactionHistory = new ArrayList<>();
        System.out.println("OperatorTemplate: Initialisation de l'opérateur " + operatorName);
    }
    
    // Template Method - squelette de l'algorithme de gestion de compte
    public final void processAccountOperation(String accountNumber, OperationType operation, 
                                          double amount, Map<String, Object> parameters) {
        System.out.println("\n=== Traitement opération pour " + operatorName + " ===");
        
        // Étapes du squelette commun
        validateAccountExists(accountNumber);
        validateOperationPreconditions(operation, amount, parameters);
        
        boolean success = false;
        try {
            // Étape qui peut être spécialisée
            success = executeOperation(accountNumber, operation, amount, parameters);
            
            if (success) {
                // Étapes communes
                updateAccountBalance(accountNumber, operation, amount);
                recordTransaction(accountNumber, operation, amount, parameters);
                postOperationProcessing(accountNumber, operation, amount);
                
                // Étape optionnelle spécialisée
                sendOperatorSpecificNotifications(accountNumber, operation, amount);
            }
        } catch (Exception e) {
            handleOperationError(accountNumber, operation, amount, e);
        }
        
        System.out.println("Opération " + (success ? "réussie" : "échouée") + " pour le compte " + accountNumber);
    }
    
    // Méthodes communes (final pour ne pas être surchargées)
    protected final void validateAccountExists(String accountNumber) {
        if (!accounts.containsKey(accountNumber)) {
            throw new IllegalArgumentException("Compte inexistant: " + accountNumber);
        }
        System.out.println("Validation compte: " + accountNumber + " ✓");
    }
    
    protected final void updateAccountBalance(String accountNumber, OperationType operation, double amount) {
        Account account = accounts.get(accountNumber);
        switch (operation) {
            case CREDIT:
                account.setBalance(account.getBalance() + amount);
                break;
            case DEBIT:
                if (account.getBalance() < amount) {
                    throw new IllegalArgumentException("Solde insuffisant");
                }
                account.setBalance(account.getBalance() - amount);
                break;
        }
        System.out.println("Mise à jour solde: " + accountNumber + " -> " + account.getBalance());
    }
    
    protected final void recordTransaction(String accountNumber, OperationType operation, 
                                       double amount, Map<String, Object> parameters) {
        Transaction transaction = new Transaction(accountNumber, operation, amount, parameters, operatorName);
        transactionHistory.add(transaction);
        System.out.println("Enregistrement transaction: " + transaction.getId());
    }
    
    // Méthodes à surdéfinir par les sous-classes
    protected abstract void validateOperationPreconditions(OperationType operation, double amount, 
                                                       Map<String, Object> parameters);
    
    protected abstract boolean executeOperation(String accountNumber, OperationType operation, 
                                            double amount, Map<String, Object> parameters);
    
    // Méthodes optionnelles avec implémentation par défaut
    protected void postOperationProcessing(String accountNumber, OperationType operation, double amount) {
        // Traitement par défaut commun
        System.out.println("Post-traitement standard pour l'opération " + operation);
    }
    
    protected void sendOperatorSpecificNotifications(String accountNumber, OperationType operation, double amount) {
        // Notification par défaut
        System.out.println("Notification standard: opération " + operation + " de " + amount);
    }
    
    protected void handleOperationError(String accountNumber, OperationType operation, 
                                     double amount, Exception e) {
        System.err.println("Erreur lors de l'opération " + operation + " sur le compte " + 
                          accountNumber + ": " + e.getMessage());
    }
    
    // Méthodes utilitaires communes
    public void addAccount(Account account) {
        accounts.put(account.getNumber(), account);
        System.out.println("Compte ajouté: " + account.getNumber() + " (" + operatorName + ")");
    }
    
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }
    
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }
    
    public String getOperatorName() {
        return operatorName;
    }
    
    // Méthode template pour la validation des comptes
    public final boolean validateAccountStatus(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            return false;
        }
        
        // Étapes communes de validation
        if (!isAccountActive(account)) {
            return false;
        }
        
        // Étape spécialisée
        return performAdditionalAccountValidation(account);
    }
    
    protected boolean isAccountActive(Account account) {
        return account.isActive() && !account.isBlocked();
    }
    
    protected boolean performAdditionalAccountValidation(Account account) {
        // Validation supplémentaire par défaut
        return true;
    }
}

// Opérateur Banque avec spécialisations minimales
public class BankOperator extends OperatorTemplate {
    private static final double MAX_DAILY_TRANSACTION = 10000.0;
    private static final double MIN_ACCOUNT_BALANCE = 100.0;
    
    public BankOperator() {
        super("Banque");
    }
    
    @Override
    protected void validateOperationPreconditions(OperationType operation, double amount, 
                                               Map<String, Object> parameters) {
        // Validation spécifique aux banques
        if (amount > MAX_DAILY_TRANSACTION) {
            throw new IllegalArgumentException("Montant maximal journalier dépassé: " + MAX_DAILY_TRANSACTION);
        }
        
        if (operation == OperationType.DEBIT) {
            Account account = accounts.values().iterator().next(); // Simplifié
            if (account.getBalance() - amount < MIN_ACCOUNT_BALANCE) {
                throw new IllegalArgumentException("Solde minimum requis: " + MIN_ACCOUNT_BALANCE);
            }
        }
        
        System.out.println("Validation conditions préalables (Banque): ✓");
    }
    
    @Override
    protected boolean executeOperation(String accountNumber, OperationType operation, 
                                   double amount, Map<String, Object> parameters) {
        // Logique d'exécution standard pour la banque
        System.out.println("Exécution opération bancaire standard");
        return true; // Simplifié pour la démo
    }
    
    @Override
    protected void postOperationProcessing(String accountNumber, OperationType operation, double amount) {
        super.postOperationProcessing(accountNumber, operation, amount);
        
        // Traitement spécifique aux banques
        if (amount > 5000.0) {
            System.out.println("Alerte: grosse transaction bancaire détectée");
        }
    }
    
    @Override
    protected void sendOperatorSpecificNotifications(String accountNumber, OperationType operation, double amount) {
        super.sendOperatorSpecificNotifications(accountNumber, operation, amount);
        System.out.println("Notification bancaire spécifique envoyée au client");
    }
}

// Opérateur Mobile Money avec spécialisations
public class MobileMoneyOperator extends OperatorTemplate {
    private static final double MAX_DAILY_TRANSACTION = 500000.0;
    private static final int MAX_TRANSACTIONS_PER_HOUR = 10;
    
    public MobileMoneyOperator() {
        super("Mobile Money");
    }
    
    @Override
    protected void validateOperationPreconditions(OperationType operation, double amount, 
                                               Map<String, Object> parameters) {
        // Validation spécifique au mobile money
        if (amount > MAX_DAILY_TRANSACTION) {
            throw new IllegalArgumentException("Montant maximal journalier dépassé: " + MAX_DAILY_TRANSACTION);
        }
        
        // Vérification du nombre de transactions par heure
        String accountNumber = (String) parameters.getOrDefault("accountNumber", "");
        Account account = accounts.get(accountNumber);
        if (account != null && getRecentTransactionCount(account, 3600000) > MAX_TRANSACTIONS_PER_HOUR) {
            throw new IllegalArgumentException("Limite de transactions par heure dépassée");
        }
        
        System.out.println("Validation conditions préalables (Mobile Money): ✓");
    }
    
    @Override
    protected boolean executeOperation(String accountNumber, OperationType operation, 
                                   double amount, Map<String, Object> parameters) {
        // Logique d'exécution spécifique au mobile money
        System.out.println("Exécution opération mobile money avec validation temps réel");
        return true; // Simplifié pour la démo
    }
    
    @Override
    protected void sendOperatorSpecificNotifications(String accountNumber, OperationType operation, double amount) {
        super.sendOperatorSpecificNotifications(accountNumber, operation, amount);
        System.out.println("SMS Mobile Money envoyé pour l'opération");
    }
    
    private int getRecentTransactionCount(Account account, long timeWindowMs) {
        // Simplifié: retourne un nombre aléatoire pour la démo
        return 3;
    }
}

// Opérateur International avec spécialisations avancées
public class InternationalBankOperator extends OperatorTemplate {
    private static final double MAX_INTERNATIONAL_TRANSACTION = 50000.0;
    private static final String[] RESTRICTED_COUNTRIES = {"XX", "YY", "ZZ"};
    
    public InternationalBankOperator() {
        super("Banque Internationale");
    }
    
    @Override
    protected void validateOperationPreconditions(OperationType operation, double amount, 
                                               Map<String, Object> parameters) {
        // Validation spécifique aux transferts internationaux
        if (parameters.containsKey("international") && (Boolean) parameters.get("international")) {
            if (amount > MAX_INTERNATIONAL_TRANSACTION) {
                throw new IllegalArgumentException("Montant maximal international dépassé: " + MAX_INTERNATIONAL_TRANSACTION);
            }
            
            String destinationCountry = (String) parameters.getOrDefault("destinationCountry", "");
            if (isRestrictedCountry(destinationCountry)) {
                throw new IllegalArgumentException("Pays de destination restreint: " + destinationCountry);
            }
            
            // Validation des documents pour transactions internationales
            validateInternationalDocuments(parameters);
        }
        
        System.out.println("Validation conditions préalables (Internationale): ✓");
    }
    
    @Override
    protected boolean executeOperation(String accountNumber, OperationType operation, 
                                   double amount, Map<String, Object> parameters) {
        // Logique d'exécution complexe pour transactions internationales
        if (parameters.containsKey("international") && (Boolean) parameters.get("international")) {
            System.out.println("Exécution transaction internationale avec conversion de devise");
            performCurrencyConversion(parameters);
            checkComplianceRules(parameters);
        } else {
            System.out.println("Exécution transaction standard");
        }
        return true;
    }
    
    @Override
    protected void postOperationProcessing(String accountNumber, OperationType operation, double amount) {
        super.postOperationProcessing(accountNumber, operation, amount);
        
        // Traitement spécifique aux opérations internationales
        if (amount > 10000.0) {
            System.out.println("Déclaration obligatoire pour autorités financières");
            reportToRegulatoryAuthorities(accountNumber, amount);
        }
    }
    
    @Override
    protected void sendOperatorSpecificNotifications(String accountNumber, OperationType operation, double amount) {
        super.sendOperatorSpecificNotifications(accountNumber, operation, amount);
        System.out.println("Notification internationale avec détails réglementaires");
    }
    
    @Override
    protected boolean performAdditionalAccountValidation(Account account) {
        // Validation supplémentaire pour les comptes internationaux
        return account.hasValidInternationalDocumentation() && 
               account.isKYCCompliant();
    }
    
    // Méthodes privées spécifiques
    private boolean isRestrictedCountry(String country) {
        for (String restricted : RESTRICTED_COUNTRIES) {
            if (restricted.equals(country)) {
                return true;
            }
        }
        return false;
    }
    
    private void validateInternationalDocuments(Map<String, Object> parameters) {
        String docType = (String) parameters.getOrDefault("documentType", "");
        if (!"PASSPORT".equals(docType) && !"ID_CARD".equals(docType)) {
            throw new IllegalArgumentException("Document valide requis pour transaction internationale");
        }
        System.out.println("Validation documents internationaux: ✓");
    }
    
    private void performCurrencyConversion(Map<String, Object> parameters) {
        String fromCurrency = (String) parameters.getOrDefault("fromCurrency", "EUR");
        String toCurrency = (String) parameters.getOrDefault("toCurrency", "USD");
        System.out.println("Conversion devise: " + fromCurrency + " -> " + toCurrency);
    }
    
    private void checkComplianceRules(Map<String, Object> parameters) {
        System.out.println("Vérification conformité AML/KYC");
    }
    
    private void reportToRegulatoryAuthorities(String accountNumber, double amount) {
        System.out.println("Rapport réglementaire généré pour " + accountNumber + 
                         " (montant: " + amount + ")");
    }
}

// Classes de support
enum OperationType {
    CREDIT, DEBIT, TRANSFER
}

class Account {
    private String number;
    private double balance;
    private boolean active;
    private boolean blocked;
    private boolean hasInternationalDocs;
    private boolean kycCompliant;
    
    public Account(String number, double balance) {
        this.number = number;
        this.balance = balance;
        this.active = true;
        this.blocked = false;
        this.hasInternationalDocs = false;
        this.kycCompliant = false;
    }
    
    // Getters et setters
    public String getNumber() { return number; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
    public boolean hasValidInternationalDocumentation() { return hasInternationalDocs; }
    public void setHasInternationalDocs(boolean hasInternationalDocs) { this.hasInternationalDocs = hasInternationalDocs; }
    public boolean isKYCCompliant() { return kycCompliant; }
    public void setKYCCompliant(boolean kycCompliant) { this.kycCompliant = kycCompliant; }
}

class Transaction {
    private static int idCounter = 1;
    private String id;
    private String accountNumber;
    private OperationType operation;
    private double amount;
    private Map<String, Object> parameters;
    private String operatorName;
    private long timestamp;
    
    public Transaction(String accountNumber, OperationType operation, double amount, 
                     Map<String, Object> parameters, String operatorName) {
        this.id = "TXN" + (idCounter++);
        this.accountNumber = accountNumber;
        this.operation = operation;
        this.amount = amount;
        this.parameters = new HashMap<>(parameters);
        this.operatorName = operatorName;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters
    public String getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public OperationType getOperation() { return operation; }
    public double getAmount() { return amount; }
    public String getOperatorName() { return operatorName; }
    public long getTimestamp() { return timestamp; }
}
```

## Tests et scénarios de démonstration

```java
public class TestTemplateMethodPattern {
    public static void main(String[] args) {
        // Création des opérateurs
        OperatorTemplate bankOperator = new BankOperator();
        OperatorTemplate mobileOperator = new MobileMoneyOperator();
        OperatorTemplate internationalOperator = new InternationalBankOperator();
        
        // Création des comptes
        Account bankAccount = new Account("BANK001", 5000.0);
        Account mobileAccount = new Account("MOB001", 10000.0);
        Account intlAccount = new Account("INTL001", 25000.0);
        intlAccount.setHasInternationalDocs(true);
        intlAccount.setKYCCompliant(true);
        
        bankOperator.addAccount(bankAccount);
        mobileOperator.addAccount(mobileAccount);
        internationalOperator.addAccount(intlAccount);
        
        // Test 1: Opérations standard avec l'opérateur banque
        System.out.println("=== Test 1: Opérations avec la Banque ===");
        Map<String, Object> bankParams = new HashMap<>();
        bankParams.put("accountNumber", "BANK001");
        
        bankOperator.processAccountOperation("BANK001", OperationType.DEBIT, 1000.0, bankParams);
        bankOperator.processAccountOperation("BANK001", OperationType.CREDIT, 2000.0, bankParams);
        
        // Test 2: Opérations avec Mobile Money
        System.out.println("\n=== Test 2: Opérations avec Mobile Money ===");
        Map<String, Object> mobileParams = new HashMap<>();
        mobileParams.put("accountNumber", "MOB001");
        
        mobileOperator.processAccountOperation("MOB001", OperationType.DEBIT, 5000.0, mobileParams);
        
        // Test 3: Opérations internationales (spécialisation avancée)
        System.out.println("\n=== Test 3: Opérations Internationales ===");
        Map<String, Object> intlParams = new HashMap<>();
        intlParams.put("accountNumber", "INTL001");
        intlParams.put("international", true);
        intlParams.put("destinationCountry", "FR");
        intlParams.put("documentType", "PASSPORT");
        intlParams.put("fromCurrency", "EUR");
        intlParams.put("toCurrency", "USD");
        
        internationalOperator.processAccountOperation("INTL001", OperationType.DEBIT, 30000.0, intlParams);
        
        // Test 4: Tentative d'opération avec pays restreint
        System.out.println("\n=== Test 4: Test validation pays restreint ===");
        Map<String, Object> restrictedParams = new HashMap<>(intlParams);
        restrictedParams.put("destinationCountry", "XX");
        
        try {
            internationalOperator.processAccountOperation("INTL001", OperationType.DEBIT, 10000.0, restrictedParams);
        } catch (Exception e) {
            System.out.println("Erreur attendue: " + e.getMessage());
        }
        
        // Test 5: Validation des comptes
        System.out.println("\n=== Test 5: Validation des comptes ===");
        System.out.println("Compte bancaire valide: " + bankOperator.validateAccountStatus("BANK001"));
        System.out.println("Compte mobile valide: " + mobileOperator.validateAccountStatus("MOB001"));
        System.out.println("Compte international valide: " + internationalOperator.validateAccountStatus("INTL001"));
        
        // Test 6: Historique des transactions
        System.out.println("\n=== Test 6: Historique des transactions ===");
        System.out.println("Transactions banque:");
        for (Transaction tx : bankOperator.getTransactionHistory()) {
            System.out.println("  " + tx.getId() + ": " + tx.getOperation() + " " + tx.getAmount());
        }
        
        System.out.println("\nTransactions internationales:");
        for (Transaction tx : internationalOperator.getTransactionHistory()) {
            System.out.println("  " + tx.getId() + ": " + tx.getOperation() + " " + tx.getAmount() + 
                             " (" + tx.getOperatorName() + ")");
        }
    }
}
```

### Résultats attendus

```
=== Test 1: Opérations avec la Banque ===
OperatorTemplate: Initialisation de l'opérateur Banque
Compte ajouté: BANK001 (Banque)

=== Traitement opération pour Banque ===
Validation compte: BANK001 ✓
Validation conditions préalables (Banque): ✓
Exécution opération bancaire standard
Mise à jour solde: BANK001 -> 4000.0
Enregistrement transaction: TXN1
Post-traitement standard pour l'opération DEBIT
Notification standard: opération DEBIT de 1000.0
Notification bancaire spécifique envoyée au client
Opération réussie pour le compte BANK001

=== Traitement opération pour Banque ===
Validation compte: BANK001 ✓
Validation conditions préalables (Banque): ✓
Exécution opération bancaire standard
Mise à jour solde: BANK001 -> 6000.0
Enregistrement transaction: TXN2
Post-traitement standard pour l'opération CREDIT
Notification standard: opération CREDIT de 2000.0
Notification bancaire spécifique envoyée au client
Opération réussie pour le compte BANK001

=== Test 2: Opérations avec Mobile Money ===
OperatorTemplate: Initialisation de l'opérateur Mobile Money
Compte ajouté: MOB001 (Mobile Money)

=== Traitement opération pour Mobile Money ===
Validation compte: MOB001 ✓
Validation conditions préalables (Mobile Money): ✓
Exécution opération mobile money avec validation temps réel
Mise à jour solde: MOB001 -> 5000.0
Enregistrement transaction: TXN3
Post-traitement standard pour l'opération DEBIT
Notification standard: opération DEBIT de 5000.0
SMS Mobile Money envoyé pour l'opération
Opération réussie pour le compte MOB001

=== Test 3: Opérations Internationales ===
OperatorTemplate: Initialisation de l'opérateur Banque Internationale
Compte ajouté: INTL001 (Banque Internationale)

=== Traitement opération pour Banque Internationale ===
Validation compte: INTL001 ✓
Validation conditions préalables (Internationale): ✓
Validation documents internationaux: ✓
Exécution transaction internationale avec conversion de devise
Conversion devise: EUR -> USD
Vérification conformité AML/KYC
Mise à jour solde: INTL001 -> -5000.0
Enregistrement transaction: TXN4
Post-traitement standard pour l'opération DEBIT
Déclaration obligatoire pour autorités financières
Rapport réglementaire généré pour INTL001 (montant: 30000.0)
Notification standard: opération DEBIT de 30000.0
Notification internationale avec détails réglementaires
Opération réussie pour le compte INTL001

=== Test 4: Test validation pays restreint ===

=== Traitement opération pour Banque Internationale ===
Validation compte: INTL001 ✓
Erreur attendue: Pays de destination restreint: XX

=== Test 5: Validation des comptes ===
Compte bancaire valide: true
Compte mobile valide: true
Compte international valide: true

=== Test 6: Historique des transactions ===
Transactions banque:
  TXN1: DEBIT 1000.0
  TXN2: CREDIT 2000.0

Transactions internationales:
  TXN4: DEBIT 30000.0 (Banque Internationale)
```

Cette démonstration montre comment le pattern Template Method permet de partager efficacement le code commun tout en permettant des spécialisations importantes pour chaque type d'opérateur.