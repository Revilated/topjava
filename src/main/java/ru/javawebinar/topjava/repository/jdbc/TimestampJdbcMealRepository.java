/*
 * (C) 2022 https://github.com/revilated
 */
package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author revilated
 */
@Repository
@Profile("hsqldb")
public class TimestampJdbcMealRepository extends AbstractJdbcMealRepository<Timestamp> {

    public TimestampJdbcMealRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(jdbcTemplate, namedParameterJdbcTemplate);
    }

    @Override
    protected Timestamp convertTimestamp(LocalDateTime dateTime) {
        return Timestamp.valueOf(dateTime);
    }
}
