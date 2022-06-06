<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="ru">
<head>
    <style>
        table, th, td {
            border: 2px solid black;
            border-collapse: collapse;
            padding: 2px;
        }
    </style>
    <title>Meals</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<hr>
<h2>Meals</h2>
<table>
    <thead>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
    </tr>
    </thead>
    <tbody>
    <jsp:useBean id="meals" scope="request" type="java.util.List"/>
    <jsp:useBean id="dateTimeFormatter" scope="request" type="java.time.format.DateTimeFormatter"/>
    <c:forEach var="meal" items="${meals}">
        <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.MealTo"/>
        <c:set var="color" scope="request" value="${meal.excess ? 'red' : 'green'}"/>
        <tr style="color: ${color}">
            <td>${dateTimeFormatter.format(meal.dateTime)}</td>
            <td>${meal.description}</td>
            <td>${meal.calories}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>