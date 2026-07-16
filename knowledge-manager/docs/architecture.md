# Architecture

The project follows a layered architecture.

```
Controller
↓

Service
↓

Repository
↓

Database
```

Packages should be organized by feature.

Example

```
problem

topic

review

solution

dashboard
```

Each feature contains

```
controller

service

repository

entity

dto

mapper
```

The frontend will use Thymeleaf templates.

Business logic must remain inside the service layer.