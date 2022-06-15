package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.service.*;
import ru.javawebinar.topjava.to.*;
import ru.javawebinar.topjava.util.*;

import java.util.*;

import static ru.javawebinar.topjava.util.MealsUtil.*;
import static ru.javawebinar.topjava.web.SecurityUtil.*;

@Controller
public class MealRestController {
    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(Meal meal) {
        meal.setUserId(authUserId());
        return service.create(meal);
    }

    public Meal get(int id) {
        return service.get(id, authUserId());
    }

    public void delete(int id) {
        service.delete(id, authUserId());
    }

    public void update(Meal meal, int id) {
        ValidationUtil.assureIdConsistent(meal, id);
        meal.setUserId(authUserId());
        service.update(meal);
    }

    public List<MealTo> getAll() {
        return getTos(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public List<MealTo> getAllFiltered(MealFilterParams filterParams) {
        return getFilteredTos(
                service.getAllFiltered(authUserId(), filterParams.getStartDate(), filterParams.getEndDate()),
                authUserCaloriesPerDay(),
                filterParams.getStartTime(), filterParams.getEndTime()
        );
    }
}