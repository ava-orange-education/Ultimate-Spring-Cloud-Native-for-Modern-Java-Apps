# Notification Service

Companion material for the book chapter on extracting the **notification** module from the monolith.

## What the monolith does today

The notification module sends enrollment confirmations and absence alerts. It stores each message in the `notifications` table and logs it (standing in for an external email provider).

**Monolith code:**
- `monolith-baseline/src/main/java/com/campusflow/notification/service/NotificationService.java`
- Called from `EnrollmentService` and `AttendanceService`

## What the book builds next

A standalone service with its own database:

```
GET  /api/notifications
POST /api/notifications/enrollment-confirmation
POST /api/notifications/absence-alert
```

## Try it in the monolith

```bash
curl -X POST http://localhost:8080/api/attendance \
  -H 'Content-Type: application/json' \
  -d '{"studentId":1,"classId":1,"date":"2026-07-14","status":"ABSENT"}'

curl http://localhost:8080/api/notifications
```
