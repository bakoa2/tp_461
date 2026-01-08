# Objectif 9: Squelettes de Workflow

## Description brève de la solution

Pour résoudre le problème des workflows avec séquences générales mais étapes spécifiques par opérateur, nous utilisons le pattern **Template Method**:

- **Template Method Pattern**: Définit le squelette d'un algorithme de workflow dans une classe de base abstraite, tout en laissant les sous-classes redéfinir certaines étapes spécifiques. Chaque opérateur peut ainsi personnaliser les étapes qui le concernent tout en suivant le même processus global.

## Justification architecturale

Cette approche est adaptée car :

1. **Réutilisation du squelette**: Le processus général est partagé entre tous les opérateurs, évitant la duplication.

2. **Flexibilité des spécialisations**: Chaque opérateur peut implémenter uniquement les étapes qui diffèrent pour lui.

3. **Cohérence du processus**: Garantit que tous les opérateurs suivent les mêmes étapes fondamentales.

4. **Maintenance facilitée**: Les modifications du squelette impactent automatiquement tous les opérateurs.

## Implémentation

```java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Classe abstraite Template Method pour les workflows
public abstract class WorkflowTemplate {
    protected String workflowName;
    protected List<String> executionSteps;
    protected Map<String, Object> context;
    protected List<String> executionLog;
    
    public WorkflowTemplate(String workflowName) {
        this.workflowName = workflowName;
        this.executionSteps = new ArrayList<>();
        this.context = new HashMap<>();
        this.executionLog = new ArrayList<>();
        System.out.println("Initialisation du workflow: " + workflowName);
    }
    
    // Template Method - squelette du workflow
    public final void execute(Map<String, Object> inputData) {
        System.out.println("\n🔄 === DÉBUT DU WORKFLOW: " + workflowName + " ===");
        
        // Initialisation du contexte
        initializeContext(inputData);
        
        boolean success = false;
        try {
            // Étapes du squelette commun
            if (!preProcessValidation()) {
                completeWorkflow(false, "Échec de la pré-validation");
                return;
            }
            
            if (!executeMainSteps()) {
                completeWorkflow(false, "Échec lors des étapes principales");
                return;
            }
            
            if (!postProcessValidation()) {
                completeWorkflow(false, "Échec de la post-validation");
                return;
            }
            
            // Étapes de finalisation
            generateReports();
            sendNotifications();
            
            success = true;
            
        } catch (Exception e) {
            logError("Exception durant le workflow: " + e.getMessage());
            success = false;
        } finally {
            completeWorkflow(success, success ? "Workflow complété avec succès" : "Workflow échoué");
        }
    }
    
    // Étapes communes (final pour ne pas être surchargées)
    private final void initializeContext(Map<String, Object> inputData) {
        context.clear();
        context.putAll(inputData);
        context.put("workflowName", workflowName);
        context.put("startTime", System.currentTimeMillis());
        
        logStep("Initialisation du contexte");
        logStep("Données d'entrée: " + inputData);
    }
    
    private final boolean executeMainSteps() {
        logStep("Début des étapes principales");
        
        // Étape 1: Vérification des documents (spécifique à l'opérateur)
        if (!validateDocuments()) {
            return false;
        }
        
        // Étape 2: Vérification des antécédents (spécifique à l'opérateur)
        if (!checkBackground()) {
            return false;
        }
        
        // Étape 3: Traitement principal (commun)
        if (!processRequest()) {
            return false;
        }
        
        // Étape 4: Validation finale (spécifique à l'opérateur)
        if (!finalValidation()) {
            return false;
        }
        
        return true;
    }
    
    private final void completeWorkflow(boolean success, String message) {
        context.put("endTime", System.currentTimeMillis());
        context.put("success", success);
        context.put("duration", (Long) context.get("endTime") - (Long) context.get("startTime"));
        
        logStep("Finalisation du workflow: " + message);
        logStep("Durée totale: " + context.get("duration") + "ms");
        
        if (success) {
            logStep("✅ Workflow " + workflowName + " terminé avec succès");
        } else {
            logStep("❌ Workflow " + workflowName + " échoué");
        }
        
        System.out.println("🔄 === FIN DU WORKFLOW: " + workflowName + " ===\n");
    }
    
    // Méthodes de validation communes avec points d'extension
    protected boolean preProcessValidation() {
        logStep("Pré-validation du traitement");
        
        // Validation commune
        if (!context.containsKey("accountId")) {
            logError("accountId manquant");
            return false;
        }
        
        if (!context.containsKey("requestType")) {
            logError("requestType manquant");
            return false;
        }
        
        // Point d'extension pour validation spécifique
        return performAdditionalPreValidation();
    }
    
    protected boolean postProcessValidation() {
        logStep("Post-validation du traitement");
        
        // Validation commune
        if (!context.containsKey("result")) {
            logError("Résultat manquant");
            return false;
        }
        
        // Point d'extension pour validation spécifique
        return performAdditionalPostValidation();
    }
    
    private final void generateReports() {
        logStep("Génération des rapports");
        generateStandardReports();
        generateOperatorSpecificReports();
    }
    
    private final void sendNotifications() {
        logStep("Envoi des notifications");
        sendStandardNotifications();
        sendOperatorSpecificNotifications();
    }
    
    // Méthodes abstraites à implémenter par les sous-classes
    protected abstract boolean validateDocuments();
    protected abstract boolean checkBackground();
    protected abstract boolean finalValidation();
    
    // Méthodes optionnelles avec implémentation par défaut
    protected boolean processRequest() {
        logStep("Traitement standard de la demande");
        
        String requestType = (String) context.get("requestType");
        String accountId = (String) context.get("accountId");
        
        // Traitement générique
        Map<String, Object> result = new HashMap<>();
        result.put("accountId", accountId);
        result.put("requestType", requestType);
        result.put("status", "APPROVED");
        result.put("processedBy", workflowName);
        result.put("timestamp", System.currentTimeMillis());
        
        context.put("result", result);
        logStep("Demande traitée avec succès");
        return true;
    }
    
    // Points d'extension avec implémentation par défaut
    protected boolean performAdditionalPreValidation() {
        // Validation supplémentaire par défaut
        return true;
    }
    
    protected boolean performAdditionalPostValidation() {
        // Validation supplémentaire par défaut
        return true;
    }
    
    protected void generateStandardReports() {
        logStep("Génération des rapports standards");
        
        Map<String, Object> report = new HashMap<>();
        report.put("workflow", workflowName);
        report.put("accountId", context.get("accountId"));
        report.put("result", context.get("result"));
        report.put("duration", context.get("duration"));
        report.put("timestamp", System.currentTimeMillis());
        
        context.put("standardReport", report);
    }
    
    protected void generateOperatorSpecificReports() {
        logStep("Génération des rapports spécifiques à l'opérateur");
        // Implémentation par défaut
    }
    
    protected void sendStandardNotifications() {
        logStep("Envoi des notifications standards");
        
        String accountId = (String) context.get("accountId");
        boolean success = (Boolean) context.get("success");
        
        String message = String.format("Workflow %s %s pour le compte %s", 
            workflowName, success ? "terminé avec succès" : "échoué", accountId);
        
        System.out.println("📧 Notification standard: " + message);
    }
    
    protected void sendOperatorSpecificNotifications() {
        logStep("Envoi des notifications spécifiques à l'opérateur");
        // Implémentation par défaut
    }
    
    // Utilitaires
    protected void logStep(String step) {
        String timestamp = java.time.LocalTime.now().toString();
        String logEntry = String.format("[%s] %s", timestamp, step);
        executionLog.add(logEntry);
        System.out.println("  " + logEntry);
    }
    
    protected void logError(String error) {
        String timestamp = java.time.LocalTime.now().toString();
        String logEntry = String.format("[%s] ❌ ERREUR: %s", timestamp, error);
        executionLog.add(logEntry);
        System.err.println("  " + logEntry);
    }
    
    protected void putContext(String key, Object value) {
        context.put(key, value);
    }
    
    protected <T> T getContext(String key, Class<T> type) {
        Object value = context.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }
    
    // Getters
    public String getWorkflowName() {
        return workflowName;
    }
    
    public List<String> getExecutionLog() {
        return new ArrayList<>(executionLog);
    }
    
    public Map<String, Object> getContext() {
        return new HashMap<>(context);
    }
}

// Implémentation concrète pour l'ouverture de compte bancaire
public class BankAccountOpeningWorkflow extends WorkflowTemplate {
    private static final String[] REQUIRED_DOCUMENTS = {"ID_CARD", "PROOF_OF_ADDRESS", "BANK_STATEMENT"};
    private static final String[] RESTRICTED_COUNTRIES = {"XX", "YY", "ZZ"};
    
    public BankAccountOpeningWorkflow() {
        super("Ouverture Compte Bancaire");
    }
    
    @Override
    protected boolean validateDocuments() {
        logStep("Validation des documents bancaires");
        
        @SuppressWarnings("unchecked")
        List<String> documents = (List<String>) getContext("documents", List.class);
        
        if (documents == null || documents.isEmpty()) {
            logError("Aucun document fourni");
            return false;
        }
        
        // Vérification des documents requis
        for (String requiredDoc : REQUIRED_DOCUMENTS) {
            if (!documents.contains(requiredDoc)) {
                logError("Document requis manquant: " + requiredDoc);
                return false;
            }
        }
        
        logStep("Documents validés: " + documents);
        return true;
    }
    
    @Override
    protected boolean checkBackground() {
        logStep("Vérification des antécédents bancaires");
        
        String customerName = getContext("customerName", String.class);
        String nationalId = getContext("nationalId", String.class);
        
        if (customerName == null || nationalId == null) {
            logError "Informations client incomplètes");
            return false;
        }
        
        // Simulation de vérification des antécédents
        logStep("Vérification du score de crédit pour: " + customerName);
        logStep("Vérification des listes noires pour: " + nationalId);
        logStep("Vérification des antécédents judiciaires");
        
        // Simulation du résultat
        boolean backgroundClear = !customerName.contains("BLACKLISTED") && 
                              !nationalId.contains("SUSPICIOUS");
        
        if (!backgroundClear) {
            logError("Antécédents non conformes");
            return false;
        }
        
        logStep("Antécédents vérifiés avec succès");
        return true;
    }
    
    @Override
    protected boolean finalValidation() {
        logStep("Validation finale bancaire");
        
        // Vérification du pays de résidence
        String residenceCountry = getContext("residenceCountry", String.class);
        if (residenceCountry != null) {
            for (String restricted : RESTRICTED_COUNTRIES) {
                if (restricted.equals(residenceCountry)) {
                    logError("Pays de résidence restreint: " + residenceCountry);
                    return false;
                }
            }
        }
        
        // Vérification du dépôt initial
        Double initialDeposit = getContext("initialDeposit", Double.class);
        if (initialDeposit == null || initialDeposit < 100.0) {
            logError("Dépôt initial minimum requis: 100.0");
            return false;
        }
        
        logStep("Validation finale réussie");
        return true;
    }
    
    @Override
    protected void generateOperatorSpecificReports() {
        logStep("Génération des rapports bancaires spécifiques");
        
        Map<String, Object> bankReport = new HashMap<>();
        bankReport.put("accountType", "CHECKING");
        bankReport.put("riskLevel", calculateRiskLevel());
        bankReport.put("creditLimit", calculateCreditLimit());
        bankReport.put("fees", calculateAccountFees());
        
        putContext("bankSpecificReport", bankReport);
    }
    
    @Override
    protected void sendOperatorSpecificNotifications() {
        logStep("Envoi des notifications bancaires spécifiques");
        
        String customerEmail = getContext("customerEmail", String.class);
        String accountNumber = generateAccountNumber();
        
        if (customerEmail != null) {
            System.out.println("📧 Email bancaire envoyé à " + customerEmail);
            System.out.println("   Numéro de compte: " + accountNumber);
            System.out.println("   Kits de bienvenue expédié");
        }
        
        putContext("generatedAccountNumber", accountNumber);
    }
    
    // Méthodes spécifiques à la banque
    private String calculateRiskLevel() {
        Double initialDeposit = getContext("initialDeposit", Double.class);
        String customerType = getContext("customerType", String.class);
        
        if (initialDeposit != null && initialDeposit > 10000.0) {
            return "LOW";
        } else if ("PREMIUM".equals(customerType)) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }
    
    private double calculateCreditLimit() {
        Double initialDeposit = getContext("initialDeposit", Double.class);
        return initialDeposit != null ? initialDeposit * 2.0 : 1000.0;
    }
    
    private double calculateAccountFees() {
        return 5.0; // Frais mensuels fixes
    }
    
    private String generateAccountNumber() {
        return "BANK" + System.currentTimeMillis() % 1000000;
    }
}

// Implémentation concrète pour l'ouverture de compte Mobile Money
public class MobileMoneyAccountOpeningWorkflow extends WorkflowTemplate {
    private static final String[] REQUIRED_DOCUMENTS = {"PHONE_NUMBER", "ID_CARD", "SELFIE"};
    private static final String[] SUPPORTED_COUNTRIES = {"CM", "FR", "US", "CA"};
    
    public MobileMoneyAccountOpeningWorkflow() {
        super("Ouverture Compte Mobile Money");
    }
    
    @Override
    protected boolean validateDocuments() {
        logStep("Validation des documents Mobile Money");
        
        String phoneNumber = getContext("phoneNumber", String.class);
        String idDocument = getContext("idDocument", String.class);
        String selfiePhoto = getContext("selfiePhoto", String.class);
        
        if (phoneNumber == null || !phoneNumber.matches("\\+?[0-9]{10,15}")) {
            logError("Numéro de téléphone invalide: " + phoneNumber);
            return false;
        }
        
        if (idDocument == null || idDocument.isEmpty()) {
            logError("Document d'identité manquant");
            return false;
        }
        
        if (selfiePhoto == null || selfiePhoto.isEmpty()) {
            logError("Photo selfie manquante");
            return false;
        }
        
        logStep("Documents validés: Téléphone, ID, Selfie");
        return true;
    }
    
    @Override
    protected boolean checkBackground() {
        logStep("Vérification des antécédents Mobile Money");
        
        String phoneNumber = getContext("phoneNumber", String.class);
        
        // Vérification spécifique Mobile Money
        logStep("Vérification du numéro de téléphone: " + phoneNumber);
        logStep("Vérification d'enregistrement SIM");
        logStep("Vérification des limitations d'âge");
        
        // Vérification de l'âge (simulation)
        String birthDate = getContext("birthDate", String.class);
        if (birthDate != null) {
            boolean isAdult = verifyAdultAge(birthDate);
            if (!isAdult) {
                logError("Client mineur non autorisé");
                return false;
            }
        }
        
        logStep("Antécédents Mobile Money vérifiés avec succès");
        return true;
    }
    
    @Override
    protected boolean finalValidation() {
        logStep("Validation finale Mobile Money");
        
        // Vérification du pays supporté
        String countryCode = getContext("countryCode", String.class);
        if (countryCode != null) {
            boolean supported = false;
            for (String supportedCountry : SUPPORTED_COUNTRIES) {
                if (supportedCountry.equals(countryCode)) {
                    supported = true;
                    break;
                }
            }
            
            if (!supported) {
                logError("Pays non supporté: " + countryCode);
                return false;
            }
        }
        
        // Vérification du plafond initial
        Double initialBalance = getContext("initialBalance", Double.class);
        if (initialBalance != null && initialBalance > 1000000.0) {
            logError("Plafond initial dépassé: " + initialBalance);
            return false;
        }
        
        logStep("Validation finale réussie");
        return true;
    }
    
    @Override
    protected void generateOperatorSpecificReports() {
        logStep("Génération des rapports Mobile Money spécifiques");
        
        Map<String, Object> mobileReport = new HashMap<>();
        mobileReport.put("accountType", "MOBILE_MONEY");
        mobileReport.put("transactionLimit", calculateTransactionLimit());
        mobileReport.put("dailyLimit", calculateDailyLimit());
        mobileReport.put("supportedServices", getSupportedServices());
        
        putContext("mobileSpecificReport", mobileReport);
    }
    
    @Override
    protected void sendOperatorSpecificNotifications() {
        logStep("Envoi des notifications Mobile Money spécifiques");
        
        String phoneNumber = getContext("phoneNumber", String.class);
        String accountNumber = generateMobileAccountNumber();
        
        if (phoneNumber != null) {
            System.out.println("📱 SMS de bienvenue envoyé à " + phoneNumber);
            System.out.println("   Numéro de compte: " + accountNumber);
            System.out.println("   PIN temporaire généré");
            System.out.println("   Instructions d'activation envoyées");
        }
        
        putContext("generatedAccountNumber", accountNumber);
    }
    
    @Override
    protected boolean performAdditionalPreValidation() {
        // Validation spécifique Mobile Money
        String phoneNumber = getContext("phoneNumber", String.class);
        if (phoneNumber != null && phoneNumber.startsWith("+237")) {
            logStep("Détection numéro camerounais - validation spécifique");
        }
        return true;
    }
    
    // Méthodes spécifiques à Mobile Money
    private boolean verifyAdultAge(String birthDate) {
        // Simplifié: vérifie si la date de naissance indique un âge >= 18 ans
        return true; // Simulation
    }
    
    private double calculateTransactionLimit() {
        return 500000.0; // Limite par transaction
    }
    
    private double calculateDailyLimit() {
        return 2000000.0; // Limite quotidienne
    }
    
    private List<String> getSupportedServices() {
        List<String> services = new ArrayList<>();
        services.add("MONEY_TRANSFER");
        services.add("BILL_PAYMENT");
        services.add("AIRTIME_TOPUP");
        services.add("MERCHANT_PAYMENT");
        return services;
    }
    
    private String generateMobileAccountNumber() {
        return "MOB" + System.currentTimeMillis() % 1000000;
    }
}

// Service de gestion des workflows
public class WorkflowService {
    private Map<String, WorkflowTemplate> availableWorkflows;
    
    public WorkflowService() {
        this.availableWorkflows = new HashMap<>();
        registerWorkflows();
    }
    
    private void registerWorkflows() {
        availableWorkflows.put("BANK_ACCOUNT_OPENING", new BankAccountOpeningWorkflow());
        availableWorkflows.put("MOBILE_MONEY_ACCOUNT_OPENING", new MobileMoneyAccountOpeningWorkflow());
    }
    
    public void executeWorkflow(String workflowKey, Map<String, Object> inputData) {
        WorkflowTemplate workflow = availableWorkflows.get(workflowKey);
        if (workflow == null) {
            System.err.println("Workflow non trouvé: " + workflowKey);
            return;
        }
        
        workflow.execute(inputData);
    }
    
    public void listAvailableWorkflows() {
        System.out.println("\n=== Workflows disponibles ===");
        for (Map.Entry<String, WorkflowTemplate> entry : availableWorkflows.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().getWorkflowName());
        }
        System.out.println();
    }
    
    public WorkflowTemplate getWorkflow(String workflowKey) {
        return availableWorkflows.get(workflowKey);
    }
    
    public void printWorkflowExecutionLog(String workflowKey) {
        WorkflowTemplate workflow = availableWorkflows.get(workflowKey);
        if (workflow != null) {
            System.out.println("\n=== Journal d'exécution du workflow " + workflowKey + " ===");
            for (String logEntry : workflow.getExecutionLog()) {
                System.out.println(logEntry);
            }
            System.out.println("=== Fin journal ===\n");
        }
    }
}
```

