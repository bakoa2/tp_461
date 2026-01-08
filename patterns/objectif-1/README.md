# Objectif 1: Authentification Multiple

## Description brève de la solution

Pour résoudre le problème d'authentification multiple, nous utilisons une combinaison des patterns **Strategy** et **Factory**:

- **Strategy Pattern**: Permet de définir une famille d'algorithmes d'authentification, de les encapsuler et de les rendre interchangeables. Chaque méthode d'authentification (mot de passe, empreinte, reconnaissance faciale, OTP) est une stratégie concrète.

- **Factory Pattern**: Fournit un mécanisme de création flexible pour instancier les stratégies d'authentification à l'exécution en fonction de la configuration de l'opérateur et des préférences du client.

## Justification architecturale

Cette approche est adaptée car :

1. **Ouverture/Fermeture**: Le système est ouvert pour l'extension (ajout de nouvelles méthodes d'authentification) mais fermé pour la modification (pas besoin de modifier le code client).

2. **Flexibilité**: La sélection de la méthode d'authentification se fait à l'exécution, permettant une configuration dynamique par opérateur.

3. **Maintenabilité**: Chaque méthode d'authentification est isolée dans sa propre classe, facilitant la maintenance et les tests.

4. **Réutilisabilité**: Les stratégies peuvent être réutilisées dans différents contextes d'authentification.

## Implémentation

```java
// Interface Strategy
public interface IAuthentification {
    boolean authentifier(Credentials credentials);
}

// Stratégies concrètes
public class AuthentificationPassword implements IAuthentification {
    @Override
    public boolean authentifier(Credentials credentials) {
        System.out.println("Authentification par mot de passe");
        return credentials.getPassword().equals("password123");
    }
}

public class AuthentificationBiometrie implements IAuthentification {
    @Override
    public boolean authentifier(Credentials credentials) {
        System.out.println("Authentification par biométrie");
        return credentials.getBiometricData() != null;
    }
}

public class AuthentificationOTP implements IAuthentification {
    @Override
    public boolean authentifier(Credentials credentials) {
        System.out.println("Authentification par OTP");
        return credentials.getOtp().equals("123456");
    }
}

// Extension: Réalité Augmentée
public class AuthentificationRealiteAugmentee implements IAuthentification {
    @Override
    public boolean authentifier(Credentials credentials) {
        System.out.println("Authentification par réalité augmentée");
        return credentials.getArData() != null;
    }
}

// Factory
public class AuthentificationFactory {
    public static IAuthentification creerAuthentification(String type) {
        switch (type.toLowerCase()) {
            case "password":
                return new AuthentificationPassword();
            case "biometrie":
                return new AuthentificationBiometrie();
            case "otp":
                return new AuthentificationOTP();
            case "realite_augmentee":
                return new AuthentificationRealiteAugmentee();
            default:
                throw new IllegalArgumentException("Méthode d'authentification non supportée: " + type);
        }
    }
}

// Classes utilitaires
public class Credentials {
    private String password;
    private String biometricData;
    private String otp;
    private String arData;
    
    // Constructeurs, getters et setters
    public Credentials(String password) { this.password = password; }
    public String getPassword() { return password; }
    
    public Credentials setBiometricData(String biometricData) { 
        this.biometricData = biometricData; 
        return this; 
    }
    public String getBiometricData() { return biometricData; }
    
    public Credentials setOtp(String otp) { 
        this.otp = otp; 
        return this; 
    }
    public String getOtp() { return otp; }
    
    public Credentials setArData(String arData) { 
        this.arData = arData; 
        return this; 
    }
    public String getArData() { return arData; }
}

// Contexte d'utilisation
public class AuthentificationService {
    private IAuthentification authentification;
    
    public void setAuthentificationMethod(String type) {
        this.authentification = AuthentificationFactory.creerAuthentification(type);
    }
    
    public boolean authentifierUser(Credentials credentials) {
        if (authentification == null) {
            throw new IllegalStateException("Méthode d'authentification non définie");
        }
        return authentification.authentifier(credentials);
    }
}
```

## Tests et scénarios de démonstration

```java
public class TestAuthentification {
    public static void main(String[] args) {
        AuthentificationService authService = new AuthentificationService();
        
        // Test 1: Authentification par mot de passe
        System.out.println("=== Test 1: Authentification par mot de passe ===");
        authService.setAuthentificationMethod("password");
        Credentials credsPassword = new Credentials("password123");
        boolean result1 = authService.authentifierUser(credsPassword);
        System.out.println("Résultat: " + result1);
        
        // Test 2: Authentification par biométrie
        System.out.println("\n=== Test 2: Authentification par biométrie ===");
        authService.setAuthentificationMethod("biometrie");
        Credentials credsBio = new Credentials("").setBiometricData("fingerprint_data");
        boolean result2 = authService.authentifierUser(credsBio);
        System.out.println("Résultat: " + result2);
        
        // Test 3: Authentification par OTP
        System.out.println("\n=== Test 3: Authentification par OTP ===");
        authService.setAuthentificationMethod("otp");
        Credentials credsOtp = new Credentials("").setOtp("123456");
        boolean result3 = authService.authentifierUser(credsOtp);
        System.out.println("Résultat: " + result3);
        
        // Test 4: Extension - Authentification par réalité augmentée
        System.out.println("\n=== Test 4: Authentification par réalité augmentée ===");
        authService.setAuthentificationMethod("realite_augmentee");
        Credentials credsAR = new Credentials("").setArData("ar_markers_data");
        boolean result4 = authService.authentifierUser(credsAR);
        System.out.println("Résultat: " + result4);
        
        // Test 5: Substitution dynamique
        System.out.println("\n=== Test 5: Substitution dynamique ===");
        authService.setAuthentificationMethod("password");
        System.out.println("Première authentification (password): " + 
                          authService.authentifierUser(new Credentials("password123")));
        
        authService.setAuthentificationMethod("otp");
        System.out.println("Deuxième authentification (OTP): " + 
                          authService.authentifierUser(new Credentials("").setOtp("123456")));
    }
}
```

### Résultats attendus

```
=== Test 1: Authentification par mot de passe ===
Authentification par mot de passe
Résultat: true

=== Test 2: Authentification par biométrie ===
Authentification par biométrie
Résultat: true

=== Test 3: Authentification par OTP ===
Authentification par OTP
Résultat: true

=== Test 4: Authentification par réalité augmentée ===
Authentification par réalité augmentée
Résultat: true

=== Test 5: Substitution dynamique ===
Authentification par mot de passe
Première authentification (password): true
Authentification par OTP
Deuxième authentification (OTP): true