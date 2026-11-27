package com.civicshield.user.dto;

import com.civicshield.user.entity.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String firstName,
    String lastName,
    Role role,
    LocalDateTime createdAt
) {}