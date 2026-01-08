package com.bankingsystem.services;

import com.bankingsystem.model.User;
import com.bankingsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des utilisateurs (implémente UserDetailsService pour Spring Security)
 */
@Service
@Transactional
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Charger un utilisateur par son nom d'utilisateur (pour Spring Security)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
            .accountExpired(!user.isActive())
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!user.isActive())
            .build();
    }
    
    /**
     * Créer un nouvel utilisateur
     */
    public User createUser(User user) {
        // Vérifier si le nom d'utilisateur existe déjà
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Nom d'utilisateur déjà utilisé");
        }
        
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        
        // Encoder le mot de passe
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Sauvegarder l'utilisateur
        User savedUser = userRepository.save(user);
        
        // Envoyer une notification de bienvenue
        try {
            notificationService.envoyerNotification(
                savedUser.getPhoneNumber(), 
                "Bienvenue dans le système bancaire multi-opérateurs!"
            );
        } catch (Exception e) {
            // Ne pas échouer la création si la notification échoue
            System.err.println("Erreur lors de l'envoi de la notification: " + e.getMessage());
        }
        
        return savedUser;
    }
    
    /**
     * Mettre à jour un utilisateur
     */
    public User updateUser(Long userId, User userDetails) {
        User user = getUserById(userId);
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setAuthType(userDetails.getAuthType());
        
        return userRepository.save(user);
    }
    
    /**
     * Changer le mot de passe d'un utilisateur
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }
        
        // Encoder et sauvegarder le nouveau mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    /**
     * Désactiver un utilisateur
     */
    public void deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setActive(false);
        userRepository.save(user);
    }
    
    /**
     * Activer un utilisateur
     */
    public void activateUser(Long userId) {
        User user = getUserById(userId);
        user.setActive(true);
        userRepository.save(user);
    }
    
    /**
     * Obtenir un utilisateur par son ID
     */
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));
    }
    
    /**
     * Obtenir un utilisateur par son nom d'utilisateur
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    /**
     * Obtenir un utilisateur par son email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Lister tous les utilisateurs
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Lister les utilisateurs actifs
     */
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userRepository.findActiveUsers();
    }
    
    /**
     * Lister les utilisateurs par type d'authentification
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByAuthType(User.AuthType authType) {
        return userRepository.findByAuthType(authType);
    }
    
    /**
     * Compter les utilisateurs par type d'authentification
     */
    @Transactional(readOnly = true)
    public long countUsersByAuthType(User.AuthType authType) {
        return userRepository.countByAuthType(authType);
    }
    
    /**
     * Supprimer un utilisateur
     */
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        userRepository.delete(user);
    }
}