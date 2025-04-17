# Spring Boot, Spring Security, MongoDB - JWT Authentication & Authorization
# auth-user-service

## 1. Initialiser le projet

Utilisez Spring Initializr pour générer la structure de base.

Choisissez les options suivantes :

*   Project : Maven
*   Language : Java
*   Spring Boot Version : 3.1.x ou plus récent
*   Dependencies :
   *   Spring Web
   *   Spring Security
   *   Spring Data MongoDB
   *   Spring Boot Starter Validation
   *   Spring Boot Starter Kafka
   *   Spring Cloud Resilience4j
   *   JSON Web Token (vous l'ajouterez manuellement).

### Fonctionnement

Lorsque vous démarrez votre service `auth-user-service`, il se connecte au Config Server (http://localhost:8888).

Le Config Server récupère le fichier `auth-user-service.yml` depuis votre référentiel GitHub.

Les configurations (MongoDB, Kafka, Eureka) sont injectées dans votre service.

### Structure des Packages

*   `model`: pour les entités (User, Role, Privilege)
*   `dto`: pour les objets de transfert de données (DTO)
*   `repository`: pour les interfaces de repository MongoDB
*   `service`: pour les services (UserService, AuthService)
*   `controller`: pour les contrôleurs (AuthController, UserController)
*   `security`: pour la configuration de la sécurité

## 2. Composants de Sécurité

### 2.1. AuthEntryPointJwt

Cette classe gère les tentatives d'accès non autorisées aux ressources protégées. Lorsqu'une requête non authentifiée essaie d'accéder à une ressource protégée, cette classe retourne une réponse HTTP 401 (Non autorisé) avec un message d'erreur.

### 2.2. AuthTokenFilter

Ce filtre s'exécute pour chaque requête HTTP. Il extrait le token JWT de l'en-tête `Authorization`, vérifie sa validité, et configure l'authentification de l'utilisateur dans le contexte de sécurité de Spring. Cela permet de vérifier que les utilisateurs sont authentifiés pour chaque requête protégée.

### 2.3. JwtTokenProvider (JwtUtils)

Cette classe gère la génération, la validation et l'extraction des informations des tokens JWT. Elle utilise la bibliothèque `io.jsonwebtoken` pour créer et valider les tokens.

### 2.4. UserDetailsServiceImpl

Cette classe implémente `UserDetailsService` et est responsable de charger les détails de l'utilisateur à partir de la base de données (MongoDB) lors du processus d'authentification.  Elle utilise `UserRepository` pour récupérer l'utilisateur par email (username).

### 2.5. UserDetailsImpl

Cette classe implémente `UserDetails` et représente les détails d'un utilisateur spécifique, tels que son nom d'utilisateur, son mot de passe et ses autorités (rôles). Elle est construite à partir d'une entité `User` et est utilisée par Spring Security pour l'authentification et l'autorisation.

### 2.6. WebSecurityConfig

Cette classe configure la sécurité de l'application avec Spring Security. Elle définit les filtres de sécurité, le fournisseur d'authentification (`DaoAuthenticationProvider`), l'encodeur de mot de passe (`BCryptPasswordEncoder`), et les règles d'autorisation pour les différentes URL.  Elle permet également l'accès à Swagger et Actuator sans authentification.

## 3. Modèle de Données (User)

L'entité `User` contient les informations de l'utilisateur, y compris son nom, prénom, email, mot de passe, téléphone, et une collection de `Role`s.  L'email est indexé comme unique.

## 4. Flux d'Authentification

1.  **Tentative de connexion :** L'utilisateur fournit ses informations d'identification (email et mot de passe).

2.  **Authentification Spring Security :** Spring Security utilise `AuthenticationManager` et `UserDetailsServiceImpl` pour authentifier l'utilisateur. `UserDetailsServiceImpl` charge les détails de l'utilisateur depuis la base de données.

3.  **Création du token JWT :** Si l'authentification réussit, `JwtTokenProvider` génère un token JWT contenant les informations de l'utilisateur.

4.  **Retour du token JWT :** Le token JWT est renvoyé à l'utilisateur.

5.  **Requêtes protégées :** Pour accéder aux ressources protégées, l'utilisateur inclut le token JWT dans l'en-tête `Authorization` de ses requêtes.

6.  **Validation du token JWT :** `AuthTokenFilter` intercepte les requêtes, extrait le token JWT, et le valide à l'aide de `JwtTokenProvider`.

7.  **Autorisation :** Si le token JWT est valide, Spring Security autorise l'accès à la ressource en fonction des rôles/autorités de l'utilisateur.

## 5.  Sécurisation des Endpoints

L'annotation `@PreAuthorize("hasRole('ROLE_MINISTRY')")` peut être utilisée dans les contrôleurs pour restreindre l'accès à certaines méthodes aux utilisateurs ayant le rôle `ROLE_MINISTRY`.  Cela permet de sécuriser les opérations d'administration sur les rôles et privilèges.

## 6.  JWT (Json Web Token)

*   **generateJwtToken(Authentication authentication):** Génère un JWT à partir des informations d'authentification de l'utilisateur.
*   **getUserNameFromJwtToken(String token):** Extrait le nom d'utilisateur du token JWT.
*   **validateJwtToken(String authToken):** Valide le token JWT.
*   **key():**  Génère la clé de signature JWT.

## 7. UserDetails

*   **UserDetailsImpl:** Implémentation personnalisée de `UserDetails` pour stocker les informations de l'utilisateur.
*   **UserDetailsServiceImpl:** Service pour charger les détails de l'utilisateur à partir de la base de données.

Ce document fournit une vue d'ensemble de l'architecture et du fonctionnement de votre service `auth-user-service`. Il décrit les principaux composants, le flux d'authentification et les mesures de sécurité mises en place.


# User Registration, Login and Authorization process.
![user_registration_login.png](user_registration_login.png)
## Spring Boot Rest API Architecture with Spring Security
![Arch spring security.png](Arch%20spring%20security.png)



Ministry of Health administrators (ROLE_MS_ADMIN), staff (ROLE_MS_STAFF), hospital administrators (ROLE_HOSPITAL_ADMIN), medical staff (ROLE_MEDICAL_STAFF), maintenance engineers (ROLE_MAINTENANCE_ENGINEER), and maintenance company staff (ROLE_STE_MAINTENANCE).
