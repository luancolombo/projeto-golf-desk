CREATE TABLE IF NOT EXISTS rental_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    total_stock INT NOT NULL,
    available_stock INT NOT NULL,
    rental_price DECIMAL(10, 2) NOT NULL,
    active BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS rental_transaction (
    id BIGINT NOT NULL AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    booking_player_id BIGINT NOT NULL,
    rental_item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (id)
);

DELETE rt
FROM rental_transaction rt
LEFT JOIN booking b ON b.id = rt.booking_id
WHERE b.id IS NULL;

DELETE rt
FROM rental_transaction rt
LEFT JOIN booking_player bp ON bp.id = rt.booking_player_id
WHERE bp.id IS NULL;

DELETE rt
FROM rental_transaction rt
LEFT JOIN rental_item ri ON ri.id = rt.rental_item_id
WHERE ri.id IS NULL;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'rental_transaction'
      AND CONSTRAINT_NAME = 'fk_rental_transaction_booking'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE rental_transaction ADD CONSTRAINT fk_rental_transaction_booking FOREIGN KEY (booking_id) REFERENCES booking (id) ON UPDATE RESTRICT ON DELETE RESTRICT',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'rental_transaction'
      AND CONSTRAINT_NAME = 'fk_rental_transaction_booking_player'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE rental_transaction ADD CONSTRAINT fk_rental_transaction_booking_player FOREIGN KEY (booking_player_id) REFERENCES booking_player (id) ON UPDATE RESTRICT ON DELETE RESTRICT',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'rental_transaction'
      AND CONSTRAINT_NAME = 'fk_rental_transaction_rental_item'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE rental_transaction ADD CONSTRAINT fk_rental_transaction_rental_item FOREIGN KEY (rental_item_id) REFERENCES rental_item (id) ON UPDATE RESTRICT ON DELETE RESTRICT',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;
