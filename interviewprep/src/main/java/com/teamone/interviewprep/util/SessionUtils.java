package com.teamone.interviewprep.util;

import java.time.Duration;
import java.time.LocalDateTime;

public final class SessionUtils {

    private SessionUtils() {
    }

    public static boolean isSessionExpired(LocalDateTime expiresAt) {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public static long getRemainingMinutes(LocalDateTime expiresAt) {
        if (expiresAt == null) {
            return 0;
        }

        if (expiresAt.isBefore(LocalDateTime.now())) {
            return 0;
        }

        return Duration.between(LocalDateTime.now(), expiresAt).toMinutes();
    }

    public static LocalDateTime createExpirationTime(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }
}