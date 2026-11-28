package com.civicshield.user.dto;

public record ErrorDetail(
        String field,
        String message
) {}