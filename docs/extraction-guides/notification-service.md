# Notification Service — Extraction Guide

Companion guide for Chapter 5 (Identifying Microservice Boundaries) and Chapter 14 (Real-World Case Studies).

This document describes how the notification module in the monolith maps to a standalone service. It is **not** a runnable project or deployable. The implementation lives in `monolith-baseline/`.

## Current state in the monolith

The notification module sends enrollment confirmations and absence alerts. It stores each message in the `notifications` table and logs it (standing in for an external email provider).

**Code:**

- `monolith-baseline/src/main/java/com/campusflow/notification/service/NotificationService.java`
- `monolith-baseline/src/main/java/com/campusflow/notification/controller/NotificationController.java`

**API today (monolith):**

```
GET /api/notifications
```

Sending happens internally. Enrollment triggers notifications through a domain event; attendance calls `NotificationService` directly. There is no public POST endpoint for creating notifications.

## Why notifications are a strong first extraction candidate

| Factor | Assessment |
|--------|------------|
| **Cohesion** | Single purpose: compose and deliver messages |
| **Data ownership** | Owns `notifications` table; no foreign keys to other tables |
| **Coupling** | Enrollment via domain event; attendance via direct call |
| **Change frequency** | Delivery channel, templates, and retry logic change independently of enrollment or attendance rules |
| **Risk** | Low — extracting notifications does not break core enrollment or attendance workflows if communication is decoupled |

## Dependencies today

**Enrollment → Notification (event-based, implemented):**

```
EnrollmentService  ── publish ──► StudentEnrolledInClassEvent
                                        │
                                        ▼
                          EnrollmentNotificationListener  ──► NotificationService
```

**Attendance → Notification (direct call, implemented):**

```
AttendanceService  ──► NotificationService.sendAbsenceAlert()
```

Enrollment does not call `NotificationService` directly. The listener runs synchronously in the same application — in-process, but decoupled through a domain event.

Absence alerts still use a direct service call. That mixed state shows gradual refactoring in progress.

## What extraction would improve

- **Independent scaling** — notification volume can grow without affecting enrollment response times
- **Failure isolation** — a broken email provider does not block enrollment
- **Technology freedom** — a dedicated service uses its own message queue, template engine, or provider
- **Clear data boundary** — `notifications` table moves to a dedicated database

## Risks and open questions

| Risk | Detail |
|------|--------|
| **Distributed transaction** | Today enrollment and notification succeed or fail together. After extraction, you need a decoupling strategy. |
| **Data at the boundary** | An extracted service needs a contract for what data crosses the boundary (email, name, class title — not entire entities). |
| **Idempotency** | Retried messages must not send duplicate emails. The monolith does not handle this today. |
| **Observability** | Tracing a notification back to its triggering enrollment or attendance record requires correlation IDs. |

## Communication options after extraction

| Style | How it works | Trade-off |
|-------|-------------|-----------|
| **Synchronous HTTP** | Enrollment calls `POST /notifications/enrollment-confirmation` and waits | Simple, but reintroduces runtime coupling and latency |
| **Asynchronous messaging** | Enrollment publishes `StudentEnrolledInClass`; notification service consumes it | Decouples failure and timing; requires a message broker |
| **Event-driven** | Domain events on a bus; notification is one of several subscribers | Most flexible; adds infrastructure complexity |

The monolith currently uses in-process Spring Application Events. The book progresses toward asynchronous and event-driven patterns as coupling is gradually removed.

## Strangler Fig fit

Notifications are a natural **first route** through an API gateway:

1. Deploy notification service alongside the monolith
2. Gateway routes `GET /api/notifications/**` to the new service
3. Replace in-process calls with HTTP or message-based communication
4. Move the `notifications` table to the service's own database

The monolith continues to handle students, classes, enrollment, and attendance. Only notification traffic migrates.

See [gateway-routing.md](gateway-routing.md) for the routing example.

## Try it in the monolith

```bash
curl -X POST http://localhost:8080/api/attendance \
  -H 'Content-Type: application/json' \
  -d '{"studentId":1,"classId":1,"date":"2026-07-14","status":"ABSENT"}'

curl http://localhost:8080/api/notifications
```

Marking a student absent triggers an absence alert notification. The second command lists all notifications stored so far.
