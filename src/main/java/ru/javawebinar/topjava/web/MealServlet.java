package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.repo.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static ru.javawebinar.topjava.util.MealsUtil.*;

public class MealServlet extends HttpServlet {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final int CALORIES_PER_DAY = 2000;
    public static final String MEALS_PAGE = "meals.jsp";
    public static final String MEAL_PAGE = "meal.jsp";
    private final MealRepository repository;
    private final AtomicInteger nextId;

    public MealServlet() {
        List<Meal> initialData = new StaticMealRepository().findAll();
        repository = new InMemoryMealRepository(initialData);
        nextId = new AtomicInteger(initialData.size());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            repository.delete(id);
            request.setAttribute("meals", getMealsTo());
            request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
            response.sendRedirect("meals");
        } else if ("edit".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
            repository.find(id).ifPresent(m -> {
                request.setAttribute("meal", m);
                forward(request, response, MEAL_PAGE);
            });
        } else if ("insert".equals(action)) {
            request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
            forward(request, response, MEAL_PAGE);
        } else {
            request.setAttribute("meals", getMealsTo());
            request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
            forward(request, response, MEALS_PAGE);
        }

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
            meal = new Meal(nextId.getAndIncrement(), dateTime, description, calories);
            repository.add(meal);
        } else {
            meal = new Meal(Integer.parseInt(idParam), dateTime, description, calories);
            repository.update(meal);
        }
        response.sendRedirect("meals");
    }

    private List<MealTo> getMealsTo() {
        return filteredByStreams(repository.findAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
    }
}
