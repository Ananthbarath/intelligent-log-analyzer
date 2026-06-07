package com.anant.loganalyzer.processor.consumer;

import com.anant.loganalyzer.common.KafkaTopics;
import com.anant.loganalyzer.common.model.DeadLetterLogEvent;
import com.anant.loganalyzer.common.model.ProcessedLogEvent;
import com.anant.loganalyzer.common.model.RawLogEvent;
import com.anant.loganalyzer.processor.service.LogProcessor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class RawLogConsumer {
    private final LogProcessor logProcessor;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public RawLogConsumer(LogProcessor logProcessor, KafkaTemplate<String, Object> kafkaTemplate) {
        this.logProcessor = logProcessor;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopics.RAW_LOGS, groupId = "processor-service")
    public void consume(RawLogEvent event) {
        logProcessor.process(event).ifPresentOrElse(
                this::publishProcessed,
                () -> publishDeadLetter(logProcessor.toDeadLetter(event))
        );
    }

    private void publishProcessed(ProcessedLogEvent event) {
        kafkaTemplate.send(KafkaTopics.PROCESSED_LOGS, event.serviceName(), event);
    }

    private void publishDeadLetter(DeadLetterLogEvent event) {
        String key = event.originalEvent() == null ? "unknown-service" : event.originalEvent().serviceName();
        kafkaTemplate.send(KafkaTopics.DEAD_LETTER_LOGS, key, event);
    }
}
