/*
 * (C) 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.repo;

import ru.javawebinar.topjava.model.*;

import java.time.*;
import java.util.*;

/**
 * @author revilated
 */
public class StaticMealRepository implements MealRepository {

    private static final List<Meal> ANY_MEALS = Collections.unmodifiableList(Arrays.asList(
            new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
            new Meal(1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
            new Meal(2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
            new Meal(3, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
            new Meal(4, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
            new Meal(5, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
            new Meal(6, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
    ));

    @Override
    public List<Meal> findAll() {
        return ANY_MEALS;
    }

    @Override
    public Optional<Meal> find(int id) {
        return ANY_MEALS.stream().filter(m -> m.getId() == id).findFirst();
    }

    @Override
    public void update(Meal meal) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(Meal meal) {
        throw new UnsupportedOperationException();
    }
}
