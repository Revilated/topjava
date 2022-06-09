package ru.javawebinar.topjava.web;

import org.slf4j.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

import static org.slf4j.LoggerFactory.*;

public class UserServlet extends HttpServlet {
    private static final Logger log = getLogger(UserServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("forward to users");
        request.getRequestDispatcher("/users.jsp").forward(request, response);
    }
}
