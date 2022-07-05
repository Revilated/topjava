/*
 * (C) 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * @author revilated
 */
@Repository
@Profile("postgres")
public class Java8TimeJdbcMealRepository extends AbstractJdbcMealRepository<LocalDateTime> {

    public Java8TimeJdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    protected LocalDateTime convertTimestamp(LocalDateTime dateTime) {
        return dateTime;
    }
}
