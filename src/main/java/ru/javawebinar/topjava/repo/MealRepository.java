/*
 * (C) 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.repo;

import ru.javawebinar.topjava.model.*;

import java.util.*;

/**
 * @author revilated
 */
public interface MealRepository {
    List<Meal> findAll();

    Optional<Meal> find(int id);

    Optional<Meal> update(Meal meal);

    void delete(int id);

    Optional<Meal> add(Meal meal);
}
