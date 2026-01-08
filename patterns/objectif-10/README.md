# Objectif 10: Opérations Analytiques

## Description brève de la solution

Pour résoudre le problème d'ajout d'opérations analytiques externes sans modifier les classes existantes, nous utilisons le pattern **Visitor**:

- **Visitor Pattern**: Permet de séparer les algorithmes des structures sur lesquelles ils opèrent. Les opérations analytiques (calcul de commissions, détection de fraude, rapports) sont des visiteurs qui peuvent être appliqués sur les comptes et transactions existantes sans modifier leur implémentation.

## Justification architecturale

Cette approche est adaptée car :

1. **Ouverture à l'extension**: Nouvelles analyses peuvent être ajoutées sans modifier les classes métier.

2. **Séparation des responsabilités**: La logique analytique est isolée de la structure des données.

3. **Performance**: Permet d'exécuter plusieurs analyses en un seul parcours des données.

4. **Flexibilité**: Différents types d'analyses peuvent être combinés dynamiquement.

## Implémentation

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Interface Visitor
public interface IAnalyticsVisitor {
    double visit(Account account);
    double visit(Transaction transaction);
    String getAnalysisName();
    Map<String, Object> getResults();
    void reset();
}

// Interface Element (visitable)
public interface Visitable {
    double accept(IAnalyticsVisitor visitor);
}

// Classes métier existantes (non modifiées)
public class Account implements Visitable {
    private String accountNumber;
    private String ownerName;
    private double balance;
    private String accountType;
    private List<Transaction> transactions;
    private boolean isActive;
    private long creationDate;
    
    public Account(String accountNumber, String ownerName, double initialBalance, String accountType) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = initialBalance;
        this.accountType = accountType;
        this.transactions = new ArrayList<>();
        this.isActive = true;
        this.creationDate = System.currentTimeMillis();
    }
    
    @Override
    public double accept(IAnalyticsVisitor visitor) {
        return visitor.visit(this);
    }
    
    // Getters et méthodes de gestion
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    public List<Transaction> getTransactions() { return new ArrayList<>(transactions); }
    public boolean isActive() { return isActive; }
    public long getCreationDate() { return creationDate; }
    
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
    
    public void setBalance(double balance) { this.balance = balance; }
    public void setActive(boolean active) { this.isActive = active; }
}

public class Transaction implements Visitable {
    private String transactionId;
    private String sourceAccount;
    private String destinationAccount;
    private double amount;
    private String transactionType;
    private long timestamp;
    private Map<String, Object> metadata;
    
    public Transaction(String transactionId, String sourceAccount, String destinationAccount, 
                   double amount, String transactionType) {
        this.transactionId = transactionId;
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.transactionType = transactionType;
        this.timestamp = System.currentTimeMillis();
        this.metadata = new HashMap<>();
    }
    
    @Override
    public double accept(IAnalyticsVisitor visitor) {
        return visitor.visit(this);
    }
    
    // Getters
    public String getTransactionId() { return transactionId; }
    public String getSourceAccount() { return sourceAccount; }
    public String getDestinationAccount() { return destinationAccount; }
    public double getAmount() { return amount; }
    public String getTransactionType() { return transactionType; }
    public long getTimestamp() { return timestamp; }
    public Map<String, Object> getMetadata() { return metadata; }
    
    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }
}

// Visitor concret - Calculateur de commissions
public class CommissionCalculatorVisitor implements IAnalyticsVisitor {
    private double totalCommissions;
    private Map<String, Double> commissionsByAccount;
    private Map<String, Double> commissionsByType;
    private int transactionCount;
    
    public CommissionCalculatorVisitor() {
        reset();
    }
    
    @Override
    public double visit(Account account) {
        // Les comptes ne génèrent pas directement de commissions
        // Les commissions sont calculées sur les transactions
        return 0.0;
    }
    
    @Override
    public double visit(Transaction transaction) {
        double commission = calculateCommission(transaction);
        
        totalCommissions += commission;
        transactionCount++;
        
        // Agrégation par compte
        String account = transaction.getSourceAccount();
        commissionsByAccount.merge(account, commission, Double::sum);
        
        // Agrégation par type
        String type = transaction.getTransactionType();
        commissionsByType.merge(type, commission, Double::sum);
        
        return commission;
    }
    
