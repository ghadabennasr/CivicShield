package com.civicshield.report.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.civicshield.report.dto.CreateReportRequest;
import com.civicshield.report.dto.ReportResponse;
import com.civicshield.report.entity.Report;
import com.civicshield.report.security.AuthenticatedUser;
import com.civicshield.report.service.ReportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportResponse> create(
            @Valid @RequestBody CreateReportRequest request,
            @AuthenticationPrincipal AuthenticatedUser user) {
        Report saved = reportService.create(user, request);
        return ResponseEntity
                .created(URI.create("/reports/" + saved.getId()))
                .body(ReportResponse.from(saved));
    }
}