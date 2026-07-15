# Gateway Routing — Extraction Guide

Companion guide for Chapter 7 (Introducing Spring Cloud Components) and Chapter 14 (Real-World Case Studies).

This document shows how an API gateway routes traffic during a Strangler Fig migration. It is **not** a runnable Spring Cloud Gateway project. The routing example illustrates target architecture described in the book.

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

## Related guides

- [notification-service.md](notification-service.md) — first extraction candidate
- [attendance-service.md](attendance-service.md) — second extraction candidate
