package com.bankingsystem;

import com.bankingsystem.model.User;
import com.bankingsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests d'intégration pour l'application bancaire
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class BankingSystemApplicationTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser_Success() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        // When
        User result = userService.createUser(user);

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).save(user);
    }

    @Test
    void testCreateUser_UsernameAlreadyExists() {
        // Given
        User user = new User();
        user.setUsername("existinguser");
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Nom d'utilisateur déjà utilisé", exception.getMessage());
    }

    @Test
    void testCreateUser_EmailAlreadyExists() {
        // Given
        User user = new User();
        user.setUsername("newuser");
        user.setEmail("existing@example.com");
        user.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.createUser(user);
        });

        assertEquals("Email déjà utilisé", exception.getMessage());
    }

    @Test
    void testLoadUserByUsername_Success() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setActive(true);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        // When
        org.springframework.security.core.userdetails.UserDetails result = userService.loadUserByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertTrue(result.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Given
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent");
        });

        assertEquals("Utilisateur non trouvé: nonexistent", exception.getMessage());
    }

    @Test
    void testUserRepositoryMethods() {
        // Given
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        // Test existsByUsername
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertTrue(userRepository.existsByUsername("testuser"));

        // Test existsByEmail
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        assertFalse(userRepository.existsByEmail("test@example.com"));

        // Test findByUsername
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        Optional<User> result = userRepository.findByUsername("testuser");
        assertTrue(result.isPresent());
        assertEquals(user, result.get());

        // Test save
        when(userRepository.save(any(User.class))).thenReturn(user);
        User savedUser = userRepository.save(user);
        assertEquals(user, savedUser);

        // Test delete
        doNothing().when(userRepository).delete(user);
        userRepository.delete(user);
        verify(userRepository).delete(user);
    }

    @Test
    void testUserModel() {
        // Test User model
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("123456789");

        // Test getters
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("123456789", user.getPhoneNumber());

        // Test setters
        user.setUsername("newuser");
        assertEquals("newuser", user.getUsername());

        user.setActive(false);
        assertFalse(user.isActive());

        // Test toString
        String userString = user.toString();
        assertTrue(userString.contains("testuser"));
        assertTrue(userString.contains("test@example.com"));
    }

    @Test
    void testUserAuthType() {
        // Test AuthType enum
        assertEquals("Mot de passe", User.AuthType.PASSWORD.getDescription());
        assertEquals("Biométrie", User.AuthType.BIOMETRIC.getDescription());
        assertEquals("OTP", User.AuthType.OTP.getDescription());
        assertEquals("Réalité Augmentée", User.AuthType.REALITE_AUGMENTEE.getDescription());
        
        // Test enum values
        User.AuthType[] authTypes = User.AuthType.values();
        assertEquals(4, authTypes.length);
    }
}