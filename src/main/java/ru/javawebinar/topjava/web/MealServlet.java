package ru.javawebinar.topjava.web;

import org.slf4j.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.repo.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;

import static org.slf4j.LoggerFactory.*;
import static ru.javawebinar.topjava.util.MealsUtil.*;

public class MealServlet extends HttpServlet {

    private static final Logger log = getLogger(MealServlet.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int CALORIES_PER_DAY = 2000;
    private static final String MEALS_PAGE = "meals.jsp";
    private static final String MEAL_PAGE = "meal.jsp";
    private MealRepository repository;


    @Override
    public void init() {
        repository = new InMemoryMealRepository();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String actionParam = request.getParameter("action");
        String action = actionParam == null ? "" : actionParam.toLowerCase();
        switch (action) {
            case "delete": {
                int id = getId(request);
                repository.delete(id);
                log.debug("Meal deleted with id: {}", id);
                response.sendRedirect("meals");
                break;
            }
            case "edit": {
                int id = getId(request);
                Meal meal = repository.find(id);
                log.debug("Editing meal with id: {}", id);
                request.setAttribute("meal", meal);
                request.setAttribute("now", LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
                request.getRequestDispatcher(MEAL_PAGE).forward(request, response);
                break;
            }
            case "insert":
                log.debug("Start creating new meal");
                request.setAttribute("now", LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
                request.getRequestDispatcher(MEAL_PAGE).forward(request, response);
                break;
            default:
                log.debug("Showing {} page", MEALS_PAGE);
                request.setAttribute("meals", getMealsTo());
                request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
                request.getRequestDispatcher(MEALS_PAGE).forward(request, response);
                break;
        }
    }

    private static int getId(HttpServletRequest request) {
        return Integer.parseInt(request.getParameter("id"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));
        String idParam = request.getParameter("id");
        Meal meal;
        if (idParam == null || idParam.isEmpty()) {
            meal = new Meal(null, dateTime, description, calories);
            repository.add(meal);
            log.debug("New meal added with id: {}", meal.getId());
        } else {
            meal = new Meal(Integer.parseInt(idParam), dateTime, description, calories);
            repository.update(meal);
            log.debug("Meal updated with id: {}", meal.getId());
        }
        response.sendRedirect("meals");
    }

    private List<MealTo> getMealsTo() {
        return filteredByStreams(repository.findAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
    }
}
