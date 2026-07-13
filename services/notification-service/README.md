# Notification Service (extraction candidate)

This folder is reserved for a later book chapter where the **notification** bounded context is extracted from the monolith.

## Why notifications first?

- Clear boundary: outbound messaging with its own persistence
- Called synchronously from enrollment and attendance today
- Natural fit for async processing and external providers
- Low risk starting point for the Strangler Fig pattern

## Planned API surface

```
GET  /api/notifications
POST /api/notifications/absence-alert
POST /api/notifications/enrollment-confirmation
```

## Migration seam in the monolith

See `monolith-baseline/src/main/java/com/campusflow/notification/service/NotificationService.java` and callers in:

- `EnrollmentService`
- `AttendanceService`

Later chapters can replace direct service calls with HTTP or messaging while routing through an API gateway.
