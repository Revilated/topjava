/*
 * 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.web;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

/**
 * @author revilated
 */
public class BindingUtil {
    public static ResponseEntity<String> generateResponse(BindingResult bindingResult, Runnable onOk) {
        if (bindingResult.hasErrors()) {
            var errorMsg = bindingResult.getFieldErrors().stream()
                    .map(fe -> String.format("[%s] %s", fe.getField(), fe.getDefaultMessage()))
                    .collect(Collectors.joining("<br>"));
            return ResponseEntity.unprocessableEntity().body(errorMsg);
        } else {
            onOk.run();
            return ResponseEntity.ok().build();
        }
    }
}
