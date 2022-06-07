/*
 * (C) 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.repo;

import ru.javawebinar.topjava.model.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * @author revilated
 */
public class InMemoryMealRepository implements MealRepository {

    private final AtomicInteger nextId = new AtomicInteger();

    private final Map<Integer, Meal> mealsById = new ConcurrentHashMap<>();

    @Override
    public List<Meal> findAll() {
        return new ArrayList<>(mealsById.values());
    }

    @Override
    public Optional<Meal> find(int id) {
        return Optional.ofNullable(mealsById.get(id));
    }

    @Override
    public Optional<Meal> update(Meal meal) {
        Meal old = mealsById.replace(meal.getId(), meal);
        if (old == null) {
            return Optional.empty();
        } else {
            return Optional.of(meal);
        }
    }

    @Override
    public void delete(int id) {
        mealsById.remove(id);
    }

    @Override
    public Optional<Meal> add(Meal meal) {
        meal.setId(nextId.getAndIncrement());
        Meal old = mealsById.putIfAbsent(meal.getId(), meal);
        if (old == null) {
            return Optional.of(meal);
        } else {
            return Optional.empty();
        }
    }
}
