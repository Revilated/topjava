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

import static org.slf4j.LoggerFactory.*;
import static ru.javawebinar.topjava.util.MealsUtil.*;

public class MealServlet extends HttpServlet {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
    public static final int CALORIES_PER_DAY = 2000;
    private final MealRepository repository = new StaticMealRepository();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MealTo> meals = filteredByStreams(repository.findAll(), LocalTime.MIN, LocalTime.MAX, CALORIES_PER_DAY);
        request.setAttribute("meals", meals);
        request.setAttribute("dateTimeFormatter", DATE_TIME_FORMATTER);
        request.getRequestDispatcher("meals.jsp").forward(request, response);
    }
}
