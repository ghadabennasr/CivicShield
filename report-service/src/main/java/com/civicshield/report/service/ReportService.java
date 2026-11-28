package com.civicshield.report.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.civicshield.report.dto.CreateReportRequest;
import com.civicshield.report.entity.Report;
import com.civicshield.report.repository.ReportRepository;
import com.civicshield.report.security.AuthenticatedUser;

@Service
public class ReportService {

    private final ReportRepository repository;

    public ReportService(ReportRepository repository) {
        this.repository = repository;
    }

    public Report create(AuthenticatedUser user, CreateReportRequest request) {
        Report report = new Report(
                UUID.randomUUID(),
                user.userId().toString(),
                request.category(),
                request.description().trim(),
                request.latitude(),
                request.longitude(),
                Instant.now());

        // The gRPC duplicate and abuse checks are not wired yet, so the report
        // is honestly marked as unchecked. The gRPC step replaces this.
        report.setFlags("UNCHECKED");
        return repository.save(report);
    }
}