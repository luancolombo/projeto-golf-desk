package com.project.golfofficeapi.enums;

import java.util.Locale;

public enum CashRegisterClosureStatus {

    OPEN,
    CLOSED,
    CANCELLED;

    public static CashRegisterClosureStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return OPEN;
        }

        return CashRegisterClosureStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
