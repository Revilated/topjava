/*
 * 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.util.exception.ErrorType;

import javax.servlet.http.HttpServletRequest;

/**
 * @author revilated
 */
public class ExceptionUtil {

    public static void logError(Logger log, HttpServletRequest req, Throwable throwable, ErrorType errorType) {
        log.error(errorType + " at request " + req.getRequestURL(), throwable);
    }

    public static void logWarn(Logger log, HttpServletRequest req, Throwable throwable, ErrorType errorType) {
        log.warn("{} at request  {}: {}", errorType, req.getRequestURL(), throwable.toString());
    }
}
