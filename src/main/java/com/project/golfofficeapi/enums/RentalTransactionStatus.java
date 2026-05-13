package com.project.golfofficeapi.enums;

import java.util.Locale;

public enum RentalTransactionStatus {
    RENTED,
    RETURNED,
    LOST,
    DAMAGED,
    CANCELLED;

    public static RentalTransactionStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return RENTED;
        }

        return RentalTransactionStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    public boolean reservesStock() {
        return this == RENTED || this == LOST || this == DAMAGED;
    }
}
