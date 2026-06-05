package com.anant.loganalyzer.common.model;

import java.time.Instant;
import java.util.UUID;

public record DeadLetterLogEvent(
        UUID id,
        RawLogEvent originalEvent,
        String reason,
        Instant failedAt
) {
}
