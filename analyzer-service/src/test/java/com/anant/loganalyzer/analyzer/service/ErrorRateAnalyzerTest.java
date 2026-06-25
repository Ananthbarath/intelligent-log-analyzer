package com.anant.loganalyzer.analyzer.service;

import com.anant.loganalyzer.common.model.AlertEvent;
import com.anant.loganalyzer.common.model.LogLevel;
import com.anant.loganalyzer.common.model.ProcessedLogEvent;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorRateAnalyzerTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-06-05T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void emitsAlertWhenServiceReachesErrorThresholdInsideWindow() {
        ErrorRateAnalyzer analyzer = new ErrorRateAnalyzer(3, Duration.ofMinutes(5), clock);

        assertThat(analyzer.analyze(log("payment-service", LogLevel.ERROR, "2026-06-05T11:58:00Z"))).isEmpty();
        assertThat(analyzer.analyze(log("payment-service", LogLevel.ERROR, "2026-06-05T11:59:00Z"))).isEmpty();

        Optional<AlertEvent> alert = analyzer.analyze(log("payment-service", LogLevel.ERROR, "2026-06-05T12:00:00Z"));

        assertThat(alert).isPresent();
        assertThat(alert.get().serviceName()).isEqualTo("payment-service");
        assertThat(alert.get().alertType()).isEqualTo("HIGH_ERROR_RATE");
        assertThat(alert.get().severity()).isEqualTo("HIGH");
    }

    @Test
    void ignoresNonErrorLogs() {
        ErrorRateAnalyzer analyzer = new ErrorRateAnalyzer(1, Duration.ofMinutes(5), clock);

        Optional<AlertEvent> alert = analyzer.analyze(log("payment-service", LogLevel.INFO, "2026-06-05T12:00:00Z"));

        assertThat(alert).isEmpty();
    }

    @Test
    void expiresOldErrorsBeforeCountingThreshold() {
        ErrorRateAnalyzer analyzer = new ErrorRateAnalyzer(2, Duration.ofMinutes(5), clock);

        assertThat(analyzer.analyze(log("payment-service", LogLevel.ERROR, "2026-06-05T11:50:00Z"))).isEmpty();
        Optional<AlertEvent> alert = analyzer.analyze(log("payment-service", LogLevel.ERROR, "2026-06-05T12:00:00Z"));

        assertThat(alert).isEmpty();
    }

    @Test
    void tracksServicesIndependently() {
        ErrorRateAnalyzer analyzer = new ErrorRateAnalyzer(2, Duration.ofMinutes(5), clock);

        assertThat(analyzer.analyze(log("payment-service", LogLevel.ERROR, "2026-06-05T11:59:00Z"))).isEmpty();
        assertThat(analyzer.analyze(log("order-service", LogLevel.ERROR, "2026-06-05T11:59:30Z"))).isEmpty();

        Optional<AlertEvent> alert = analyzer.analyze(log("payment-service", LogLevel.ERROR, "2026-06-05T12:00:00Z"));

        assertThat(alert).isPresent();
        assertThat(alert.get().serviceName()).isEqualTo("payment-service");
    }

    private ProcessedLogEvent log(String serviceName, LogLevel level, String timestamp) {
        return new ProcessedLogEvent(
                UUID.randomUUID(),
                serviceName,
                level,
                "message",
                Instant.parse(timestamp),
                "trace-1",
                "user-1",
                "host-1",
                Instant.parse(timestamp),
                "fingerprint"
        );
    }
}