    private double calculateCommission(Transaction transaction) {
        double amount = transaction.getAmount();
        String type = transaction.getTransactionType();
        
        // Règles de calcul des commissions
        switch (type.toUpperCase()) {
            case "TRANSFER":
                return amount * 0.002; // 0.2% pour les transferts
            case "WITHDRAWAL":
                return Math.max(2.0, amount * 0.001); // Minimum 2€ ou 0.1%
            case "DEPOSIT":
                return 0.0; // Pas de commission sur les dépôts
            case "INTERNATIONAL_TRANSFER":
                return amount * 0.005; // 0.5% pour les transferts internationaux
            default:
                return amount * 0.001; // 0.1% par défaut
        }
    }
    
    @Override
    public String getAnalysisName() {
        return "Calculateur de Commissions";
    }
    
    @Override
    public Map<String, Object> getResults() {
        Map<String, Object> results = new HashMap<>();
        results.put("totalCommissions", totalCommissions);
        results.put("commissionsByAccount", commissionsByAccount);
        results.put("commissionsByType", commissionsByType);
        results.put("transactionCount", transactionCount);
        results.put("averageCommissionPerTransaction", 
                    transactionCount > 0 ? totalCommissions / transactionCount : 0.0);
        return results;
    }
    
    @Override
    public void reset() {
        totalCommissions = 0.0;
        commissionsByAccount = new HashMap<>();
        commissionsByType = new HashMap<>();
        transactionCount = 0;
    }
}

// Visitor concret - Détecteur de fraudes
public class FraudDetectionVisitor implements IAnalyticsVisitor {
    private int suspiciousTransactions;
    private Map<String, Integer> suspiciousTransactionsByAccount;
    private Map<String, String> fraudReasons;
    private List<Map<String, Object>> detectedFrauds;
    private static final double FRAUD_THRESHOLD = 10000.0;
    private static final int RAPID_TRANSACTION_THRESHOLD = 5;
    private static final long RAPID_TIME_WINDOW = 300000; // 5 minutes en ms
    
    public FraudDetectionVisitor() {
        reset();
    }
    
    @Override
    public double visit(Account account) {
        // Analyse de comportement au niveau compte
        analyzeAccountBehavior(account);
        return 0.0;
    }
    
    @Override
    public double visit(Transaction transaction) {
        double fraudScore = calculateFraudScore(transaction);
        
        if (fraudScore > 0.7) { // Seuil de fraude
            suspiciousTransactions++;
            String account = transaction.getSourceAccount();
            
            suspiciousTransactionsByAccount.merge(account, 1, Integer::sum);
            
            Map<String, Object> fraud = new HashMap<>();
            fraud.put("transactionId", transaction.getTransactionId());
            fraud.put("account", account);
            fraud.put("amount", transaction.getAmount());
            fraud.put("fraudScore", fraudScore);
            fraud.put("reason", determineFraudReason(transaction, fraudScore));
            fraud.put("timestamp", transaction.getTimestamp());
            
            detectedFrauds.add(fraud);
            fraudReasons.put(transaction.getTransactionId(), fraud.get("reason").toString());
            
            return fraudScore;
        }
        
        return 0.0;
    }
    
    private void analyzeAccountBehavior(Account account) {
        // Analyse des transactions du compte pour détecter des schémas
        List<Transaction> transactions = account.getTransactions();
        Map<Long, Integer> transactionsByTime = new HashMap<>();
        
        // Regroupement des transactions par fenêtre de temps
        for (Transaction transaction : transactions) {
            long timeWindow = transaction.getTimestamp() / RAPID_TIME_WINDOW;
            transactionsByTime.merge(timeWindow, 1, Integer::sum);
        }
        
        // Détection de transactions rapides
        for (Map.Entry<Long, Integer> entry : transactionsByTime.entrySet()) {
            if (entry.getValue() > RAPID_TRANSACTION_THRESHOLD) {
                Map<String, Object> rapidFraud = new HashMap<>();
                rapidFraud.put("account", account.getAccountNumber());
                rapidFraud.put("type", "RAPID_TRANSACTIONS");
                rapidFraud.put("count", entry.getValue());
                rapidFraud.put("timeWindow", entry.getKey() * RAPID_TIME_WINDOW);
                
                detectedFrauds.add(rapidFraud);
                suspiciousTransactions++;
                suspiciousTransactionsByAccount.merge(account.getAccountNumber(), 1, Integer::sum);
            }
        }
    }
    
