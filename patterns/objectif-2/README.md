# Objectif 2: Familles d'Objets Opérateurs

## Description brève de la solution

Pour résoudre le problème des familles d'objets cohérents par opérateur, nous utilisons le pattern **Abstract Factory**:

- **Abstract Factory Pattern**: Permet de définir une interface pour créer des familles d'objets connexes ou dépendants sans spécifier leurs classes concrètes. Chaque opérateur (banque, mobile money) a sa propre factory qui produit des objets cohérents entre eux (validateurs, calculateurs de taux, notificateurs).

## Justification architecturale

Cette approche est adaptée car :

1. **Cohérence des familles**: Garantit que tous les objets créés pour un opérateur sont compatibles entre eux.

2. **Isolation des implémentations**: Le code client ne dépend que des interfaces abstraites, pas des implémentations concrètes.

3. **Extensibilité**: Facile d'ajouter de nouveaux opérateurs en créant de nouvelles factories concrètes.

4. **Configurabilité**: Permet de basculer d'un opérateur à un autre avec minimum de modifications.

## Implémentation

```java
// Interfaces abstraites pour les produits
public interface IAccountValidator {
    boolean validateAccount(String accountNumber);
    boolean validateTransaction(double amount);
}

public interface IRateCalculator {
    double calculateTransferRate(double amount, String destinationType);
    double calculateCommission(double amount);
}

public interface INotifier {
    void sendNotification(String message, String destination);
    void sendAlert(String alertType, String details);
}

// Abstract Factory
public interface IOperatorFactory {
    IAccountValidator createValidator();
    IRateCalculator createRateCalculator();
    INotifier createNotifier();
    String getOperatorName();
}

// Produits concrets pour Banque
public class BankAccountValidator implements IAccountValidator {
    @Override
    public boolean validateAccount(String accountNumber) {
        System.out.println("Validation compte bancaire: " + accountNumber);
        return accountNumber != null && accountNumber.length() >= 10;
    }
    
    @Override
    public boolean validateTransaction(double amount) {
        System.out.println("Validation transaction bancaire: " + amount);
        return amount > 0 && amount <= 100000;
    }
}

public class BankRateCalculator implements IRateCalculator {
    @Override
    public double calculateTransferRate(double amount, String destinationType) {
        System.out.println("Calcul taux bancaire pour: " + amount + " vers " + destinationType);
        if ("internal".equals(destinationType)) {
            return amount * 0.001; // 0.1%
        } else {
            return amount * 0.005; // 0.5%
        }
    }
    
    @Override
    public double calculateCommission(double amount) {
        System.out.println("Calcul commission bancaire pour: " + amount);
        return Math.max(5.0, amount * 0.002); // Minimum 5€ ou 0.2%
    }
}

public class BankNotifier implements INotifier {
    @Override
    public void sendNotification(String message, String destination) {
        System.out.println("Envoi notification bancaire à " + destination + ": " + message);
    }
    
    @Override
    public void sendAlert(String alertType, String details) {
        System.out.println("ALERTE BANCAIRE - " + alertType + ": " + details);
    }
}

// Produits concrets pour Mobile Money
public class MobileMoneyAccountValidator implements IAccountValidator {
    @Override
    public boolean validateAccount(String accountNumber) {
        System.out.println("Validation compte mobile money: " + accountNumber);
        return accountNumber != null && accountNumber.startsWith("+") && accountNumber.length() >= 10;
    }
    
    @Override
    public boolean validateTransaction(double amount) {
        System.out.println("Validation transaction mobile money: " + amount);
        return amount > 0 && amount <= 500000;
    }
}

public class MobileMoneyRateCalculator implements IRateCalculator {
    @Override
    public double calculateTransferRate(double amount, String destinationType) {
        System.out.println("Calcul taux mobile money pour: " + amount + " vers " + destinationType);
        if ("internal".equals(destinationType)) {
            return 0.0; // Gratuit pour transferts internes
        } else {
            return amount * 0.02; // 2%
        }
    }
    
    @Override
    public double calculateCommission(double amount) {
        System.out.println("Calcul commission mobile money pour: " + amount);
        return Math.max(1.0, amount * 0.01); // Minimum 1€ ou 1%
    }
}

public class MobileMoneyNotifier implements INotifier {
    @Override
    public void sendNotification(String message, String destination) {
        System.out.println("Envoi SMS mobile money à " + destination + ": " + message);
    }
    
    @Override
    public void sendAlert(String alertType, String details) {
        System.out.println("ALERTE MOBILE MONEY - " + alertType + ": " + details);
    }
}

// Factories concrètes
public class BankFactory implements IOperatorFactory {
    @Override
    public IAccountValidator createValidator() {
        return new BankAccountValidator();
    }
    
    @Override
    public IRateCalculator createRateCalculator() {
        return new BankRateCalculator();
    }
    
    @Override
    public INotifier createNotifier() {
        return new BankNotifier();
    }
    
    @Override
    public String getOperatorName() {
        return "Banque";
    }
}

public class MobileMoneyFactory implements IOperatorFactory {
    @Override
    public IAccountValidator createValidator() {
        return new MobileMoneyAccountValidator();
    }
    
    @Override
    public IRateCalculator createRateCalculator() {
        return new MobileMoneyRateCalculator();
    }
    
    @Override
    public INotifier createNotifier() {
        return new MobileMoneyNotifier();
    }
    
    @Override
    public String getOperatorName() {
        return "Mobile Money";
    }
}

// Client qui utilise les factories
public class OperatorService {
    private IOperatorFactory factory;
    private IAccountValidator validator;
    private IRateCalculator rateCalculator;
    private INotifier notifier;
    
    public void setOperator(IOperatorFactory factory) {
        this.factory = factory;
        this.validator = factory.createValidator();
        this.rateCalculator = factory.createRateCalculator();
        this.notifier = factory.createNotifier();
    }
    
    public boolean processTransaction(String accountNumber, double amount, String destinationType) {
        System.out.println("\n=== Traitement transaction avec " + factory.getOperatorName() + " ===");
        
        // Validation du compte
        if (!validator.validateAccount(accountNumber)) {
            notifier.sendAlert("ERREUR_VALIDATION", "Compte invalide: " + accountNumber);
            return false;
        }
        
        // Validation du montant
        if (!validator.validateTransaction(amount)) {
            notifier.sendAlert("ERREUR_MONTANT", "Montant invalide: " + amount);
            return false;
        }
        
        // Calcul des frais
        double taux = rateCalculator.calculateTransferRate(amount, destinationType);
        double commission = rateCalculator.calculateCommission(amount);
        double totalFrais = taux + commission;
        
        System.out.println("Taux: " + taux + ", Commission: " + commission + ", Total: " + totalFrais);
        
        // Notification
        notifier.sendNotification("Transaction traitée avec succès", accountNumber);
        
        return true;
    }
    
    public String getCurrentOperator() {
        return factory != null ? factory.getOperatorName() : "Non défini";
    }
}
```

