package com.civicshield.user.dto;

import java.time.Instant;

public record ErrorResponse(
        String code,
        String message,
        String correlationId,
        Instant timestamp
) {}