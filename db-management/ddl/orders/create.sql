CREATE TABLE orders (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `concert_id` BIGINT NOT NULL,
    `concert_schedule_id` BIGINT NOT NULL,
    `uuid` VARCHAR(255) NOT NULL,
    `reservation_ids` JSON NOT NULL,
    `order_status` VARCHAR(50) NOT NULL,
    `total_price` BIGINT NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);