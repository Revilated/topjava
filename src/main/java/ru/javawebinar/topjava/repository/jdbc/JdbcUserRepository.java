package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;
import ru.javawebinar.topjava.util.ValidationUtil;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> USERS_ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);
    private static final RowMapper<Role> ROLES_ROW_MAPPER = (rs, rowNum) -> Role.valueOf(rs.getString("role"));

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final ValidationUtil validationUtil;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              ValidationUtil validationUtil) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.validationUtil = validationUtil;
    }

    @Override
    @Transactional
    public User save(User user) {
        validationUtil.validateOrThrow(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            Set<Role> roles = user.getRoles();
            if (roles != null) {
                insertRoles(roles, user.id());
            }
        } else {
            if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password, 
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
                return null;
            }
            Set<Role> roles = user.getRoles();
            if (roles != null) {
                jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.id());
                if (!roles.isEmpty()) {
                    insertRoles(roles, user.id());
                }
            }
        }
        return user;
    }

    private void insertRoles(Set<Role> roles, int userId) {
        var params = getStatementParamsForRoles(roles, userId);
        jdbcTemplate.batchUpdate("INSERT INTO user_roles (role, user_id) VALUES (?, ?)", params);
    }

    private List<Object[]> getStatementParamsForRoles(Set<Role> roles, int userId) {
        return roles.stream().map(r -> new Object[]{r.name(), userId}).toList();
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", USERS_ROW_MAPPER, id);
        User user = DataAccessUtils.singleResult(users);
        if (user != null) {
            user.setRoles(getRoles(id));
        }
        return user;
    }

    private Set<Role> getRoles(int userId) {
        return jdbcTemplate.queryForStream("SELECT * FROM user_roles WHERE user_id=?",
                        ROLES_ROW_MAPPER, userId)
                .collect(Collectors.toSet());
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        // Используется Stream.distinct(), потому что DataAccessUtils.uniqueResult сравнивает по ссылкам, а ссылки тут
        // разные у одинаковых объектов в отличие от Hibernate
        List<User> users = jdbcTemplate.queryForStream(
                "SELECT * FROM users LEFT JOIN user_roles ur ON users.id = ur.user_id WHERE email=?",
                USERS_ROW_MAPPER, email).distinct().collect(Collectors.toList());
        User user = DataAccessUtils.uniqueResult(users);
        if (user != null) {
            Set<Role> roles = getRoles(user.id());
            user.setRoles(roles);
        }
        return user;
    }

    @Override
    public List<User> getAll() {
        SqlRowSet roleRows = jdbcTemplate.queryForRowSet("SELECT * FROM user_roles");
        Map<Integer, Set<Role>> rolesByUserId = new HashMap<>();
        while (roleRows.next()) {
            rolesByUserId.compute(roleRows.getInt("user_id"), (key, value) -> {
                Set<Role> newValue = Objects.requireNonNullElseGet(value, HashSet::new);
                newValue.add(Role.valueOf(roleRows.getString("role")));
                return newValue;
            });
        }
        List<User> users = jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", USERS_ROW_MAPPER);
        users.forEach(u -> u.setRoles(rolesByUserId.get(u.id())));
        return users;
    }
}
