package ru.javawebinar.topjava.web;

import org.slf4j.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.repo.*;
import ru.javawebinar.topjava.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.concurrent.atomic.*;

import static org.slf4j.LoggerFactory.*;
import static ru.javawebinar.topjava.util.MealsUtil.*;

public class MealServlet extends HttpServlet {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
    public static final int CALORIES_PER_DAY = 2000;
    private final MealRepository repository;
    private final AtomicInteger nextId;

    public MealServlet() {
        List<Meal> initialData = new StaticMealRepository().findAll();
        repository = new InMemoryMealRepository(initialData);
        nextId = new AtomicInteger(initialData.size());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward;
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            repository.delete(id);
            request.setAttribute("meals", getMealsTo());
            request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
            forward = "meals.jsp";
        } else if ("edit".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            forward = "meal.jsp";
            repository.find(id).ifPresent(m -> request.setAttribute("meal", m));
        } else if ("insert".equals(action)) {
            forward = "meal.jsp";
        } else {
            forward = "meals.jsp";
            request.setAttribute("meals", getMealsTo());
            request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
        }
        request.getRequestDispatcher(forward).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        request.setAttribute("meals", getMealsTo());
        request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }

    private List<MealTo> getMealsTo() {
        return filteredByStreams(repository.findAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
    }
}
