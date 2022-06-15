<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://topjava.javawebinar.ru/functions" %>
<%--<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>--%>
<html>
<head>
    <title>Meal list</title>
    <style>
        .normal {
            color: green;
        }

        .excess {
            color: red;
        }
    </style>
</head>
<body>
<section>
    <h3><a href="index.html">Home</a></h3>
    <hr/>
    <h2>Meals</h2>
    <jsp:useBean id="filterParams" class="ru.javawebinar.topjava.model.MealFilterParams" scope="request"/>
    <form method="get" action="meals">
        <input type="hidden" name="action" value="filter" />
        <table cellpadding="8" cellspacing="0" style="border: 1px solid black">
            <tr>
            <td><label>
                От даты<br>(включая):<br>
                <input type="date" name="startDate" value="${filterParams.startDate}">
            </label></td>
            <td><label>
                До даты<br>(включая):<br>
                <input type="date" name="endDate" value="${filterParams.endDate}">
            </label></td>
                <td><label>
                    От времени<br>(включая):<br>
                    <input type="time" name="startTime" value="${filterParams.startTime}">
                </label></td>
                <td><label>
                    До времени<br>(включая):<br>
                    <input type="time" name="endTime" value="${filterParams.endTime}">
                </label></td>
            </tr>
            <tr>
                <td colspan="4" style="text-align: right">
                    <button type="submit" style="float: right">Отфильтровать</button>
                </td>
            </tr>
        </table>
    </form>
    <a href="meals?action=create">Add Meal</a>
    <br><br>
    <table border="1" cellpadding="8" cellspacing="0">
        <thead>
        <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Calories</th>
            <th></th>
            <th></th>
        </tr>
        </thead>
        <c:forEach items="${requestScope.meals}" var="meal">
            <jsp:useBean id="meal" type="ru.javawebinar.topjava.to.MealTo"/>
            <tr class="${meal.excess ? 'excess' : 'normal'}">
                <td>
                        <%--${meal.dateTime.toLocalDate()} ${meal.dateTime.toLocalTime()}--%>
                        <%--<%=TimeUtil.toString(meal.getDateTime())%>--%>
                        <%--${fn:replace(meal.dateTime, 'T', ' ')}--%>
                        ${fn:formatDateTime(meal.dateTime)}
                </td>
                <td>${meal.description}</td>
                <td>${meal.calories}</td>
                <td><a href="meals?action=update&id=${meal.id}">Update</a></td>
                <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
            </tr>
        </c:forEach>
    </table>
</section>
</body>
</html>