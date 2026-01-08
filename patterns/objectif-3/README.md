# Objectif 3: Construction de Transactions

## Description brève de la solution

Pour résoudre le problème de construction flexible des transactions avec étapes optionnelles, nous utilisons le pattern **Builder**:

- **Builder Pattern**: Permet de construire des objets complexes étape par étape, en séparant la construction d'un objet de sa représentation. Chaque transaction peut être configurée avec différentes étapes (vérifications, conversion, commissions, journalisation, notifications) selon les besoins.

## Justification architecturale

Cette approche est adaptée car :

1. **Flexibilité de construction**: Permet d'ajouter ou de retirer des étapes de traitement selon le type de transaction.

2. **API fluide**: Offre une interface explicite et lisible pour construire les transactions.

3. **Variations sans duplication**: Évite la duplication de code en permettant de créer différentes variantes de transactions avec le même builder.

4. **Extensibilité**: Facile d'ajouter de nouvelles étapes de traitement sans modifier les transactions existantes.

## Implémentation

```java
// Interfaces pour les étapes de transaction
public interface TransactionStep {
    boolean execute(TransactionContext context);
    String getDescription();
}

public interface TransactionStepBuilder {
    TransactionStepBuilder addStep(TransactionStep step);
    TransactionStepBuilder removeStep(String stepName);
    Transaction build();
}

// Contexte de transaction
public class TransactionContext {
    private String sourceAccount;
    private String destinationAccount;
    private double amount;
    private String currency;
    private Map<String, Object> metadata;
    private List<String> executionLog;
    
    public TransactionContext(String sourceAccount, String destinationAccount, double amount) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.currency = "EUR";
        this.metadata = new HashMap<>();
        this.executionLog = new ArrayList<>();
    }
    
    // Getters et setters
    public String getSourceAccount() { return sourceAccount; }
    public String getDestinationAccount() { return destinationAccount; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Map<String, Object> getMetadata() { return metadata; }
    public List<String> getExecutionLog() { return executionLog; }
    
    public void setCurrency(String currency) { this.currency = currency; }
    public void addMetadata(String key, Object value) { this.metadata.put(key, value); }
    public void logExecution(String message) { this.executionLog.add(message); }
}

// Étapes concrètes
public class ValidationStep implements TransactionStep {
    private IAccountValidator validator;
    
    public ValidationStep(IAccountValidator validator) {
        this.validator = validator;
    }
    
    @Override
    public boolean execute(TransactionContext context) {
        context.logExecution("Validation des comptes");
        return validator.validateAccount(context.getSourceAccount()) && 
               validator.validateAccount(context.getDestinationAccount()) &&
               validator.validateTransaction(context.getAmount());
    }
    
    @Override
    public String getDescription() {
        return "Validation des comptes";
    }
}

public class CurrencyConversionStep implements TransactionStep {
    private String targetCurrency;
    private double conversionRate;
    
    public CurrencyConversionStep(String targetCurrency, double conversionRate) {
        this.targetCurrency = targetCurrency;
        this.conversionRate = conversionRate;
    }
    
    @Override
    public boolean execute(TransactionContext context) {
        context.logExecution("Conversion de devise: " + context.getCurrency() + " -> " + targetCurrency);
        if (!context.getCurrency().equals(targetCurrency)) {
            context.setCurrency(targetCurrency);
            context.addMetadata("originalAmount", context.getAmount());
            context.addMetadata("originalCurrency", context.getCurrency());
            // Note: Le montant converti serait calculé ici
            context.addMetadata("conversionRate", conversionRate);
        }
        return true;
    }
    
    @Override
    public String getDescription() {
        return "Conversion de devise vers " + targetCurrency;
    }
}

public class CommissionStep implements TransactionStep {
    private IRateCalculator rateCalculator;
    private String transactionType;
    
    public CommissionStep(IRateCalculator rateCalculator, String transactionType) {
        this.rateCalculator = rateCalculator;
        this.transactionType = transactionType;
    }
    
    @Override
    public boolean execute(TransactionContext context) {
        context.logExecution("Calcul des commissions");
        double commission = rateCalculator.calculateCommission(context.getAmount());
        double rate = rateCalculator.calculateTransferRate(context.getAmount(), transactionType);
        
        context.addMetadata("commission", commission);
        context.addMetadata("transferRate", rate);
        context.addMetadata("totalFees", commission + rate);
        return true;
    }
    
    @Override
    public String getDescription() {
        return "Application des commissions";
    }
}

public class LoggingStep implements TransactionStep {
    private String logLevel;
    
    public LoggingStep(String logLevel) {
        this.logLevel = logLevel;
    }
    
    @Override
    public boolean execute(TransactionContext context) {
        String logMessage = String.format("[%s] Transaction: %s -> %s, montant: %.2f %s", 
            logLevel, context.getSourceAccount(), context.getDestinationAccount(), 
            context.getAmount(), context.getCurrency());
        context.logExecution("Journalisation: " + logMessage);
        return true;
    }
    
    @Override
    public String getDescription() {
        return "Journalisation (" + logLevel + ")";
    }
}

public class NotificationStep implements TransactionStep {
    private INotifier notifier;
    private boolean notifySource;
    private boolean notifyDestination;
    
    public NotificationStep(INotifier notifier, boolean notifySource, boolean notifyDestination) {
        this.notifier = notifier;
        this.notifySource = notifySource;
        this.notifyDestination = notifyDestination;
    }
    
    @Override
    public boolean execute(TransactionContext context) {
        context.logExecution("Envoi des notifications");
        String message = String.format("Transaction de %.2f %s traitée", context.getAmount(), context.getCurrency());
        
        if (notifySource) {
            notifier.sendNotification(message, context.getSourceAccount());
        }
        if (notifyDestination) {
            notifier.sendNotification(message, context.getDestinationAccount());
        }
        return true;
    }
    
    @Override
    public String getDescription() {
        return "Notification des parties";
    }
}

// Builder principal
public class TransactionBuilder implements TransactionStepBuilder {
    private List<TransactionStep> steps;
    private TransactionContext context;
    
    public TransactionBuilder(String sourceAccount, String destinationAccount, double amount) {
        this.steps = new ArrayList<>();
        this.context = new TransactionContext(sourceAccount, destinationAccount, amount);
    }
    
    public TransactionBuilder addValidation(IAccountValidator validator) {
        steps.add(new ValidationStep(validator));
        return this;
    }
    
    public TransactionBuilder addCurrencyConversion(String targetCurrency, double rate) {
        steps.add(new CurrencyConversionStep(targetCurrency, rate));
        return this;
    }
    
    public TransactionBuilder addCommission(IRateCalculator rateCalculator, String transactionType) {
        steps.add(new CommissionStep(rateCalculator, transactionType));
        return this;
    }
    
    public TransactionBuilder addLogging(String logLevel) {
        steps.add(new LoggingStep(logLevel));
        return this;
    }
    
    public TransactionBuilder addNotification(INotifier notifier, boolean notifySource, boolean notifyDestination) {
        steps.add(new NotificationStep(notifier, notifySource, notifyDestination));
        return this;
    }
    
    @Override
    public TransactionStepBuilder addStep(TransactionStep step) {
        steps.add(step);
        return this;
    }
    
    @Override
    public TransactionStepBuilder removeStep(String stepName) {
        steps.removeIf(step -> step.getDescription().contains(stepName));
        return this;
    }
    
    @Override
    public Transaction build() {
        return new Transaction(context, new ArrayList<>(steps));
    }
    
    // Méthodes utilitaires pour créer des variantes
    public static TransactionBuilder createShortTransaction(String source, String dest, double amount) {
        return new TransactionBuilder(source, dest, amount);
    }
    
    public static TransactionBuilder createFullTransaction(String source, String dest, double amount, 
                                                          IAccountValidator validator, IRateCalculator rateCalculator, 
                                                          INotifier notifier) {
        return new TransactionBuilder(source, dest, amount)
            .addValidation(validator)
            .addCommission(rateCalculator, "external")
            .addLogging("INFO")
            .addNotification(notifier, true, false);
    }
}

// Classe Transaction finale
public class Transaction {
    private TransactionContext context;
    private List<TransactionStep> steps;
    private boolean executed;
    private boolean successful;
    
    public Transaction(TransactionContext context, List<TransactionStep> steps) {
        this.context = context;
        this.steps = steps;
        this.executed = false;
        this.successful = false;
    }
    
    public boolean execute() {
        if (executed) {
            throw new IllegalStateException("Transaction déjà exécutée");
        }
        
        context.logExecution("Début de la transaction");
        successful = true;
        
        for (TransactionStep step : steps) {
            try {
                context.logExecution("Exécution: " + step.getDescription());
                boolean stepResult = step.execute(context);
                if (!stepResult) {
                    context.logExecution("Échec de l'étape: " + step.getDescription());
                    successful = false;
                    break;
                }
            } catch (Exception e) {
                context.logExecution("Erreur lors de l'étape: " + step.getDescription() + " - " + e.getMessage());
                successful = false;
                break;
            }
        }
        
        context.logExecution("Fin de la transaction - " + (successful ? "SUCCÈS" : "ÉCHEC"));
        executed = true;
        return successful;
    }
    
    public TransactionContext getContext() { return context; }
    public boolean isExecuted() { return executed; }
    public boolean isSuccessful() { return successful; }
    
    public void printSummary() {
        System.out.println("\n=== Résumé de la Transaction ===");
        System.out.println("Source: " + context.getSourceAccount());
        System.out.println("Destination: " + context.getDestinationAccount());
        System.out.println("Montant: " + context.getAmount() + " " + context.getCurrency());
        System.out.println("Statut: " + (successful ? "SUCCÈS" : "ÉCHEC"));
        System.out.println("Étapes exécutées:");
        for (String log : context.getExecutionLog()) {
            System.out.println("  - " + log);
        }
        System.out.println("Métadonnées: " + context.getMetadata());
    }
}
```

