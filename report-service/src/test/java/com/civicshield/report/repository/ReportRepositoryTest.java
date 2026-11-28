package com.civicshield.report.repository;

import com.civicshield.report.entity.Report;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class ReportRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    ReportRepository repository;

    @Test
    void savesAndFindsReportsByUser() {
        repository.save(new Report(UUID.randomUUID(), "user-1", "POTHOLE",
                "Big hole", 36.8, 10.18, Instant.now()));
        repository.save(new Report(UUID.randomUUID(), "user-2", "BROKEN_LIGHT",
                "Lamp out", 36.81, 10.19, Instant.now()));

        var page = repository.findByUserId("user-1", PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getCategory()).isEqualTo("POTHOLE");
        assertThat(page.getContent().get(0).getFlags()).isEmpty();
    }
}