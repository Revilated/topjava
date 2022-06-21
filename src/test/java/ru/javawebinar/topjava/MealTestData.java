/*
 * (C) 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

/**
 * @author revilated
 */
public class MealTestData {

    public static final int USER_ID = START_SEQ;
    public static final int ANOTHER_USER_ID = START_SEQ + 1;
    public static final int START_MEAL_ID = START_SEQ + 3;
    public static final int MEAL_ID = START_MEAL_ID + 1;
    public static final LocalDate START_DATE = LocalDate.of(2020, Month.JANUARY, 30);
    public static final LocalDate END_DATE = LocalDate.of(2020, Month.JANUARY, 30);
    public static final Meal meal1 = new Meal(START_MEAL_ID, LocalDateTime.of(
            2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500);
    public static final Meal meal2 = new Meal(START_MEAL_ID + 1, LocalDateTime.of(
            2020, Month.JANUARY, 30, 13, 0), "Обед", 1000);
    public static final Meal meal3 = new Meal(START_MEAL_ID + 2, LocalDateTime.of(
            2020, Month.JANUARY, 30, 20, 0), "Ужин", 500);
    public static final Meal meal4 = new Meal(START_MEAL_ID + 3, LocalDateTime.of(
            2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100);
    public static final Meal meal5 = new Meal(START_MEAL_ID + 4, LocalDateTime.of(
            2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000);
    public static final Meal meal6 = new Meal(START_MEAL_ID + 5, LocalDateTime.of(
            2020, Month.JANUARY, 31, 13, 0), "Обед", 500);
    public static final Meal meal7 = new Meal(START_MEAL_ID + 6, LocalDateTime.of(
            2020, Month.JANUARY, 31, 20, 0), "Ужин", 410);
    public static final Meal meal = meal2;

    public static Meal getNew() {
        return new Meal(LocalDateTime.of(2022, Month.JUNE, 10, 19, 0), "Ужин",
                410);
    }

    public static Meal getUpdated() {
        Meal updated = new Meal(meal);
        updated.setDateTime(LocalDateTime.of(2022, Month.JUNE, 10, 15, 0));
        updated.setDescription("Ланч");
        updated.setCalories(50);
        return updated;
    }

    public static Meal getDuplicateDateTimeMeal() {
        return new Meal(meal.getDateTime(), "Какая-то еда", 1000);
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}
