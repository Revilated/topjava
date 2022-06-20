/*
 * (C) 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

/**
 * @author revilated
 */
public class MealTestData {

    public static final int USER_ID = START_SEQ;
    public static final int ANOTHER_USER_ID = START_SEQ + 1;
    public static final int START_MEAL_ID = START_SEQ + 3;
    public static final int MEAL_ID = START_MEAL_ID + 1;
    public static Map<Integer, Map<Integer, Meal>> mealsByUsers = new HashMap<>();
    public static List<Meal> filteredMeals = new ArrayList<>();
    public static final LocalDate START_DATE = LocalDate.of(2020, Month.JANUARY, 30);
    public static final LocalDate END_DATE = LocalDate.of(2020, Month.JANUARY, 30);

    static {
        mealsByUsers.put(USER_ID, new LinkedHashMap<>());
        Map<Integer, Meal> meals = mealsByUsers.get(USER_ID);
        int endId = START_MEAL_ID + 6;
        meals.put(endId, new Meal(endId, LocalDateTime.of(
                2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
        meals.put(endId - 1, new Meal(endId - 1, LocalDateTime.of(
                2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        meals.put(endId - 2, new Meal(endId - 2, LocalDateTime.of(
                2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        meals.put(endId - 3, new Meal(endId - 3, LocalDateTime.of(
                2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        meals.put(endId - 4, new Meal(endId - 4, LocalDateTime.of(
                2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        meals.put(endId - 5, new Meal(endId - 5, LocalDateTime.of(
                2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        meals.put(endId - 6, new Meal(endId - 6, LocalDateTime.of(
                2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));

        filteredMeals.add(meals.get(endId - 4));
        filteredMeals.add(meals.get(endId - 5));
        filteredMeals.add(meals.get(endId - 6));
    }

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2022, Month.JUNE, 10, 19, 0), "Ужин",
                410);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(mealsByUsers.get(USER_ID).get(MEAL_ID));
        updated.setDateTime(LocalDateTime.of(2022, Month.JUNE, 10, 15, 0));
        updated.setDescription("Ланч");
        updated.setCalories(50);
        return updated;
    }

    public static Meal getDuplicateDateTimeMeal() {
        Meal meal = mealsByUsers.get(USER_ID).get(MEAL_ID);
        return new Meal(meal.getDateTime(), "Какая-то еда", 1000);
    }
}
