package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
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
        filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
        System.out.println("streams:");
        filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
        System.out.println("cycles optional 2:");
        filteredByCyclesOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
        System.out.println("streams optional 2:");
        filteredByStreamsOptional2(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime,
                                                            LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dayToCalories = new HashMap<>();
        for (UserMeal meal : meals) {
            dayToCalories.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
        }
        List<UserMealWithExcess> result = new ArrayList<>();
        for (UserMeal meal : meals) {
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                result.add(mealWithExcess(
                        meal, dayToCalories.get(meal.getDateTime().toLocalDate()) > caloriesPerDay
                ));
            }
        }
        return result;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> dayToCalories = meals.stream()
                .collect(Collectors.toMap(m -> m.getDateTime().toLocalDate(), UserMeal::getCalories, Integer::sum));
        return meals.stream()
                .filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                .map(m -> mealWithExcess(m, dayToCalories.get(m.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByCyclesOptional2(List<UserMeal> meals, LocalTime startTime,
                                                                     LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> result = new ArrayList<>();
        filteredByCyclesOptional2Impl(meals.size() - 1, meals, new HashMap<>(), startTime, endTime,
                caloriesPerDay, result);
        return result;
    }

    private static void filteredByCyclesOptional2Impl(int i, List<UserMeal> meals,
                                                      Map<LocalDate, Integer> dayToCalories,
                                                      LocalTime startTime,
                                                      LocalTime endTime, int caloriesPerDay,
                                                      List<UserMealWithExcess> result) {
        if (i >= 0) {
            UserMeal meal = meals.get(i--);
            dayToCalories.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
            filteredByCyclesOptional2Impl(i, meals, dayToCalories, startTime, endTime, caloriesPerDay, result);
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                result.add(mealWithExcess(meal,
                        dayToCalories.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        }
    }

    public static List<UserMealWithExcess> filteredByStreamsOptional2(List<UserMeal> meals, LocalTime startTime,
                                                                      LocalTime endTime, int caloriesPerDay) {
        return meals.stream().collect(new ToListWithExcessCollector(caloriesPerDay, startTime, endTime));
    }

    private static UserMealWithExcess mealWithExcess(UserMeal meal, boolean excess) {
        return new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }

    private static class ToListWithExcessCollector implements
            Collector<UserMeal, ToListWithExcessCollector.MealsWithCaloriesMap, List<UserMealWithExcess>> {

        private final int caloriesPerDay;
        private final LocalTime startTime;
        private final LocalTime endTime;

        private ToListWithExcessCollector(int caloriesPerDay, LocalTime startTime, LocalTime endTime) {
            this.caloriesPerDay = caloriesPerDay;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public Supplier<ToListWithExcessCollector.MealsWithCaloriesMap> supplier() {
            return MealsWithCaloriesMap::new;
        }

        @Override
        public BiConsumer<ToListWithExcessCollector.MealsWithCaloriesMap, UserMeal> accumulator() {
            return (mc, m) -> {
                mc.caloriesByDate.merge(m.getDateTime().toLocalDate(), m.getCalories(), Integer::sum);
                mc.meals.add(m);
            };
        }

        @Override
        public BinaryOperator<ToListWithExcessCollector.MealsWithCaloriesMap> combiner() {
            return (mc1, mc2) -> {
                mc1.meals.addAll(mc2.meals);
                for (Map.Entry<LocalDate, Integer> e : mc2.caloriesByDate.entrySet()) {
                    mc1.caloriesByDate.merge(e.getKey(), e.getValue(), Integer::sum);
                }
                return mc1;
            };
        }

        @Override
        public Function<ToListWithExcessCollector.MealsWithCaloriesMap, List<UserMealWithExcess>> finisher() {
            return mc -> mc.meals.stream()
                    .filter(m -> TimeUtil.isBetweenHalfOpen(m.getDateTime().toLocalTime(), startTime, endTime))
                    .map(m -> mealWithExcess(m,
                            mc.caloriesByDate.get(m.getDateTime().toLocalDate()) > caloriesPerDay))
                    .collect(Collectors.toList());
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.unmodifiableSet(EnumSet.of(Characteristics.CONCURRENT));
        }

        private static class MealsWithCaloriesMap {
            final List<UserMeal> meals = new CopyOnWriteArrayList<>();
            final Map<LocalDate, Integer> caloriesByDate = new ConcurrentHashMap<>();
        }
    }
}
