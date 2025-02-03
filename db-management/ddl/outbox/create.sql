CREATE TABLE outbox (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sender VARCHAR(255) NOT NULL COMMENT '발신자, domain name을 의미한다.',
    recipient VARCHAR(255) NOT NULL COMMENT '수신자, 메시지를 발송할 Kafka topic을 의미한다.',
    subject VARCHAR(255) NOT NULL COMMENT '제목, Event Type을 의미한다.',
    message TEXT NOT NULL COMMENT '메시지, 보낸 Event 내용을 의미한다.',
    sent BOOLEAN NOT NULL COMMENT '메시지 발송 여부를 의미한다.',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시각',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시각'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
