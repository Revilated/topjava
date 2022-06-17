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

    public Meal create(Meal meal, int userId) {
        return repository.save(meal, userId);
    }

    public void update(Meal meal, int userId) {
        checkNotFoundWithId(repository.save(meal, userId), meal.getId());
    }

    public void delete(int mealId, int userId) {
        checkNotFoundWithId(repository.delete(mealId, userId), mealId);
    }

    public Meal get(int mealId, int userId) {
        return checkNotFoundWithId(repository.get(mealId, userId), mealId);
    }

    public List<Meal> getAll(int userId) {
        return repository.getAll(userId);
    }

    public List<Meal> getAllFiltered(int userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate == null ? null : endDate.atStartOfDay().plusDays(1);
        return repository.getAllBetweenHalfOpen(userId, startDateTime, endDateTime);
    }
}