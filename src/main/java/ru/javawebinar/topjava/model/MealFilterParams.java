/*
 * (C) 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.model;

import java.time.*;

/**
 * @author revilated
 */
public class MealFilterParams {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public MealFilterParams() {
        this(null, null, null, null);
    }

    public MealFilterParams(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
