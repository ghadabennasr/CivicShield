package com.civicshield.user.controller;

import com.civicshield.user.config.SecurityConfig;
import com.civicshield.user.dto.AuthResponse;
import com.civicshield.user.dto.RegisterRequest;
import com.civicshield.user.dto.UserResponse;
import com.civicshield.user.entity.Role;
import com.civicshield.user.entity.User;
import com.civicshield.user.repository.UserRepository;
import com.civicshield.user.security.JwtAuthenticationFilter;
import com.civicshield.user.security.SecurityErrorHandler;
import com.civicshield.user.service.AuthService;
import com.civicshield.user.service.JwtService;
import com.civicshield.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, UserController.class})
@ImportAutoConfiguration(exclude = UserDetailsServiceAutoConfiguration.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        SecurityErrorHandler.class
})
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        user.setEmail("citizen@example.com");
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setRole(Role.CITIZEN);
        user.setEnabled(true);
    }

    @Test
    void registerReturnsCreated() throws Exception {
        UserResponse response = new UserResponse(
                userId, "citizen@example.com", "Jane", "Doe",
                Role.CITIZEN, null
        );

        when(userService.register(any(RegisterRequest.class))).thenReturn(user);
        when(userService.toUserResponse(user)).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "citizen@example.com",
                                  "password": "StrongPass123",
                                  "firstName": "Jane",
                                  "lastName": "Doe"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("citizen@example.com"))
                .andExpect(jsonPath("$.role").value("CITIZEN"));
    }

    @Test
    void loginReturnsJwt() throws Exception {
        when(authService.authenticate("citizen@example.com", "StrongPass123"))
                .thenReturn(new AuthResponse(
                        "test-jwt-token", "Bearer", userId,
                        "citizen@example.com", Role.CITIZEN
                ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "citizen@example.com",
                                  "password": "StrongPass123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void profileWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.correlationId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void profileWithValidTokenReturnsUser() throws Exception {
        when(jwtService.extractUserId("valid-token")).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid("valid-token", user)).thenReturn(true);

        UserResponse response = new UserResponse(
                userId, "citizen@example.com", "Jane", "Doe",
                Role.CITIZEN, null
        );

        when(userService.toUserResponse(user)).thenReturn(response);

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("citizen@example.com"))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.role").value("CITIZEN"));
    }

    @Test
    void invalidRegistrationReturnsValidationError() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "not-an-email",
                                  "password": "StrongPass123",
                                  "firstName": "Jane",
                                  "lastName": "Doe"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.correlationId").exists());
    }

    @Test
    void duplicateEmailReturnsConflict() throws Exception {
        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new com.civicshield.user.exception.EmailAlreadyExistsException(
                        "Email already exists"
                ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "citizen@example.com",
                                  "password": "StrongPass123",
                                  "firstName": "Jane",
                                  "lastName": "Doe"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void invalidTokenReturnsUnauthorized() throws Exception {
        when(jwtService.extractUserId("invalid-token"))
                .thenThrow(new IllegalArgumentException("Invalid token"));

        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.correlationId").exists());
    }
}