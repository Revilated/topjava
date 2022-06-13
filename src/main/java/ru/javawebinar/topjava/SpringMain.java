package ru.javawebinar.topjava;

import org.springframework.context.*;
import org.springframework.context.support.*;
import ru.javawebinar.topjava.model.*;
import ru.javawebinar.topjava.web.user.*;

import java.util.*;

public class SpringMain {
    public static void main(String[] args) {
        // java 7 automatic resource management (ARM)
        try (ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml")) {
            System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));
            AdminRestController adminUserController = appCtx.getBean(AdminRestController.class);
            adminUserController.create(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));
        }
    }
}
