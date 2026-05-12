DELETE ri
FROM receipt_item ri
LEFT JOIN receipt r ON r.id = ri.receipt_id
WHERE ri.receipt_id IS NULL OR r.id IS NULL;

DELETE r
FROM receipt r
LEFT JOIN booking b ON b.id = r.booking_id
LEFT JOIN booking_player bp ON bp.id = r.booking_player_id
LEFT JOIN payment p ON p.id = r.payment_id
WHERE r.booking_id IS NULL
   OR r.booking_player_id IS NULL
   OR r.payment_id IS NULL
   OR b.id IS NULL
   OR bp.id IS NULL
   OR p.id IS NULL;

DELETE p
FROM payment p
LEFT JOIN booking b ON b.id = p.booking_id
LEFT JOIN booking_player bp ON bp.id = p.booking_player_id
WHERE p.booking_id IS NULL
   OR p.booking_player_id IS NULL
   OR b.id IS NULL
   OR bp.id IS NULL;

DELETE rt
FROM rental_transaction rt
LEFT JOIN booking b ON b.id = rt.booking_id
LEFT JOIN booking_player bp ON bp.id = rt.booking_player_id
LEFT JOIN rental_item ri ON ri.id = rt.rental_item_id
WHERE rt.booking_id IS NULL
   OR rt.booking_player_id IS NULL
   OR rt.rental_item_id IS NULL
   OR b.id IS NULL
   OR bp.id IS NULL
   OR ri.id IS NULL;

DELETE bp
FROM booking_player bp
LEFT JOIN booking b ON b.id = bp.booking_id
LEFT JOIN player p ON p.id = bp.player_id
WHERE bp.booking_id IS NULL
   OR bp.player_id IS NULL
   OR b.id IS NULL
   OR p.id IS NULL;

DELETE b
FROM booking b
LEFT JOIN tee_time tt ON tt.id = b.tee_time_id
WHERE b.tee_time_id IS NULL OR tt.id IS NULL;

UPDATE player
SET full_name = CONCAT('Player ', id)
WHERE full_name IS NULL OR full_name = '';

UPDATE player
SET tax_number = CONCAT('missing-tax-', id)
WHERE tax_number IS NULL OR tax_number = '';

UPDATE player
SET email = CONCAT('missing.email.', id, '@golf.local')
WHERE email IS NULL OR email = '';

UPDATE player
SET phone = CONCAT('missing-', id)
WHERE phone IS NULL OR phone = '';

UPDATE player
SET hand_cap = 'N/A'
WHERE hand_cap IS NULL OR hand_cap = '';

UPDATE player
SET notes = ''
WHERE notes IS NULL;

UPDATE tee_time
SET play_date = DATE_ADD(CURRENT_DATE(), INTERVAL id DAY)
WHERE play_date IS NULL;

UPDATE tee_time
SET start_time = '07:00:00'
WHERE start_time IS NULL;

UPDATE tee_time
SET max_players = 4
WHERE max_players IS NULL;

UPDATE tee_time
SET booked_players = 0
WHERE booked_players IS NULL;

UPDATE tee_time
SET status = 'AVAILABLE'
WHERE status IS NULL OR status = '';

UPDATE tee_time
SET base_green_fee = 0.00
WHERE base_green_fee IS NULL;

UPDATE booking
SET code = CONCAT('BK-MIGRATED-', id)
WHERE code IS NULL OR code = '';

UPDATE booking
SET created_at = CURRENT_TIMESTAMP(6)
WHERE created_at IS NULL;

UPDATE booking
SET status = 'CREATED'
WHERE status IS NULL OR status = '';

UPDATE booking
SET total_amount = 0.00
WHERE total_amount IS NULL;

UPDATE booking_player
SET green_fee_amount = 0.00
WHERE green_fee_amount IS NULL;

UPDATE booking_player
SET checked_in = false
WHERE checked_in IS NULL;

UPDATE rental_item
SET name = CONCAT('Rental item ', id)
WHERE name IS NULL OR name = '';

UPDATE rental_item
SET type = 'GENERAL'
WHERE type IS NULL OR type = '';

UPDATE rental_item
SET total_stock = 0
WHERE total_stock IS NULL;

UPDATE rental_item
SET available_stock = 0
WHERE available_stock IS NULL;

UPDATE rental_item
SET rental_price = 0.00
WHERE rental_price IS NULL;

UPDATE rental_item
SET active = true
WHERE active IS NULL;

UPDATE rental_transaction
SET quantity = 1
WHERE quantity IS NULL;

UPDATE rental_transaction
SET status = 'RENTED'
WHERE status IS NULL OR status = '';

UPDATE rental_transaction
SET unit_price = 0.00
WHERE unit_price IS NULL;

UPDATE rental_transaction
SET total_price = 0.00
WHERE total_price IS NULL;

