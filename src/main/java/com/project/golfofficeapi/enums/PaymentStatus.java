package com.project.golfofficeapi.enums;

import java.util.Locale;

public enum PaymentStatus {
    PENDING,
    PAID,
    REFUNDED,
    CANCELLED;

    public static PaymentStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return PAID;
        }

        return PaymentStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
