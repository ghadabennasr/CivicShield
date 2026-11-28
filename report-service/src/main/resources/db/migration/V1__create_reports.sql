CREATE TABLE reports (
    id                     UUID PRIMARY KEY,
    user_id                VARCHAR(100)  NOT NULL,
    category               VARCHAR(30)   NOT NULL,
    description            VARCHAR(1000) NOT NULL,
    latitude               DOUBLE PRECISION NOT NULL,
    longitude              DOUBLE PRECISION NOT NULL,
    flags                  VARCHAR(100)  NOT NULL DEFAULT '',
    duplicate_of_report_id UUID,
    abuse_score            INTEGER,
    created_at             TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_reports_user_id ON reports (user_id);