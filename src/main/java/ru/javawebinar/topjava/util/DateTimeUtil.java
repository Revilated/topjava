package ru.javawebinar.topjava.util;

import java.time.*;
import java.time.format.*;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static <T> boolean isBetweenHalfOpen(Comparable<T> value, T leftBoundary, T rightBoundary) {
        return (leftBoundary == null || value.compareTo(leftBoundary) >= 0)
                && (rightBoundary == null || value.compareTo(rightBoundary) < 0);
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }
}