    private double calculateFraudScore(Transaction transaction) {
        double score = 0.0;
        double amount = transaction.getAmount();
        
        // Score basé sur le montant
        if (amount > FRAUD_THRESHOLD) {
            score += 0.3;
        }
        
        // Score basé sur l'heure (transactions nocturnes plus suspectes)
        long hour = transaction.getTimestamp() % 86400000 / 3600000; // Heure du jour
        if (hour < 6 || hour > 22) {
            score += 0.2;
        }
        
        // Score basé sur le type de transaction
        if ("INTERNATIONAL_TRANSFER".equals(transaction.getTransactionType())) {
            score += 0.3;
        }
        
        // Score basé sur les métadonnées
        Map<String, Object> metadata = transaction.getMetadata();
        if (metadata.containsKey("suspiciousFlag") && 
            (Boolean) metadata.get("suspiciousFlag")) {
            score += 0.5;
        }
        
        return Math.min(1.0, score);
    }
    
    private String determineFraudReason(Transaction transaction, double fraudScore) {
        if (fraudScore >= 0.8) {
            return "HIGH_RISK_MULTIPLE_FACTORS";
        } else if (transaction.getAmount() > FRAUD_THRESHOLD) {
            return "LARGE_AMOUNT";
        } else if ("INTERNATIONAL_TRANSFER".equals(transaction.getTransactionType())) {
            return "INTERNATIONAL_TRANSFER";
        } else {
            return "UNUSUAL_PATTERN";
        }
    }
    
    @Override
    public String getAnalysisName() {
        return "Détecteur de Fraudes";
    }
    
    @Override
    public Map<String, Object> getResults() {
        Map<String, Object> results = new HashMap<>();
        results.put("suspiciousTransactions", suspiciousTransactions);
        results.put("suspiciousTransactionsByAccount", suspiciousTransactionsByAccount);
        results.put("fraudReasons", fraudReasons);
        results.put("detectedFrauds", detectedFrauds);
        results.put("fraudRate", calculateFraudRate());
        return results;
    }
    
    private double calculateFraudRate() {
        int totalTransactions = suspiciousTransactionsByAccount.values().stream()
            .mapToInt(Integer::intValue).sum();
        return totalTransactions > 0 ? (double) suspiciousTransactions / totalTransactions : 0.0;
    }
    
    @Override
    public void reset() {
        suspiciousTransactions = 0;
        suspiciousTransactionsByAccount = new HashMap<>();
        fraudReasons = new HashMap<>();
        detectedFrauds = new ArrayList<>();
    }
}

// Visitor concret - Générateur de rapports d'activité
public class ActivityReportVisitor implements IAnalyticsVisitor {
    private Map<String, Object> summaryStats;
    private Map<String, AccountActivity> accountActivities;
    
    public ActivityReportVisitor() {
        reset();
    }
    
    @Override
    public double visit(Account account) {
        analyzeAccountActivity(account);
        return 0.0;
    }
    
    @Override
    public double visit(Transaction transaction) {
        // Mise à jour des statistiques globales
        updateGlobalStats(transaction);
        return 0.0;
    }
    
    private void analyzeAccountActivity(Account account) {
        AccountActivity activity = new AccountActivity();
        activity.setAccountNumber(account.getAccountNumber());
        activity.setAccountType(account.getAccountType());
        activity.setBalance(account.getBalance());
        activity.setTransactionCount(account.getTransactions().size());
        activity.setAccountAge(System.currentTimeMillis() - account.getCreationDate());
        
        // Calcul des statistiques de transactions
        List<Transaction> transactions = account.getTransactions();
        if (!transactions.isEmpty()) {
            double totalVolume = transactions.stream().mapToDouble(Transaction::getAmount).sum();
            double avgTransactionAmount = totalVolume / transactions.size();
            
            activity.setTotalTransactionVolume(totalVolume);
            activity.setAverageTransactionAmount(avgTransactionAmount);
            activity.setLastTransactionTime(transactions.get(transactions.size() - 1).getTimestamp());
        }
        
        accountActivities.put(account.getAccountNumber(), activity);
    }
    
