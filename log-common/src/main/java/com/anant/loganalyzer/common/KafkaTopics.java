package com.anant.loganalyzer.common;

public final class KafkaTopics {
    public static final String RAW_LOGS = "raw-logs";
    public static final String PROCESSED_LOGS = "processed-logs";
    public static final String DEAD_LETTER_LOGS = "dead-letter-logs";
    public static final String ALERT_EVENTS = "alert-events";

    private KafkaTopics() {
    }
}
