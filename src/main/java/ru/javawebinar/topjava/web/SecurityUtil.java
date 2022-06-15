package ru.javawebinar.topjava.web;

import java.util.concurrent.atomic.*;

import static ru.javawebinar.topjava.util.MealsUtil.*;

public class SecurityUtil {

    private static final AtomicInteger userId = new AtomicInteger(1);

    public static int authUserId() {
        return userId.get();
    }

    public static int authUserCaloriesPerDay() {
        return DEFAULT_CALORIES_PER_DAY;
    }

    public static void setAuthUserId(int id) {
        userId.set(id);
    }
}