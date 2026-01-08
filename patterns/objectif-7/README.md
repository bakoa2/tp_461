# Objectif 7: Cibles de Notification

## Description brève de la solution

Pour résoudre le problème du traitement uniforme des cibles de notification (individuelles et groupées), nous utilisons le pattern **Composite**:

- **Composite Pattern**: Permet de traiter des objets individuels et des compositions d'objets de manière uniforme. Nous créons une structure arborescente où les clients peuvent envoyer des notifications à des comptes individuels, des groupes ou à tous les clients de la même manière.

## Justification architecturale

Cette approche est adaptée car :

1. **Uniformité du traitement**: Le même code peut traiter des cibles individuelles et des groupes sans distinction.

2. **Flexibilité de composition**: Permet de créer des structures complexes (groupes de groupes, etc.).

3. **Extensibilité**: Facile d'ajouter de nouveaux types de cibles sans modifier le code client.

4. **Simplification du client**: Le code client n'a pas besoin de connaître la structure interne des cibles.

## Implémentation

```java
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// Interface Component pour les cibles de notification
public interface NotificationTarget {
    void send(String message);
    void add(NotificationTarget target);
    void remove(NotificationTarget target);
    NotificationTarget getChild(int index);
    String getName();
    String getDescription();
    int getRecipientCount();
    List<String> getRecipientDetails();
}

// Leaf - Compte individuel
public class SingleAccount implements NotificationTarget {
    private String accountNumber;
    private String ownerName;
    private String email;
    private String phoneNumber;
    private NotificationPreference preference;
    
    public SingleAccount(String accountNumber, String ownerName, String email, String phoneNumber) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.preference = NotificationPreference.BOTH; // Par défaut
    }
    
    @Override
    public void send(String message) {
        System.out.println("Envoi notification au compte individuel:");
        System.out.println("  Compte: " + accountNumber + " (" + ownerName + ")");
        System.out.println("  Message: " + message);
        
        // Envoi selon les préférences
        if (preference == NotificationPreference.EMAIL || preference == NotificationPreference.BOTH) {
            sendEmail(message);
        }
        
        if (preference == NotificationPreference.SMS || preference == NotificationPreference.BOTH) {
            sendSMS(message);
        }
        
        if (preference == NotificationPreference.PUSH) {
            sendPushNotification(message);
        }
    }
    
    private void sendEmail(String message) {
        System.out.println("    -> Email envoyé à: " + email);
    }
    
    private void sendSMS(String message) {
        System.out.println("    -> SMS envoyé au: " + phoneNumber);
    }
    
    private void sendPushNotification(String message) {
        System.out.println("    -> Push notification envoyée à: " + accountNumber);
    }
    
    @Override
    public void add(NotificationTarget target) {
        throw new UnsupportedOperationException("Impossible d'ajouter à une feuille (SingleAccount)");
    }
    
    @Override
    public void remove(NotificationTarget target) {
        throw new UnsupportedOperationException("Impossible de supprimer d'une feuille (SingleAccount)");
    }
    
    @Override
    public NotificationTarget getChild(int index) {
        throw new UnsupportedOperationException("Les feuilles n'ont pas d'enfants");
    }
    
    @Override
    public String getName() {
        return accountNumber;
    }
    
    @Override
    public String getDescription() {
        return "Compte: " + accountNumber + " - " + ownerName;
    }
    
    @Override
    public int getRecipientCount() {
        return 1;
    }
    
    @Override
    public List<String> getRecipientDetails() {
        List<String> details = new ArrayList<>();
        details.add(accountNumber + " - " + ownerName + " (" + preference + ")");
        return details;
    }
    
    // Getters et setters
    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public NotificationPreference getPreference() { return preference; }
    public void setPreference(NotificationPreference preference) { this.preference = preference; }
}

// Composite - Groupe de comptes
public class AccountGroup implements NotificationTarget {
    private String groupName;
    private String description;
    private List<NotificationTarget> children;
    private NotificationPriority priority;
    
    public AccountGroup(String groupName, String description) {
        this.groupName = groupName;
        this.description = description;
        this.children = new ArrayList<>();
        this.priority = NotificationPriority.NORMAL;
    }
    
    @Override
    public void send(String message) {
        System.out.println("\n=== Envoi notification au groupe: " + groupName + " ===");
        System.out.println("Description: " + description);
        System.out.println("Nombre de destinataires: " + getRecipientCount());
        System.out.println("Priorité: " + priority);
        
        // Application de la priorité au message
        String finalMessage = applyPriority(message);
        
        // Parcours récursif des enfants
        for (NotificationTarget child : children) {
            try {
                child.send(finalMessage);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi à " + child.getName() + ": " + e.getMessage());
            }
        }
        
        System.out.println("=== Fin envoi groupe: " + groupName + " ===\n");
    }
    
    private String applyPriority(String message) {
        switch (priority) {
            case HIGH:
                return "[URGENT] " + message;
            case LOW:
                return "[INFO] " + message;
            default:
                return message;
        }
    }
    
    @Override
    public void add(NotificationTarget target) {
        children.add(target);
        System.out.println("Ajouté au groupe " + groupName + ": " + target.getDescription());
    }
    
    @Override
    public void remove(NotificationTarget target) {
        children.remove(target);
        System.out.println("Retiré du groupe " + groupName + ": " + target.getDescription());
    }
    
    @Override
    public NotificationTarget getChild(int index) {
        return children.get(index);
    }
    
    @Override
    public String getName() {
        return groupName;
    }
    
    @Override
    public String getDescription() {
        return "Groupe: " + groupName + " - " + description + " (" + getRecipientCount() + " membres)";
    }
    
    @Override
    public int getRecipientCount() {
        int total = 0;
        for (NotificationTarget child : children) {
            total += child.getRecipientCount();
        }
        return total;
    }
    
    @Override
    public List<String> getRecipientDetails() {
        List<String> details = new ArrayList<>();
        details.add("=== GROUPE: " + groupName + " ===");
        for (NotificationTarget child : children) {
            details.addAll(child.getRecipientDetails());
        }
        return details;
    }
    
    // Méthodes spécifiques aux groupes
    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }
    
    public NotificationPriority getPriority() {
        return priority;
    }
    
    public void printHierarchy() {
        printHierarchy(0);
    }
    
    private void printHierarchy(int level) {
        String indent = "  ".repeat(level);
        System.out.println(indent + "├─ " + getDescription());
        
        for (NotificationTarget child : children) {
            if (child instanceof AccountGroup) {
                ((AccountGroup) child).printHierarchy(level + 1);
            } else {
                System.out.println(indent + "  └─ " + child.getDescription());
            }
        }
    }
    
    public List<SingleAccount> getFlatListOfAccounts() {
        List<SingleAccount> accounts = new ArrayList<>();
        for (NotificationTarget child : children) {
            if (child instanceof SingleAccount) {
                accounts.add((SingleAccount) child);
            } else if (child instanceof AccountGroup) {
                accounts.addAll(((AccountGroup) child).getFlatListOfAccounts());
            }
        }
        return accounts;
    }
}

// Composite spécialisé - Tous les clients du système
public class AllAccounts implements NotificationTarget {
    private Map<String, SingleAccount> allAccounts;
    private List<AccountGroup> operatorGroups;
    private String systemName;
    
    public AllAccounts(String systemName) {
        this.systemName = systemName;
        this.allAccounts = new HashMap<>();
        this.operatorGroups = new ArrayList<>();
    }
    
    @Override
    public void send(String message) {
        System.out.println("\n🌐 === CAMPAGNE GLOBALE: " + systemName + " ===");
        System.out.println("Message: " + message);
        System.out.println("Total destinataires: " + getRecipientCount());
        System.out.println("Groupes opérateurs: " + operatorGroups.size());
        
        // Envoi à tous les comptes individuels
        System.out.println("\n--- Envoi aux comptes individuels ---");
        for (SingleAccount account : allAccounts.values()) {
            account.send(message);
        }
        
        // Envoi à tous les groupes
        System.out.println("\n--- Envoi aux groupes ---");
        for (AccountGroup group : operatorGroups) {
            group.send(message);
        }
        
        System.out.println("🌐 === FIN CAMPAGNE GLOBALE ===\n");
    }
    
    @Override
    public void add(NotificationTarget target) {
        if (target instanceof SingleAccount) {
            SingleAccount account = (SingleAccount) target;
            allAccounts.put(account.getAccountNumber(), account);
            System.out.println("Compte ajouté au système global: " + account.getDescription());
        } else if (target instanceof AccountGroup) {
            operatorGroups.add((AccountGroup) target);
            System.out.println("Groupe ajouté au système global: " + target.getDescription());
        }
    }
    
    @Override
    public void remove(NotificationTarget target) {
        if (target instanceof SingleAccount) {
            SingleAccount account = (SingleAccount) target;
            allAccounts.remove(account.getAccountNumber());
            System.out.println("Compte retiré du système global: " + account.getDescription());
        } else if (target instanceof AccountGroup) {
            operatorGroups.remove(target);
            System.out.println("Groupe retiré du système global: " + target.getDescription());
        }
    }
    
    @Override
    public NotificationTarget getChild(int index) {
        throw new UnsupportedOperationException("AllAccounts n'implémente pas getChild");
    }
    
    @Override
    public String getName() {
        return systemName;
    }
    
    @Override
    public String getDescription() {
        return "Système global: " + systemName + " (" + getRecipientCount() + " comptes)";
    }
    
    @Override
    public int getRecipientCount() {
        int total = allAccounts.size();
        for (AccountGroup group : operatorGroups) {
            total += group.getRecipientCount();
        }
        return total;
    }
    
    @Override
    public List<String> getRecipientDetails() {
        List<String> details = new ArrayList<>();
        details.add("=== SYSTÈME GLOBAL: " + systemName + " ===");
        details.add("Comptes individuels:");
        for (SingleAccount account : allAccounts.values()) {
            details.addAll(account.getRecipientDetails());
        }
        details.add("Groupes:");
        for (AccountGroup group : operatorGroups) {
            details.addAll(group.getRecipientDetails());
        }
        return details;
    }
    
    // Méthodes spécifiques
    public SingleAccount getAccount(String accountNumber) {
        return allAccounts.get(accountNumber);
    }
    
    public List<AccountGroup> getOperatorGroups() {
        return new ArrayList<>(operatorGroups);
    }
    
    public Map<String, Integer> getStatisticsByOperator() {
        Map<String, Integer> stats = new HashMap<>();
        
        // Comptes individuels
        stats.put("INDIVIDUALS", allAccounts.size());
        
        // Groupes par opérateur
        for (AccountGroup group : operatorGroups) {
            stats.put(group.getName(), group.getRecipientCount());
        }
        
        return stats;
    }
}

// Énumérations pour les préférences et priorités
enum NotificationPreference {
    EMAIL, SMS, PUSH, BOTH
}

enum NotificationPriority {
    LOW, NORMAL, HIGH
}

// Classe de composition avancée - Notification composée
public class ComposedNotification implements NotificationTarget {
    private String title;
    private String content;
    private List<String> attachments;
    private NotificationTarget target;
    private NotificationPriority priority;
    
    public ComposedNotification(String title, String content, NotificationTarget target) {
        this.title = title;
        this.content = content;
        this.target = target;
        this.attachments = new ArrayList<>();
        this.priority = NotificationPriority.NORMAL;
    }
    
    public void addAttachment(String attachment) {
        attachments.add(attachment);
    }
    
    @Override
    public void send(String message) {
        String composedMessage = buildComposedMessage(message);
        System.out.println("\n📧 === NOTIFICATION COMPOSÉE ===");
        System.out.println("Titre: " + title);
        System.out.println("Contenu: " + content);
        System.out.println("Pièces jointes: " + attachments.size());
        System.out.println("Cible: " + target.getDescription());
        
        target.send(composedMessage);
        
        System.out.println("📧 === FIN NOTIFICATION COMPOSÉE ===\n");
    }
    
    private String buildComposedMessage(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 ").append(title).append("\n");
        sb.append("📝 ").append(content).append("\n");
        sb.append("💬 ").append(message).append("\n");
        
        if (!attachments.isEmpty()) {
            sb.append("📎 Pièces jointes: ").append(String.join(", ", attachments)).append("\n");
        }
        
        return sb.toString();
    }
    
    @Override
    public void add(NotificationTarget target) {
        throw new UnsupportedOperationException("ComposedNotification est un wrapper");
    }
    
    @Override
    public void remove(NotificationTarget target) {
        throw new UnsupportedOperationException("ComposedNotification est un wrapper");
    }
    
    @Override
    public NotificationTarget getChild(int index) {
        throw new UnsupportedOperationException("ComposedNotification est un wrapper");
    }
    
    @Override
    public String getName() {
        return "COMPOSED_" + target.getName();
    }
    
    @Override
    public String getDescription() {
        return "Notification composée pour: " + target.getDescription();
    }
    
    @Override
    public int getRecipientCount() {
        return target.getRecipientCount();
    }
    
    @Override
    public List<String> getRecipientDetails() {
        return target.getRecipientDetails();
    }
    
    public void setPriority(NotificationPriority priority) {
        this.priority = priority;
    }
    
    public NotificationPriority getPriority() {
        return priority;
    }
}

// Service de notification qui utilise le pattern Composite
public class NotificationServiceComposite {
    private AllAccounts systemWideTargets;
    private Map<String, AccountGroup> operatorGroups;
    
    public NotificationServiceComposite(String systemName) {
        this.systemWideTargets = new AllAccounts(systemName);
        this.operatorGroups = new HashMap<>();
    }
    
    public void sendToAccount(String accountNumber, String message) {
        SingleAccount account = systemWideTargets.getAccount(accountNumber);
        if (account != null) {
            account.send(message);
        } else {
            System.err.println("Compte non trouvé: " + accountNumber);
        }
    }
    
    public void sendToGroup(String groupName, String message) {
        AccountGroup group = operatorGroups.get(groupName);
        if (group != null) {
            group.send(message);
        } else {
            System.err.println("Groupe non trouvé: " + groupName);
        }
    }
    
    public void sendToAll(String message) {
        systemWideTargets.send(message);
    }
    
    public void sendComposedNotification(String title, String content, String targetKey, String message) {
        NotificationTarget target = findTarget(targetKey);
        if (target != null) {
            ComposedNotification composed = new ComposedNotification(title, content, target);
            composed.send(message);
        } else {
            System.err.println("Cible non trouvée: " + targetKey);
        }
    }
    
    public void createGroup(String groupName, String description) {
        AccountGroup group = new AccountGroup(groupName, description);
        operatorGroups.put(groupName, group);
        systemWideTargets.add(group);
        System.out.println("Groupe créé: " + groupName);
    }
    
    public void addAccountToGroup(String groupName, SingleAccount account) {
        AccountGroup group = operatorGroups.get(groupName);
        if (group != null) {
            group.add(account);
        } else {
            System.err.println("Groupe non trouvé: " + groupName);
        }
    }
    
    public void addAccountToSystem(SingleAccount account) {
        systemWideTargets.add(account);
    }
    
    private NotificationTarget findTarget(String key) {
        // Recherche par numéro de compte
        SingleAccount account = systemWideTargets.getAccount(key);
        if (account != null) {
            return account;
        }
        
        // Recherche par nom de groupe
        return operatorGroups.get(key);
    }
    
    public void printSystemStatistics() {
        System.out.println("\n=== Statistiques du système ===");
        System.out.println("Nom du système: " + systemWideTargets.getName());
        System.out.println("Total destinataires: " + systemWideTargets.getRecipientCount());
        System.out.println("Groupes: " + operatorGroups.size());
        
        Map<String, Integer> stats = systemWideTargets.getStatisticsByOperator();
        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " destinataires");
        }
    }
    
    public void printGroupHierarchy() {
        System.out.println("\n=== Hiérarchie des groupes ===");
        for (AccountGroup group : operatorGroups.values()) {
            group.printHierarchy();
        }
    }
}
```

