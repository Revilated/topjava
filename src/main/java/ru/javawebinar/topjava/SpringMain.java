package ru.javawebinar.topjava;

import org.springframework.context.*;
import org.springframework.context.support.*;
import ru.javawebinar.topjava.repository.*;

import java.util.*;

public class SpringMain {
    public static void main(String[] args) {
        ConfigurableApplicationContext appCtx = new ClassPathXmlApplicationContext("spring/spring-app.xml");
        System.out.println("Bean definition names: " + Arrays.toString(appCtx.getBeanDefinitionNames()));

//        UserRepository userRepository = (UserRepository) appCtx.getBean("inmemoryUserRepository");
        UserRepository userRepository = appCtx.getBean(UserRepository.class);
        userRepository.getAll();
        appCtx.close();
    }
}
