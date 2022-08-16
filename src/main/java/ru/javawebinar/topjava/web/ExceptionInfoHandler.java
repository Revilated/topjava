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
import org.springframework.web.bind.MethodArgumentNotValidException;
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
        return logAndGetErrorInfo(req, e, false, DATA_NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.CONFLICT)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorInfo conflict(HttpServletRequest req, DataIntegrityViolationException e) {
        String rootMsg = ValidationUtil.getRootCause(e).getMessage();
        return findLocalizedError(rootMsg)
                .map(error -> logAndGetErrorInfo(req, e, error.toFieldError(messageSource).getDefaultMessage()))
                .orElseGet(() -> logAndGetErrorInfo(req, e, true, DATA_ERROR));
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({IllegalRequestDataException.class, MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ErrorInfo illegalRequestDataError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, false, VALIDATION_ERROR);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)  // 422
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    public ErrorInfo validationError(HttpServletRequest req, BindException e) {
        return logAndGetErrorInfo(req, e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorInfo handleError(HttpServletRequest req, Exception e) {
        return logAndGetErrorInfo(req, e, true, APP_ERROR);
    }

    //    https://stackoverflow.com/questions/538870/should-private-helper-methods-be-static-if-they-can-be-static
    private ErrorInfo logAndGetErrorInfo(HttpServletRequest req, Exception e, boolean logException,
                                         ErrorType errorType) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        if (logException) {
            logError(req, rootCause, errorType);
        } else {
            logWarn(req, rootCause, errorType);
        }
        return getErrorInfo(req, errorType, rootCause.toString());
    }

    private ErrorInfo logAndGetErrorInfo(HttpServletRequest req, BindException e) {
        logWarn(req, e, VALIDATION_ERROR);
        var detail = ValidationUtil.getBindingResultMessage(e.getBindingResult());
        return getErrorInfo(req, VALIDATION_ERROR, detail);
    }

    private ErrorInfo logAndGetErrorInfo(HttpServletRequest req, DataIntegrityViolationException e, String detail) {
        Throwable rootCause = ValidationUtil.getRootCause(e);
        logError(req, rootCause, DATA_ERROR);
        return getErrorInfo(req, DATA_ERROR, detail);
    }

    private static ErrorInfo getErrorInfo(HttpServletRequest req, ErrorType errorType, String detail) {
        return new ErrorInfo(req.getRequestURL(), errorType, detail);
    }

    private void logError(HttpServletRequest req, Throwable throwable, ErrorType errorType) {
        log.error(errorType + " at request " + req.getRequestURL(), throwable);
    }

    private void logWarn(HttpServletRequest req, Throwable throwable, ErrorType errorType) {
        log.warn("{} at request  {}: {}", errorType, req.getRequestURL(), throwable.toString());
    }
}