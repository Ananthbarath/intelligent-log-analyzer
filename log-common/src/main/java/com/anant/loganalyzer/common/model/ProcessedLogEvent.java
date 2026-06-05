package com.anant.loganalyzer.common.model;

import java.time.Instant;
import java.util.UUID;

public record ProcessedLogEvent(
        UUID id,
        String serviceName,
        LogLevel level,
        String message,
        Instant timestamp,
        String traceId,
        String userId,
        String host,
        Instant processedAt,
        String fingerprint
) {
}