## Tests et scénarios de démonstration

```java
public class TestOperatorFactory {
    public static void main(String[] args) {
        OperatorService service = new OperatorService();
        
        // Test 1: Configuration avec Banque
        System.out.println("=== Test 1: Configuration avec Banque ===");
        service.setOperator(new BankFactory());
        boolean result1 = service.processTransaction("1234567890", 1000.0, "external");
        System.out.println("Résultat transaction banque: " + result1);
        
        // Test 2: Configuration avec Mobile Money
        System.out.println("\n=== Test 2: Configuration avec Mobile Money ===");
        service.setOperator(new MobileMoneyFactory());
        boolean result2 = service.processTransaction("+237123456789", 5000.0, "internal");
        System.out.println("Résultat transaction mobile money: " + result2);
        
        // Test 3: Basculement dynamique
        System.out.println("\n=== Test 3: Basculement dynamique ===");
        System.out.println("Opérateur actuel: " + service.getCurrentOperator());
        
        // Traitement avec Mobile Money
        service.setOperator(new MobileMoneyFactory());
        service.processTransaction("+237987654321", 2000.0, "external");
        
        // Basculement vers Banque
        service.setOperator(new BankFactory());
        service.processTransaction("0987654321", 750.0, "internal");
        
        // Test 4: Validation des limites
        System.out.println("\n=== Test 4: Validation des limites ===");
        service.setOperator(new BankFactory());
        
        // Transaction trop grande pour la banque
        service.processTransaction("1234567890", 200000.0, "external");
        
        // Transaction valide pour mobile money
        service.setOperator(new MobileMoneyFactory());
        service.processTransaction("+237123456789", 100000.0, "external");
        
        // Test 5: Compte invalide
        System.out.println("\n=== Test 5: Compte invalide ===");
        service.setOperator(new MobileMoneyFactory());
        service.processTransaction("123", 1000.0, "external"); // Numéro invalide
    }
}
```

