/*
 * 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.web.format;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author revilated
 */
public class LocalDateFormatter implements Formatter<LocalDate> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;

    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        return StringUtils.hasText(text) ? LocalDate.parse(text, FORMATTER.localizedBy(locale)) : null;
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return object.format(FORMATTER.localizedBy(locale));
    }
}
