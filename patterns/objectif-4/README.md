# Objectif 4: Ressources Globales

## Description brève de la solution

Pour résoudre le problème des ressources globales accessibles de manière thread-safe, nous utilisons le pattern **Singleton**:

- **Singleton Pattern**: Garantit qu'une classe n'a qu'une seule instance tout en fournissant un point d'accès global à cette instance. Nous l'appliquons aux services de notification, à la configuration d'authentification par défaut et au gestionnaire d'événements système.

## Justification architecturale

Cette approche est adaptée car :

1. **Contrôle d'accès**: Garantit qu'il n'y a qu'une seule instance des ressources critiques (service SMS, gestionnaire d'événements).

2. **Thread-safety**: Implémentation thread-safe pour éviter les problèmes de concurrence dans un environnement multi-threadé.

3. **Point d'accès global**: Fournit un accès uniforme à ces ressources depuis n'importe où dans l'application.

4. **Gestion des ressources**: Permet un contrôle centralisé du cycle de vie des ressources globales.

## Implémentation

```java
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

// Singleton pour le service de notifications
public class NotificationService {
    private static volatile NotificationService instance;
    private static final ReentrantLock lock = new ReentrantLock();
    
    private ISMSAdapter smsAdapter;
    private final Map<String, Object> configuration;
    private final List<NotificationListener> listeners;
    
    // Constructeur privé pour éviter l'instanciation externe
    private NotificationService() {
        this.configuration = new ConcurrentHashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.smsAdapter = null; // Sera configuré plus tard
        System.out.println("NotificationService: Instance unique créée");
    }
    
    // Double-checked locking pour thread-safety
    public static NotificationService getInstance() {
        if (instance == null) {
            lock.lock();
            try {
                if (instance == null) {
                    instance = new NotificationService();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }
    
    // Configuration de l'adaptateur SMS
    public void setSMSAdapter(ISMSAdapter adapter) {
        this.smsAdapter = adapter;
        System.out.println("NotificationService: Adaptateur SMS configuré");
    }
    
    // Configuration globale
    public void setConfiguration(String key, Object value) {
        configuration.put(key, value);
        System.out.println("NotificationService: Configuration mise à jour - " + key + " = " + value);
    }
    
    public Object getConfiguration(String key) {
        return configuration.get(key);
    }
    
    // Envoi de notification
    public void envoyerNotification(String destination, String message) {
        if (smsAdapter == null) {
            throw new IllegalStateException("Adaptateur SMS non configuré");
        }
        
        try {
            smsAdapter.sendSMS(destination, message);
            notifyListeners(destination, message, true);
            System.out.println("NotificationService: Notification envoyée à " + destination);
        } catch (Exception e) {
            notifyListeners(destination, message, false);
            System.err.println("NotificationService: Erreur lors de l'envoi - " + e.getMessage());
        }
    }
    
    // Gestion des listeners
    public void addListener(NotificationListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(NotificationListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(String destination, String message, boolean success) {
        for (NotificationListener listener : listeners) {
            try {
                listener.onNotificationSent(destination, message, success);
            } catch (Exception e) {
                System.err.println("Erreur dans le listener: " + e.getMessage());
            }
        }
    }
    
    // Nettoyage à la fin de l'exécution
    public void cleanup() {
        System.out.println("NotificationService: Nettoyage des ressources");
        listeners.clear();
        configuration.clear();
        smsAdapter = null;
    }
    
    // Interface pour les listeners
    public interface NotificationListener {
        void onNotificationSent(String destination, String message, boolean success);
    }
}

// Singleton pour la configuration d'authentification par défaut
public class AuthentificationConfig {
    private static final AtomicReference<AuthentificationConfig> instance = new AtomicReference<>();
    
    private final Map<String, String> defaultMethods;
    private final Map<String, Object> securitySettings;
    
    private AuthentificationConfig() {
        this.defaultMethods = new ConcurrentHashMap<>();
        this.securitySettings = new ConcurrentHashMap<>();
        
        // Configuration par défaut
        defaultMethods.put("bank", "password");
        defaultMethods.put("mobile_money", "otp");
        defaultMethods.put("admin", "biometrie");
        
        securitySettings.put("max_attempts", 3);
        securitySettings.put("lockout_duration", 300); // 5 minutes
        securitySettings.put("session_timeout", 1800); // 30 minutes
        
        System.out.println("AuthentificationConfig: Configuration globale créée");
    }
    
    public static AuthentificationConfig getInstance() {
        if (instance.get() == null) {
            instance.compareAndSet(null, new AuthentificationConfig());
        }
        return instance.get();
    }
    
    public String getDefaultMethod(String operatorType) {
        return defaultMethods.get(operatorType);
    }
    
    public void setDefaultMethod(String operatorType, String method) {
        defaultMethods.put(operatorType, method);
        System.out.println("AuthentificationConfig: Méthode par défaut pour " + operatorType + " = " + method);
    }
    
    public Object getSecuritySetting(String key) {
        return securitySettings.get(key);
    }
    
    public void setSecuritySetting(String key, Object value) {
        securitySettings.put(key, value);
    }
    
    public Map<String, String> getAllDefaultMethods() {
        return new ConcurrentHashMap<>(defaultMethods);
    }
}

// Singleton pour le gestionnaire d'événements système
public class EventManager {
    private static volatile EventManager instance;
    private static final Object lockObject = new Object();
    
    private final Map<String, List<EventListener>> subscribers;
    private final Map<String, Integer> eventCounters;
    
    private EventManager() {
        this.subscribers = new ConcurrentHashMap<>();
        this.eventCounters = new ConcurrentHashMap<>();
        System.out.println("EventManager: Gestionnaire d'événements créé");
    }
    
    public static EventManager getInstance() {
        if (instance == null) {
            synchronized (lockObject) {
                if (instance == null) {
                    instance = new EventManager();
                }
            }
        }
        return instance;
    }
    
    // Abonnement à des événements
    public void subscribe(String eventType, EventListener listener) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
        System.out.println("EventManager: Abonnement à l'événement " + eventType);
    }
    
    public void unsubscribe(String eventType, EventListener listener) {
        List<EventListener> listeners = subscribers.get(eventType);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                subscribers.remove(eventType);
            }
        }
    }
    
    // Publication d'événements
    public void publishEvent(Event event) {
        String eventType = event.getType();
        
        // Incrément du compteur
        eventCounters.merge(eventType, 1, Integer::sum);
        
        List<EventListener> listeners = subscribers.get(eventType);
        if (listeners != null) {
            System.out.println("EventManager: Publication de l'événement " + eventType + " à " + listeners.size() + " abonnés");
            
            for (EventListener listener : listeners) {
                try {
                    listener.handleEvent(event);
                } catch (Exception e) {
                    System.err.println("Erreur lors du traitement de l'événement: " + e.getMessage());
                }
            }
        } else {
            System.out.println("EventManager: Aucun abonné pour l'événement " + eventType);
        }
    }
    
    // Statistiques
    public int getEventCount(String eventType) {
        return eventCounters.getOrDefault(eventType, 0);
    }
    
    public Map<String, Integer> getAllEventCounts() {
        return new ConcurrentHashMap<>(eventCounters);
    }
    
    // Nettoyage
    public void cleanup() {
        System.out.println("EventManager: Nettoyage des abonnements");
        subscribers.clear();
        eventCounters.clear();
    }
    
    // Classes internes
    public interface EventListener {
        void handleEvent(Event event);
    }
    
    public static class Event {
        private final String type;
        private final String source;
        private final Map<String, Object> data;
        private final long timestamp;
        
        public Event(String type, String source, Map<String, Object> data) {
            this.type = type;
            this.source = source;
            this.data = new ConcurrentHashMap<>(data);
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getType() { return type; }
        public String getSource() { return source; }
        public Map<String, Object> getData() { return data; }
        public long getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("Event{type='%s', source='%s', timestamp=%d}", type, source, timestamp);
        }
    }
}

// Interface pour les adaptateurs SMS (réutilisée de l'objectif 5)
public interface ISMSAdapter {
    void sendSMS(String destination, String message);
}

// Test de concurrence minimal
public class ConcurrencyTest {
    private static final int THREAD_COUNT = 10;
    private static final int OPERATIONS_PER_THREAD = 100;
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Test de concurrence des Singletons ===");
        
        // Test 1: Vérification de l'unicité des instances
        testSingletonUniqueness();
        
        // Test 2: Accès concurrent
        testConcurrentAccess();
        
        // Test 3: Nettoyage en fin d'exécution
        testCleanup();
    }
    
    private static void testSingletonUniqueness() {
        System.out.println("\n--- Test 1: Unicité des instances ---");
        
        // Création de plusieurs références
        NotificationService ns1 = NotificationService.getInstance();
        NotificationService ns2 = NotificationService.getInstance();
        NotificationService ns3 = NotificationService.getInstance();
        
        AuthentificationConfig ac1 = AuthentificationConfig.getInstance();
        AuthentificationConfig ac2 = AuthentificationConfig.getInstance();
        
        EventManager em1 = EventManager.getInstance();
        EventManager em2 = EventManager.getInstance();
        
        // Vérification que toutes les références pointent vers la même instance
        System.out.println("NotificationService instances identiques: " + (ns1 == ns2 && ns2 == ns3));
        System.out.println("AuthentificationConfig instances identiques: " + (ac1 == ac2));
        System.out.println("EventManager instances identiques: " + (em1 == em2));
    }
    
    private static void testConcurrentAccess() throws InterruptedException {
        System.out.println("\n--- Test 2: Accès concurrent ---");
        
        Thread[] threads = new Thread[THREAD_COUNT];
        int[] successCount = new int[THREAD_COUNT];
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    try {
                        // Accès concurrent aux singletons
                        NotificationService ns = NotificationService.getInstance();
                        AuthentificationConfig ac = AuthentificationConfig.getInstance();
                        EventManager em = EventManager.getInstance();
                        
                        // Publication d'événements
                        Map<String, Object> eventData = new ConcurrentHashMap<>();
                        eventData.put("thread", threadIndex);
                        eventData.put("operation", j);
                        
                        EventManager.Event event = new EventManager.Event("TEST_EVENT", 
                            "Thread-" + threadIndex, eventData);
                        em.publishEvent(event);
                        
                        // Configuration
                        ac.setSecuritySetting("thread_" + threadIndex + "_ops", j);
                        
                        successCount[threadIndex]++;
                        
                    } catch (Exception e) {
                        System.err.println("Erreur dans le thread " + threadIndex + ": " + e.getMessage());
                    }
                }
            });
        }
        
        // Démarrage des threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Attente de la fin
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Vérification des résultats
        int totalOperations = 0;
        for (int count : successCount) {
            totalOperations += count;
        }
        
        System.out.println("Opérations réussies: " + totalOperations + "/" + (THREAD_COUNT * OPERATIONS_PER_THREAD));
        System.out.println("Statistiques des événements: " + EventManager.getInstance().getAllEventCounts());
    }
    
    private static void testCleanup() {
        System.out.println("\n--- Test 3: Nettoyage des ressources ---");
        
        // Configuration avant nettoyage
        NotificationService ns = NotificationService.getInstance();
        ns.setConfiguration("test", "value");
        ns.addListener((dest, msg, success) -> {});
        
        EventManager em = EventManager.getInstance();
        em.subscribe("CLEANUP_TEST", event -> {});
        
        // Publication d'un événement de test
        Map<String, Object> data = new ConcurrentHashMap<>();
        data.put("action", "cleanup_test");
        em.publishEvent(new EventManager.Event("CLEANUP_TEST", "TestThread", data));
        
        // Nettoyage
        ns.cleanup();
        em.cleanup();
        
        System.out.println("Nettoyage terminé");
    }
}
```

## Tests et scénarios de démonstration

```java
public class TestSingletonPatterns {
    public static void main(String[] args) throws InterruptedException {
        // Test 1: Configuration et utilisation des singletons
        System.out.println("=== Test 1: Configuration des services globaux ===");
        
        NotificationService notificationService = NotificationService.getInstance();
        AuthentificationConfig authConfig = AuthentificationConfig.getInstance();
        EventManager eventManager = EventManager.getInstance();
        
        // Configuration du service de notification
        notificationService.setConfiguration("retry_attempts", 3);
        notificationService.setConfiguration("timeout_ms", 5000);
        
        // Configuration de l'authentification
        authConfig.setDefaultMethod("bank", "biometrie");
        authConfig.setSecuritySetting("max_attempts", 5);
        
        // Ajout de listeners
        notificationService.addListener((dest, msg, success) -> {
            System.out.println("Listener: Notification " + (success ? "réussie" : "échouée") + " vers " + dest);
        });
        
        eventManager.subscribe("TRANSACTION", event -> {
            System.out.println("Listener: Événement transaction reçu de " + event.getSource());
        });
        
        eventManager.subscribe("AUTHENTIFICATION", event -> {
            System.out.println("Listener: Événement authentification: " + event.getData());
        });
        
        // Test 2: Utilisation des services
        System.out.println("\n=== Test 2: Utilisation des services ===");
        
        // Publication d'événements
        Map<String, Object> transactionData = new ConcurrentHashMap<>();
        transactionData.put("amount", 1000.0);
        transactionData.put("source", "ACC001");
        transactionData.put("dest", "ACC002");
        
        EventManager.Event transactionEvent = new EventManager.Event("TRANSACTION", 
            "BankService", transactionData);
        eventManager.publishEvent(transactionEvent);
        
        Map<String, Object> authData = new ConcurrentHashMap<>();
        authData.put("user", "user123");
        authData.put("method", "password");
        authData.put("success", true);
        
        EventManager.Event authEvent = new EventManager.Event("AUTHENTIFICATION", 
            "AuthService", authData);
        eventManager.publishEvent(authEvent);
        
        // Test 3: Accès depuis différents contextes
        System.out.println("\n=== Test 3: Accès depuis différents contextes ===");
        
        // Simulation d'accès depuis différentes parties du système
        simulateBankServiceAccess();
        simulateMobileMoneyServiceAccess();
        simulateAdminServiceAccess();
        
        // Test 4: Statistiques et monitoring
        System.out.println("\n=== Test 4: Statistiques ===");
        System.out.println("Événements publiés: " + eventManager.getAllEventCounts());
        System.out.println("Configuration authentification: " + authConfig.getAllDefaultMethods());
        System.out.println("Paramètres sécurité: " + authConfig.getSecuritySetting("max_attempts"));
        
        // Test de concurrence
        System.out.println("\n=== Test 5: Test de concurrence ===");
        ConcurrencyTest.main(new String[]{});
    }
    
    private static void simulateBankServiceAccess() {
        NotificationService ns = NotificationService.getInstance();
        AuthentificationConfig ac = AuthentificationConfig.getInstance();
        
        System.out.println("Service Bancaire - Méthode par défaut: " + ac.getDefaultMethod("bank"));
        ns.setConfiguration("bank_priority", "high");
    }
    
    private static void simulateMobileMoneyServiceAccess() {
        NotificationService ns = NotificationService.getInstance();
        AuthentificationConfig ac = AuthentificationConfig.getInstance();
        
        System.out.println("Service Mobile Money - Méthode par défaut: " + ac.getDefaultMethod("mobile_money"));
        ns.setConfiguration("mobile_money_priority", "normal");
    }
    
    private static void simulateAdminServiceAccess() {
        NotificationService ns = NotificationService.getInstance();
        AuthentificationConfig ac = AuthentificationConfig.getInstance();
        
        System.out.println("Service Admin - Méthode par défaut: " + ac.getDefaultMethod("admin"));
        ns.setConfiguration("admin_priority", "critical");
    }
}
```

### Résultats attendus

```
=== Test 1: Configuration des services globaux ===
NotificationService: Instance unique créée
AuthentificationConfig: Configuration globale créée
EventManager: Gestionnaire d'événements créé
NotificationService: Configuration mise à jour - retry_attempts = 3
NotificationService: Configuration mise à jour - timeout_ms = 5000
AuthentificationConfig: Méthode par défaut pour bank = biometrie
EventManager: Abonnement à l'événement TRANSACTION
EventManager: Abonnement à l'événement AUTHENTIFICATION

=== Test 2: Utilisation des services ===
EventManager: Publication de l'événement TRANSACTION à 1 abonnés
Listener: Événement transaction reçu de BankService
EventManager: Publication de l'événement AUTHENTIFICATION à 1 abonnés
Listener: Événement authentification: {user=user123, method=password, success=true}

=== Test 3: Accès depuis différents contextes ===
Service Bancaire - Méthode par défaut: biometrie
NotificationService: Configuration mise à jour - bank_priority = high
Service Mobile Money - Méthode par défaut: otp
NotificationService: Configuration mise à jour - mobile_money_priority = normal
Service Admin - Méthode par défaut: biometrie
NotificationService: Configuration mise à jour - admin_priority = critical

=== Test 4: Statistiques ===
Événements publiés: {TRANSACTION=1, AUTHENTIFICATION=1}
Configuration authentification: {admin=biometrie, bank=biometrie, mobile_money=otp}
Paramètres sécurité: 5

=== Test 5: Test de concurrence ===
=== Test de concurrence des Singletons ===

--- Test 1: Unicité des instances ---
NotificationService: Instance unique créée
AuthentificationConfig: Configuration globale créée
EventManager: Gestionnaire d'événements créé
NotificationService instances identiques: true
AuthentificationConfig instances identiques: true
EventManager instances identiques: true

--- Test 2: Accès concurrent ---
EventManager: Publication de l'événement TEST_EVENT à 0 abonnés
EventManager: Publication de l'événement TEST_EVENT à 0 abonnés
...
Opérations réussies: 1000/1000
Statistiques des événements: {TEST_EVENT=1000}

--- Test 3: Nettoyage des ressources ---
EventManager: Publication de l'événement CLEANUP_TEST à 1 abonnés
Listener: Événement cleanup_test reçu de TestThread
NotificationService: Nettoyage des ressources
EventManager: Nettoyage des abonnements
Nettoyage terminé