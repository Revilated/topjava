package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.javawebinar.topjava.AuthorizedUser;
import ru.javawebinar.topjava.to.UserTo;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.util.exception.ErrorType;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static ru.javawebinar.topjava.util.exception.ErrorType.APP_ERROR;
import static ru.javawebinar.topjava.util.exception.ErrorType.DATA_ERROR;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BindException.class)
    public ModelAndView conflict(HttpServletRequest req, BindException e) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        log(req, rootCause, DATA_ERROR);
        ModelAndView mav = new ModelAndView("profile", e.getModel());
        mav.setStatus(HttpStatus.CONFLICT);
        var target = e.getTarget();
        if (target instanceof UserTo) {
            mav.addObject("register", ((UserTo) target).isNew());
            mav.addObject("userTo", target);
        }
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        log(req, e, APP_ERROR);
        Throwable rootCause = ValidationUtil.getRootCause(e);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        ModelAndView mav = new ModelAndView("exception",
                Map.of("exception", rootCause, "message", rootCause.toString(), "status", httpStatus));
        mav.setStatus(httpStatus);

        // Interceptor is not invoked, put userTo
        AuthorizedUser authorizedUser = SecurityUtil.safeGet();
        if (authorizedUser != null) {
            mav.addObject("userTo", authorizedUser.getUserTo());
        }
        return mav;
    }

    private void log(HttpServletRequest req, Throwable throwable, ErrorType errorType) {
        log.error(errorType + " at request " + req.getRequestURL(), throwable);
    }
}
