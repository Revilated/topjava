/*
 * (C) 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.repo;

import ru.javawebinar.topjava.model.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * @author revilated
 */
public class InMemoryMealRepository implements MealRepository {

    private final AtomicInteger nextId = new AtomicInteger();
    private final Map<Integer, Meal> mealsById = new ConcurrentHashMap<>();

    public InMemoryMealRepository() {
        addMeals(
                new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new Meal(null, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );
    }

    private void addMeals(Meal... meals) {
        for (Meal meal : meals) {
            add(meal);
        }
    }

    @Override
    public List<Meal> findAll() {
        return new ArrayList<>(mealsById.values());
    }

    @Override
    public Meal find(int id) {
        return mealsById.get(id);
    }

    @Override
    public Meal update(Meal meal) {
        return mealsById.replace(meal.getId(), meal);
    }

    @Override
    public void delete(int id) {
        mealsById.remove(id);
    }

    @Override
    public Meal add(Meal meal) {
        meal.setId(nextId.getAndIncrement());
        mealsById.put(meal.getId(), meal);
        return meal;
    }
}
