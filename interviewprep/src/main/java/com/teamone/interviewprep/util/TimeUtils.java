package com.teamone.interviewprep.util;

import java.time.Duration;
import java.time.LocalDateTime;

public final class TimeUtils {

    private TimeUtils() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static LocalDateTime plusMinutes(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }

    public static LocalDateTime plusHours(int hours) {
        return LocalDateTime.now().plusHours(hours);
    }

    public static boolean isBeforeNow(LocalDateTime time) {
        return time != null && time.isBefore(LocalDateTime.now());
    }

    public static boolean isAfterNow(LocalDateTime time) {
        return time != null && time.isAfter(LocalDateTime.now());
    }

    public static long minutesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        return Duration.between(start, end).toMinutes();
    }
}