## Tests et scénarios de démonstration

```java
public class TestCompositePattern {
    public static void main(String[] args) {
        // Création du service de notification
        NotificationServiceComposite notificationService = new NotificationServiceComposite("Système Bancaire Multi-opérateurs");
        
        // Test 1: Création de comptes individuels
        System.out.println("=== Test 1: Création de comptes individuels ===");
        SingleAccount account1 = new SingleAccount("ACC001", "Jean Dupont", "jean@email.com", "+237123456789");
        SingleAccount account2 = new SingleAccount("ACC002", "Marie Curie", "marie@email.com", "+237987654321");
        SingleAccount account3 = new SingleAccount("ACC003", "Albert Einstein", "albert@email.com", "+237555555555");
        
        account1.setPreference(NotificationPreference.EMAIL);
        account2.setPreference(NotificationPreference.SMS);
        account3.setPreference(NotificationPreference.BOTH);
        
        notificationService.addAccountToSystem(account1);
        notificationService.addAccountToSystem(account2);
        notificationService.addAccountToSystem(account3);
        
        // Test 2: Envoi à des comptes individuels
        System.out.println("\n=== Test 2: Envoi à des comptes individuels ===");
        notificationService.sendToAccount("ACC001", "Votre solde a été mis à jour");
        notificationService.sendToAccount("ACC002", "Nouvelle transaction détectée");
        
        // Test 3: Création de groupes
        System.out.println("\n=== Test 3: Création de groupes ===");
        notificationService.createGroup("BANK_CLIENTS", "Clients de la banque");
        notificationService.createGroup("MOBILE_MONEY_CLIENTS", "Clients Mobile Money");
        notificationService.createGroup("VIP_CLIENTS", "Clients VIP");
        
        // Test 4: Ajout de comptes aux groupes
        System.out.println("\n=== Test 4: Ajout de comptes aux groupes ===");
        notificationService.addAccountToGroup("BANK_CLIENTS", account1);
        notificationService.addAccountToGroup("BANK_CLIENTS", account3);
        notificationService.addAccountToGroup("MOBILE_MONEY_CLIENTS", account2);
        
        // Création de comptes supplémentaires pour les groupes
        SingleAccount account4 = new SingleAccount("ACC004", "Pierre Paul", "pierre@email.com", "+237111111111");
        SingleAccount account5 = new SingleAccount("ACC005", "Jacques Martin", "jacques@email.com", "+237222222222");
        
        notificationService.addAccountToSystem(account4);
        notificationService.addAccountToSystem(account5);
        
        notificationService.addAccountToGroup("VIP_CLIENTS", account3);
        notificationService.addAccountToGroup("VIP_CLIENTS", account4);
        
        // Test 5: Envoi aux groupes
        System.out.println("\n=== Test 5: Envoi aux groupes ===");
        notificationService.sendToGroup("BANK_CLIENTS", "Promotion spéciale bancaire this month!");
        notificationService.sendToGroup("VIP_CLIENTS", "Offre exclusive VIP disponible");
        
        // Test 6: Groupes imbriqués (composite dans composite)
        System.out.println("\n=== Test 6: Groupes imbriqués ===");
        AccountGroup premiumGroup = new AccountGroup("PREMIUM", "Clients Premium");
        AccountGroup bankPremium = new AccountGroup("BANK_PREMIUM", "Clients Premium Banque");
        AccountGroup mobilePremium = new AccountGroup("MOBILE_PREMIUM", "Clients Premium Mobile Money");
        
        // Ajout des groupes premium aux groupes principaux
        notificationService.addAccountToGroup("BANK_CLIENTS", bankPremium);
        notificationService.addAccountToGroup("MOBILE_MONEY_CLIENTS", mobilePremium);
        
        // Ajout de comptes premium
        SingleAccount account6 = new SingleAccount("ACC006", "Sophie Laurent", "sophie@email.com", "+237333333333");
        notificationService.addAccountToSystem(account6);
        
        bankPremium.add(account4); // Pierre Paul dans premium banque
        mobilePremium.add(account6); // Sophie dans premium mobile
        
        // Test 7: Notification composée
        System.out.println("\n=== Test 7: Notification composée ===");
        notificationService.sendComposedNotification(
            "Nouvelle offre spéciale",
            "Découvrez nos nouvelles conditions avantageuses",
            "BANK_CLIENTS",
            "Profitez de taux réduits ce mois-ci"
        );
        
        // Test 8: Campagne globale
        System.out.println("\n=== Test 8: Campagne globale ===");
        notificationService.sendToAll("Maintenance système prévue ce week-end");
        
        // Test 9: Statistiques et hiérarchie
        System.out.println("\n=== Test 9: Statistiques et hiérarchie ===");
        notificationService.printSystemStatistics();
        notificationService.printGroupHierarchy();
        
        // Test 10: Priorités et préférences
        System.out.println("\n=== Test 10: Priorités et préférences ===");
        AccountGroup urgentGroup = new AccountGroup("URGENT", "Notifications urgentes");
        urgentGroup.setPriority(NotificationPriority.HIGH);
        
        urgentGroup.add(account1);
        urgentGroup.add(account2);
        
        notificationService.systemWideTargets.add(urgentGroup);
        urgentGroup.send("Alerte sécurité: connexion suspectée détectée");
    }
}
```

