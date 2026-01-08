# Cahier des Charges - Projet INF461

## Université de Yaoundé I - 2025/2026

### Présentation du Projet

Ce document présente le cahier des charges pour le projet de développement d'un système bancaire multi-opérateurs en utilisant 10 patterns de conception différents. Le projet vise à démontrer une maîtrise complète des patterns de conception logicielle à travers une application bancaire intégrée.

## Objectifs Pédagogiques

- **Mettre en œuvre les 10 patterns de conception fondamentaux** avec des applications concrètes
- **Démontrer l'intégration harmonieuse** des patterns dans une architecture cohérente
- **Appliquer les principes SOLID** et de conception orientée objet
- **Réaliser un système bancaire fonctionnel** avec des fonctionnalités avancées
- **Documenter chaque pattern** avec sa justification, implémentation et tests

## Architecture Cible

Le système bancaire permettra :
- **Gestion multi-opérateurs** (banque, mobile money, international)
- **Authentification flexible** avec plusieurs méthodes
- **Construction de transactions complexes** avec validation et notifications
- **Gestion des notifications** groupées et ciblées
- **Analytics avancées** pour détection de fraude et rapports
- **Comptes avec comportements optionnels** (journalisation, verrouillage, limites)
- **Workflows personnalisés** pour chaque type d'opérateur

## Patterns de Conception Implémentés

### 1. Strategy + Factory Patterns
**Objectif**: Authentification multiple avec méthodes interchangeables
**Application**: Authentification par mot de passe, biométrie, OTP
**Classes**: `IAuthentificationStrategy`, `AuthentificationFactory`, `PasswordStrategy`, `BiometrieStrategy`, `OTPStrategy`

### 2. Abstract Factory Pattern
**Objectif**: Familles d'objets cohérents pour chaque opérateur
**Application**: Validateurs, calculateurs de taux, notificateurs par opérateur
**Classes**: `IOperatorFactory`, `BankFactory`, `MobileMoneyFactory`, `InternationalBankFactory`

### 3. Builder Pattern
**Objectif**: Construction flexible de transactions avec étapes optionnelles
**Application**: Transactions avec validation, conversion devise, commissions, notifications
**Classes**: `TransactionBuilder`, `TransactionStep`, `ValidationStep`, `CommissionStep`, etc.

### 4. Singleton Pattern
**Objectif**: Ressources globales uniques accessibles partout
**Application**: Service de notifications, configuration d'authentification, gestionnaire d'événements
**Classes**: `NotificationService`, `AuthentificationConfig`, `EventManager`

### 5. Adapter Pattern
**Objectif**: Homogénéisation des services SMS externes
**Application**: Adaptateurs pour différents fournisseurs SMS (JSON, SOAP, URL personnalisée)
**Classes**: `ISMSAdapter`, `SMSAdapterA`, `SMSAdapterB`, `SMSAdapterC`

### 6. Template Method Pattern (Opérateurs)
**Objectif**: Comportements communs avec spécialisations par opérateur
**Application**: Logique métier commune des opérateurs avec validations spécifiques
**Classes**: `OperatorTemplate`, `BankOperator`, `MobileMoneyOperator`, `InternationalBankOperator`

### 7. Composite Pattern
**Objectif**: Cibles de notification uniformes (individus et groupes)
**Application**: Notifications hiérarchiques pour comptes et campagnes
**Classes**: `NotificationTarget`, `SingleAccount`, `AccountGroup`, `AllAccounts`

### 8. Decorator Pattern
**Objectif**: Comportements optionnels de compte ajoutés dynamiquement
**Application**: Journalisation, verrouillage, plafonnement temporaire
**Classes**: `Account`, `AccountDecorator`, `LoggingDecorator`, `LockDecorator`, `LimitDecorator`

