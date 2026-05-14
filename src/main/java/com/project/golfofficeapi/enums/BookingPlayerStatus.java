package com.project.golfofficeapi.enums;

import java.util.Locale;

public enum BookingPlayerStatus {
    ACTIVE,
    REFUNDED,
    CANCELLED;

    public static BookingPlayerStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }

        return BookingPlayerStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
