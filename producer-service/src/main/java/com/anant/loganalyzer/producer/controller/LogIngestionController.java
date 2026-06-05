package com.anant.loganalyzer.producer.controller;

import com.anant.loganalyzer.common.model.RawLogEvent;
import com.anant.loganalyzer.producer.service.LogPublisher;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class LogIngestionController {
    private final LogPublisher logPublisher;

    public LogIngestionController(LogPublisher logPublisher) {
        this.logPublisher = logPublisher;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> ingest(@Valid @RequestBody RawLogEvent request) {
        RawLogEvent event = request.withDefaults();
        logPublisher.publish(event);
        return Map.of("id", event.id().toString(), "status", "accepted");
    }
}
