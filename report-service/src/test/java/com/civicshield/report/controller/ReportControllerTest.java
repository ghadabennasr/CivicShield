package com.civicshield.report.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class ReportControllerTest {

    private static final String SECRET_B64 = Base64.getEncoder().encodeToString(
            "test-secret-test-secret-test-secret-123456".getBytes(StandardCharsets.UTF_8));

    private static final String VALID_BODY = """
            {"category":"POTHOLE","description":"Big hole near the school","latitude":36.8,"longitude":10.18}
            """;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void jwtProperties(DynamicPropertyRegistry registry) {
        registry.add("security.jwt.secret", () -> SECRET_B64);
    }

    @Autowired
    MockMvc mockMvc;

    @Test
    void createsReportForCitizen() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(post("/reports")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(userId, "CITIZEN", inMinutes(15)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.category").value("POTHOLE"))
                .andExpect(jsonPath("$.flags[0]").value("UNCHECKED"));
    }

    @Test
    void rejectsInvalidBodyWithSharedErrorModel() throws Exception {
        String badBody = """
                {"category":"POTHOLE","description":"Big hole","latitude":120,"longitude":10.18}
                """;

        mockMvc.perform(post("/reports")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(UUID.randomUUID(), "CITIZEN", inMinutes(15)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.correlationId").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void rejectsMissingToken() throws Exception {
        mockMvc.perform(post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    void rejectsExpiredToken() throws Exception {
        mockMvc.perform(post("/reports")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(UUID.randomUUID(), "CITIZEN", inMinutes(-5)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }

    @Test
    void rejectsNonCitizenRole() throws Exception {
        mockMvc.perform(post("/reports")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(UUID.randomUUID(), "OPERATOR", inMinutes(15)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    private static String token(UUID userId, String role, Date expiration) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_B64));
        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    private static Date inMinutes(int minutes) {
        return new Date(System.currentTimeMillis() + minutes * 60_000L);
    }
}