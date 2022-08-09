/*
 * 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.web.converter;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Locale;

/**
 * @author revilated
 */
public class IntFormatter implements Formatter<Integer> {
    @Override
    public Integer parse(String text, Locale locale) throws ParseException {
        return StringUtils.hasLength(text) ? Integer.valueOf(text) : null;
    }

    @Override
    public String print(Integer object, Locale locale) {
        return object.toString();
    }
}
