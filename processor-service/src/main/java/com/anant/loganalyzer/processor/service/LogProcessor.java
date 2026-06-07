package com.anant.loganalyzer.processor.service;

import com.anant.loganalyzer.common.model.DeadLetterLogEvent;
import com.anant.loganalyzer.common.model.ProcessedLogEvent;
import com.anant.loganalyzer.common.model.RawLogEvent;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
public class LogProcessor {
    public Optional<ProcessedLogEvent> process(RawLogEvent raw) {
        if (isInvalid(raw)) {
            return Optional.empty();
        }

        RawLogEvent event = raw.withDefaults();
        String serviceName = event.serviceName().trim();
        String message = event.message().trim();
        String fingerprint = fingerprint(serviceName, event.level().name(), message);

        return Optional.of(new ProcessedLogEvent(
                event.id(),
                serviceName,
                event.level(),
                message,
                event.timestamp(),
                event.traceId(),
                event.userId(),
                event.host(),
                Instant.now(),
                fingerprint
        ));
    }

    public DeadLetterLogEvent toDeadLetter(RawLogEvent event) {
        return new DeadLetterLogEvent(
                event == null || event.id() == null ? UUID.randomUUID() : event.id(),
                event,
                "Missing required log fields",
                Instant.now()
        );
    }

    private boolean isInvalid(RawLogEvent event) {
        return event == null
                || isBlank(event.serviceName())
                || event.level() == null
                || isBlank(event.message());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String fingerprint(String serviceName, String level, String message) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((serviceName + "|" + level + "|" + message).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes).substring(0, 16);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 digest is not available", exception);
        }
    }
}