## Tests et scénarios de démonstration

```java
public class TestWorkflowTemplate {
    public static void main(String[] args) {
        WorkflowService workflowService = new WorkflowService();
        
        // Test 1: Ouverture de compte bancaire réussie
        System.out.println("=== Test 1: Ouverture de compte bancaire réussie ===");
        Map<String, Object> bankData = createValidBankAccountData();
        workflowService.executeWorkflow("BANK_ACCOUNT_OPENING", bankData);
        
        // Test 2: Ouverture de compte bancaire échouée (documents manquants)
        System.out.println("\n=== Test 2: Ouverture de compte bancaire échouée ===");
        Map<String, Object> incompleteBankData = createIncompleteBankAccountData();
        workflowService.executeWorkflow("BANK_ACCOUNT_OPENING", incompleteBankData);
        
        // Test 3: Ouverture de compte Mobile Money réussie
        System.out.println("\n=== Test 3: Ouverture de compte Mobile Money réussie ===");
        Map<String, Object> mobileData = createValidMobileAccountData();
        workflowService.executeWorkflow("MOBILE_MONEY_ACCOUNT_OPENING", mobileData);
        
        // Test 4: Ouverture de compte Mobile Money échouée (pays non supporté)
        System.out.println("\n=== Test 4: Ouverture de compte Mobile Money échouée ===");
        Map<String, Object> invalidMobileData = createInvalidMobileAccountData();
        workflowService.executeWorkflow("MOBILE_MONEY_ACCOUNT_OPENING", invalidMobileData);
        
        // Test 5: Affichage des workflows disponibles
        System.out.println("\n=== Test 5: Workflows disponibles ===");
        workflowService.listAvailableWorkflows();
        
        // Test 6: Journal d'exécution détaillé
        System.out.println("\n=== Test 6: Journal d'exécution détaillé ===");
        workflowService.printWorkflowExecutionLog("BANK_ACCOUNT_OPENING");
        workflowService.printWorkflowExecutionLog("MOBILE_MONEY_ACCOUNT_OPENING");
        
        // Test 7: Comparaison des workflows
        System.out.println("\n=== Test 7: Comparaison des workflows ===");
        compareWorkflowStructures();
    }
    
    private static Map<String, Object> createValidBankAccountData() {
        Map<String, Object> data = new HashMap<>();
        data.put("accountId", "BANK123456");
        data.put("requestType", "ACCOUNT_OPENING");
        data.put("customerName", "Alice Martin");
        data.put("customerEmail", "alice@email.com");
        data.put("nationalId", "CM123456789");
        data.put("residenceCountry", "CM");
        data.put("initialDeposit", 5000.0);
        data.put("customerType", "STANDARD");
        
        List<String> documents = new ArrayList<>();
        documents.add("ID_CARD");
        documents.add("PROOF_OF_ADDRESS");
        documents.add("BANK_STATEMENT");
        data.put("documents", documents);
        
        return data;
    }
    
    private static Map<String, Object> createIncompleteBankAccountData() {
        Map<String, Object> data = createValidBankAccountData();
        List<String> incompleteDocuments = new ArrayList<>();
        incompleteDocuments.add("ID_CARD");
        incompleteDocuments.add("PROOF_OF_ADDRESS");
        // BANK_STATEMENT manquant
        data.put("documents", incompleteDocuments);
        return data;
    }
    
    private static Map<String, Object> createValidMobileAccountData() {
        Map<String, Object> data = new HashMap<>();
        data.put("accountId", "MOB789012");
        data.put("requestType", "ACCOUNT_OPENING");
        data.put("phoneNumber", "+237123456789");
        data.put("countryCode", "CM");
        data.put("initialBalance", 50000.0);
        data.put("birthDate", "1990-01-01");
        data.put("idDocument", "ID_CARD_BASE64");
        data.put("selfiePhoto", "SELFIE_BASE64");
        
        return data;
    }
    
    private static Map<String, Object> createInvalidMobileAccountData() {
        Map<String, Object> data = createValidMobileAccountData();
        data.put("countryCode", "XX"); // Pays non supporté
        data.put("initialBalance", 2000000.0); // Plafond dépassé
        return data;
    }
    
    private static void compareWorkflowStructures() {
        System.out.println("=== Comparaison des structures de workflow ===");
        
        WorkflowTemplate bankWorkflow = new BankAccountOpeningWorkflow();
        WorkflowTemplate mobileWorkflow = new MobileMoneyAccountOpeningWorkflow();
        
        System.out.println("\nWorkflow Bancaire:");
        System.out.println("- Documents requis: ID_CARD, PROOF_OF_ADDRESS, BANK_STATEMENT");
        System.out.println("- Dépôt minimum: 100.0");
        System.out.println("- Vérification pays: Restreint (XX, YY, ZZ)");
        
        System.out.println("\nWorkflow Mobile Money:");
        System.out.println("- Documents requis: PHONE_NUMBER, ID_CARD, SELFIE");
        System.out.println("- Pas de dépôt minimum");
        System.out.println("- Vérification pays: Supporté (CM, FR, US, CA)");
        
        System.out.println("\nPoints communs:");
        System.out.println("- Squelette identique (Template Method)");
        System.out.println("- Validation préliminaire commune");
        System.out.println("- Étapes principales: Documents -> Antécédents -> Traitement -> Validation finale");
        System.out.println("- Génération de rapports");
        System.out.println("- Envoi de notifications");
        
        System.out.println("\nPoints de divergence:");
        System.out.println("- Types de documents différents");
        System.out.println("- Critères de vérification spécifiques");
        System.out.println("- Rapports spécialisés");
        System.out.println("- Canaux de notification différents");
    }
}
```

