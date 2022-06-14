package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.repository.*;
import ru.javawebinar.topjava.util.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private final Map<Integer, Meal> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(this::save);
    }

    @Override
    public Meal save(Meal meal) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.put(meal.getId(), meal);
            return meal;
        }
        // handle case: update, but not present in storage
        if (isNotBelongsToUser(meal.getId(), meal.getUserId())) {
            return null;
        }
        return repository.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(int mealId, int userId) {
        return isNotBelongsToUser(mealId, userId) || repository.remove(mealId) != null;
    }

    @Override
    public Meal get(int mealId, int userId) {
        return isNotBelongsToUser(mealId, userId) ? null : repository.get(mealId);
    }

    @Override
    public Collection<Meal> getAll(int userId) {
        return repository.values().stream()
                .filter(m -> m.getUserId() == userId)
                .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Meal> getAllBetweenHalfOpen(int userId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return getAll(userId).stream()
                .filter(m -> startDateTime == null
                        || endDateTime == null
                        || DateTimeUtil.isBetweenHalfOpen(m.getDateTime(), startDateTime, endDateTime))
                .collect(Collectors.toList());
    }

    private boolean isNotBelongsToUser(int mealId, int userId) {
        Meal meal = repository.get(mealId);
        return meal == null || meal.getUserId() != userId;
    }
}

