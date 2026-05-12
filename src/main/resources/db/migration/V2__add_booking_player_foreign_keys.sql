CREATE TABLE IF NOT EXISTS booking (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(40) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    status VARCHAR(30) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    created_by BIGINT NULL,
    tee_time_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_booking_code UNIQUE (code)
);

CREATE TABLE IF NOT EXISTS booking_player (
    id BIGINT NOT NULL AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    player_id BIGINT NOT NULL,
    green_fee_amount DECIMAL(10, 2) NOT NULL,
    checked_in BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE booking_player
    ADD CONSTRAINT fk_booking_player_booking
    FOREIGN KEY (booking_id)
    REFERENCES booking (id)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT;

ALTER TABLE booking_player
    ADD CONSTRAINT fk_booking_player_player
    FOREIGN KEY (player_id)
    REFERENCES player (id)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT;
