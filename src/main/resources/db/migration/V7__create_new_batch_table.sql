CREATE TABLE batch_job_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL,
    scheduled_time DATETIME NOT NULL,
    started_at DATETIME,
    ended_at DATETIME,
    status VARCHAR(30) NOT NULL,
    error_message TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);