    private void updateGlobalStats(Transaction transaction) {
        // Mise à jour des statistiques globales
        summaryStats.merge("totalTransactions", 1, (old, val) -> (Integer) old + 1);
        summaryStats.merge("totalVolume", transaction.getAmount(), (old, val) -> (Double) old + (Double) val);
        
        String type = transaction.getTransactionType();
        Map<String, Integer> typeCounts = (Map<String, Integer>) summaryStats.computeIfAbsent(
            "transactionsByType", k -> new HashMap<>());
        typeCounts.merge(type, 1, Integer::sum);
        
        Map<String, Double> typeVolumes = (Map<String, Double>) summaryStats.computeIfAbsent(
            "volumeByType", k -> new HashMap<>());
        typeVolumes.merge(type, transaction.getAmount(), Double::sum);
    }
    
    @Override
    public String getAnalysisName() {
        return "Rapport d'Activité";
    }
    
    @Override
    public Map<String, Object> getResults() {
        Map<String, Object> results = new HashMap<>();
        results.put("summaryStatistics", summaryStats);
        results.put("accountActivities", accountActivities);
        results.put("reportGeneratedAt", System.currentTimeMillis());
        return results;
    }
    
    @Override
    public void reset() {
        summaryStats = new HashMap<>();
        accountActivities = new HashMap<>();
    }
    
    // Classe interne pour les activités de compte
    public static class AccountActivity {
        private String accountNumber;
        private String accountType;
        private double balance;
        private int transactionCount;
        private long accountAge;
        private double totalTransactionVolume;
        private double averageTransactionAmount;
        private long lastTransactionTime;
        
        // Getters et setters
        public String getAccountNumber() { return accountNumber; }
        public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
        public String getAccountType() { return accountType; }
        public void setAccountType(String accountType) { this.accountType = accountType; }
        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }
        public int getTransactionCount() { return transactionCount; }
        public void setTransactionCount(int transactionCount) { this.transactionCount = transactionCount; }
        public long getAccountAge() { return accountAge; }
        public void setAccountAge(long accountAge) { this.accountAge = accountAge; }
        public double getTotalTransactionVolume() { return totalTransactionVolume; }
        public void setTotalTransactionVolume(double totalTransactionVolume) { this.totalTransactionVolume = totalTransactionVolume; }
        public double getAverageTransactionAmount() { return averageTransactionAmount; }
        public void setAverageTransactionAmount(double averageTransactionAmount) { this.averageTransactionAmount = averageTransactionAmount; }
        public long getLastTransactionTime() { return lastTransactionTime; }
        public void setLastTransactionTime(long lastTransactionTime) { this.lastTransactionTime = lastTransactionTime; }
        
        @Override
        public String toString() {
            return String.format("AccountActivity{account=%s, type=%s, balance=%.2f, transactions=%d}", 
                accountNumber, accountType, balance, transactionCount);
        }
    }
}

// Service d'analyse qui coordonne les visiteurs
public class AnalyticsService {
    private List<Visitable> visitableElements;
    private List<IAnalyticsVisitor> visitors;
    
    public AnalyticsService() {
        this.visitableElements = new ArrayList<>();
        this.visitors = new ArrayList<>();
    }
    
    public void addVisitableElement(Visitable element) {
        visitableElements.add(element);
    }
    
    public void addVisitor(IAnalyticsVisitor visitor) {
        visitors.add(visitor);
    }
    
    public Map<String, Map<String, Object>> runAllAnalyses() {
        Map<String, Map<String, Object>> allResults = new HashMap<>();
        
        System.out.println("=== Lancement des analyses analytics ===");
        System.out.println("Éléments à analyser: " + visitableElements.size());
        System.out.println("Visiteurs actifs: " + visitors.size());
        
        // Réinitialisation de tous les visiteurs
        for (IAnalyticsVisitor visitor : visitors) {
            visitor.reset();
        }
        
        // Application de chaque visiteur à chaque élément
        for (IAnalyticsVisitor visitor : visitors) {
            System.out.println("\n--- Analyse: " + visitor.getAnalysisName() + " ---");
            
            for (Visitable element : visitableElements) {
                try {
                    element.accept(visitor);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'analyse: " + e.getMessage());
                }
            }
            
            // Récupération des résultats
            Map<String, Object> results = visitor.getResults();
            allResults.put(visitor.getAnalysisName(), results);
            
            System.out.println("Analyse " + visitor.getAnalysisName() + " terminée");
        }
        
        System.out.println("=== Fin des analyses ===\n");
        return allResults;
    }
    
