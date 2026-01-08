package com.bankingsystem.controller;

import com.bankingsystem.dto.AuthRequest;
import com.bankingsystem.dto.AuthResponse;
import com.bankingsystem.model.User;
import com.bankingsystem.security.JwtUtil;
import com.bankingsystem.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour l'authentification
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Endpoint d'authentification
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest authRequest) {
        try {
            // Authentifier l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    authRequest.getUsername(),
                    authRequest.getPassword()
                )
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Générer le token JWT
            String token = jwtUtil.generateToken(userDetails);
            
            // Obtenir les informations de l'utilisateur
            User user = userService.getUserByUsername(authRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
            
            AuthResponse response = new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail());
            
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Nom d'utilisateur ou mot de passe incorrect"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Erreur lors de l'authentification: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint d'inscription
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            // Créer l'utilisateur
            User createdUser = userService.createUser(user);
            
            // Générer le token JWT automatiquement après l'inscription
            UserDetails userDetails = userService.loadUserByUsername(createdUser.getUsername());
            String token = jwtUtil.generateToken(userDetails);
            
            AuthResponse response = new AuthResponse(token, createdUser.getId(), createdUser.getUsername(), createdUser.getEmail());
            response.setMessage("Inscription réussie");
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new AuthResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Erreur lors de l'inscription: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint de rafraîchissement de token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                String username = jwtUtil.extractUsername(token);
                
                // Valider le token actuel
                User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
                
                UserDetails userDetails = userService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(token, userDetails)) {
                    // Générer un nouveau token
                    String newToken = jwtUtil.refreshToken(token);
                    
                    AuthResponse response = new AuthResponse(newToken, user.getId(), user.getUsername(), user.getEmail());
                    response.setMessage("Token rafraîchi avec succès");
                    
                    return ResponseEntity.ok(response);
                }
            }
            
            return ResponseEntity.badRequest().body(new AuthResponse("Token invalide"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Erreur lors du rafraîchissement du token: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint de validation de token
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);
                String username = jwtUtil.extractUsername(token);
                
                UserDetails userDetails = userService.loadUserByUsername(username);
                
                if (jwtUtil.validateToken(token, userDetails)) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", true);
                    response.put("username", username);
                    response.put("expiresAt", jwtUtil.extractExpiration(token));
                    
                    return ResponseEntity.ok(response);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Token invalide");
            
            return ResponseEntity.badRequest().body(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Erreur lors de la validation du token: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Endpoint de déconnexion (pour information, le JWT est stateless)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Déconnexion réussie. Supprimez le token du côté client.");
        return ResponseEntity.ok(response);
    }
}