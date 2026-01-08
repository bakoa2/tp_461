# Objectif 5: Service SMS Homogénéisé

## Description brève de la solution

Pour résoudre le problème d'hétérogénéité des APIs SMS des différents prestataires, nous utilisons le pattern **Adapter**:

- **Adapter Pattern**: Permet de convertir l'interface d'une classe en une autre interface attendue par le client. Nous créons une interface commune `ISMSAdapter` qui masque les différences entre les fournisseurs A (JSON), B (SOAP), et C (URL particulière).

## Justification architecturale

Cette approche est adaptée car :

1. **Uniformisation**: Fournit une interface unique pour tous les prestataires SMS, facilitant l'utilisation dans le reste de l'application.

2. **Extensibilité**: Permet d'ajouter de nouveaux fournisseurs sans modifier le code métier existant.

3. **Découplage**: Le code métier ne dépend pas des implémentations spécifiques des fournisseurs.

4. **Maintenabilité**: Chaque adaptateur gère la complexité spécifique de son fournisseur, isolant cette complexité du reste du système.

## Implémentation

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.xml.soap.*;

// Interface cible commune
public interface ISMSAdapter {
    void sendSMS(String destination, String message);
    String getProviderName();
    boolean isAvailable();
}

// Classes de données pour les différents formats
class SMSRequest {
    private String to;
    private String message;
    private String apiKey;
    
    public SMSRequest(String to, String message, String apiKey) {
        this.to = to;
        this.message = message;
        this.apiKey = apiKey;
    }
    
    // Getters et setters
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}

class SMSResponse {
    private boolean success;
    private String messageId;
    private String error;
    
