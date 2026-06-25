package com.anant.loganalyzer.analyzer.service;

import com.anant.loganalyzer.common.model.AlertEvent;
import com.anant.loganalyzer.common.model.LogLevel;
import com.anant.loganalyzer.common.model.ProcessedLogEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;

@Service
public class ErrorRateAnalyzer {
    private final int errorThreshold;
    private final Duration window;
    private final Clock clock;
    private final Map<String, Queue<Instant>> serviceErrors = new HashMap<>();

    public ErrorRateAnalyzer(
            @Value("${analyzer.error-rate.threshold:5}") int errorThreshold,
            @Value("${analyzer.error-rate.window:PT5M}") Duration window
    ) {
        this(errorThreshold, window, Clock.systemUTC());
    }

    ErrorRateAnalyzer(int errorThreshold, Duration window, Clock clock) {
        if (errorThreshold < 1) {
            throw new IllegalArgumentException("errorThreshold must be at least 1");
        }
        if (window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("window must be positive");
        }
        this.errorThreshold = errorThreshold;
        this.window = window;
        this.clock = clock;
    }

    public synchronized Optional<AlertEvent> analyze(ProcessedLogEvent event) {
        if (event == null || event.level() != LogLevel.ERROR || isBlank(event.serviceName())) {
            return Optional.empty();
        }

        Instant observedAt = event.timestamp() == null ? Instant.now(clock) : event.timestamp();
        Queue<Instant> errors = serviceErrors.computeIfAbsent(event.serviceName(), ignored -> new ArrayDeque<>());
        errors.add(observedAt);
        evictExpired(errors, observedAt.minus(window));

        if (errors.size() < errorThreshold) {
            return Optional.empty();
        }

        errors.clear();
        return Optional.of(new AlertEvent(
                UUID.randomUUID(),
                event.serviceName(),
                "HIGH_ERROR_RATE",
                "HIGH",
                "Service " + event.serviceName() + " reached " + errorThreshold + " errors within " + window,
                Instant.now(clock)
        ));
    }

    private void evictExpired(Queue<Instant> errors, Instant cutoff) {
        while (!errors.isEmpty() && errors.peek().isBefore(cutoff)) {
            errors.poll();
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
