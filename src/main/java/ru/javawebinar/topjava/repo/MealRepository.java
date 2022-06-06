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
}
