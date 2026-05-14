CREATE TABLE IF NOT EXISTS cash_register_closure (
    id BIGINT NOT NULL AUTO_INCREMENT,
    business_date DATE NOT NULL,
    opened_at DATETIME(6) NOT NULL,
    closed_at DATETIME(6) NULL,
    status VARCHAR(30) NOT NULL,
    closed_by BIGINT NULL,
    cash_total DECIMAL(10, 2) NOT NULL,
    card_total DECIMAL(10, 2) NOT NULL,
    mbway_total DECIMAL(10, 2) NOT NULL,
    transfer_total DECIMAL(10, 2) NOT NULL,
    gross_total DECIMAL(10, 2) NOT NULL,
    refunded_total DECIMAL(10, 2) NOT NULL,
    net_total DECIMAL(10, 2) NOT NULL,
    paid_payments_count INT NOT NULL,
    refunded_payments_count INT NOT NULL,
    issued_receipts_count INT NOT NULL,
    cancelled_receipts_count INT NOT NULL,
    pending_bookings_count INT NOT NULL,
    unreturned_rentals_count INT NOT NULL,
    notes VARCHAR(255) NULL,
    CONSTRAINT pk_cash_register_closure PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS cash_register_closure_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cash_register_closure_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    reference_id BIGINT NULL,
    reference_code VARCHAR(60) NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(30) NULL,
    payment_status VARCHAR(30) NULL,
    occurred_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_cash_register_closure_item PRIMARY KEY (id),
    CONSTRAINT fk_cash_register_closure_item_closure
        FOREIGN KEY (cash_register_closure_id)
        REFERENCES cash_register_closure (id)
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
);

CREATE INDEX idx_cash_register_closure_business_date_status ON cash_register_closure (business_date, status);
CREATE INDEX idx_cash_register_closure_status ON cash_register_closure (status);
CREATE INDEX idx_cash_register_closure_item_closure ON cash_register_closure_item (cash_register_closure_id);
CREATE INDEX idx_cash_register_closure_item_type ON cash_register_closure_item (type);