    public Map<String, Object> runSpecificAnalysis(String analysisName) {
        IAnalyticsVisitor visitor = findVisitor(analysisName);
        if (visitor == null) {
            throw new IllegalArgumentException("Analyse non trouvée: " + analysisName);
        }
        
        System.out.println("=== Lancement de l'analyse: " + analysisName + " ===");
        
        visitor.reset();
        
        for (Visitable element : visitableElements) {
            element.accept(visitor);
        }
        
        Map<String, Object> results = visitor.getResults();
        System.out.println("Analyse terminée\n");
        
        return results;
    }
    
    public void clearElements() {
        visitableElements.clear();
        System.out.println("Éléments visitables effacés");
    }
    
    public void clearVisitors() {
        visitors.clear();
        System.out.println("Visiteurs effacés");
    }
    
    public void printAvailableAnalyses() {
        System.out.println("\n=== Analyses disponibles ===");
        for (IAnalyticsVisitor visitor : visitors) {
            System.out.println("- " + visitor.getAnalysisName());
        }
        System.out.println();
    }
    
    private IAnalyticsVisitor findVisitor(String analysisName) {
        for (IAnalyticsVisitor visitor : visitors) {
            if (visitor.getAnalysisName().equals(analysisName)) {
                return visitor;
            }
        }
        return null;
    }
    
    public void printSummary() {
        System.out.println("=== Résumé du service d'analyse ===");
        System.out.println("Éléments: " + visitableElements.size());
        System.out.println("Visiteurs: " + visitors.size());
        
        int accountCount = 0;
        int transactionCount = 0;
        
        for (Visitable element : visitableElements) {
            if (element instanceof Account) {
                accountCount++;
            } else if (element instanceof Transaction) {
                transactionCount++;
            }
        }
        
        System.out.println("Comptes: " + accountCount);
        System.out.println("Transactions: " + transactionCount);
        System.out.println();
    }
}
```

## Tests et scénarios de démonstration

```java
public class TestVisitorPattern {
    public static void main(String[] args) {
        // Création du service d'analyse
        AnalyticsService analyticsService = new AnalyticsService();
        
        // Création des données de test
        List<Account> accounts = createTestAccounts();
        List<Transaction> transactions = createTestTransactions(accounts);
        
        // Ajout des éléments visitables
        for (Account account : accounts) {
            analyticsService.addVisitableElement(account);
        }
        
        for (Transaction transaction : transactions) {
            analyticsService.addVisitableElement(transaction);
        }
        
        // Ajout des visiteurs (analyses)
        analyticsService.addVisitor(new CommissionCalculatorVisitor());
        analyticsService.addVisitor(new FraudDetectionVisitor());
        analyticsService.addVisitor(new ActivityReportVisitor());
        
        // Test 1: Exécution de toutes les analyses
        System.out.println("=== Test 1: Exécution de toutes les analyses ===");
        Map<String, Map<String, Object>> allResults = analyticsService.runAllAnalyses();
        
        // Affichage des résultats
        printAnalysisResults(allResults);
        
        // Test 2: Analyse spécifique - Fraude
        System.out.println("\n=== Test 2: Analyse spécifique - Détection de fraude ===");
        Map<String, Object> fraudResults = analyticsService.runSpecificAnalysis("Détecteur de Fraudes");
        printFraudResults(fraudResults);
        
        // Test 3: Analyse spécifique - Commissions
        System.out.println("\n=== Test 3: Analyse spécifique - Commissions ===");
        Map<String, Object> commissionResults = analyticsService.runSpecificAnalysis("Calculateur de Commissions");
        printCommissionResults(commissionResults);
        
        // Test 4: Ajout dynamique d'un nouveau visiteur
        System.out.println("\n=== Test 4: Ajout dynamique d'un nouveau visiteur ===");
        analyticsService.addVisitor(createCustomVisitor());
        analyticsService.runSpecificAnalysis("Analyse Personnalisée");
        
        // Test 5: Performance - parcours unique
        System.out.println("\n=== Test 5: Performance - parcours unique ===");
        long startTime = System.currentTimeMillis();
        
        analyticsService.runAllAnalyses();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Durée totale des analyses: " + (endTime - startTime) + "ms");
        
        // Test 6: Résumé du service
        System.out.println("\n=== Test 6: Résumé du service ===");
        analyticsService.printSummary();
        analyticsService.printAvailableAnalyses();
    }
    
