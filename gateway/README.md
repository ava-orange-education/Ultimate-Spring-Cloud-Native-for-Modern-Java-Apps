# API Gateway

Companion material for the book chapter on the **Strangler Fig** pattern and API gateway routing.

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
