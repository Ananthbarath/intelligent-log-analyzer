package com.anant.loganalyzer.processor.service;

import com.anant.loganalyzer.common.model.LogLevel;
import com.anant.loganalyzer.common.model.ProcessedLogEvent;
import com.anant.loganalyzer.common.model.RawLogEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LogProcessorTest {
    private final LogProcessor processor = new LogProcessor();

    @Test
    void enrichesValidLogsWithProcessingMetadata() {
        RawLogEvent raw = new RawLogEvent(UUID.randomUUID(), " payment-service ", LogLevel.ERROR,
                " Gateway timeout ", Instant.parse("2026-06-05T12:00:00Z"), "trace-1", "user-1", "host-1");

        Optional<ProcessedLogEvent> result = processor.process(raw);

        assertThat(result).isPresent();
        assertThat(result.get().serviceName()).isEqualTo("payment-service");
        assertThat(result.get().message()).isEqualTo("Gateway timeout");
        assertThat(result.get().processedAt()).isNotNull();
        assertThat(result.get().fingerprint()).hasSize(16);
    }

    @Test
    void rejectsLogsMissingRequiredFields() {
        RawLogEvent raw = new RawLogEvent(UUID.randomUUID(), "", LogLevel.ERROR,
                "Gateway timeout", Instant.now(), null, null, null);

        assertThat(processor.process(raw)).isEmpty();
    }
}
