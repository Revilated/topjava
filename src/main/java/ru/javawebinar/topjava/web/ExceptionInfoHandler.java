package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.javawebinar.topjava.util.ValidationUtil;
import ru.javawebinar.topjava.util.exception.ErrorInfo;
import ru.javawebinar.topjava.util.exception.ErrorType;
import ru.javawebinar.topjava.util.exception.IllegalRequestDataException;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import javax.servlet.http.HttpServletRequest;

import static ru.javawebinar.topjava.util.ValidationUtil.findLocalizedError;
import static ru.javawebinar.topjava.util.exception.ErrorType.*;
import static ru.javawebinar.topjava.web.ExceptionUtil.logError;
import static ru.javawebinar.topjava.web.ExceptionUtil.logWarn;

@RestControllerAdvice(annotations = RestController.class)
@Order(Ordered.HIGHEST_PRECEDENCE + 5)
public class ExceptionInfoHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionInfoHandler.class);

    private final MessageSourceAccessor messageSource;

    public ExceptionInfoHandler(MessageSourceAccessor messageSource) {
        this.messageSource = messageSource;
    }

    //  http://stackoverflow.com/a/22358422/548473
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(NotFoundException.class)
    public ErrorInfo handleError(HttpServletRequest req, NotFoundException e) {
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo conflict(HttpServletRequest req, DataIntegrityViolationException e) {
        return findLocalizedError(e.getMessage(), messageSource)
                .map(error -> logAndGetErrorInfo(req, e, true, DATA_ERROR, error))
                .orElseGet(() -> logAndGetErrorInfo(req, e, true, DATA_ERROR, e.toString()));
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({IllegalRequestDataException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ErrorInfo illegalRequestDataError(HttpServletRequest req, Exception e) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        return logAndGetErrorInfo(req, rootCause, false, VALIDATION_ERROR, rootCause.toString());
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({BindException.class})
    public ErrorInfo validationError(HttpServletRequest req, BindException e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR,
                ValidationUtil.getBindingResultMessage(e.getBindingResult()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo handleError(HttpServletRequest req, Exception e) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        return logAndGetErrorInfo(req, rootCause, true, APP_ERROR, rootCause.toString());
    }

    private ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Throwable e, boolean logException,
                                         ErrorType errorType, String detail) {
        if (logException) {
            logError(log, req, e, errorType);
        } else {
            logWarn(log, req, e, errorType);
        }
        return getErrorInfo(req, errorType, detail);
    }

    private static ErrorInfo getErrorInfo(HttpServletRequest req, ErrorType errorType, String detail) {
        return new ErrorInfo(req.getRequestURL(), errorType, detail);
    }
}