package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.repository.*;
import ru.javawebinar.topjava.util.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> save(meal, 1));
        MealsUtil.anotherMeals.forEach(meal -> save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.computeIfAbsent(userId, key -> new ConcurrentHashMap<>());
            repository.get(userId).put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null ? null : meals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int mealId, int userId) {
        Map<Integer, Meal> meals = repository.get(userId);
        return meals != null && meals.remove(mealId) != null;
    }

    @Override
    public Meal get(int mealId, int userId) {
        Map<Integer, Meal> meals = repository.get(userId);
        return meals == null ? null : meals.get(mealId);
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getAllFiltered(userId, m -> true);
    }

    @Override
    public List<Meal> getAllBetweenHalfOpen(int userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return getAllFiltered(userId, m -> DateTimeUtil.isBetweenHalfOpen(m.getDateTime(), startDateTime, endDateTime));
    }

    private List<Meal> getAllFiltered(int userId, Predicate<Meal> filter) {
        Map<Integer, Meal> meals = repository.get(userId);
        if (meals == null) {
            return Collections.emptyList();
        }
        return meals.values().stream()
                .filter(filter)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }
}

