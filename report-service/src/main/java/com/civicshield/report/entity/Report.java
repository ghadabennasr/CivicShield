package com.civicshield.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    // Comma-separated flags, e.g. "DUPLICATE,UNCHECKED". Empty when clean.
    @Column(nullable = false)
    private String flags = "";

    @Column(name = "duplicate_of_report_id")
    private UUID duplicateOfReportId;

    @Column(name = "abuse_score")
    private Integer abuseScore;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Report() {
    }

    public Report(UUID id, String userId, String category, String description,
                  double latitude, double longitude, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.category = category;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getUserId() { return userId; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getFlags() { return flags; }
    public UUID getDuplicateOfReportId() { return duplicateOfReportId; }
    public Integer getAbuseScore() { return abuseScore; }
    public Instant getCreatedAt() { return createdAt; }

    public void setFlags(String flags) { this.flags = flags; }
    public void setDuplicateOfReportId(UUID id) { this.duplicateOfReportId = id; }
    public void setAbuseScore(Integer abuseScore) { this.abuseScore = abuseScore; }
}