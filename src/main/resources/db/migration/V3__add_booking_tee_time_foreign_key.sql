CREATE TABLE IF NOT EXISTS tee_time (
    id BIGINT NOT NULL AUTO_INCREMENT,
    play_date DATE NOT NULL,
    start_time TIME(6) NOT NULL,
    max_players INT NOT NULL,
    booked_players INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    base_green_fee DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_tee_time_play_date_start_time UNIQUE (play_date, start_time)
);

ALTER TABLE booking
    ADD CONSTRAINT fk_booking_tee_time
    FOREIGN KEY (tee_time_id)
    REFERENCES tee_time (id)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT;
