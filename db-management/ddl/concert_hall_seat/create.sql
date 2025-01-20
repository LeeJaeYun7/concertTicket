CREATE TABLE concert_hall_seat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    concert_hall_id BIGINT NOT NULL,
    number BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);