UPDATE payment
SET amount = 0.00
WHERE amount IS NULL;

UPDATE payment
SET method = 'CASH'
WHERE method IS NULL OR method = '';

UPDATE payment
SET status = 'PAID'
WHERE status IS NULL OR status = '';

UPDATE receipt
SET receipt_number = CONCAT('RC-MIGRATED-', id)
WHERE receipt_number IS NULL OR receipt_number = '';

UPDATE receipt
SET player_name_snapshot = CONCAT('Player ', booking_player_id)
WHERE player_name_snapshot IS NULL OR player_name_snapshot = '';

UPDATE receipt
SET booking_code_snapshot = CONCAT('Booking ', booking_id)
WHERE booking_code_snapshot IS NULL OR booking_code_snapshot = '';

UPDATE receipt
SET play_date = CURRENT_DATE()
WHERE play_date IS NULL;

UPDATE receipt
SET start_time = '07:00:00'
WHERE start_time IS NULL;

UPDATE receipt
SET green_fee_amount = 0.00
WHERE green_fee_amount IS NULL;

UPDATE receipt
SET rental_amount = 0.00
WHERE rental_amount IS NULL;

UPDATE receipt
SET total_amount = 0.00
WHERE total_amount IS NULL;

UPDATE receipt
SET payment_method = 'CASH'
WHERE payment_method IS NULL OR payment_method = '';

UPDATE receipt
SET payment_status = 'PAID'
WHERE payment_status IS NULL OR payment_status = '';

UPDATE receipt
SET issued_at = CURRENT_TIMESTAMP(6)
WHERE issued_at IS NULL;

UPDATE receipt
SET cancelled = false
WHERE cancelled IS NULL;

UPDATE receipt_item
SET description = 'Receipt item'
WHERE description IS NULL OR description = '';

UPDATE receipt_item
SET quantity = 1
WHERE quantity IS NULL;

UPDATE receipt_item
SET unit_price = 0.00
WHERE unit_price IS NULL;

UPDATE receipt_item
SET total_price = 0.00
WHERE total_price IS NULL;

ALTER TABLE player
    MODIFY full_name VARCHAR(50) NOT NULL,
    MODIFY tax_number VARCHAR(50) NOT NULL,
    MODIFY email VARCHAR(50) NOT NULL,
    MODIFY phone VARCHAR(50) NOT NULL,
    MODIFY hand_cap VARCHAR(50) NOT NULL,
    MODIFY member BOOLEAN NOT NULL,
    MODIFY notes VARCHAR(100) NOT NULL;

ALTER TABLE tee_time
    MODIFY play_date DATE NOT NULL,
    MODIFY start_time TIME(6) NOT NULL,
    MODIFY max_players INT NOT NULL,
    MODIFY booked_players INT NOT NULL,
    MODIFY status VARCHAR(30) NOT NULL,
    MODIFY base_green_fee DECIMAL(10, 2) NOT NULL;

ALTER TABLE booking
    MODIFY code VARCHAR(40) NOT NULL,
    MODIFY created_at DATETIME(6) NOT NULL,
    MODIFY status VARCHAR(30) NOT NULL,
    MODIFY total_amount DECIMAL(10, 2) NOT NULL,
    MODIFY created_by BIGINT NULL,
    MODIFY tee_time_id BIGINT NOT NULL;

ALTER TABLE booking_player
    MODIFY booking_id BIGINT NOT NULL,
    MODIFY player_id BIGINT NOT NULL,
    MODIFY green_fee_amount DECIMAL(10, 2) NOT NULL,
    MODIFY checked_in BOOLEAN NOT NULL;

ALTER TABLE rental_item
    MODIFY name VARCHAR(50) NOT NULL,
    MODIFY type VARCHAR(50) NOT NULL,
    MODIFY total_stock INT NOT NULL,
    MODIFY available_stock INT NOT NULL,
    MODIFY rental_price DECIMAL(10, 2) NOT NULL,
    MODIFY active BOOLEAN NOT NULL;

ALTER TABLE rental_transaction
    MODIFY booking_id BIGINT NOT NULL,
    MODIFY booking_player_id BIGINT NOT NULL,
    MODIFY rental_item_id BIGINT NOT NULL,
    MODIFY quantity INT NOT NULL,
    MODIFY status VARCHAR(30) NOT NULL,
    MODIFY unit_price DECIMAL(10, 2) NOT NULL,
    MODIFY total_price DECIMAL(10, 2) NOT NULL;

ALTER TABLE payment
    MODIFY booking_id BIGINT NOT NULL,
    MODIFY booking_player_id BIGINT NOT NULL,
    MODIFY amount DECIMAL(10, 2) NOT NULL,
    MODIFY method VARCHAR(30) NOT NULL,
    MODIFY status VARCHAR(30) NOT NULL,
    MODIFY paid_at DATETIME(6) NULL;