### Résultats attendus

```
=== Test 1: Ouverture de compte bancaire réussie ===
🔄 === DÉBUT DU WORKFLOW: Ouverture Compte Bancaire ===
  [timestamp] Initialisation du contexte
  [timestamp] Données d'entrée: {accountId=BANK123456, requestType=ACCOUNT_OPENING, ...}
  [timestamp] Pré-validation du traitement
  [timestamp] Début des étapes principales
  [timestamp] Validation des documents bancaires
  [timestamp] Documents validés: [ID_CARD, PROOF_OF_ADDRESS, BANK_STATEMENT]
  [timestamp] Vérification des antécédents bancaires
  [timestamp] Vérification du score de crédit pour: Alice Martin
  [timestamp] Vérification des listes noires pour: CM123456789
  [timestamp] Vérification des antécédents judiciaires
  [timestamp] Antécédents vérifiés avec succès
  [timestamp] Traitement standard de la demande
  [timestamp] Demande traitée avec succès
  [timestamp] Validation finale bancaire
  [timestamp] Validation finale réussie
  [timestamp] Post-validation du traitement
  [timestamp] Génération des rapports
  [timestamp] Génération des rapports standards
  [timestamp] Génération des rapports bancaires spécifiques
  [timestamp] Envoi des notifications
  [timestamp] Envoi des notifications standards
  [timestamp] Envoi des notifications bancaires spécifiques
📧 Email bancaire envoyé à alice@email.com
   Numéro de compte: BANK123456
   Kits de bienvenue expédié
  [timestamp] Finalisation du workflow: Workflow complété avec succès
  [timestamp] Durée totale: 150ms
  [timestamp] ✅ Workflow Ouverture Compte Bancaire terminé avec succès
🔄 === FIN DU WORKFLOW: Ouverture Compte Bancaire ===

=== Test 2: Ouverture de compte bancaire échouée ===
🔄 === DÉBUT DU WORKFLOW: Ouverture Compte Bancaire ===
  [timestamp] Validation des documents bancaires
  [timestamp] ❌ ERREUR: Document requis manquant: BANK_STATEMENT
  [timestamp] Finalisation du workflow: Échec lors des étapes principales
  [timestamp] Durée totale: 25ms
  [timestamp] ❌ Workflow Ouverture Compte Bancaire échoué
🔄 === FIN DU WORKFLOW: Ouverture Compte Bancaire ===

=== Test 3: Ouverture de compte Mobile Money réussie ===
🔄 === DÉBUT DU WORKFLOW: Ouverture Compte Mobile Money ===
  [timestamp] Validation des documents Mobile Money
  [timestamp] Documents validés: Téléphone, ID, Selfie
  [timestamp] Vérification des antécédents Mobile Money
  [timestamp] Vérification du numéro de téléphone: +237123456789
  [timestamp] Vérification d'enregistrement SIM
  [timestamp] Vérification des limitations d'âge
  [timestamp] Antécédents Mobile Money vérifiés avec succès
  [timestamp] Traitement standard de la demande
  [timestamp] Validation finale Mobile Money
  [timestamp] Validation finale réussie
  [timestamp] Génération des rapports Mobile Money spécifiques
  [timestamp] Envoi des notifications Mobile Money spécifiques
📱 SMS de bienvenue envoyé à +237123456789
   Numéro de compte: MOB789012
   PIN temporaire généré
   Instructions d'activation envoyées
  [timestamp] ✅ Workflow Ouverture Compte Mobile Money terminé avec succès
🔄 === FIN DU WORKFLOW: Ouverture Compte Mobile Money ===

=== Test 7: Comparaison des structures de workflow ===
=== Comparaison des structures de workflow ===

Workflow Bancaire:
- Documents requis: ID_CARD, PROOF_OF_ADDRESS, BANK_STATEMENT
- Dépôt minimum: 100.0
- Vérification pays: Restreint (XX, YY, ZZ)

Workflow Mobile Money:
- Documents requis: PHONE_NUMBER, ID_CARD, SELFIE
- Pas de dépôt minimum
- Vérification pays: Supporté (CM, FR, US, CA)

Points communs:
- Squelette identique (Template Method)
- Validation préliminaire commune
- Étapes principales: Documents -> Antécédents -> Traitement -> Validation finale
- Génération de rapports
- Envoi de notifications

Points de divergence:
- Types de documents différents
- Critères de vérification spécifiques
- Rapports spécialisés
- Canaux de notification différents
```

Cette démonstration montre comment le pattern Template Method permet de créer des workflows avec un squelette commun tout en permettant des spécialisations significatives pour chaque type d'opérateur.