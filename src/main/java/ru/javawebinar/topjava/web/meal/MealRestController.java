package ru.javawebinar.topjava.web.meal;

import org.slf4j.*;
import org.springframework.stereotype.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.service.*;
import ru.javawebinar.topjava.to.*;
import ru.javawebinar.topjava.util.*;

import java.time.*;
import java.util.*;

import static org.slf4j.LoggerFactory.*;
import static ru.javawebinar.topjava.util.MealsUtil.*;
import static ru.javawebinar.topjava.util.ValidationUtil.*;
import static ru.javawebinar.topjava.web.SecurityUtil.*;

@Controller
public class MealRestController {

    private static final Logger log = getLogger(MealRestController.class);
    private final MealService service;

    public MealRestController(MealService service) {
        this.service = service;
    }

    public Meal create(Meal meal) {
        meal.setUserId(authUserId());
        log.debug("create meal: {}", meal);
        checkNew(meal);
        return service.create(meal);
    }

    public Meal get(int id) {
        log.debug("get meal by id: {}", id);
        return service.get(id, authUserId());
    }

    public void delete(int id) {
        log.debug("delete meal by id: {}", id);
        service.delete(id, authUserId());
    }

    public void update(Meal meal, int id) {
        log.debug("update meal: {}", meal);
        ValidationUtil.assureIdConsistent(meal, id);
        service.update(meal, authUserId());
    }

    public List<MealTo> getAll() {
        log.debug("get meals");
        return getTos(service.getAll(authUserId()), authUserCaloriesPerDay());
    }

    public List<MealTo> getAllFiltered(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.debug("get filtered meals");
        return getFilteredTos(service.getAllFiltered(authUserId(), startDate, endDate), authUserCaloriesPerDay(),
                startTime, endTime);
    }
}