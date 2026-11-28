package com.civicshield.user.dto;

import com.civicshield.user.entity.Role;
import jakarta.validation.constraints.NotNull;

public record RoleUpdateRequest(
        @NotNull(message = "A role is required.")
        Role role
) {}