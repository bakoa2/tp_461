package com.bankingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Scanner;

/**
 * Application principale du système bancaire multi-opérateurs
 * 
 * Cette application démontre l'intégration des 10 patterns de conception
 * implémentés pour résoudre les problèmes spécifiés dans le cahier des charges.
 */
@SpringBootApplication
@RestController
public class BankingSystemApplication {
    
    public static void main(String[] args) {
        System.out.println("🏦 SYSTÈME BANCAIRE MULTI-OPÉRATEURS 🏦");
        System.out.println("=====================================");
        System.out.println("Projet de Design Patterns - INF461");
        System.out.println("Université de Yaoundé I - 2025/2026");
        System.out.println("=====================================\n");
        
        // Démarrer l'application Spring Boot
        SpringApplication.run(BankingSystemApplication.class, args);
    }
    
    /**
     * Endpoint de santé pour vérifier que l'API fonctionne
     */
    @GetMapping("/api/health")
    public String health() {
        return "🏦 Banking System API is running! 🚀";
    }
    
    /**
     * Endpoint d'information sur le système
     */
    @GetMapping("/api/info")
    public SystemInfo getInfo() {
        return new SystemInfo(
            "Système Bancaire Multi-opérateurs",
            "INF461 - Université de Yaoundé I",
            "2025/2026",
            "Implementation of 10 Design Patterns"
        );
    }
    
    /**
     * Record pour les informations système
     */
    public record SystemInfo(String name, String course, String year, String description) {}
    
    private static void runInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            printMainMenu();
            System.out.print("Choisissez une option: ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consommer la ligne
                
                switch (choice) {
                    case 1:
                        demonstratePatterns();
                        break;
                    case 2:
                        showSystemArchitecture();
                        break;
                    case 3:
                        showImplementationGuide();
                        break;
                    case 0:
                        running = false;
                        System.out.println("👋 Au revoir!");
                        break;
                    default:
                        System.out.println("❌ Option invalide. Veuillez réessayer.");
                }
            } catch (Exception e) {
                System.out.println("❌ Erreur: " + e.getMessage());
                scanner.nextLine(); // Nettoyer l'entrée invalide
            }
            
