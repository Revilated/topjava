package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.repo.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import static ru.javawebinar.topjava.util.MealsUtil.*;

public class MealServlet extends HttpServlet {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final int CALORIES_PER_DAY = 2000;
    public static final String MEALS_PAGE = "meals.jsp";
    public static final String MEAL_PAGE = "meal.jsp";
    private final MealRepository repository = new InMemoryMealRepository();


    @Override
    public void init() {
        repository.add(new Meal(0, LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500));
        repository.add(new Meal(1, LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000));
        repository.add(new Meal(2, LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500));
        repository.add(new Meal(3, LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100));
        repository.add(new Meal(4, LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000));
        repository.add(new Meal(5, LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500));
        repository.add(new Meal(6, LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String actionParam = request.getParameter("action");
        String action = actionParam == null ? "" : actionParam.toLowerCase();
        switch (action) {
            case "delete": {
                int id = getId(request);
                repository.delete(id);
                request.setAttribute("meals", getMealsTo());
                request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
                response.sendRedirect("meals");
                break;
            }
            case "edit": {
                int id = getId(request);
                request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
                Meal meal = repository.find(id).orElseThrow(() -> new RuntimeException("No meal with id: " + id));
                request.setAttribute("meal", meal);
                forward(request, response, MEAL_PAGE);
                break;
            }
            case "insert":
                request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
                forward(request, response, MEAL_PAGE);
                break;
            default:
                request.setAttribute("meals", getMealsTo());
                request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
                forward(request, response, MEALS_PAGE);
                break;
        }

    }

    private static int getId(HttpServletRequest request) {
        return Integer.parseInt(request.getParameter("id"));
    }

    private static void forward(HttpServletRequest request, HttpServletResponse response, String path) {
        try {
            request.getRequestDispatcher(path).forward(request, response);
        } catch (ServletException | IOException e) {
            throw new RuntimeException(e);
        }
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
            repository.add(meal).orElseThrow(() ->
                    new RuntimeException(String.format("Meal with id=%s already exists", meal.getId())));
        } else {
            meal = new Meal(Integer.parseInt(idParam), dateTime, description, calories);
            repository.update(meal).orElseThrow(() -> new RuntimeException("No meal with id: " + meal.getId()));
        }
        response.sendRedirect("meals");
    }

    private List<MealTo> getMealsTo() {
        return filteredByStreams(repository.findAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
    }
}
