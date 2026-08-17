CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    notification_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_user BIGINT NOT NULL,
    INDEX idx_notification_user (id_user)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;