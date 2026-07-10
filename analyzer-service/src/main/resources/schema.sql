CREATE TABLE IF NOT EXISTS alerts (
    id UUID PRIMARY KEY,
    service_name VARCHAR(255) NOT NULL,
    alert_type VARCHAR(100) NOT NULL,
    severity VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    detected_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_alerts_service_detected_at
    ON alerts (service_name, detected_at DESC);