## Tests et scénarios de démonstration

```java
public class TestTransactionBuilder {
    public static void main(String[] args) {
        // Création des dépendances (réutilisées des objectifs précédents)
        IOperatorFactory bankFactory = new BankFactory();
        IOperatorFactory mobileFactory = new MobileMoneyFactory();
        
        // Test 1: Transaction courte (minimum d'étapes)
        System.out.println("=== Test 1: Transaction courte ===");
        Transaction shortTransaction = TransactionBuilder.createShortTransaction("ACC001", "ACC002", 500.0)
            .addValidation(bankFactory.createValidator())
            .addLogging("ERROR") // Journalisation minimale
            .build();
        
        shortTransaction.execute();
        shortTransaction.printSummary();
        
        // Test 2: Transaction complète (toutes les étapes)
        System.out.println("\n=== Test 2: Transaction complète ===");
        Transaction fullTransaction = TransactionBuilder.createFullTransaction(
            "ACC003", "ACC004", 2000.0,
            mobileFactory.createValidator(),
            mobileFactory.createRateCalculator(),
            mobileFactory.createNotifier()
        )
        .addCurrencyConversion("USD", 1.1) // Ajout conversion
        .build();
        
        fullTransaction.execute();
        fullTransaction.printSummary();
        
        // Test 3: Transaction personnalisée étape par étape
        System.out.println("\n=== Test 3: Transaction personnalisée ===");
        Transaction customTransaction = new TransactionBuilder("ACC005", "+237123456789", 1500.0)
            .addValidation(bankFactory.createValidator())
            .addCurrencyConversion("XAF", 655.0)
            .addCommission(mobileFactory.createRateCalculator(), "external")
            .addNotification(mobileFactory.createNotifier(), true, true)
            .addLogging("DEBUG")
            .build();
        
        customTransaction.execute();
        customTransaction.printSummary();
        
        // Test 4: Construction dynamique avec conditions
        System.out.println("\n=== Test 4: Construction dynamique ===");
        TransactionBuilder builder = new TransactionBuilder("ACC006", "ACC007", 3000.0);
        
        // Ajout conditionnel d'étapes
        if (3000.0 > 1000.0) {
            builder.addValidation(bankFactory.createValidator());
            builder.addCommission(bankFactory.createRateCalculator(), "external");
        }
        
        if (Math.random() > 0.5) {
            builder.addNotification(bankFactory.createNotifier(), true, false);
        }
        
        Transaction dynamicTransaction = builder.addLogging("INFO").build();
        dynamicTransaction.execute();
        dynamicTransaction.printSummary();
        
        // Test 5: Modification avant construction
        System.out.println("\n=== Test 5: Modification avant construction ===");
        TransactionBuilder modifiableBuilder = new TransactionBuilder("ACC008", "ACC009", 1000.0)
            .addValidation(bankFactory.createValidator())
            .addCommission(bankFactory.createRateCalculator(), "external")
            .addLogging("WARNING")
            .addNotification(bankFactory.createNotifier(), true, true);
        
        // Retrait d'une étape
        modifiableBuilder.removeStep("Notification");
        
        // Ajout d'une étape différente
        modifiableBuilder.addCurrencyConversion("GBP", 0.85);
        
        Transaction modifiedTransaction = modifiableBuilder.build();
        modifiedTransaction.execute();
        modifiedTransaction.printSummary();
    }
}
```

