<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Edit meal</h2>
<jsp:useBean id="meal" scope="request" class="ru.javawebinar.topjava.model.Meal"/>
<c:set var="id" scope="page" value="${meal.id == -1 ? null : meal.id}"/>-->
<form action="meals?id=${id}" method="post">
    <label for="datetime">DateTime:</label>
    <input type="datetime-local" id="datetime" name="dateTime" value="${meal.dateTime}"><br>
    <label for="description">Description</label>
    <input type="text" id="description" name="description" value="${meal.description}"><br>
    <label for="calories">Calories</label>
    <input type="number" id="calories" name="calories" value="${meal.calories}"><br>
    <input type="submit" value="Save">
    <input class="button" type="button" onclick="window.location.replace('meals')" value="Cancel" />
</form>
</body>
</html>