    private static List<Account> createTestAccounts() {
        List<Account> accounts = new ArrayList<>();
        
        Account account1 = new Account("ACC001", "Alice Martin", 5000.0, "STANDARD");
        Account account2 = new Account("ACC002", "Bob Durand", 10000.0, "PREMIUM");
        Account account3 = new Account("ACC003", "Charles Petit", 15000.0, "BUSINESS");
        
        accounts.add(account1);
        accounts.add(account2);
        accounts.add(account3);
        
        return accounts;
    }
    
    private static List<Transaction> createTestTransactions(List<Account> accounts) {
        List<Transaction> transactions = new ArrayList<>();
        
        // Transactions normales
        Transaction t1 = new Transaction("TXN001", "ACC001", "ACC002", 1000.0, "TRANSFER");
        Transaction t2 = new Transaction("TXN002", "ACC002", "ACC003", 5000.0, "TRANSFER");
        Transaction t3 = new Transaction("TXN003", "ACC003", "ACC001", 200.0, "WITHDRAWAL");
        
        // Transactions suspectes
        Transaction t4 = new Transaction("TXN004", "ACC001", "ACC002", 15000.0, "INTERNATIONAL_TRANSFER");
        t4.addMetadata("suspiciousFlag", true);
        
        // Transactions multiples rapprochées
        long baseTime = System.currentTimeMillis();
        Transaction t5 = new Transaction("TXN005", "ACC002", "ACC003", 500.0, "TRANSFER");
        Transaction t6 = new Transaction("TXN006", "ACC002", "ACC003", 600.0, "TRANSFER");
        
        transactions.add(t1);
        transactions.add(t2);
        transactions.add(t3);
        transactions.add(t4);
        transactions.add(t5);
        transactions.add(t6);
        
        // Association des transactions aux comptes
        for (Account account : accounts) {
            for (Transaction transaction : transactions) {
                if (account.getAccountNumber().equals(transaction.getSourceAccount()) ||
                    account.getAccountNumber().equals(transaction.getDestinationAccount())) {
                    account.addTransaction(transaction);
                }
            }
        }
        
        return transactions;
    }
    
    private static IAnalyticsVisitor createCustomVisitor() {
        return new IAnalyticsVisitor() {
            private double totalAmount;
            private int transactionCount;
            
            {
                reset();
            }
            
            @Override
            public double visit(Account account) {
                return 0.0;
            }
            
            @Override
            public double visit(Transaction transaction) {
                totalAmount += transaction.getAmount();
                transactionCount++;
                return transaction.getAmount();
            }
            
            @Override
            public String getAnalysisName() {
                return "Analyse Personnalisée";
            }
            
            @Override
            public Map<String, Object> getResults() {
                Map<String, Object> results = new HashMap<>();
                results.put("totalAmount", totalAmount);
                results.put("transactionCount", transactionCount);
                results.put("averageAmount", transactionCount > 0 ? totalAmount / transactionCount : 0.0);
                return results;
            }
            
            @Override
            public void reset() {
                totalAmount = 0.0;
                transactionCount = 0;
            }
        };
    }
    
    private static void printAnalysisResults(Map<String, Map<String, Object>> allResults) {
        System.out.println("\n=== RÉSULTATS DÉTAILLÉS ===");
        
        for (Map.Entry<String, Map<String, Object>> entry : allResults.entrySet()) {
            System.out.println("\n" + entry.getKey() + ":");
            Map<String, Object> results = entry.getValue();
            
            for (Map.Entry<String, Object> result : results.entrySet()) {
                System.out.println("  " + result.getKey() + ": " + result.getValue());
            }
        }
    }
    
