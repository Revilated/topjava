/*
 * (C) 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.User;

import static ru.javawebinar.topjava.MealTestData.MEAL_MATCHER;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.UserTestData.USER_MATCHER;

/**
 * @author revilated
 */
@ActiveProfiles("datajpa")
public class DataJpaUserServiceTest extends AbstractUserServiceTest {

    @Test
    public void getWithMeals() {
        User user = service.getWithMeals(USER_ID);
        System.out.println(user.getMeals());
        USER_MATCHER.assertMatch(user, UserTestData.userWithMeals);
        MEAL_MATCHER.assertMatch(user.getMeals(), UserTestData.userWithMeals.getMeals());
    }
}
