package com.civicshield.user.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        String correlationId,
        Instant timestamp,
        List<ErrorDetail> details
) {}