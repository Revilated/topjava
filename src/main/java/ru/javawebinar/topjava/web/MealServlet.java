package ru.javawebinar.topjava.web;

import org.slf4j.*;
import ru.javawebinar.topjava.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.info("forward to meals");
        request.setAttribute("meals", MealsUtil.getTos(MealsUtil.meals, MealsUtil.DEFAULT_CALORIES_PER_DAY));
        request.getRequestDispatcher("/meals.jsp").forward(request, response);
    }
}
