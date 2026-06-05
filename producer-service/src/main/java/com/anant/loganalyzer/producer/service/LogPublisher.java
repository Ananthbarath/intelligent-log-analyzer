package com.anant.loganalyzer.producer.service;

import com.anant.loganalyzer.common.KafkaTopics;
import com.anant.loganalyzer.common.model.RawLogEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogPublisher {
    private final KafkaTemplate<String, RawLogEvent> kafkaTemplate;

    public LogPublisher(KafkaTemplate<String, RawLogEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(RawLogEvent event) {
        kafkaTemplate.send(KafkaTopics.RAW_LOGS, event.serviceName(), event);
    }
}
