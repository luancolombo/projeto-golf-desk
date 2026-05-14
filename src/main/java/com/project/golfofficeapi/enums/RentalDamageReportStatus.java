package com.project.golfofficeapi.enums;

import java.util.Locale;

public enum RentalDamageReportStatus {
    OPEN,
    RESOLVED,
    CANCELLED;

    public static RentalDamageReportStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return OPEN;
        }

        return RentalDamageReportStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
