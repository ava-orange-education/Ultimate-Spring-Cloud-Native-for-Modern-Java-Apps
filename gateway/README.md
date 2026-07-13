# API Gateway (future chapter)

This folder is reserved for a **Spring Cloud Gateway** that supports the Strangler Fig migration.

## Intended responsibilities

- Route `/api/attendance/**` to the extracted attendance service
- Route `/api/notifications/**` to the extracted notification service
- Forward remaining `/api/**` traffic to the monolith
- Centralize cross-cutting concerns (correlation IDs, rate limiting demos)

## Example route configuration (to be implemented)

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: attendance-service
          uri: http://attendance-service:8080
          predicates:
            - Path=/api/attendance/**
        - id: monolith-fallback
          uri: http://campusflow-monolith:8080
          predicates:
            - Path=/api/**
```

## Book chapter topics

- Edge routing during incremental extraction
- Feature-flagged cutover
- Health-aware load balancing
- Observability at the gateway
