package com.project.golfofficeapi.enums;

import java.util.Locale;

public enum UserRole {

    RECEPTIONIST,
    MANAGER;

    public static UserRole fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("User role is required");
        }

        String normalizedValue = value.trim().toUpperCase(Locale.ROOT);

        if ("RECEPTION".equals(normalizedValue)) {
            return RECEPTIONIST;
        }

        return UserRole.valueOf(normalizedValue);
    }
}
