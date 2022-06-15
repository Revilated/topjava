package ru.javawebinar.topjava.util;

import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static <T extends Temporal> boolean isBetweenHalfOpen(Comparable<T> temporal, T startTemporal,
                                                                 T endTemporal) {
        return startTemporal == null && endTemporal == null
                || startTemporal == null && temporal.compareTo(endTemporal) < 0
                || endTemporal == null && temporal.compareTo(startTemporal) >= 0
                || startTemporal != null && endTemporal != null
                && temporal.compareTo(startTemporal) >= 0 && temporal.compareTo(endTemporal) < 0;
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }
}

