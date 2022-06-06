/*
 * (C) 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.repo;

import ru.javawebinar.topjava.model.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * @author revilated
 */
public class InMemoryMealRepository implements MealRepository {

    private final Map<Integer, Meal> mealsById;

    public InMemoryMealRepository(List<Meal> initial) {
        this.mealsById = initial.stream().collect(Collectors.toMap(
                Meal::getId,
                Function.identity(),
                (m1, m2) -> {
                    throw new IllegalStateException("Duplicated meal IDs");
                },
                () -> Collections.synchronizedMap(new LinkedHashMap<>())
        ));
    }

    @Override
    public List<Meal> findAll() {
        return new ArrayList<>(mealsById.values());
    }

    @Override
    public Optional<Meal> find(int id) {
        return Optional.ofNullable(mealsById.get(id));
    }

    @Override
    public void update(Meal meal) {
        mealsById.replace(meal.getId(), meal);
    }

    @Override
    public void delete(int id) {
        mealsById.remove(id);
    }

    @Override
    public void add(Meal meal) {
        mealsById.putIfAbsent(meal.getId(), meal);
    }
}