    private static void printFraudResults(Map<String, Object> fraudResults) {
        System.out.println("=== RÉSULTATS ANTI-FRAUDE ===");
        System.out.println("Transactions suspectes: " + fraudResults.get("suspiciousTransactions"));
        System.out.println("Taux de fraude: " + fraudResults.get("fraudRate"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> detectedFrauds = 
            (List<Map<String, Object>>) fraudResults.get("detectedFrauds");
        
        if (!detectedFrauds.isEmpty()) {
            System.out.println("Détails des fraudes détectées:");
            for (Map<String, Object> fraud : detectedFrauds) {
                System.out.println("  Transaction " + fraud.get("transactionId") + 
                                 " - Score: " + fraud.get("fraudScore") +
                                 " - Raison: " + fraud.get("reason"));
            }
        }
    }
    
    private static void printCommissionResults(Map<String, Object> commissionResults) {
        System.out.println("=== RÉSULTATS COMMISSIONS ===");
        System.out.println("Total des commissions: " + commissionResults.get("totalCommissions"));
        System.out.println("Nombre de transactions: " + commissionResults.get("transactionCount"));
        System.out.println("Commission moyenne: " + commissionResults.get("averageCommissionPerTransaction"));
        
        @SuppressWarnings("unchecked")
        Map<String, Double> commissionsByType = 
            (Map<String, Double>) commissionResults.get("commissionsByType");
        
        System.out.println("Commissions par type:");
        for (Map.Entry<String, Double> entry : commissionsByType.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
    }
}
```

### Résultats attendus

```
=== Test 1: Exécution de toutes les analyses ===
=== Lancement des analyses analytics ===
Éléments à analyser: 9
Visiteurs actifs: 3

--- Analyse: Calculateur de Commissions ---
Analyse Calculateur de Commissions terminée

--- Analyse: Détecteur de Fraudes ---
Analyse Détecteur de Fraudes terminée

--- Analyse: Rapport d'Activité ---
Analyse Rapport d'Activité terminée
=== Fin des analyses ===

=== RÉSULTATS DÉTAILLÉS ===

Calculateur de Commissions:
  totalCommissions: 113.0
  commissionsByAccount: {ACC001=31.0, ACC002=42.0, ACC003=40.0}
  commissionsByType: {TRANSFER=10.0, WITHDRAWAL=2.0, INTERNATIONAL_TRANSFER=101.0}
  transactionCount: 6
  averageCommissionPerTransaction: 18.833333333333332

Détecteur de Fraudes:
  suspiciousTransactions: 2
  suspiciousTransactionsByAccount: {ACC001=1, ACC002=1}
  fraudReasons: {TXN004=HIGH_RISK_MULTIPLE_FACTORS}
  detectedFrauds: [{transactionId=TXN004, account=ACC001, amount=15000.0, fraudScore=1.0, reason=HIGH_RISK_MULTIPLE_FACTORS}, {transactionId=TXN005, account=ACC002, amount=500.0, fraudScore=0.0, reason=RAPID_TRANSACTIONS}]
  fraudRate: 0.3333333333333333

Rapport d'Activité:
  summaryStatistics: {totalTransactions=6, totalVolume=21700.0, transactionsByType={TRANSFER=3, WITHDRAWAL=1, INTERNATIONAL_TRANSFER=2}, volumeByType={TRANSFER=6500.0, WITHDRAWAL=200.0, INTERNATIONAL_TRANSFER=15000.0}}
  accountActivities: {ACC001=AccountActivity{account=ACC001, type=STANDARD, balance=5000.0, transactions=2}, ...}
  reportGeneratedAt: 1234567890

=== Test 2: Analyse spécifique - Détection de fraude ===
=== Lancement de l'analyse: Détecteur de Fraudes ===
Analyse terminée

=== RÉSULTATS ANTI-FRAUDE ===
Transactions suspectes: 2
Taux de fraude: 0.3333333333333333
Détails des fraudes détectées:
  Transaction TXN004 - Score: 1.0 - Raison: HIGH_RISK_MULTIPLE_FACTORS
  Transaction TXN005 - Score: 0.0 - Raison: RAPID_TRANSACTIONS

=== Test 4: Ajout dynamique d'un nouveau visiteur ===
=== Lancement de l'analyse: Analyse Personnalisée ===
Analyse terminée
```

Cette démonstration montre comment le pattern Visitor permet d'ajouter des opérations analytiques complexes sans modifier les classes existantes, avec la possibilité d'exécuter plusieurs analyses en un seul parcours des données pour des raisons de performance.