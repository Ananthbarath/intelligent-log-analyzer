package com.anant.loganalyzer.common.model;

import java.time.Instant;
import java.util.UUID;

public record AlertEvent(
        UUID id,
        String serviceName,
        String alertType,
        String severity,
        String message,
        Instant detectedAt
) {
}
