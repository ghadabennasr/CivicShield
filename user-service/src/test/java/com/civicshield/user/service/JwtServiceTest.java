package com.civicshield.user.service;

import com.civicshield.user.entity.Role;
import com.civicshield.user.entity.User;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    private static final String TEST_SECRET =
            "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";

    @Test
    void generateTokenShouldContainCorrectUserClaims() {

        JwtService jwtService = new JwtService(TEST_SECRET, 15);

        UUID userId = UUID.randomUUID();

        User user = mock(User.class);

        when(user.getId()).thenReturn(userId);
        when(user.getEmail()).thenReturn("test@example.com");
        when(user.getRole()).thenReturn(Role.CITIZEN);
        when(user.isEnabled()).thenReturn(true);

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertEquals(userId, jwtService.extractUserId(token));
        assertEquals("test@example.com", jwtService.extractEmail(token));
        assertEquals("CITIZEN", jwtService.extractRole(token));
        assertTrue(jwtService.isTokenValid(token, user));
    }

    @Test
    void tokenShouldBeRejectedForAnotherUser() {

        JwtService jwtService = new JwtService(TEST_SECRET, 15);

        User originalUser = mock(User.class);
        User anotherUser = mock(User.class);

        when(originalUser.getId()).thenReturn(UUID.randomUUID());
        when(originalUser.getEmail()).thenReturn("original@example.com");
        when(originalUser.getRole()).thenReturn(Role.CITIZEN);

        when(anotherUser.getId()).thenReturn(UUID.randomUUID());
        when(anotherUser.isEnabled()).thenReturn(true);

        String token = jwtService.generateToken(originalUser);

        assertFalse(jwtService.isTokenValid(token, anotherUser));
    }
}