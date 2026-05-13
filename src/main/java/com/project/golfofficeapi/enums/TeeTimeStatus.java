package com.project.golfofficeapi.enums;

import java.util.Locale;

public enum TeeTimeStatus {
    AVAILABLE,
    FULL,
    CANCELLED;

    public static TeeTimeStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return AVAILABLE;
        }

        return TeeTimeStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
