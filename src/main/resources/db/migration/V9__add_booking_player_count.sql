ALTER TABLE booking_player
    ADD COLUMN player_count INT NOT NULL DEFAULT 1 AFTER green_fee_amount;

UPDATE booking_player
SET player_count = 1
WHERE player_count IS NULL OR player_count < 1;

ALTER TABLE booking_player
    ADD CONSTRAINT chk_booking_player_count
        CHECK (player_count BETWEEN 1 AND 4);
