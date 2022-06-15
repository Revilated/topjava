package ru.javawebinar.topjava.service;

import org.springframework.stereotype.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.repository.*;

import java.time.*;
import java.util.*;

import static ru.javawebinar.topjava.util.ValidationUtil.*;
@Service
public class MealService {

    private final MealRepository repository;

    public MealService(MealRepository repository) {
        this.repository = repository;
    }

    public Meal create(Meal meal) {
        return checkNotFoundWithId(repository.save(meal), meal.getId());
    }

    public void update(Meal meal) {
        checkNotFoundWithId(repository.save(meal), meal.getId());
    }

    public void delete(int mealId, int userId) {
        checkNotFoundWithId(repository.delete(mealId, userId), mealId);
    }

    public Meal get(int mealId, int userId) {
        return checkNotFoundWithId(repository.get(mealId, userId), mealId);
    }

    public Collection<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    public Collection<Meal> getAllFiltered(int userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDt = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime endDt = endDate == null ? null : endDate.atStartOfDay().plusDays(1);
        return repository.getAllBetweenHalfOpen(userId, startDt, endDt);
    }
}