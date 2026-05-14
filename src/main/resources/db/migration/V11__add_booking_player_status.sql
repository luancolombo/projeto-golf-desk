ALTER TABLE booking_player
    ADD COLUMN status VARCHAR(30) NULL;

UPDATE booking_player
SET status = 'ACTIVE'
WHERE status IS NULL OR status = '';

ALTER TABLE booking_player
    MODIFY status VARCHAR(30) NOT NULL;

CREATE INDEX idx_booking_player_booking_status ON booking_player (booking_id, status);
