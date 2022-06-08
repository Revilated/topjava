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

    Meal find(int id);

    Meal update(Meal meal);

    void delete(int id);

    Meal add(Meal meal);
}
