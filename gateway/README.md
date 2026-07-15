# API Gateway

Companion guidance for Chapter 7 (Introducing Spring Cloud Components) and Chapter 14 (Strangler Fig routing).

This folder contains a **routing example only** — not a runnable Spring Cloud Gateway project.

## Routing model

| Path | Target |
|------|--------|
| `/api/notifications/**` | Notification service |
| `/api/attendance/**` | Attendance service |
| `/api/**` | Monolith |

## Example configuration

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: notification-service
          uri: http://notification-service:8081
          predicates:
            - Path=/api/notifications/**
        - id: attendance-service
          uri: http://attendance-service:8082
          predicates:
            - Path=/api/attendance/**
        - id: monolith
          uri: http://campusflow-monolith:8080
          predicates:
            - Path=/api/**
```

## Monolith APIs referenced

- `monolith-baseline/src/main/java/com/campusflow/notification/controller/NotificationController.java`
- `monolith-baseline/src/main/java/com/campusflow/attendance/controller/AttendanceController.java`
