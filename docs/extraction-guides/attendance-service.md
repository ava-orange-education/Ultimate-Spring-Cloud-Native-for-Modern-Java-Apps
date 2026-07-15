# Attendance Service — Extraction Guide

Companion guide for Chapter 5 (Identifying Microservice Boundaries) and Chapter 14 (Real-World Case Studies).

This document describes how the attendance module in the monolith maps to a standalone service. It is **not** a runnable project or deployable. The implementation lives in `monolith-baseline/`.

## Current state in the monolith

The attendance module records daily presence or absence per student and class. It validates that the student is enrolled before accepting a record.

**Code:**

- `monolith-baseline/src/main/java/com/campusflow/attendance/service/AttendanceService.java`
- `monolith-baseline/src/main/java/com/campusflow/attendance/controller/AttendanceController.java`

**API today (monolith):**

```
GET  /api/attendance?classId=&date=
POST /api/attendance
```

## Why attendance is a sensible but harder extraction candidate

Attendance has clear domain cohesion — it owns attendance records and rules about marking presence. However, it depends on enrollment state to enforce eligibility, which makes it **more complex to extract than notifications**.

| Factor | Assessment |
|--------|------------|
| **Cohesion** | High — attendance rules and data are self-contained |
| **Data ownership** | Owns `attendance_records`; does not own enrollment data |
| **Coupling** | Medium — reads enrollment via `EnrollmentRepository` directly; calls `NotificationService` synchronously |
| **Change frequency** | Attendance policies (grace periods, excused absences) may evolve independently of enrollment |
| **Risk** | Medium — incorrect enrollment checks across a service boundary allow invalid records |

## Dependencies today

```
AttendanceService
    ├── StudentService.getStudent()           (read master data)
    ├── SchoolClassService.getSchoolClass()   (read master data)
    ├── EnrollmentRepository.exists...()      (validate enrollment — direct repository access)
    └── NotificationService.sendAbsenceAlert() (direct call — not yet event-based)
```

Enrollment already decouples from notifications through `StudentEnrolledInClassEvent`. Attendance still uses a direct call for absence alerts — the next refactoring step in the monolith.

The enrollment check bypasses `EnrollmentService` and queries `EnrollmentRepository` directly. This is a common monolith shortcut that becomes a **cross-service contract** after extraction.

## Data and consistency questions

| Question | Monolith answer | After extraction |
|----------|----------------|------------------|
| Is the student enrolled? | Local SQL query on `enrollments` table | Remote call to enrollment API, cached read, or replicated enrollment snapshot |
| What if enrollment is revoked after attendance is marked? | Same database — consistency is immediate | Eventual consistency; attendance service may hold stale enrollment view |
| Who owns the student-class relationship? | Enrollment module | Attendance must treat enrollment as an external dependency |
| Duplicate attendance for same day? | `UNIQUE (student_id, class_id, attendance_date)` constraint | Same constraint in attendance service's own database |

These questions are what Chapter 5 asks you to resolve before drawing service boundaries.

## Why attendance needs more coordination than notifications

Notifications receive all required data from callers and own their table completely. Attendance must **ask another domain a question** ("is this student enrolled?") before it can accept work. That question crosses a boundary that does not exist in the monolith today.

Extracting attendance before solving this dependency would either:

- Force synchronous calls to an enrollment API on every attendance mark, or
- Require a local copy of enrollment data that must be kept in sync

Both options add complexity that notification extraction does not face.

## Risks of extraction

| Risk | Detail |
|------|--------|
| **Stale enrollment data** | Attendance service might mark records for students who were unenrolled milliseconds ago |
| **Increased latency** | Every attendance mark requires a cross-service enrollment check |
| **Partial failure** | Attendance saved but absence notification fails — today both roll back; after split, needs compensation |
| **Reporting across domains** | "Attendance rate per class" may need data from enrollment and attendance services |

## Stepwise extraction path

A practical sequence:

1. **Strengthen the monolith boundary first** — replace `EnrollmentRepository` access with a call to `EnrollmentService` (or a dedicated enrollment query API within the monolith)
2. **Extract notifications** — enrollment already uses domain events; attendance still calls directly. Replace the absence call with an event before routing traffic through a gateway.
3. **Extract attendance** — deploy as a service with its own `attendance_records` database
4. **Define enrollment check contract** — attendance calls `GET /api/enrollments/exists?studentId=&classId=` or consumes enrollment events
5. **Route through gateway** — [gateway-routing.md](gateway-routing.md) shows `/api/attendance/**` routing to the new service

## Strangler Fig fit

Attendance is typically the **second route** after notifications:

```
/api/notifications/**  →  notification-service
/api/attendance/**     →  attendance-service
/api/**                →  monolith (students, classes, enrollment)
```

The core monolith keeps shrinking as routes migrate, but students, classes, and enrollment remain until their boundaries are equally clear.

## Try it in the monolith

```bash
curl "http://localhost:8080/api/attendance?classId=1&date=2026-07-14"

curl -X POST http://localhost:8080/api/attendance \
  -H 'Content-Type: application/json' \
  -d '{"studentId":1,"classId":1,"date":"2026-07-14","status":"PRESENT"}'
```

Alex (student 1) is pre-enrolled in Algebra I (class 1). Attempting attendance for a non-enrolled student returns `409 Conflict`.
