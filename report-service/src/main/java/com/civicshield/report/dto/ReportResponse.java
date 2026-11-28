package com.civicshield.report.dto;

import com.civicshield.report.entity.Report;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record ReportResponse(
        UUID id,
        String userId,
        String category,
        String description,
        double latitude,
        double longitude,
        List<String> flags,
        UUID duplicateOfReportId,
        Integer abuseScore,
        Instant createdAt
) {
    public static ReportResponse from(Report r) {
        List<String> flags = r.getFlags().isBlank()
                ? List.of()
                : Arrays.asList(r.getFlags().split(","));
        return new ReportResponse(r.getId(), r.getUserId(), r.getCategory(),
                r.getDescription(), r.getLatitude(), r.getLongitude(), flags,
                r.getDuplicateOfReportId(), r.getAbuseScore(), r.getCreatedAt());
    }
}