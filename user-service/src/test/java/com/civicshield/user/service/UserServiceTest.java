package com.civicshield.user.service;

import com.civicshield.user.dto.RegisterRequest;
import com.civicshield.user.entity.Role;
import com.civicshield.user.entity.User;
import com.civicshield.user.exception.EmailAlreadyExistsException;
import com.civicshield.user.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerShouldCreateCitizenWithNormalizedEmailAndHashedPassword() {

        RegisterRequest request = new RegisterRequest(
                "  GHADA@EXAMPLE.COM  ",
                "SecurePassword123",
                "Ghada",
                "Ben Nasr"
        );

        when(userRepository.existsByEmail("ghada@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("SecurePassword123"))
                .thenReturn("hashed-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.register(request);

        assertEquals("ghada@example.com", savedUser.getEmail());
        assertEquals("hashed-password", savedUser.getPasswordHash());
        assertEquals("Ghada", savedUser.getFirstName());
        assertEquals("Ben Nasr", savedUser.getLastName());
        assertEquals(Role.CITIZEN, savedUser.getRole());
        assertTrue(savedUser.isEnabled());

        verify(passwordEncoder).encode("SecurePassword123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerShouldRejectAnExistingEmail() {

        RegisterRequest request = new RegisterRequest(
                "existing@example.com",
                "SecurePassword123",
                "Test",
                "User"
        );

        when(userRepository.existsByEmail("existing@example.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.register(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

	    @Test
    void registerShouldTrimFirstNameAndLastName() {

        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "SecurePassword123",
                "  John  ",
                "  Smith  "
        );

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("SecurePassword123"))
                .thenReturn("hashed-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.register(request);

        assertEquals("John", savedUser.getFirstName());
        assertEquals("Smith", savedUser.getLastName());
    }
}