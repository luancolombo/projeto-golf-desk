package com.project.golfofficeapi.enums;

import java.util.Locale;

public enum PaymentMethod {
    CASH,
    CARD,
    MBWAY,
    TRANSFER;

    public static PaymentMethod fromString(String value) {
        if (value == null || value.isBlank()) {
            return CARD;
        }

        return PaymentMethod.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
