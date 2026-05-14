CREATE TABLE IF NOT EXISTS rental_damage_report (
    id BIGINT NOT NULL AUTO_INCREMENT,
    rental_transaction_id BIGINT NULL,
    rental_item_id BIGINT NULL,
    description VARCHAR(500) NOT NULL,
    status VARCHAR(30) NOT NULL,
    reported_at DATETIME NOT NULL,
    resolved_at DATETIME NULL,
    reported_by BIGINT NULL,
    resolved_by BIGINT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_rental_damage_report_transaction
        FOREIGN KEY (rental_transaction_id) REFERENCES rental_transaction(id),
    CONSTRAINT fk_rental_damage_report_item
        FOREIGN KEY (rental_item_id) REFERENCES rental_item(id)
);

CREATE INDEX idx_rental_damage_report_status
    ON rental_damage_report(status);

CREATE INDEX idx_rental_damage_report_rental_item
    ON rental_damage_report(rental_item_id);

CREATE INDEX idx_rental_damage_report_rental_transaction
    ON rental_damage_report(rental_transaction_id);