### 9. Template Method Pattern (Workflows)
**Objectif**: Squelettes de processus avec étapes personnalisables
**Application**: Workflows d'ouverture de compte pour différents types d'opérateurs
**Classes**: `WorkflowTemplate`, `BankAccountOpeningWorkflow`, `MobileMoneyAccountOpeningWorkflow`

### 10. Visitor Pattern
**Objectif**: Opérations analytiques sans modification des classes existantes
**Application**: Calcul de commissions, détection de fraude, rapports d'activité
**Classes**: `IAnalyticsVisitor`, `CommissionCalculatorVisitor`, `FraudDetectionVisitor`, `ActivityReportVisitor`

## Technologies

### Backend
- **Language**: Java 11+
- **Build Tool**: Maven
- **Framework**: Spring Boot (pour l'injection de dépendances)
- **Testing**: JUnit 5+ avec Mockito
- **Logging**: SLF4J avec Logback
- **Database**: PostgreSQL avec Spring Data JPA (optionnel)

### Frontend
- **Framework**: React 18+
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **Build Tool**: Vite
- **Testing**: Jest avec React Testing Library
- **State Management**: React Context + useReducer

### Documentation
- **Diagrams**: PlantUML pour les diagrammes de classes
- **API**: Swagger/OpenAPI 3.0
- **Documentation**: Markdown avec guides d'implémentation

### Infrastructures
- **Development**: Docker Compose
- **CI/CD**: GitHub Actions avec Maven
- **Code Quality**: SonarQube pour l'analyse statique
- **Monitoring**: Application logs et métriques

## Contraintes Techniques

### Contraintes Fonctionnelles
1. **Sécurité**: Implémentation sécurisée de l'authentification avec cryptage des mots de passe
2. **Performance**: Optimisation des requêtes base de données et du traitement des transactions
3. **Scalabilité**: Architecture supportant la charge élevée avec 10,000+ utilisateurs concurrents
4. **Disponibilité**: Tolérance aux pannes et mécanismes de fallback
5. **Audit Trail**: Traçabilité complète de toutes les opérations pour conformité réglementaire

### Contraintes Non-Fonctionnelles
1. **Temps**: Projet académique avec échéance fixe (6 mois)
2. **Ressources**: Utilisation de frameworks open-source et hébergement gratuit
3. **Équipe**: Projet individuel ou équipe de 2-3 étudiants maximum

### Contraintes Réglementaires
1. **RGPD**: Conformité avec le règlement général sur la protection des données
2. **Normes Bancaires**: Simulation de conformité avec les directives KYC et AML
3. **API Standards**: Documentation RESTful selon les normes OpenAPI
4. **Accessibilité**: Interface conforme aux normes WCAG 2.1

## Livrables

### Code Source
- **Backend**: Application Java complète avec tous les patterns implémentés
- **Frontend**: Interface React responsive et accessible
- **Tests**: Suite de tests unitaires et d'intégration avec couverture >80%
- **Documentation**: Guide d'implémentation et documentation API

### Documentation
- **Cahier des charges**: Ce document avec analyse détaillée
- **Conception**: Document de conception d'architecture avec UML
- **Patterns**: Documentation détaillée pour chaque pattern
- **API**: Documentation Swagger et guide d'utilisation
- **Déploiement**: Guide de configuration et déploiement
- **Rapport final**: Rapport synthétique du projet et leçons apprises

### Démonstration
- **Application démo**: Exécutable montrant tous les patterns en action
- **Scénarios de test**: Scripts de démonstration pour chaque pattern intégré
- **Présentation**: Support de présentation avec captures d'écran
- **Vidéo**: Démonstration vidéo des fonctionnalités clés (optionnel)

## Critères d'Évaluation

### Évaluation Technique (60%)
- **Qualité du code**: Respect des standards de codage, design propre, commentaires appropriés
- **Architecture**: Séparation claire des responsabilités, utilisation correcte des patterns
- **Tests**: Couverture adéquate des tests unitaires et d'intégration
- **Performance**: Application performante sans goulots d'étranglement
- **Documentation**: Documentation complète et claire

### Évaluation Pédagogique (25%)
- **Justification des patterns**: Bonne justification du choix et de l'implémentation
- **Complexité**: Niveau de complexité approprié pour un projet académique
- **Apprentissage**: Démonstration claire de la compréhension des concepts
- **Innovation**: Solutions créatives et approches originales
- **Progression**: Complexité croissante appropriée du début à la fin

### Évaluation Fonctionnelle (15%)
- **Exhaustivité**: Tous les 10 patterns implémentés avec leurs variantes
- **Intégration**: Patterns bien intégrés dans l'application globale
- **Fonctionnalités**: Toutes les fonctionnalités bancaires requises implémentées
- **Utilisabilité**: Interface utilisateur intuitive et fonctionnelle
- **Robustesse**: Gestion appropriée des erreurs et cas limites

## Planning

### Phase 1 (2 mois)
- **Semaine 1-2**: Analyse détaillée du cahier des charges
- **Semaine 3-4**: Conception de l'architecture globale et des patterns
- **Semaine 5-6**: Implémentation des patterns fondamentaux (1-5)
- **Semaine 7-8**: Implémentation des patterns avancés (6-10)
- **Semaine 9-10**: Intégration, tests et documentation

### Phase 2 (2 mois)
- **Semaine 11-12**: Développement du backend et des services
- **Semaine 13-14**: Développement du frontend et des API REST
- **Semaine 15-16**: Tests complets et correction des bugs
- **Semaine 17-18**: Documentation finale et préparation de la présentation

### Phase 3 (2 mois)
- **Semaine 19-20**: Finalisation du projet et tests d'acceptation
- **Semaine 21-22**: Préparation de la soutenance et du rapport final
- **Semaine 23-24**: Soutenance orale et remise des livrables

## Risques et Atténuation

### Risques Techniques
- **Complexité**: 10 patterns dans un seul projet peuvent devenir complexes à maintenir
- **Intégration**: Risque de mauvaise intégration entre les différents patterns
- **Performance**: Impact potentiel sur les performances si les patterns ne sont pas bien optimisés
- **Compatibilité**: Différentes versions des frameworks peuvent causer des problèmes

### Stratégies d'Atténuation
- **Développement incrémental**: Implémentation progressive avec tests continus
- **Documentation continue**: Mise à jour de la documentation au fur et à mesure
- **Revue par les pairs**: Sessions régulières de revue de code pour assurer la qualité
- **Prototypage**: Prototypage des patterns complexes avant l'implémentation finale
- **Tests automatisés**: CI/CD avec tests automatisés pour détecter les régressions

## Success Criteria

### Critères de Succès Minimal
- Tous les 10 patterns implémentés correctement selon leurs spécifications
- Application compilable et exécutable sans erreurs critiques
- Interface utilisateur fonctionnelle avec les principales fonctionnalités bancaires
- Documentation technique et pédagogique complète

### Critères de Succès Avancé
- Performance supérieure avec traitement de 1000+ transactions/secondes
- Tests avec couverture >90% incluant les cas limites
- Architecture propre et extensible permettant l'ajout de nouveaux patterns
- Conformité complète avec toutes les contraintes réglementaires
- Interface professionnelle avec UX moderne et responsive design
- Documentation API Swagger complète avec exemples d'utilisation

## Conclusion

Ce projet représente une opportunité exceptionnelle de maîtriser les patterns de conception à travers une application bancaire complète. L'approche pédagogique progressive, combinée avec la complexité technique réelle mais gérable, offrira une expérience d'apprentissage riche tout en produisant un système bancaire fonctionnel et professionnel.

L'utilisation de 10 patterns différents dans une seule application démontrera une compréhension approfondie des principes de conception logicielle et la capacité à les appliquer de manière appropriée pour résoudre des problèmes complexes.