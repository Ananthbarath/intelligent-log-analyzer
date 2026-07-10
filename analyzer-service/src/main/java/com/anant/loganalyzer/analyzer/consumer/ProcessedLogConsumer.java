package com.anant.loganalyzer.analyzer.consumer;

import com.anant.loganalyzer.analyzer.repository.AlertRepository;
import com.anant.loganalyzer.analyzer.service.ErrorRateAnalyzer;
import com.anant.loganalyzer.common.KafkaTopics;
import com.anant.loganalyzer.common.model.AlertEvent;
import com.anant.loganalyzer.common.model.ProcessedLogEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProcessedLogConsumer {
    private final ErrorRateAnalyzer analyzer;
    private final AlertRepository alertRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProcessedLogConsumer(
            ErrorRateAnalyzer analyzer,
            AlertRepository alertRepository,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.analyzer = analyzer;
        this.alertRepository = alertRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopics.PROCESSED_LOGS, groupId = "analyzer-service")
    public void consume(ProcessedLogEvent event) {
        analyzer.analyze(event).ifPresent(this::publishAlert);
    }

    private void publishAlert(AlertEvent alert) {
        alertRepository.save(alert);
        kafkaTemplate.send(KafkaTopics.ALERT_EVENTS, alert.serviceName(), alert);
    }
}
