package com.anant.loganalyzer.common.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record RawLogEvent(
        @NotNull UUID id,
        @NotBlank String serviceName,
        @NotNull LogLevel level,
        @NotBlank String message,
        Instant timestamp,
        String traceId,
        String userId,
        String host
) {
    public RawLogEvent withDefaults() {
        return new RawLogEvent(
                id == null ? UUID.randomUUID() : id,
                serviceName,
                level,
                message,
                timestamp == null ? Instant.now() : timestamp,
                traceId,
                userId,
                host
        );
    }
}
