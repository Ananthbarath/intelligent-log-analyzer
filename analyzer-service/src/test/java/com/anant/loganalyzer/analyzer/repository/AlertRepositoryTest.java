package com.anant.loganalyzer.analyzer.repository;

import com.anant.loganalyzer.common.model.AlertEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AlertRepositoryTest {
    @Test
    void savesAlertToDatabase() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        AlertRepository repository = new AlertRepository(jdbcTemplate);
        AlertEvent alert = new AlertEvent(
                UUID.fromString("6ca68697-74dc-45a8-9123-a4e792af8071"),
                "payment-service",
                "HIGH_ERROR_RATE",
                "HIGH",
                "Service payment-service reached 5 errors within PT5M",
                Instant.parse("2026-06-05T12:00:00Z")
        );

        repository.save(alert);

        ArgumentCaptor<Object[]> args = ArgumentCaptor.forClass(Object[].class);
        verify(jdbcTemplate).update(anyString(), args.capture());
        assertThat(args.getValue()).containsExactly(
                alert.id(),
                alert.serviceName(),
                alert.alertType(),
                alert.severity(),
                alert.message(),
                Timestamp.from(alert.detectedAt())
        );
    }
}
