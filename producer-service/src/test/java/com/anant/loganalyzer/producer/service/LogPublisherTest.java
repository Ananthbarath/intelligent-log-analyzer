package com.anant.loganalyzer.producer.service;

import com.anant.loganalyzer.common.KafkaTopics;
import com.anant.loganalyzer.common.model.LogLevel;
import com.anant.loganalyzer.common.model.RawLogEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.UUID;

class LogPublisherTest {
    @Test
    void publishesRawLogWithServiceNameAsKey() {
        KafkaTemplate<String, RawLogEvent> kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        LogPublisher publisher = new LogPublisher(kafkaTemplate);
        RawLogEvent event = new RawLogEvent(UUID.randomUUID(), "payment-service", LogLevel.ERROR,
                "Gateway timeout", Instant.now(), "trace-1", "user-1", "host-1");

        publisher.publish(event);

        Mockito.verify(kafkaTemplate).send(KafkaTopics.RAW_LOGS, "payment-service", event);
    }
}
