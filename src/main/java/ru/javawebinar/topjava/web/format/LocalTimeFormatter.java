/*
 * 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.web.format;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author revilated
 */
public class LocalTimeFormatter implements Formatter<LocalTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_TIME;

    @Override
    public LocalTime parse(String text, Locale locale) throws ParseException {
        return StringUtils.hasText(text) ? LocalTime.parse(text, FORMATTER.localizedBy(locale)) : null;
    }

    @Override
    public String print(LocalTime object, Locale locale) {
        return object.format(FORMATTER.localizedBy(locale));
    }
}
