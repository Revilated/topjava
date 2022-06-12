package ru.javawebinar.topjava.web.user;

import ru.javawebinar.topjava.model.*;

import static ru.javawebinar.topjava.web.SecurityUtil.*;

public class ProfileRestController extends AbstractUserController {

    public User get() {
        return super.get(authUserId());
    }

    public void delete() {
        super.delete(authUserId());
    }

    public void update(User user) {
        super.update(user, authUserId());
    }
}