package ru.javawebinar.topjava.service;

import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.repository.*;

import java.util.*;

import static ru.javawebinar.topjava.util.ValidationUtil.*;

public class UserService {

    private UserRepository repository;

    public User create(User user) {
        return repository.save(user);
    }

    public void delete(int id) {
        checkNotFoundWithId(repository.delete(id), id);
    }

    public User get(int id) {
        return checkNotFoundWithId(repository.get(id), id);
    }

    public User getByEmail(String email) {
        return checkNotFound(repository.getByEmail(email), "email=" + email);
    }

    public List<User> getAll() {
        return repository.getAll();
    }

    public void update(User user) {
        checkNotFoundWithId(repository.save(user), user.getId());
    }
}