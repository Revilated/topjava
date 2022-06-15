package ru.javawebinar.topjava;

import org.springframework.context.*;
import org.springframework.context.support.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.web.meal.*;
import ru.javawebinar.topjava.web.user.*;

import java.time.*;
import java.util.*;

public class SpringMain {
    public static void main(String[] args) {
        // java 7 automatic resource management (ARM)
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            AdminRestController adminUserController = appCtx.getBean(AdminRestController.class);
            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));
            MealRestController mealRestController = appCtx.getBean(MealRestController.class);
            System.out.println("getAll():");
            mealRestController.getAll().forEach(System.out::println);
            System.out.println("getAllFiltered():");
            LocalDate date = LocalDate.of(2020, Month.JANUARY, 31);
            MealFilterParams filterParams = new MealFilterParams(date, date, LocalTime.of(10, 0),
                    LocalTime.of(14, 0));
            mealRestController.getAllFiltered(filterParams).forEach(System.out::println);
            System.out.println("get():");
            System.out.println(mealRestController.get(1));
            System.out.println("create(), update():");
            Meal newMeal = new Meal(
                    LocalDateTime.of(2020, Month.JANUARY, 30, 11, 0), "Завтрак 2",
                    100);
            mealRestController.create(newMeal);
            Meal updatedMeal = new Meal(4, null,
                    LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0),
                    "Еда на граничное значение", 50);
            mealRestController.update(updatedMeal, 4);
            mealRestController.getAll().forEach(System.out::println);
            System.out.println("delete():");
            mealRestController.delete(4);
            mealRestController.getAll().forEach(System.out::println);
        }
    }
}
