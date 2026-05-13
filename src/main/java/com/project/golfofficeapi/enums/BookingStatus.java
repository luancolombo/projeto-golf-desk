package com.project.golfofficeapi.enums;

import java.util.Locale;

public enum BookingStatus {
    CREATED,
    CONFIRMED,
    CANCELLED;

    public static BookingStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return CREATED;
        }

        return BookingStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
