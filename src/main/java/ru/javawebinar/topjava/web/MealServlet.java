package ru.javawebinar.topjava.web;

import org.slf4j.*;
import org.springframework.context.*;
import org.springframework.context.support.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.web.meal.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;

public class MealServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(MealServlet.class);

    private MealRestController controller;
    private ConfigurableApplicationContext appCtx;

    @Override
    public void init() {
        appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        controller = appCtx.getBean(MealRestController.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String id = request.getParameter("id");
        Meal meal = new Meal(id.isEmpty() ? null : Integer.valueOf(id),
                null,
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        if (meal.isNew()) {
            log.info("Create {}", meal);
            controller.create(meal);
        } else {
            log.info("Update {}", meal);
            controller.update(meal, meal.getId());
        }
        response.sendRedirect("meals");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "all" : action) {
            case "filter":
                LocalDate startDate = getDate(request, "startDate");
                LocalDate endDate = getDate(request, "endDate");
                LocalTime startTime = getTime(request, "startTime");
                LocalTime endTime = getTime(request, "endTime");
                MealFilterParams filterParams = new MealFilterParams(startDate, endDate, startTime, endTime);
                request.setAttribute("meals", controller.getAllFiltered(filterParams));
                request.setAttribute("filterParams", filterParams);
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
            case "delete":
                int id = getId(request);
                log.info("Delete id={}", id);
                controller.delete(id);
                response.sendRedirect("meals");
                break;
            case "create":
            case "update":
                final Meal meal = "create".equals(action) ?
                        new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000) :
                        controller.get(getId(request));
                request.setAttribute("meal", meal);
                request.getRequestDispatcher("/mealForm.jsp").forward(request, response);
                break;
            case "all":
            default:
                log.info("getAll");
                request.setAttribute("meals", controller.getAll());
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }

    @Override
    public void destroy() {
        appCtx.close();
    }

    private int getId(HttpServletRequest request) {
        String paramId = Objects.requireNonNull(request.getParameter("id"));
        return Integer.parseInt(paramId);
    }

    private LocalDate getDate(HttpServletRequest request, String name) {
        String dateParam = request.getParameter(name);
        return dateParam == null || dateParam.isEmpty() ? null : LocalDate.parse(dateParam);
    }

    private LocalTime getTime(HttpServletRequest request, String name) {
        String timeParam = request.getParameter(name);
        return timeParam == null || timeParam.isEmpty() ? null : LocalTime.parse(timeParam);
    }
}
