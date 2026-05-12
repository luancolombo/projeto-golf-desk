CREATE TABLE check_in_ticket (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ticket_number VARCHAR(40) NOT NULL,
    booking_player_id BIGINT NOT NULL,
    player_name_snapshot VARCHAR(100) NOT NULL,
    booking_code_snapshot VARCHAR(40) NOT NULL,
    play_date DATE NOT NULL,
    start_time TIME NOT NULL,
    starting_tee VARCHAR(20) NOT NULL,
    crossing_tee VARCHAR(20) NOT NULL,
    crossing_time TIME NOT NULL,
    issued_at DATETIME(6) NOT NULL,
    cancelled BIT NOT NULL,
    cancelled_at DATETIME(6) NULL,
    cancellation_reason VARCHAR(255) NULL,
    CONSTRAINT pk_check_in_ticket PRIMARY KEY (id),
    CONSTRAINT uk_check_in_ticket_number UNIQUE (ticket_number),
    CONSTRAINT fk_check_in_ticket_booking_player FOREIGN KEY (booking_player_id) REFERENCES booking_player(id)
);

CREATE INDEX idx_check_in_ticket_booking_player_id ON check_in_ticket (booking_player_id);
CREATE INDEX idx_check_in_ticket_play_date ON check_in_ticket (play_date);
