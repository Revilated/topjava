package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal actual = service.get(MEAL_ID, USER_ID);
        Meal expected = mealsByUsers.get(USER_ID).get(MEAL_ID);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void getSomeoneElseFood() {
        assertThatThrownBy(() -> service.get(MEAL_ID, ANOTHER_USER_ID)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void delete() {
        service.delete(MEAL_ID, USER_ID);
        assertThatThrownBy(() -> service.get(MEAL_ID, USER_ID)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void deleteSomeoneElseFood() {
        assertThatThrownBy(() -> service.delete(MEAL_ID, ANOTHER_USER_ID)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> actual = service.getBetweenInclusive(START_DATE, END_DATE, USER_ID);
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(filteredMeals);
    }

    @Test
    public void getAll() {
        List<Meal> actual = service.getAll(USER_ID);
        Collection<Meal> expected = mealsByUsers.get(USER_ID).values();
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }

    @Test
    public void update() {
        Meal expected = getUpdated();
        service.update(expected, USER_ID);
        Meal actual = service.get(MEAL_ID, USER_ID);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void updateSomeoneElseFood() {
        Meal meal = mealsByUsers.get(USER_ID).get(MEAL_ID);
        assertThatThrownBy(() -> service.update(meal, ANOTHER_USER_ID)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Integer newId = created.getId();
        Meal expected = getNew();
        expected.setId(newId);
        assertThat(created).usingRecursiveComparison().isEqualTo(expected);
        assertThat(service.get(newId, USER_ID)).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void duplicateDateTimeCreate() {
        assertThatThrownBy(() -> service.create(getDuplicateDateTimeMeal(), USER_ID))
                .isInstanceOf(DataAccessException.class);
    }
}