ALTER TABLE receipt
    MODIFY receipt_number VARCHAR(40) NOT NULL,
    MODIFY booking_id BIGINT NOT NULL,
    MODIFY booking_player_id BIGINT NOT NULL,
    MODIFY payment_id BIGINT NOT NULL,
    MODIFY player_name_snapshot VARCHAR(100) NOT NULL,
    MODIFY player_tax_number_snapshot VARCHAR(50) NULL,
    MODIFY booking_code_snapshot VARCHAR(40) NOT NULL,
    MODIFY play_date DATE NOT NULL,
    MODIFY start_time TIME(6) NOT NULL,
    MODIFY green_fee_amount DECIMAL(10, 2) NOT NULL,
    MODIFY rental_amount DECIMAL(10, 2) NOT NULL,
    MODIFY total_amount DECIMAL(10, 2) NOT NULL,
    MODIFY payment_method VARCHAR(30) NOT NULL,
    MODIFY payment_status VARCHAR(30) NOT NULL,
    MODIFY issued_at DATETIME(6) NOT NULL,
    MODIFY cancelled BOOLEAN NOT NULL,
    MODIFY cancelled_at DATETIME(6) NULL,
    MODIFY cancellation_reason VARCHAR(255) NULL;

ALTER TABLE receipt_item
    MODIFY receipt_id BIGINT NOT NULL,
    MODIFY description VARCHAR(120) NOT NULL,
    MODIFY quantity INT NOT NULL,
    MODIFY unit_price DECIMAL(10, 2) NOT NULL,
    MODIFY total_price DECIMAL(10, 2) NOT NULL;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'player'
      AND CONSTRAINT_NAME = 'uk_player_tax_number'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE player ADD CONSTRAINT uk_player_tax_number UNIQUE (tax_number)',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'player'
      AND CONSTRAINT_NAME = 'uk_player_email'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE player ADD CONSTRAINT uk_player_email UNIQUE (email)',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'player'
      AND CONSTRAINT_NAME = 'uk_player_phone'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE player ADD CONSTRAINT uk_player_phone UNIQUE (phone)',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'booking'
      AND CONSTRAINT_NAME = 'uk_booking_code'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE booking ADD CONSTRAINT uk_booking_code UNIQUE (code)',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'tee_time'
      AND CONSTRAINT_NAME = 'uk_tee_time_play_date_start_time'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE tee_time ADD CONSTRAINT uk_tee_time_play_date_start_time UNIQUE (play_date, start_time)',
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
      AND CONSTRAINT_NAME = 'uk_receipt_receipt_number'
);
SET @sql = IF(
    @constraint_exists = 0,
    'ALTER TABLE receipt ADD CONSTRAINT uk_receipt_receipt_number UNIQUE (receipt_number)',
    'SELECT 1'
);
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'player'
      AND INDEX_NAME = 'idx_player_full_name'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_player_full_name ON player (full_name)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'booking'
      AND INDEX_NAME = 'idx_booking_tee_time_status'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_booking_tee_time_status ON booking (tee_time_id, status)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'booking_player'
      AND INDEX_NAME = 'idx_booking_player_booking_player'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_booking_player_booking_player ON booking_player (booking_id, player_id)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'rental_item'
      AND INDEX_NAME = 'idx_rental_item_active_type'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_rental_item_active_type ON rental_item (active, type)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'rental_transaction'
      AND INDEX_NAME = 'idx_rental_transaction_booking_status'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_rental_transaction_booking_status ON rental_transaction (booking_id, status)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'rental_transaction'
      AND INDEX_NAME = 'idx_rental_transaction_booking_player_status'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_rental_transaction_booking_player_status ON rental_transaction (booking_player_id, status)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'payment'
      AND INDEX_NAME = 'idx_payment_booking_player_status'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_payment_booking_player_status ON payment (booking_player_id, status)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'payment'
      AND INDEX_NAME = 'idx_payment_booking_status'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_payment_booking_status ON payment (booking_id, status)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt'
      AND INDEX_NAME = 'idx_receipt_payment_cancelled'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_receipt_payment_cancelled ON receipt (payment_id, cancelled)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt'
      AND INDEX_NAME = 'idx_receipt_booking_player_cancelled'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_receipt_booking_player_cancelled ON receipt (booking_player_id, cancelled)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt'
      AND INDEX_NAME = 'idx_receipt_booking_cancelled'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_receipt_booking_cancelled ON receipt (booking_id, cancelled)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @index_exists = (
    SELECT COUNT(*)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'receipt_item'
      AND INDEX_NAME = 'idx_receipt_item_receipt'
);
SET @sql = IF(@index_exists = 0, 'CREATE INDEX idx_receipt_item_receipt ON receipt_item (receipt_id)', 'SELECT 1');
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;
