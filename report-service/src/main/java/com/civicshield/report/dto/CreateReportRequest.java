package com.civicshield.report.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateReportRequest(
        @NotNull
        @Pattern(regexp = "POTHOLE|BROKEN_LIGHT|GARBAGE|GRAFFITI|OTHER",
                message = "must be one of POTHOLE, BROKEN_LIGHT, GARBAGE, GRAFFITI, OTHER")
        String category,

        @NotBlank
        @Size(max = 1000)
        String description,

        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        Double latitude,

        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        Double longitude
) {
}