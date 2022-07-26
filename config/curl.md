# Admin
`curl -i http://localhost:8080/topjava/rest/admin/users`

`curl -i http://localhost:8080/topjava/rest/admin/users/100000`

`curl -i http://localhost:8080/topjava/rest/admin/users/100000/with-meals`

`curl -i -H 'Content-Type: application/json' -d '{"name": "New2", "email": "new2@yandex.ru", "password": "passwordNew", "roles": ["USER"]}' http://localhost:8080/topjava/rest/admin/users`

`curl -X PUT -i -H 'Content-Type: application/json' -d '{"name": "UserUpdated", "email": "user@yandex.ru", "password": "passwordNew", "roles": ["USER"]}' http://localhost:8080/topjava/rest/admin/users/100000`

# Profile
`curl -i http://localhost:8080/topjava/rest/profile`

`curl -i http://localhost:8080/topjava/rest/profile/with-meals`

`curl -X PUT -i -H 'Content-Type: application/json' -d '{"name": "New777", "email": "new777@yandex.ru", "password": "passwordNew", "roles": ["USER"]}' http://localhost:8080/topjava/rest/profile`

`curl -X DELETE -i http://localhost:8080/topjava/rest/profile`

# Meals
`curl -i http://localhost:8080/topjava/rest/meals`

`curl -i "http://localhost:8080/topjava/rest/meals?startDate=2020-01-30&endDate=2020-01-30&startTime=&endTime="`

`curl -i http://localhost:8080/topjava/rest/meals/100003`

`curl -i -H 'Content-Type: application/json; charset=UTF-8' -d '{"dateTime": "2020-02-01T18:00:00", "description": "Созданный ужин", "calories": 300}' http://localhost:8080/topjava/rest/meals`

`curl -X DELETE -i http://localhost:8080/topjava/rest/meals/100012`

`curl -X PUT -i -H 'Content-Type: application/json' -d '{"dateTime": "2020-01-30T10:15:00", "description": "Обновленный завтрак", "calories": 200}' http://localhost:8080/topjava/rest/meals/100003`


