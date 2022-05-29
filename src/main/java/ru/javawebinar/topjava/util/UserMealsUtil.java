package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        System.out.println("cycles:");
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);
        System.out.println("streams:");
        filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime,
                                                            LocalTime endTime, int caloriesPerDay) {
        return filteredByCyclesImpl(meals.size() - 1, meals, new HashMap<>(), new ArrayList<>(), startTime,
                endTime, caloriesPerDay);
    }

    private static List<UserMealWithExcess> filteredByCyclesImpl(int i, List<UserMeal> meals,
                                                                 Map<LocalDate, Integer> dayToCalories,
                                                                 List<UserMealWithExcess> initial, LocalTime startTime,
                                                                 LocalTime endTime, int caloriesPerDay) {
        if (i < 0)
            return initial;
        UserMeal meal = meals.get(i--);
        dayToCalories.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        List<UserMealWithExcess> result = filteredByCyclesImpl(i, meals, dayToCalories, initial, startTime, endTime,
                caloriesPerDay);
        if (!TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime))
            return result;
        if (dayToCalories.get(meal.getDateTime().toLocalDate()) > caloriesPerDay) {
            result.add(UserMealWithExcess.from(meal, true));
        } else {
            result.add(UserMealWithExcess.from(meal, false));
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime,
                                                             LocalTime endTime, int caloriesPerDay) {
        return meals.stream().collect(new ToListWithExcessCollector(caloriesPerDay, startTime, endTime));
    }

    private static class ToListWithExcessCollector implements
            Collector<UserMeal, List<ToListWithExcessCollector.MealWithCaloriesMap>, List<UserMealWithExcess>> {

        private final int caloriesPerDay;
        private final LocalTime startTime;
        private final LocalTime endTime;

        private ToListWithExcessCollector(int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
            this.caloriesPerDay = caloriesPerDay;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public Supplier<List<ToListWithExcessCollector.MealWithCaloriesMap>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<ToListWithExcessCollector.MealWithCaloriesMap>, UserMeal> accumulator() {
            return (list, meal) -> {
                Map<LocalDate, Integer> map;
                if (list.size() == 0) {
                    map = new HashMap<>();
                } else {
                    map = list.get(list.size() - 1).caloriesByDate;
                }
                map.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
                list.add(new MealWithCaloriesMap(meal, map));
            };
        }

        @Override
        public BinaryOperator<List<ToListWithExcessCollector.MealWithCaloriesMap>> combiner() {
            return (l1, l2) -> { l1.addAll(l2); return l1; };
        }

        @Override
        public Function<List<ToListWithExcessCollector.MealWithCaloriesMap>, List<UserMealWithExcess>> finisher() {
            return l -> l.stream()
                    .map(m -> {
                        if (m.caloriesByDate.get(m.meal.getDateTime().toLocalDate()) > caloriesPerDay) {
                            return UserMealWithExcess.from(m.meal, true);
                        } else {
                            return UserMealWithExcess.from(m.meal, false);
                        }
                    })
                    .filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                    .collect(Collectors.toList());
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Characteristics.CONCURRENT));
        }

        private static class MealWithCaloriesMap {
            final UserMeal meal;
            final Map<LocalDate, Integer> caloriesByDate;

            private MealWithCaloriesMap(UserMeal meal, Map<LocalDate, Integer> caloriesByDate) {
                this.meal = meal;
                this.caloriesByDate = caloriesByDate;
            }
        }
    }
}
