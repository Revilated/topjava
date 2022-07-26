# curl запросы для проверки REST API
## Admin
Запрос всех пользователей:

`curl -i http://localhost:8080/topjava/rest/admin/users`

Запрос одного пользователя:

`curl -i http://localhost:8080/topjava/rest/admin/users/100000`

Запрос одного пользователя с едой:

`curl -i http://localhost:8080/topjava/rest/admin/users/100000/with-meals`

Создание нового пользователя:

`curl -i -H 'Content-Type: application/json' -d '{"name": "New2", "email": "new2@yandex.ru", "password": "passwordNew", "roles": ["USER"]}' http://localhost:8080/topjava/rest/admin/users`

Изменение пользователя:

`curl -X PUT -i -H 'Content-Type: application/json' -d '{"name": "UserUpdated", "email": "user@yandex.ru", "password": "passwordNew", "roles": ["USER"]}' http://localhost:8080/topjava/rest/admin/users/100000`

## Profile
Запрос текущего пользователя:

`curl -i http://localhost:8080/topjava/rest/profile`

Запрос текущего пользователя с едой:

`curl -i http://localhost:8080/topjava/rest/profile/with-meals`

Изменение текущего пользователя:

`curl -X PUT -i -H 'Content-Type: application/json' -d '{"name": "New777", "email": "new777@yandex.ru", "password": "passwordNew", "roles": ["USER"]}' http://localhost:8080/topjava/rest/profile`

Удаление пользователя:

`curl -X DELETE -i http://localhost:8080/topjava/rest/profile`

## Meals
Запрос всей еды текущего пользователя:

`curl -i http://localhost:8080/topjava/rest/meals`

Запрос всей еды текущего пользователя с фильтрацией по дате и времени:

`curl -i "http://localhost:8080/topjava/rest/meals?startDate=2020-01-30&endDate=2020-01-30&startTime=&endTime="`

Запрос одной еды текущего пользователя:

`curl -i http://localhost:8080/topjava/rest/meals/100003`

Создание новой еды для текущего пользователя:

`curl -i -H 'Content-Type: application/json; charset=UTF-8' -d '{"dateTime": "2020-02-01T18:00:00", "description": "Созданный ужин", "calories": 300}' http://localhost:8080/topjava/rest/meals`

Удаление еды текущего пользователя:

`curl -X DELETE -i http://localhost:8080/topjava/rest/meals/100012`

Изменение еды текущего пользователя:

`curl -X PUT -i -H 'Content-Type: application/json' -d '{"dateTime": "2020-01-30T10:15:00", "description": "Обновленный завтрак", "calories": 200}' http://localhost:8080/topjava/rest/meals/100003`
