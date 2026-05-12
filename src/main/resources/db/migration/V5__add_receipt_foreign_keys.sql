CREATE TABLE IF NOT EXISTS payment (
    id BIGINT NOT NULL AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    booking_player_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    paid_at DATETIME,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS receipt (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receipt_number VARCHAR(40) NOT NULL,
    booking_id BIGINT NOT NULL,
    booking_player_id BIGINT NOT NULL,
    payment_id BIGINT NOT NULL,
    player_name_snapshot VARCHAR(100) NOT NULL,
    player_tax_number_snapshot VARCHAR(50),
    booking_code_snapshot VARCHAR(40) NOT NULL,
    play_date DATE NOT NULL,
    start_time TIME NOT NULL,
    green_fee_amount DECIMAL(10, 2) NOT NULL,
    rental_amount DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    payment_status VARCHAR(30) NOT NULL,
    issued_at DATETIME NOT NULL,
    cancelled BOOLEAN NOT NULL,
    cancelled_at DATETIME,
    cancellation_reason VARCHAR(255),
    PRIMARY KEY (id),
    UNIQUE KEY uk_receipt_receipt_number (receipt_number)
);

CREATE TABLE IF NOT EXISTS receipt_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    receipt_id BIGINT NOT NULL,
    description VARCHAR(120) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (id)
);

DELETE p
FROM payment p
LEFT JOIN booking b ON b.id = p.booking_id
WHERE b.id IS NULL;

DELETE p
FROM payment p
LEFT JOIN booking_player bp ON bp.id = p.booking_player_id
WHERE bp.id IS NULL;

DELETE r
FROM receipt r
LEFT JOIN booking b ON b.id = r.booking_id
WHERE b.id IS NULL;

DELETE r
FROM receipt r
LEFT JOIN booking_player bp ON bp.id = r.booking_player_id
WHERE bp.id IS NULL;

DELETE r
FROM receipt r
LEFT JOIN payment p ON p.id = r.payment_id
WHERE p.id IS NULL;

DELETE ri
FROM receipt_item ri
LEFT JOIN receipt r ON r.id = ri.receipt_id
WHERE r.id IS NULL;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'payment'
      AND CONSTRAINT_NAME = 'fk_payment_booking'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE payment ADD CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES booking (id) ON UPDATE RESTRICT ON DELETE RESTRICT',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'payment'
      AND CONSTRAINT_NAME = 'fk_payment_booking_player'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE payment ADD CONSTRAINT fk_payment_booking_player FOREIGN KEY (booking_player_id) REFERENCES booking_player (id) ON UPDATE RESTRICT ON DELETE RESTRICT',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt'
      AND CONSTRAINT_NAME = 'fk_receipt_booking'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE receipt ADD CONSTRAINT fk_receipt_booking FOREIGN KEY (booking_id) REFERENCES booking (id) ON UPDATE RESTRICT ON DELETE RESTRICT',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt'
      AND CONSTRAINT_NAME = 'fk_receipt_booking_player'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE receipt ADD CONSTRAINT fk_receipt_booking_player FOREIGN KEY (booking_player_id) REFERENCES booking_player (id) ON UPDATE RESTRICT ON DELETE RESTRICT',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt'
      AND CONSTRAINT_NAME = 'fk_receipt_payment'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE receipt ADD CONSTRAINT fk_receipt_payment FOREIGN KEY (payment_id) REFERENCES payment (id) ON UPDATE RESTRICT ON DELETE RESTRICT',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt_item'
      AND CONSTRAINT_NAME = 'fk_receipt_item_receipt'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE receipt_item ADD CONSTRAINT fk_receipt_item_receipt FOREIGN KEY (receipt_id) REFERENCES receipt (id) ON UPDATE RESTRICT ON DELETE RESTRICT',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;
