package com.civicshield.user.dto;

import com.civicshield.user.entity.Role;

import java.util.UUID;

public record AuthResponse(
    String accessToken,
    String tokenType,
    UUID userId,
    String email,
    Role role
) {}