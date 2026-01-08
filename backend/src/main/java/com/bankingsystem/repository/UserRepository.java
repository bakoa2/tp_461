package com.bankingsystem.repository;

import com.bankingsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour les utilisateurs (Pattern Repository)
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Trouver un utilisateur par son nom d'utilisateur
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Trouver un utilisateur par son email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Vérifier si un nom d'utilisateur existe déjà
     */
    boolean existsByUsername(String username);
    
    /**
     * Vérifier si un email existe déjà
     */
    boolean existsByEmail(String email);
    
    /**
     * Trouver des utilisateurs par leur type d'authentification
     */
    @Query("SELECT u FROM User u WHERE u.authType = :authType")
    java.util.List<User> findByAuthType(@Param("authType") User.AuthType authType);
    
    /**
     * Trouver des utilisateurs actifs
     */
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    java.util.List<User> findActiveUsers();
    
    /**
     * Compter les utilisateurs par type d'authentification
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.authType = :authType")
    long countByAuthType(@Param("authType") User.AuthType authType);
}