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
        MealsUtil.meals.forEach(meal -> save(meal, meal.getUserId()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.compute(userId, (key, map) -> {
                Map<Integer, Meal> result = map == null ? new ConcurrentHashMap<>() : map;
                result.put(meal.getId(), meal);
                return result;
            });
            return meal;
        }
        // handle case: update, but not present in storage
        if (isBelongsToUser(meal.getId(), userId)) {
            meal.setUserId(userId);
        } else {
            return null;
        }
        return repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int mealId, int userId) {
        return isBelongsToUser(mealId, userId) && repository.get(userId).remove(mealId) != null;
    }

    @Override
    public Meal get(int mealId, int userId) {
        if (!repository.containsKey(userId)) {
            return null;
        }
        Meal meal = repository.get(userId).get(mealId);
        return meal != null && meal.getUserId() == userId ? meal : null;
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
        if (!repository.containsKey(userId)) {
            return Collections.emptyList();
        }
        return repository.get(userId).values().stream()
                .filter(m -> m.getUserId() == userId && filter.test(m))
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    private boolean isBelongsToUser(int mealId, int userId) {
        if (!repository.containsKey(userId)) {
            return false;
        }
        Meal meal = repository.get(userId).get(mealId);
        return meal != null && meal.getUserId() == userId;
    }
}

