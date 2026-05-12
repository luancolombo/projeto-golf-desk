SET @duplicate_unique_indexes = (
    SELECT GROUP_CONCAT(CONCAT('DROP INDEX `', INDEX_NAME, '`') SEPARATOR ', ')
    FROM (
        SELECT INDEX_NAME, GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS indexed_columns
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'player'
          AND NON_UNIQUE = 0
          AND INDEX_NAME NOT IN ('PRIMARY', 'uk_player_tax_number')
        GROUP BY INDEX_NAME
        HAVING indexed_columns = 'tax_number'
    ) duplicate_indexes
);
SET @sql = IF(@duplicate_unique_indexes IS NULL, 'SELECT 1', CONCAT('ALTER TABLE player ', @duplicate_unique_indexes));
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @duplicate_unique_indexes = (
    SELECT GROUP_CONCAT(CONCAT('DROP INDEX `', INDEX_NAME, '`') SEPARATOR ', ')
    FROM (
        SELECT INDEX_NAME, GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS indexed_columns
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'player'
          AND NON_UNIQUE = 0
          AND INDEX_NAME NOT IN ('PRIMARY', 'uk_player_email')
        GROUP BY INDEX_NAME
        HAVING indexed_columns = 'email'
    ) duplicate_indexes
);
SET @sql = IF(@duplicate_unique_indexes IS NULL, 'SELECT 1', CONCAT('ALTER TABLE player ', @duplicate_unique_indexes));
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @duplicate_unique_indexes = (
    SELECT GROUP_CONCAT(CONCAT('DROP INDEX `', INDEX_NAME, '`') SEPARATOR ', ')
    FROM (
        SELECT INDEX_NAME, GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS indexed_columns
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'player'
          AND NON_UNIQUE = 0
          AND INDEX_NAME NOT IN ('PRIMARY', 'uk_player_phone')
        GROUP BY INDEX_NAME
        HAVING indexed_columns = 'phone'
    ) duplicate_indexes
);
SET @sql = IF(@duplicate_unique_indexes IS NULL, 'SELECT 1', CONCAT('ALTER TABLE player ', @duplicate_unique_indexes));
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @duplicate_unique_indexes = (
    SELECT GROUP_CONCAT(CONCAT('DROP INDEX `', INDEX_NAME, '`') SEPARATOR ', ')
    FROM (
        SELECT INDEX_NAME, GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS indexed_columns
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'booking'
          AND NON_UNIQUE = 0
          AND INDEX_NAME NOT IN ('PRIMARY', 'uk_booking_code')
        GROUP BY INDEX_NAME
        HAVING indexed_columns = 'code'
    ) duplicate_indexes
);
SET @sql = IF(@duplicate_unique_indexes IS NULL, 'SELECT 1', CONCAT('ALTER TABLE booking ', @duplicate_unique_indexes));
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @duplicate_unique_indexes = (
    SELECT GROUP_CONCAT(CONCAT('DROP INDEX `', INDEX_NAME, '`') SEPARATOR ', ')
    FROM (
        SELECT INDEX_NAME, GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS indexed_columns
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'tee_time'
          AND NON_UNIQUE = 0
          AND INDEX_NAME NOT IN ('PRIMARY', 'uk_tee_time_play_date_start_time')
        GROUP BY INDEX_NAME
        HAVING indexed_columns = 'play_date,start_time'
    ) duplicate_indexes
);
SET @sql = IF(@duplicate_unique_indexes IS NULL, 'SELECT 1', CONCAT('ALTER TABLE tee_time ', @duplicate_unique_indexes));
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;

SET @duplicate_unique_indexes = (
    SELECT GROUP_CONCAT(CONCAT('DROP INDEX `', INDEX_NAME, '`') SEPARATOR ', ')
    FROM (
        SELECT INDEX_NAME, GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS indexed_columns
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'receipt'
          AND NON_UNIQUE = 0
          AND INDEX_NAME NOT IN ('PRIMARY', 'uk_receipt_receipt_number')
        GROUP BY INDEX_NAME
        HAVING indexed_columns = 'receipt_number'
    ) duplicate_indexes
);
SET @sql = IF(@duplicate_unique_indexes IS NULL, 'SELECT 1', CONCAT('ALTER TABLE receipt ', @duplicate_unique_indexes));
PREPARE statement FROM @sql;
EXECUTE statement;
DEALLOCATE PREPARE statement;
