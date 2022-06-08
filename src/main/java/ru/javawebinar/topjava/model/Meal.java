package ru.javawebinar.topjava.model;

import java.time.*;
import java.time.temporal.*;

public class Meal {
    private Integer id;

    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    public Meal() {
        this(null, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), null, 0);
    }

    public Meal(Integer id, LocalDateTime dateTime, String description, int calories) {
        this.id = id;
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }
}