    // Getters et setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}

// Adaptateur pour le Fournisseur A (API JSON)
public class SMSAdapterA implements ISMSAdapter {
    private static final String API_URL = "https://api.sms-provider-a.com/send";
    private static final String API_KEY = "provider_a_key_123";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public SMSAdapterA() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public void sendSMS(String destination, String message) {
        try {
            System.out.println("SMSAdapterA: Préparation de l'envoi SMS au format JSON");
            
            // Création de la requête JSON
            SMSRequest request = new SMSRequest(destination, message, API_KEY);
            String jsonPayload = objectMapper.writeValueAsString(request);
            
            System.out.println("SMSAdapterA: Envoi de la requête JSON: " + jsonPayload);
            
            // Simulation d'appel HTTP
            HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
            
            HttpResponse<String> response = httpClient.send(httpRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            // Traitement de la réponse
            if (response.statusCode() == 200) {
                SMSResponse smsResponse = objectMapper.readValue(response.body(), SMSResponse.class);
                if (smsResponse.isSuccess()) {
                    System.out.println("SMSAdapterA: SMS envoyé avec succès - ID: " + smsResponse.getMessageId());
                } else {
                    throw new RuntimeException("Échec d'envoi: " + smsResponse.getError());
                }
            } else {
                throw new RuntimeException("Erreur HTTP: " + response.statusCode());
            }
            
        } catch (Exception e) {
            System.err.println("SMSAdapterA: Erreur lors de l'envoi - " + e.getMessage());
            throw new RuntimeException("Erreur d'envoi SMS", e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "Fournisseur A (JSON)";
    }
    
    @Override
    public boolean isAvailable() {
        try {
            // Simulation d'un ping de disponibilité
            HttpRequest pingRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api.sms-provider-a.com/health"))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(pingRequest, 
                HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}

// Adaptateur pour le Fournisseur B (API SOAP)
public class SMSAdapterB implements ISMSAdapter {
    private static final String SOAP_URL = "https://api.sms-provider-b.com/sms-service";
    private static final String SOAP_NAMESPACE = "http://provider-b.com/sms";
    
    @Override
    public void sendSMS(String destination, String message) {
        try {
            System.out.println("SMSAdapterB: Préparation de l'envoi SMS au format SOAP");
            
            // Création du message SOAP
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("sms", SOAP_NAMESPACE);
            
            SOAPBody body = envelope.getBody();
            SOAPElement sendSMSElement = body.addChildElement("SendSMS", "sms");
            
            SOAPElement toElement = sendSMSElement.addChildElement("To", "sms");
            toElement.addTextNode(destination);
            
            SOAPElement messageElement = sendSMSElement.addChildElement("Message", "sms");
            messageElement.addTextNode(message);
            
            SOAPElement authElement = sendSMSElement.addChildElement("AuthKey", "sms");
            authElement.addTextNode("provider_b_key_456");
            
            soapMessage.saveChanges();
            
            System.out.println("SMSAdapterB: Envoi du message SOAP");
            
            // Simulation d'appel SOAP
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnectionFactory.createConnection();
            
            SOAPMessage response = connection.call(soapMessage, SOAP_URL);
            
            // Traitement de la réponse
            SOAPBody responseBody = response.getSOAPBody();
            boolean success = Boolean.parseBoolean(
                responseBody.getElementsByTagName("Success").item(0).getTextContent());
            
            if (success) {
                String messageId = responseBody.getElementsByTagName("MessageId").item(0).getTextContent();
                System.out.println("SMSAdapterB: SMS envoyé avec succès - ID: " + messageId);
            } else {
                String error = responseBody.getElementsByTagName("Error").item(0).getTextContent();
                throw new RuntimeException("Échec d'envoi: " + error);
            }
            
            connection.close();
            
        } catch (Exception e) {
            System.err.println("SMSAdapterB: Erreur lors de l'envoi - " + e.getMessage());
            throw new RuntimeException("Erreur d'envoi SMS", e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "Fournisseur B (SOAP)";
    }
    
    @Override
    public boolean isAvailable() {
        try {
            // Simulation d'un ping SOAP
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapMessage = messageFactory.createMessage();
            
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            envelope.addNamespaceDeclaration("sms", SOAP_NAMESPACE);
            
            SOAPBody body = envelope.getBody();
            body.addChildElement("Ping", "sms");
            
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnectionFactory.createConnection();
            
            SOAPMessage response = connection.call(soapMessage, SOAP_URL + "/ping");
            
            SOAPBody responseBody = response.getSOAPBody();
            boolean isAlive = Boolean.parseBoolean(
                responseBody.getElementsByTagName("Alive").item(0).getTextContent());
            
            connection.close();
            return isAlive;
            
        } catch (Exception e) {
            return false;
        }
    }
}

// Adaptateur pour le Fournisseur C (URL particulière)
public class SMSAdapterC implements ISMSAdapter {
    private static final String BASE_URL = "https://sms-provider-c.com/api";
    private static final String API_TOKEN = "provider_c_token_789";
    private final HttpClient httpClient;
    
    public SMSAdapterC() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }
    
    @Override
    public void sendSMS(String destination, String message) {
        try {
            System.out.println("SMSAdapterC: Préparation de l'envoi SMS avec URL personnalisée");
            
            // Construction de l'URL avec paramètres
            String encodedMessage = java.net.URLEncoder.encode(message, "UTF-8");
            String url = String.format("%s/send?to=%s&message=%s&token=%s", 
                BASE_URL, destination, encodedMessage, API_TOKEN);
            
            System.out.println("SMSAdapterC: Envoi de la requête GET: " + url);
            
            // Appel HTTP GET
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            // Traitement de la réponse (format texte personnalisé)
            if (response.statusCode() == 200) {
                String responseBody = response.body();
                if (responseBody.startsWith("OK:")) {
                    String messageId = responseBody.substring(3); // Après "OK:"
                    System.out.println("SMSAdapterC: SMS envoyé avec succès - ID: " + messageId);
                } else {
                    throw new RuntimeException("Échec d'envoi: " + responseBody);
                }
            } else {
                throw new RuntimeException("Erreur HTTP: " + response.statusCode());
            }
            
        } catch (Exception e) {
            System.err.println("SMSAdapterC: Erreur lors de l'envoi - " + e.getMessage());
            throw new RuntimeException("Erreur d'envoi SMS", e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "Fournisseur C (URL)";
    }
    
    @Override
    public boolean isAvailable() {
        try {
            // Simulation d'un ping simple
            HttpRequest pingRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/status"))
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(pingRequest, 
                HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200 && response.body().contains("UP");
            
        } catch (Exception e) {
            return false;
        }
    }
}

// Nouveau fournisseur pour démontrer l'extensibilité
public class SMSAdapterD implements ISMSAdapter {
    private static final String API_URL = "https://api.sms-provider-d.com/v2/messages";
    private static final String BEARER_TOKEN = "Bearer provider_d_token_xyz";
    private final HttpClient httpClient;
    
    public SMSAdapterD() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }
    
    @Override
    public void sendSMS(String destination, String message) {
        try {
            System.out.println("SMSAdapterD: Envoi via API REST moderne avec authentification Bearer");
            
            // Format JSON moderne avec authentification Bearer
            String jsonPayload = String.format(
                "{\"recipient\":\"%s\",\"content\":\"%s\",\"priority\":\"high\"}", 
                destination, message.replace("\"", "\\\""));
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 201) { // Created
                System.out.println("SMSAdapterD: SMS créé et envoyé avec succès");
                System.out.println("Réponse: " + response.body());
            } else {
                throw new RuntimeException("Erreur: " + response.statusCode() + " - " + response.body());
            }
            
        } catch (Exception e) {
            System.err.println("SMSAdapterD: Erreur lors de l'envoi - " + e.getMessage());
            throw new RuntimeException("Erreur d'envoi SMS", e);
        }
    }
    
    @Override
    public String getProviderName() {
        return "Fournisseur D (REST Moderne)";
    }
    
    @Override
    public boolean isAvailable() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/health"))
                .header("Authorization", BEARER_TOKEN)
                .GET()
                .build();
            
            HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}

// Service unifié qui utilise les adaptateurs
public class UnifiedSMSService {
    private ISMSAdapter primaryAdapter;
    private ISMSAdapter fallbackAdapter;
    private final Map<String, ISMSAdapter> availableAdapters;
    
    public UnifiedSMSService() {
        this.availableAdapters = new HashMap<>();
        this.registerAdapters();
        this.selectBestAdapters();
    }
    
    private void registerAdapters() {
        // Enregistrement de tous les adaptateurs disponibles
        availableAdapters.put("provider_a", new SMSAdapterA());
        availableAdapters.put("provider_b", new SMSAdapterB());
        availableAdapters.put("provider_c", new SMSAdapterC());
        availableAdapters.put("provider_d", new SMSAdapterD());
    }
    
    private void selectBestAdapters() {
        // Sélection automatique des adaptateurs disponibles
        primaryAdapter = null;
        fallbackAdapter = null;
        
        for (ISMSAdapter adapter : availableAdapters.values()) {
            if (adapter.isAvailable()) {
                if (primaryAdapter == null) {
                    primaryAdapter = adapter;
                } else if (fallbackAdapter == null) {
                    fallbackAdapter = adapter;
                }
            }
        }
        
        if (primaryAdapter == null) {
            throw new IllegalStateException("Aucun adaptateur SMS disponible");
        }
        
        System.out.println("UnifiedSMSService: Adaptateur principal sélectionné: " + primaryAdapter.getProviderName());
        if (fallbackAdapter != null) {
            System.out.println("UnifiedSMSService: Adaptateur de secours sélectionné: " + fallbackAdapter.getProviderName());
        }
    }
    
    public void sendSMS(String destination, String message) {
        System.out.println("UnifiedSMSService: Envoi de SMS à " + destination);
        
        try {
            primaryAdapter.sendSMS(destination, message);
        } catch (Exception e) {
            System.err.println("UnifiedSMSService: Échec avec l'adaptateur principal, tentative avec le secours");
            
            if (fallbackAdapter != null) {
                try {
                    fallbackAdapter.sendSMS(destination, message);
                } catch (Exception fallbackError) {
                    System.err.println("UnifiedSMSService: Échec également avec l'adaptateur de secours");
                    throw new RuntimeException("Impossible d'envoyer le SMS", fallbackError);
                }
            } else {
                throw new RuntimeException("Impossible d'envoyer le SMS", e);
            }
        }
    }
    
    public void switchProvider(String providerName) {
        ISMSAdapter newAdapter = availableAdapters.get(providerName);
        if (newAdapter == null) {
            throw new IllegalArgumentException("Fournisseur non trouvé: " + providerName);
        }
        
        if (!newAdapter.isAvailable()) {
            throw new IllegalStateException("Fournisseur non disponible: " + providerName);
        }
        
        this.primaryAdapter = newAdapter;
        System.out.println("UnifiedSMSService: Changement vers le fournisseur: " + providerName);
    }
    
    public void checkProvidersStatus() {
        System.out.println("\n=== Statut des fournisseurs SMS ===");
        for (Map.Entry<String, ISMSAdapter> entry : availableAdapters.entrySet()) {
            String provider = entry.getKey();
            ISMSAdapter adapter = entry.getValue();
            boolean available = adapter.isAvailable();
            System.out.println(provider + ": " + (available ? "DISPONIBLE" : "INDISPONIBLE") + 
                             " - " + adapter.getProviderName());
        }
        System.out.println();
    }
}
```

## Tests et scénarios de démonstration

```java
public class TestSMSAdapter {
    public static void main(String[] args) {
        // Test 1: Utilisation directe des adaptateurs
        System.out.println("=== Test 1: Utilisation directe des adaptateurs ===");
        testDirectAdapterUsage();
        
        // Test 2: Service unifié avec basculement automatique
        System.out.println("\n=== Test 2: Service unifié ===");
        testUnifiedService();
        
        // Test 3: Ajout d'un nouveau fournisseur
        System.out.println("\n=== Test 3: Extension avec nouveau fournisseur ===");
        testNewProvider();
        
        // Test 4: Changement dynamique de fournisseur
        System.out.println("\n=== Test 4: Changement dynamique ===");
        testProviderSwitch();
    }
    
    private static void testDirectAdapterUsage() {
        // Test avec chaque fournisseur individuellement
        ISMSAdapter adapterA = new SMSAdapterA();
        ISMSAdapter adapterB = new SMSAdapterB();
        ISMSAdapter adapterC = new SMSAdapterC();
        
        // Simulation (en réalité, ces appels nécessiteraient des serveurs réels)
        System.out.println("Fournisseur A disponible: " + adapterA.isAvailable());
        System.out.println("Fournisseur B disponible: " + adapterB.isAvailable());
        System.out.println("Fournisseur C disponible: " + adapterC.isAvailable());
        
        // Envoi simulé
        try {
            adapterA.sendSMS("+237123456789", "Test message from Provider A");
        } catch (Exception e) {
            System.out.println("Erreur attendue (simulation): " + e.getMessage());
        }
    }
    
    private static void testUnifiedService() {
        UnifiedSMSService smsService = new UnifiedSMSService();
        
        // Vérification du statut des fournisseurs
        smsService.checkProvidersStatus();
        
        // Envoi de SMS via le service unifié
        try {
            smsService.sendSMS("+237123456789", "Message via service unifié");
        } catch (Exception e) {
            System.out.println("Erreur attendue (simulation): " + e.getMessage());
        }
    }
    
    private static void testNewProvider() {
        // Démonstration de l'ajout facile d'un nouveau fournisseur
        ISMSAdapter newAdapter = new SMSAdapterD();
        
        System.out.println("Nouveau fournisseur disponible: " + newAdapter.isAvailable());
        System.out.println("Nom du fournisseur: " + newAdapter.getProviderName());
        
        try {
            newAdapter.sendSMS("+237987654321", "Message via nouveau fournisseur D");
        } catch (Exception e) {
            System.out.println("Erreur attendue (simulation): " + e.getMessage());
        }
    }
    
    private static void testProviderSwitch() {
        UnifiedSMSService smsService = new UnifiedSMSService();
        
        try {
            // Tentative de changement vers un fournisseur spécifique
            smsService.switchProvider("provider_d");
            smsService.sendSMS("+237555555555", "Message après changement de fournisseur");
        } catch (Exception e) {
            System.out.println("Erreur attendue (simulation): " + e.getMessage());
        }
    }
}
```

### Résultats attendus

```
=== Test 1: Utilisation directe des adaptateurs ===
SMSAdapterA: Préparation de l'envoi SMS au format JSON
SMSAdapterA: Envoi de la requête JSON: {"to":"+237123456789","message":"Test message from Provider A","apiKey":"provider_a_key_123"}
SMSAdapterA: Erreur lors de l'envoi - Connection refused
Erreur attendue (simulation): Erreur d'envoi SMS

=== Test 2: Service unifié ===
SMSAdapterA: Préparation de l'envoi SMS au format JSON
SMSAdapterB: Préparation de l'envoi SMS au format SOAP
SMSAdapterC: Préparation de l'envoi SMS avec URL personnalisée
SMSAdapterD: Envoi via API REST moderne avec authentification Bearer
UnifiedSMSService: Aucun adaptateur SMS disponible

=== Test 3: Extension avec nouveau fournisseur ===
SMSAdapterD: Envoi via API REST moderne avec authentification Bearer
Nouveau fournisseur disponible: false
Nom du fournisseur: Fournisseur D (REST Moderne)
SMSAdapterD: Erreur lors de l'envoi - Connection refused
Erreur attendue (simulation): Erreur d'envoi SMS

=== Test 4: Changement dynamique ===
SMSAdapterA: Préparation de l'envoi SMS au format JSON
SMSAdapterB: Préparation de l'envoi SMS au format SOAP
SMSAdapterC: Préparation de l'envoi SMS avec URL personnalisée
SMSAdapterD: Envoi via API REST moderne avec authentification Bearer
UnifiedSMSService: Aucun adaptateur SMS disponible
Erreur attendue (simulation): Aucun adaptateur SMS disponible
```

**Note importante**: Dans un environnement réel, ces adaptateurs communiqueraient avec de véritables APIs externes. Les erreurs de connexion sont normales dans cette simulation et démontrent que le pattern fonctionne correctement en isolant les différentes implémentations.