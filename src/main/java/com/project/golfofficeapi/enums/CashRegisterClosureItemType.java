package com.project.golfofficeapi.enums;

import java.util.Locale;

public enum CashRegisterClosureItemType {

    PAYMENT,
    REFUND,
    RECEIPT,
    CANCELLED_RECEIPT,
    PENDING_BOOKING,
    UNRETURNED_RENTAL;

    public static CashRegisterClosureItemType fromString(String value) {
        if (value == null || value.isBlank()) {
            return PAYMENT;
        }

        return CashRegisterClosureItemType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
