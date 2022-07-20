package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.User;

import javax.persistence.QueryHint;

@Transactional(readOnly = true)
public interface CrudUserRepository extends JpaRepository<User, Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM User u WHERE u.id=:id")
    int delete(@Param("id") int id);

    User getByEmail(String email);

    // Это попытка убрать дублирующиеся записи в user.meals, которая не увенчалась успехом, так как в результате
    // появился еще один select на роли. @QueryHint и DISTINCT по идее должны были решить проблему, но у меня не вышло
    // заставить их работать, либо я не до конца понял принцип действия (https://stackoverflow.com/a/53406102).
    //@EntityGraph(attributePaths = {"roles"})
    @QueryHints(
            @QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false")
    )
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.meals WHERE u.id=?1")
    User getWithMeals(int id);
}