### Résultats attendus

```
=== Test 1: Création de comptes individuels ===
Compte ajouté au système global: Compte: ACC001 - Jean Dupont
Compte ajouté au système global: Compte: ACC002 - Marie Curie
Compte ajouté au système global: Compte: ACC003 - Albert Einstein

=== Test 2: Envoi à des comptes individuels ===
Envoi notification au compte individuel:
  Compte: ACC001 (Jean Dupont)
  Message: Votre solde a été mis à jour
    -> Email envoyé à: jean@email.com

Envoi notification au compte individuel:
  Compte: ACC002 (Marie Curie)
  Message: Nouvelle transaction détectée
    -> SMS envoyé au: +237987654321

=== Test 3: Création de groupes ===
Groupe créé: BANK_CLIENTS
Ajouté au groupe BANK_CLIENTS: Groupe: BANK_CLIENTS - Clients de la banque (0 membres)
Groupe ajouté au système global: Groupe: BANK_CLIENTS - Clients de la banque (0 membres)
Groupe créé: MOBILE_MONEY_CLIENTS
Ajouté au groupe MOBILE_MONEY_CLIENTS: Groupe: MOBILE_MONEY_CLIENTS - Clients Mobile Money (0 membres)
Groupe ajouté au système global: Groupe: MOBILE_MONEY_CLIENTS - Clients Mobile Money (0 membres)
Groupe créé: VIP_CLIENTS
Ajouté au groupe VIP_CLIENTS: Groupe: VIP_CLIENTS - Clients VIP (0 membres)
Groupe ajouté au système global: Groupe: VIP_CLIENTS - Clients VIP (0 membres)

=== Test 4: Ajout de comptes aux groupes ===
Ajouté au groupe BANK_CLIENTS: Compte: ACC001 - Jean Dupont
Ajouté au groupe BANK_CLIENTS: Compte: ACC003 - Albert Einstein
Ajouté au groupe MOBILE_MONEY_CLIENTS: Compte: ACC002 - Marie Curie
Compte ajouté au système global: Compte: ACC004 - Pierre Paul
Compte ajouté au système global: Compte: ACC005 - Jacques Martin
Ajouté au groupe VIP_CLIENTS: Compte: ACC003 - Albert Einstein
Ajouté au groupe VIP_CLIENTS: Compte: ACC004 - Pierre Paul

=== Test 5: Envoi aux groupes ===
=== Envoi notification au groupe: BANK_CLIENTS ===
Description: Clients de la banque
Nombre de destinataires: 2
Priorité: NORMAL
Envoi notification au compte individuel:
  Compte: ACC001 (Jean Dupont)
  Message: Promotion spéciale bancaire this month!
    -> Email envoyé à: jean@email.com
Envoi notification au compte individuel:
  Compte: ACC003 (Albert Einstein)
  Message: Promotion spéciale bancaire this month!
    -> Email envoyé à: albert@email.com
    -> SMS envoyé au: +237555555555
=== Fin envoi groupe: BANK_CLIENTS ===

=== Envoi notification au groupe: VIP_CLIENTS ===
Description: Clients VIP
Nombre de destinataires: 2
Priorité: NORMAL
Envoi notification au compte individuel:
  Compte: ACC003 (Albert Einstein)
  Message: Offre exclusive VIP disponible
    -> Email envoyé à: albert@email.com
    -> SMS envoyé au: +237555555555
Envoi notification au compte individuel:
  Compte: ACC004 (Pierre Paul)
  Message: Offre exclusive VIP disponible
    -> Email envoyé à: pierre@email.com
    -> SMS envoyé au: +237111111111
=== Fin envoi groupe: VIP_CLIENTS ===

=== Test 6: Groupes imbriqués (composite dans composite) ===
Ajouté au groupe BANK_CLIENTS: Groupe: BANK_PREMIUM - Clients Premium Banque (0 membres)
Ajouté au groupe MOBILE_MONEY_CLIENTS: Groupe: MOBILE_PREMIUM - Clients Premium Mobile Money (0 membres)
Compte ajouté au système global: Compte: ACC006 - Sophie Laurent
Ajouté au groupe BANK_PREMIUM: Compte: ACC004 - Pierre Paul
Ajouté au groupe MOBILE_PREMIUM: Compte: ACC006 - Sophie Laurent

=== Test 7: Notification composée ===

📧 === NOTIFICATION COMPOSÉE ===
Titre: Nouvelle offre spéciale
Contenu: Découvrez nos nouvelles conditions avantageuses
Pièces jointes: 0
Cible: Groupe: BANK_CLIENTS - Clients de la banque (2 membres)

=== Envoi notification au groupe: BANK_CLIENTS ===
Description: Clients de la banque
Nombre de destinataires: 2
Priorité: NORMAL
Envoi notification au compte individuel:
  Compte: ACC001 (Jean Dupont)
  Message: 📋 Nouvelle offre spéciale
📝 Découvrez nos nouvelles conditions avantageuses
💬 Profitez de taux réduits ce mois-ci
    -> Email envoyé à: jean@email.com
Envoi notification au compte individuel:
  Compte: ACC003 (Albert Einstein)
  Message: 📋 Nouvelle offre spéciale
📝 Découvrez nos nouvelles conditions avantageuses
💬 Profitez de taux réduits ce mois-ci
    -> Email envoyé à: albert@email.com
    -> SMS envoyé au: +237555555555
=== Fin envoi groupe: BANK_CLIENTS ===

📧 === FIN NOTIFICATION COMPOSÉE ===

=== Test 8: Campagne globale ===

🌐 === CAMPAGNE GLOBALE: Système Bancaire Multi-opérateurs ===
Message: Maintenance système prévue ce week-end
Total destinataires: 6
Groupes opérateurs: 3

--- Envoi aux comptes individuels ---
Envoi notification au compte individuel:
  Compte: ACC001 (Jean Dupont)
  Message: Maintenance système prévue ce week-end
    -> Email envoyé à: jean@email.com
... (autres comptes individuels)

--- Envoi aux groupes ---
=== Envoi notification au groupe: BANK_CLIENTS ===
... (envoi aux groupes)

🌐 === FIN CAMPAGNE GLOBALE ===

=== Test 9: Statistiques et hiérarchie ===

=== Statistiques du système ===
Nom du système: Système Bancaire Multi-opérateurs
Total destinataires: 6
Groupes: 3
  INDIVIDUALS: 3 destinataires
  BANK_CLIENTS: 2 destinataires
  MOBILE_MONEY_CLIENTS: 1 destinataires
  VIP_CLIENTS: 2 destinataires

=== Hiérarchie des groupes ===
├─ Groupe: BANK_CLIENTS - Clients de la banque (2 membres)
  ├─ Compte: ACC001 - Jean Dupont
  ├─ Compte: ACC003 - Albert Einstein
  └─ Groupe: BANK_PREMIUM - Clients Premium Banque (1 membres)
    └─ Compte: ACC004 - Pierre Paul

=== Test 10: Priorités et préférences ===
Ajouté au groupe URGENT: Groupe: URGENT - Notifications urgentes (0 membres)
Ajouté au groupe URGENT: Compte: ACC001 - Jean Dupont
Ajouté au groupe URGENT: Compte: ACC002 - Marie Curie
Ajouté au groupe URGENT: Groupe: URGENT - Notifications urgentes (2 membres)

=== Envoi notification au groupe: URGENT ===
Description: Notifications urgentes
Nombre de destinataires: 2
Priorité: HIGH
Envoi notification au compte individuel:
  Compte: ACC001 (Jean Dupont)
  Message: [URGENT] Alerte sécurité: connexion suspectée détectée
    -> Email envoyé à: jean@email.com
Envoi notification au compte individuel:
  Compte: ACC002 (Marie Curie)
  Message: [URGENT] Alerte sécurité: connexion suspectée détectée
    -> SMS envoyé au: +237987654321
=== Fin envoi groupe: URGENT ===
```

Cette démonstration montre comment le pattern Composite permet de traiter uniformément des cibles individuelles et des groupes, avec des structures imbriquées complexes et des fonctionnalités avancées comme les notifications composées.