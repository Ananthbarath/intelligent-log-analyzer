package com.anant.loganalyzer.analyzer.repository;

import com.anant.loganalyzer.common.model.AlertEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class AlertRepository {
    private final JdbcTemplate jdbcTemplate;

    public AlertRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(AlertEvent alert) {
        jdbcTemplate.update("""
                        INSERT INTO alerts (id, service_name, alert_type, severity, message, detected_at)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                alert.id(),
                alert.serviceName(),
                alert.alertType(),
                alert.severity(),
                alert.message(),
                Timestamp.from(alert.detectedAt())
        );
    }
}
