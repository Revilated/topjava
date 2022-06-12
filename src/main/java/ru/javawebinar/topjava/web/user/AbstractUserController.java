package ru.javawebinar.topjava.web.user;

import org.slf4j.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.service.*;

import java.util.*;

import static ru.javawebinar.topjava.util.ValidationUtil.*;

public abstract class AbstractUserController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    private UserService service;

    public List<User> getAll() {
        log.info("getAll");
        return service.getAll();
    }

    public User get(int id) {
        log.info("get {}", id);
        return service.get(id);
    }

    public User create(User user) {
        log.info("create {}", user);
        checkNew(user);
        return service.create(user);
    }

    public void delete(int id) {
        log.info("delete {}", id);
        service.delete(id);
    }

    public void update(User user, int id) {
        log.info("update {} with id={}", user, id);
        assureIdConsistent(user, id);
        service.update(user);
    }

    public User getByMail(String email) {
        log.info("getByEmail {}", email);
        return service.getByEmail(email);
    }
}