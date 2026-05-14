ALTER TABLE check_in_ticket
    ADD COLUMN player_count_snapshot INT NOT NULL DEFAULT 1 AFTER player_name_snapshot;

UPDATE check_in_ticket
SET player_count_snapshot = 1
WHERE player_count_snapshot IS NULL OR player_count_snapshot < 1;

ALTER TABLE check_in_ticket
    ADD CONSTRAINT chk_check_in_ticket_player_count_snapshot
        CHECK (player_count_snapshot BETWEEN 1 AND 4);
