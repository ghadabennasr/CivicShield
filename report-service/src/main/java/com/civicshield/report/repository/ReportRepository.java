package com.civicshield.report.repository;

import com.civicshield.report.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {

    Page<Report> findByUserId(String userId, Pageable pageable);
}