### Résultats attendus

```
=== Test 1: Transaction courte ===
Début de la transaction
Exécution: Validation des comptes
Validation compte bancaire: ACC001
Validation compte bancaire: ACC002
Validation transaction bancaire: 500.0
Exécution: Journalisation (ERROR)
Journalisation: [ERROR] Transaction: ACC001 -> ACC002, montant: 500.00 EUR
Fin de la transaction - SUCCÈS

=== Résumé de la Transaction ===
Source: ACC001
Destination: ACC002
Montant: 500.0 EUR
Statut: SUCCÈS
Étapes exécutées:
  - Début de la transaction
  - Exécution: Validation des comptes
  - Validation compte bancaire: ACC001
  - Validation compte bancaire: ACC002
  - Validation transaction bancaire: 500.0
  - Exécution: Journalisation (ERROR)
  - Journalisation: [ERROR] Transaction: ACC001 -> ACC002, montant: 500.00 EUR
  - Fin de la transaction - SUCCÈS
Métadonnées: {}

=== Test 2: Transaction complète ===
Début de la transaction
Exécution: Validation des comptes
Validation compte mobile money: ACC003
Validation compte mobile money: ACC004
Validation transaction mobile money: 2000.0
Exécution: Application des commissions
Calcul commission mobile money pour: 2000.0
Calcul taux mobile money pour: 2000.0 vers external
Exécution: Journalisation (INFO)
Journalisation: [INFO] Transaction: ACC003 -> ACC004, montant: 2000.00 EUR
Exécution: Notification des parties
Envoi SMS mobile money à ACC003: Transaction de 2000.00 EUR traitée
Fin de la transaction - SUCCÈS

=== Résumé de la Transaction ===
Source: ACC003
Destination: ACC004
Montant: 2000.0 EUR
Statut: SUCCÈS
Étapes exécutées:
  - Début de la transaction
  - Exécution: Validation des comptes
  - Validation compte mobile money: ACC003
  - Validation compte mobile money: ACC004
  - Validation transaction mobile money: 2000.0
  - Exécution: Application des commissions
  - Calcul commission mobile money pour: 2000.0
  - Calcul taux mobile money pour: 2000.0 vers external
  - Exécution: Journalisation (INFO)
  - Journalisation: [INFO] Transaction: ACC003 -> ACC004, montant: 2000.00 EUR
  - Exécution: Notification des parties
  - Envoi SMS mobile money à ACC003: Transaction de 2000.00 EUR traitée
  - Fin de la transaction - SUCCÈS
Métadonnées: {commission=20.0, transferRate=40.0, totalFees=60.0}