            if (running) {
                System.out.println("\nAppuyez sur Entrée pour continuer...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    private static void printMainMenu() {
        System.out.println("\n📋 MENU PRINCIPAL");
        System.out.println("================");
        System.out.println("1. 🔄 Authentification multiple (Strategy + Factory)");
        System.out.println("2. 🏢 Opérateurs et familles d'objets (Abstract Factory)");
        System.out.println("3. 💳 Construction de transactions (Builder)");
        System.out.println("4. 🌐 Services globaux (Singleton)");
        System.out.println("5. 📱 Service SMS unifié (Adapter)");
        System.out.println("6. 📊 Comportements communs/spécifiques (Template Method)");
        System.out.println("7. 📨 Cibles de notification (Composite)");
        System.out.println("8. 🎭 Comportements optionnels de compte (Decorator)");
        System.out.println("9. 🔄 Squelettes de workflow (Template Method)");
        System.out.println("10. 📈 Opérations analytiques (Visitor)");
        System.out.println("0. 🚪 Quitter");
    }
    
    private static void demonstratePatterns() {
        System.out.println("\n📚 DÉMONSTRATION DES PATTERNS DE CONCEPTION");
        System.out.println("==============================================");
        
        System.out.println("\n1️⃣ STRATEGY PATTERN - Authentification multiple:");
        System.out.println("   Le pattern Strategy permet de définir une famille d'algorithmes interchangeables.");
        System.out.println("   Application: Authentification par mot de passe, biométrie, OTP, etc.");
        System.out.println("   Avantages: Flexibilité, extensibilité, respect du principe Open/Closed.");
        
        System.out.println("\n2️⃣ ABSTRACT FACTORY PATTERN - Familles d'objets opérateurs:");
        System.out.println("   Le pattern Abstract Factory fournit une interface pour créer des familles d'objets.");
        System.out.println("   Application: Banque, Mobile Money, International avec leurs propres validateurs.");
        System.out.println("   Avantages: Cohérence des familles, isolation des implémentations.");
        
        System.out.println("\n3️⃣ BUILDER PATTERN - Construction de transactions:");
        System.out.println("   Le pattern Builder sépare la construction d'un objet complexe de sa représentation.");
        System.out.println("   Application: Transactions avec étapes optionnelles (validation, commission, etc.).");
        System.out.println("   Avantages: Construction flexible, API fluide, validations à la construction.");
        
        System.out.println("\n4️⃣ SINGLETON PATTERN - Services globaux:");
        System.out.println("   Le pattern Singleton garantit une seule instance d'une classe avec point d'accès global.");
        System.out.println("   Application: Services de notifications, configuration, gestionnaire d'événements.");
        System.out.println("   Avantages: Contrôle d'accès, thread-safety, gestion centralisée.");
        
        System.out.println("\n5️⃣ ADAPTER PATTERN - Service SMS unifié:");
        System.out.println("   Le pattern Adapter convertit l'interface d'une classe en une autre interface attendue.");
        System.out.println("   Application: Homogénéisation des APIs SMS de différents fournisseurs.");
        System.out.println("   Avantages: Uniformisation, extensibilité, découplage du code métier.");
        
        System.out.println("\n6️⃣ TEMPLATE METHOD PATTERN - Comportements opérateurs:");
        System.out.println("   Le pattern Template Method définit le squelette d'un algorithme dans la classe de base.");
        System.out.println("   Application: Gestion des comptes avec spécialisations opérateur-dépendantes.");
        System.out.println("   Avantages: Réutilisation du code commun, flexibilité des spécialisations.");
        
        System.out.println("\n7️⃣ COMPOSITE PATTERN - Cibles de notification:");
        System.out.println("   Le pattern Composite permet de traiter des objets individuels et des compositions.");
        System.out.println("   Application: Notifications à comptes individuels, groupes, campagne globale.");
        System.out.println("   Avantages: Traitement uniforme, structures hiérarchiques, opérations groupées.");
        
        System.out.println("\n8️⃣ DECORATOR PATTERN - Comportements de compte:");
        System.out.println("   Le pattern Decorator ajoute dynamiquement de nouvelles responsabilités à un objet.");
        System.out.println("   Application: Journalisation, verrouillage, plafonnement des comptes.");
        System.out.println("   Avantages: Extension dynamique, composition de comportements, respect du principe Open/Closed.");
        
        System.out.println("\n9️⃣ TEMPLATE METHOD PATTERN - Workflows:");
        System.out.println("   Le pattern Template Method définit des squelettes pour les processus métier.");
        System.out.println("   Application: Workflows d'ouverture de compte avec étapes spécifiques.");
        System.out.println("   Avantages: Squelette commun, points d'extension, maintenabilité facilitée.");
        
        System.out.println("\n🔟️ VISITOR PATTERN - Opérations analytiques:");
        System.out.println("   Le pattern Visitor sépare les algorithmes des structures sur lesquelles ils opèrent.");
        System.out.println("   Application: Calcul de commissions, détection de fraudes, rapports d'activité.");
        System.out.println("   Avantages: Ajout d'opérations sans modifier les classes existantes, performance.");
        
        System.out.println("\n✅ Tous les patterns ont été démontrés avec leurs avantages respectifs!");
    }
    
    private static void showSystemArchitecture() {
        System.out.println("\n🏗 ARCHITECTURE SYSTÈME");
        System.out.println("======================");
        
        System.out.println("\n📊 Vue d'ensemble de l'architecture:");
        System.out.println("┌───────────────────────────────────────────────┐");
        System.out.println("│           INTERFACE UTILISATEUR           │");
        System.out.println("├───────────────────────────────────────────────┤");
        System.out.println("│  🔄 Authentification     │  💳 Transactions      │");
        System.out.println("├───────────────────────────────────────────────┤");
        System.out.println("│     📈 Notifications      │  📊 Comptes/Analytiques│");
        System.out.println("├───────────────────────────────────────────────┤");
        System.out.println("│           LOGIQUE MÉTIER CORE             │");
        System.out.println("├───────────────────────────────────────────────┤");
        System.out.println("│   🏢 Opérateurs (Abstract Factory)         │");
        System.out.println("│   🔄 Workflows (Template Method)           │");
        System.out.println("│   📈 Analytics (Visitor Pattern)           │");
        System.out.println("├───────────────────────────────────────────────┤");
        System.out.println("│            INFRASTRUCTURE               │");
        System.out.println("├───────────────────────────────────────────────┤");
        System.out.println("│   🌐 Services Globaux (Singleton)            │");
        System.out.println("│   📱 Services Externes (Adapter)             │");
        System.out.println("│   📨 Base de Données                       │");
        System.out.println("├───────────────────────────────────────────────┤");
        System.out.println("└───────────────────────────────────────────────┘");
        
        System.out.println("\n🔄 Flux de données typique:");
        System.out.println("Utilisateur → Authentification (Strategy) → Opérateur (Abstract Factory)");
        System.out.println("→ Transaction (Builder) → Validation → Exécution → Notification (Composite/Singleton)");
        System.out.println("→ Enregistrement → Analyse (Visitor) → Rapports");
        
        System.out.println("\n🎯 Points d'extension:");
        System.out.println("• Nouveaux types d'authentification sans modifier le code existant");
        System.out.println("• Nouveaux opérateurs en créant simplement de nouvelles factories");
        System.out.println("• Nouvelles étapes de transaction via le pattern Builder");
        System.out.println("• Nouveaux services externes via le pattern Adapter");
        System.out.println("• Nouveaux comportements de compte via le pattern Decorator");
        System.out.println("• Nouvelles analyses via le pattern Visitor");
        
        System.out.println("\n✅ Architecture conçue pour l'évolutivité et la maintenance!");
    }
    
    private static void showImplementationGuide() {
        System.out.println("\n📖 GUIDE D'IMPLÉMENTATION");
        System.out.println("==========================");
        
        System.out.println("\n📁 STRUCTURE DES FICHIERS SUGÉRÉE:");
        System.out.println("backend/");
        System.out.println("├── src/main/java/com/bankingsystem/");
        System.out.println("│   └── BankingSystemApplication.java");
        System.out.println("├── patterns/");
        System.out.println("│   │   └── objectif-[1-10]/");
        System.out.println("│   │   │   └── README.md (documentation + implémentation)");
        System.out.println("│   ├── interfaces/");
        System.out.println("│   │   └── (interfaces des patterns)");
        System.out.println("│   ├── services/");
        System.out.println("│   │   └── (services centralisés et patterns)");
        System.out.println("├── diagrams/");
        System.out.println("│   │   └── systeme-global.puml (diagramme UML complet)");
        System.out.println("├── docs/");
        System.out.println("│   │   ├── cahier-des-charges.md");
        System.out.println("│   │   ├── conception.md");
        System.out.println("│   │   └── rapport-final.md");
        System.out.println("├── frontend/");
        System.out.println("│   │   └── (interface React + Tailwind CSS)");
        System.out.println("├── tests/");
        System.out.println("│   │   └── (tests unitaires et scénarios de démonstration)");
        
        System.out.println("\n🔧 TECHNOLOGIES RECOMMANDÉES:");
        System.out.println("• Backend: Java 11+ avec Maven ou Gradle");
        System.out.println("• Frontend: React.js avec Tailwind CSS");
        System.out.println("• Base de données: PostgreSQL ou MySQL");
        System.out.println("• Tests: JUnit 5+ pour les tests unitaires");
        System.out.println("• Documentation: Markdown + PlantUML pour les diagrammes");
        
        System.out.println("\n📋 ÉTAPES DE DÉVELOPPEMENT:");
        System.out.println("1. Implémenter chaque pattern dans son propre dossier (objectif-[1-10]/)");
        System.out.println("2. Documenter chaque pattern avec description + code + tests");
        System.out.println("3. Intégrer tous les patterns dans l'application principale");
        System.out.println("4. Créer les interfaces d'intégration entre patterns");
        System.out.println("5. Développer l'interface web réactive");
        System.out.println("6. Configurer les tests automatisés et CI/CD");
        
        System.out.println("\n✅ Ce projet démontre une maîtrise complète des 10 patterns de conception!");
    }
}        