### Résultats attendus

```
=== Test 1: Configuration avec Banque ===
=== Traitement transaction avec Banque ===
Validation compte bancaire: 1234567890
Validation transaction bancaire: 1000.0
Calcul taux bancaire pour: 1000.0 vers external
Calcul commission bancaire pour: 1000.0
Taux: 5.0, Commission: 5.0, Total: 10.0
Envoi notification bancaire à 1234567890: Transaction traitée avec succès
Résultat transaction banque: true

=== Test 2: Configuration avec Mobile Money ===
=== Traitement transaction avec Mobile Money ===
Validation compte mobile money: +237123456789
Validation transaction mobile money: 5000.0
Calcul taux mobile money pour: 5000.0 vers internal
Calcul commission mobile money pour: 5000.0
Taux: 0.0, Commission: 50.0, Total: 50.0
Envoi SMS mobile money à +237123456789: Transaction traitée avec succès
Résultat transaction mobile money: true

=== Test 3: Basculement dynamique ===
Opérateur actuel: Non défini
=== Traitement transaction avec Mobile Money ===
Validation compte mobile money: +237987654321
Validation transaction mobile money: 2000.0
Calcul taux mobile money pour: 2000.0 vers external
Calcul commission mobile money pour: 2000.0
Taux: 40.0, Commission: 20.0, Total: 60.0
Envoi SMS mobile money à +237987654321: Transaction traitée avec succès

=== Traitement transaction avec Banque ===
Validation compte bancaire: 0987654321
Validation transaction bancaire: 750.0
Calcul taux bancaire pour: 750.0 vers internal
Calcul commission bancaire pour: 750.0
Taux: 0.75, Commission: 5.0, Total: 5.75
Envoi notification bancaire à 0987654321: Transaction traitée avec succès

=== Test 4: Validation des limites ===
=== Traitement transaction avec Banque ===
Validation compte bancaire: 1234567890
Validation transaction bancaire: 200000.0
ALERTE BANCAIRE - ERREUR_MONTANT: Montant invalide: 200000.0

=== Traitement transaction avec Mobile Money ===
Validation compte mobile money: +237123456789
Validation transaction mobile money: 100000.0
Calcul taux mobile money pour: 100000.0 vers external
Calcul commission mobile money pour: 100000.0
Taux: 2000.0, Commission: 1000.0, Total: 3000.0
Envoi SMS mobile money à +237123456789: Transaction traitée avec succès

=== Test 5: Compte invalide ===
=== Traitement transaction avec Mobile Money ===
Validation compte mobile money: 123
ALERTE MOBILE MONEY - ERREUR_VALIDATION: Compte invalide: 123